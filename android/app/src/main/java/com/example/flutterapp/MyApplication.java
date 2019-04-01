package com.example.flutterapp;

import android.content.Context;

import com.holdingfuture.flutterapp.hfsdk.HfSdkPlugin;

import io.flutter.app.FlutterApplication;
import io.flutter.view.FlutterMain;

/**
 * Created by ASUS on 2019/3/29.
 */

public class MyApplication extends FlutterApplication {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        //bugly初始化
        FlutterMain.startInitialization(this);
//        HfSdkPlugin.init(this);
        context = this;
    }
}
