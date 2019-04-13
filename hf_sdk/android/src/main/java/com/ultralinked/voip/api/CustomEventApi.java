package com.ultralinked.voip.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


/**
 * Created by yongjun on 4/25/16.
 */
public class CustomEventApi {

    private static final String EVENT_ROOM_USER_UPDATE = "userinfo_update";
    private static String EVENT_ROOM_RESPONSE = "my room response";
    public static final String EVENT_QR_LOGIN_RESPONSE = "scan_qr_code_to_login";

    private static final String TAG = "CustomEventApi";

    private static Context mContext;

    /**
     * read socket address from configure. when first time, will use default "http://uc.ultralinked.com:80" ,
     * after login, will redirect to the address from responsed http result
     */
    public static void redirect() {
        Log.i("sokect", "redirect domain address");
        connect();
    }

    protected static void init(Context context) {

        mContext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();

    }


    protected static synchronized void connect() {


    }



    public static void qrConnect(final String sid, final String access_token) {


    }
}
