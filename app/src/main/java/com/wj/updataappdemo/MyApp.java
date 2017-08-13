package com.wj.updataappdemo;

import android.app.Application;
import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by wj on 2017/5/9.
 */

public class MyApp extends Application {

    public static Context mContext;

    private String TAG = "MyApp";


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = MyApp.this;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(TAG))
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static Context getMyApplicationContext() {
        return mContext;
    }

}
