package com.wj.updataappdemo.utils;

import android.content.pm.PackageManager;

import com.wj.updataappdemo.MyApp;
import com.wj.updataappdemo.R;

/**
 * Created by wj on 2017/5/9.
 */

public class Utils {

    /**
     * 获取应用当前版本代码
     *
     * @return
     */
    public static int getCurrentVerCode() {
        String packageName = MyApp.getMyApplicationContext().getPackageName();
        int currentVer = -1;
        try {
            currentVer = MyApp.getMyApplicationContext().getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVer;
    }

    /**
     * 获取应用当前版本名称
     *
     * @return
     */
    public static String getCurrentVerName() {
        String packageName = MyApp.getMyApplicationContext().getPackageName();
        String currentVerName = "";
        try {
            currentVerName = MyApp.getMyApplicationContext().getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVerName;
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName() {
        return MyApp.getMyApplicationContext().getResources().getText(R.string.app_name).toString();
    }
}
