package com.wj.updataappdemo;

import android.app.Application;
import android.content.Context;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by wj on 2017/5/9.
 */

public class MyApp extends Application {

    public static Context mContext;

    private String TAG = "MyApp";


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        FileDownloader.setup(mContext);
    }

    public static Context getContext() {
        return mContext;
    }

}
