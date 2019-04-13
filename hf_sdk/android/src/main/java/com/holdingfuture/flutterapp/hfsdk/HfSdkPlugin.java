package com.holdingfuture.flutterapp.hfsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import com.ultralinked.uluc.ChatModule;
import com.ultralinked.voip.api.CallApi;
import com.ultralinked.voip.api.Conversation;
import com.ultralinked.voip.api.LoginApi;
import com.ultralinked.voip.api.MLoginApi;
import com.ultralinked.voip.api.Message;
import com.ultralinked.voip.api.MessagingApi;
import com.ultralinked.voip.rtcapi.rtcapij;

import com.ultralinked.voip.imapi.imapij;

import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * HfSdkPlugin
 */
public class HfSdkPlugin implements MethodCallHandler {
    private  rtcapij rtcapij;
    private  imapij imapij;
    private static  String TAG = "HfSdkPlugin";
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
        } else if (call.method.equals("getAllConversations")) {
            List<Conversation> conversations = MessagingApi.getAllConversations();
            result.success(conversations);
        } else if (call.method.equals("getAllConversations")) {
//            List<Message> messages = ChatModule.getMessageListWithFront();
//            result.success(messages);
        } else if (call.method.equals("sendTextMessage")) {
            ChatModule.sendTextMessage();
          //  result.success(conversations);
        }  else if (call.method.equals("auth")) {

            ChatModule.Auth();


        }else {
            result.notImplemented();
        }
    }

    public static void attachBaseContext(Context base) {
        MultiDex.install(base);
    }
}
