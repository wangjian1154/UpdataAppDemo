package com.wj.updataappdemo.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.wj.updataappdemo.MyApp;
import com.wj.updataappdemo.R;
import com.wj.updataappdemo.utils.SdcardUtils;
import com.wj.updataappdemo.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by wj on 2017/5/9.
 */

public class UpdateService extends Service {

    private String apkURL;

    private NotificationManager notificationManager;

    private NotificationCompat.Builder builder;

    private int mProceess = -1;

    private SdcardUtils sdcardUtils;

    private static final String PACKAGE_NAME = MyApp.getMyApplicationContext().getPackageName();

    private static final String APP_NAME = Utils.getAppName();

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sdcardUtils = new SdcardUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyUser("下载失败", 0);
            stopSelf();//取消
        } else {
            apkURL = intent.getStringExtra("apkUrl");
            initNotify();
            initFileDir();
            notifyUser("下载开始", 0);
            startDownload();//启动下载
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建文件夹
     */
    private void initFileDir() {
        if (SdcardUtils.existSdcard()) {
            if (!sdcardUtils.isFileExist(PACKAGE_NAME)) {//不存在就重新创建文件
                sdcardUtils.creatSDDir(PACKAGE_NAME);
            }
        } else {
            Toast.makeText(this, "sd卡不存在！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initNotify() {
        builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(APP_NAME);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void startDownload() {
        OkHttpUtils//
                .get()//
                .tag(this)
                .url(apkURL)//
                .build()//
                .execute(new FileCallBack(sdcardUtils.getSDPATH() + "/" + PACKAGE_NAME, APP_NAME)//
                {
                    int fProgress;

                    @Override
                    public void inProgress(float progress, long total, int id) {//下载进度
                        super.inProgress(progress, total, id);
                        fProgress = (int) (100 * progress);
                        if (fProgress != mProceess) {//避免刷新太快，会卡死
                            mProceess = fProgress;
                            notifyUser("正在下载", fProgress);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {//下载失败
                        notifyUser("下载失败", 0);
                        stopSelf();
                    }

                    @Override
                    public void onResponse(File response, int id) {//下载完成
                        notifyUser("下载完成", 100);
                        stopSelf();

                        getContentIntent();
                        if (notificationManager != null) {
                            notificationManager.cancelAll();
                        }

                    }
                });
    }

    private void notifyUser(String result, int process) {
        if (builder == null) {
            return;
        }
        if (process > 0 && process < 100) {
            builder.setProgress(100, process, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        if (process >= 100) {
//            builder.setContentIntent(getContentIntent());
            getContentIntent();
        }

        builder.setContentInfo(process + "%");
        builder.setContentText(result);
        notificationManager.notify(0, builder.build());
    }

    private void getContentIntent() {
        File apkFile = new File(sdcardUtils.getSDPATH() + "/" + PACKAGE_NAME + "/" + APP_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(UpdateService.this, PACKAGE_NAME + ".fileprovider", apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
