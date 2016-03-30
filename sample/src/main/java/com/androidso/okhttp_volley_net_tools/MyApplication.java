package com.androidso.okhttp_volley_net_tools;

import android.app.Application;

import com.androidso.lib.net.api.HttpConfig;
import com.androidso.lib.net.controller.DaoRequest;
import com.androidso.lib.net.http.RequestManager;

/**
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.getInstance(this);
        HttpConfig.DEBUG = BuildConfig.DEBUG;
        DaoRequest.addCommonParams("json", "1");
    }
}
