package com.ultralinked.voip.api;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ultralinked.voip.imapi.c_Broadcast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yongjun on 5/9/16.
 */
public class BroadcastApi {

    static  final String TAG_COMPOSING = "composing";

    static  final String TAG_DATA = "data";

    static  final String TAG_PAINT = "paint";

    public  static  final String TAG = "BroadcastApi";


    public static Broadcast convert2Broadcast(c_Broadcast c_broadcast) {
        if (c_broadcast==null){
            Log.i(TAG,"c_broadcast is null");
            return null;
        }
        Broadcast broadcast = new Broadcast();
        String content = null;
        try {
            content = new String(c_broadcast.getMsg_body(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MLoginApi.initAccount();
        if (!c_broadcast.getMsg_sender().equalsIgnoreCase(MLoginApi.currentAccount.id)) {

            broadcast.setSender(false);

        }else{
            broadcast.setSender(true);
        }

        broadcast.setSenderName(c_broadcast.getMsg_sender());
        broadcast.setReceiver(c_broadcast.getMsg_receiver());
        broadcast.setBody(content);

        return broadcast;
    }


    private  static Map<String,Broadcast> typingConvMap = new HashMap<String,Broadcast>();
     static Object locker = new Object();

     static final class   TypingRunable implements Runnable {
         String senderName ;
        public TypingRunable(String senderName){
            this.senderName  = senderName;
        }

         @Override
         public void run() {
             synchronized (locker){
                 Broadcast typingBroadcast =  typingConvMap.get(senderName);
                 if (typingBroadcast!=null){
                     Log.i(TAG,"stop typing ;"+senderName);
                     try {
                         JSONObject jsonObject = new JSONObject(typingBroadcast.getBody());
                         JSONObject dataObj = jsonObject.optJSONObject(TAG_DATA);
                         dataObj.put(MessagingApi.PARAM_COMPOSING_STATUS,false);
                         jsonObject.put(TAG_DATA,dataObj);
                         typingBroadcast.setBody(jsonObject.toString());
                         sendIncomingBroadcast(typingBroadcast);
                     } catch (Exception e) {
                         e.printStackTrace();
                         Log.i(TAG, "stop typing error;" + e.getLocalizedMessage());
                     }
                 }
             }


         }
     }

    public static void sendIncomingBroadcast(Broadcast broadcast) throws JSONException {
        Intent callHandlerIntent = new Intent();
        JSONObject jsonObject = new JSONObject(broadcast.getBody());
        Log.i(TAG, "recevieBroadcast body: " + broadcast.getBody());
        String tag = jsonObject.optString(MessagingApi.PARAM_BROADCAST_TYPE);
        JSONObject dataObj = jsonObject.optJSONObject(TAG_DATA);
        callHandlerIntent.putExtra(MessagingApi.PARAM_FROM_TO, broadcast.getSenderName());
        if (TAG_COMPOSING.equals(tag)){
            //{"BroadcastType":"composing","data":{"composing_status":true}}
           callHandlerIntent.setAction(MessagingApi.EVENT_COMPOSING);
            boolean composingStatus = dataObj.optBoolean(MessagingApi.PARAM_COMPOSING_STATUS);
            synchronized (locker) {
                if (composingStatus) {
                    Broadcast typingBroadcast = typingConvMap.get(broadcast.getSenderName());
                    if (typingBroadcast != null) {//delay to next
                        typingConvMap.remove(broadcast.getSenderName());
                    }
                    typingConvMap.put(broadcast.getSenderName(), broadcast);
                    Conversation.stopTyping(new TypingRunable(broadcast.getSenderName()));
                } else {
                    typingConvMap.remove(broadcast.getSenderName());
                }
            }
           callHandlerIntent.putExtra(MessagingApi.PARAM_COMPOSING_STATUS,composingStatus);

       }else if(TAG_PAINT.equals(tag)){

            callHandlerIntent.setAction(MessagingApi.EVENT_PAINT);
            String paintInfo = dataObj.optString(MessagingApi.PARAM_PAINT_INFO);
            callHandlerIntent.putExtra(MessagingApi.PARAM_PAINT_INFO,paintInfo);

        }else{

            callHandlerIntent.setAction(MessagingApi.EVENT_BROADCAST);
            String dataInfo = dataObj.toString();
            callHandlerIntent.putExtra(MessagingApi.PARAM_BROADCAST_INFO,dataInfo);
            callHandlerIntent.putExtra(MessagingApi.PARAM_BROADCAST_TYPE,tag);

        }

        if (MessagingApi.mContext != null) {

            LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

        } else {
            Log.i(TAG, "sendIncomingBroadcast--> context is null");
        }

    }

    public static String getComposingStatusJson(boolean isComposing) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(MessagingApi.PARAM_COMPOSING_STATUS, isComposing);
            return getFormattedBroadcastJsonStr(TAG_COMPOSING,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }





    public static String getCustomJson(String tag, JSONObject jsonObject) {

        try {
            return getFormattedBroadcastJsonStr(tag,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public static String getPaintStatusJson(JSONObject paint) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(MessagingApi.PARAM_PAINT_INFO, paint);
            return getFormattedBroadcastJsonStr(TAG_PAINT,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    private  static String getFormattedBroadcastJsonStr(String customMsgType, JSONObject data) throws
            JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessagingApi.PARAM_BROADCAST_TYPE, customMsgType);
            jsonObject.put(TAG_DATA,data);
            return  jsonObject.toString();
        }


}
