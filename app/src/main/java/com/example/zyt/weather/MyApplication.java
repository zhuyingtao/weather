package com.example.zyt.weather;

import android.app.Application;

import com.baidu.apistore.sdk.ApiStoreSDK;

/**
 * Created by zyt on 15/12/22 17:00.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        // add my baidu apikey.
        ApiStoreSDK.init(this, "9bdd57dcca271529842ea806fcb07573");
        super.onCreate();
    }
}
