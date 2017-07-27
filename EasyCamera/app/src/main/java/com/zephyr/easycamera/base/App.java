package com.zephyr.easycamera.base;

import android.app.Application;

/**
 * Created by zephyr on 2017/7/27.
 */

public class App extends Application {

    public static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
