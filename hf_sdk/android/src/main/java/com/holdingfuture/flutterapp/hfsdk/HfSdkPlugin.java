package com.holdingfuture.flutterapp.hfsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * HfSdkPlugin
 */
public class HfSdkPlugin implements MethodCallHandler {
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "hf_sdk");
        HfSdkPlugin instance = new HfSdkPlugin(registrar.activity());
        channel.setMethodCallHandler(instance);
    }

    private HfSdkPlugin(Activity activity) {
        this.activity = activity;
    }

    public static void init(Application application) {
        Log.i("bugly", "init bugly info");
        Bugly.init(application, "10828316ff", true);
    }


    private Activity activity;

    private boolean flag = true;

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE+"wy");
        } else if (call.method.equals("buglyInit")) {
            Bugly.init(activity.getApplication(), "10828316ff", true);
        } else if (call.method.equals("getLatestVersion")) {
            Beta.checkUpgrade();
        } else {
            result.notImplemented();
        }
    }

    public static void attachBaseContext(Context base) {

        MultiDex.install(base);
    }
}
