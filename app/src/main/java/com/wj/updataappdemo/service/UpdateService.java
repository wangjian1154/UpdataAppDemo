package com.wj.updataappdemo.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.wj.updataappdemo.utils.Utils;

import java.io.File;


/**
 * 版本更新Service
 */
public class UpdateService extends Service {

    private String TAG = "UpdateService";
    private String apkURL;
    private static final String PACKAGE_NAME = Utils.getPackageName();
    private static final String APP_NAME = Utils.getAppName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();//取消
        } else {
            apkURL = intent.getStringExtra("apkUrl");
            startDownload();//启动下载
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startDownload() {
        String path = getDownloadPathName();
        File file = new File(path);
        if (file != null && file.exists()) {
            file.delete();
        }
        FileDownloader.getImpl().create(apkURL)
                .setPath(path)
                .setListener(new FileDownloadListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i(TAG, "pending");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i(TAG, "progress");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //下载完成
                        Log.i(TAG, "completed");
                        Toast.makeText(UpdateService.this, "下载完成", Toast.LENGTH_SHORT).show();
                        installApk();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i(TAG, "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.i(TAG, "error");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.i(TAG, "warn");
                    }
                }).start();
    }

    private String getDownloadPathName() {
        String cacheDir = getExternalCacheDir().getAbsolutePath();
        String path = cacheDir + "/" + APP_NAME + ".apk";
        return path;
    }

    private void installApk() {
        File apkFile = new File(getDownloadPathName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(UpdateService.this, PACKAGE_NAME + ".fileprovider", apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
