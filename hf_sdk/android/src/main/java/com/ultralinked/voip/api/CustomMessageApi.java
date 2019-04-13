package com.ultralinked.voip.api;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ultralinked.voip.rtcapi.rtcapij;

/**
 * Created by wenjie on 2016/3/15.
 */
public class CustomMessageApi {

    public static final String EVENT_CUSTOM_MESSAGE_RECEIVER = "com.ultralinked.voip.customMessage";
    public static final String PARAM_FROM = "from";
    public static final String PARAM_MESSAGE = "message";
    private final static String TAG = "CustomMessageApi";


    public  static CustomMessage createNewCustomMessage(){
        return  new CustomMessage();
    }


    public static void sendCustomMessage(String contact, String message) {

        Log.i(TAG,"send custom message to : "+contact+" content : "+message);

        rtcapij.netrtc_acc_message(CallApi.configName, contact, message);

    }


    protected static void sendCustomMessageBroadcast(String from, String message) {

        Log.i(TAG,"receiver the custom message : from : "+from+" content : "+message);

        Intent callHandlerIntent = new Intent(EVENT_CUSTOM_MESSAGE_RECEIVER);

        callHandlerIntent.putExtra(PARAM_FROM, from);

        callHandlerIntent.putExtra(PARAM_MESSAGE, message);

        if (CallApi.getContext() != null) {

            LocalBroadcastManager.getInstance(CallApi.getContext()).sendBroadcast(callHandlerIntent);

        } else {


        }

    }
}
