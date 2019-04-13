package com.ultralinked.voip.gcm;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ultralinked.voip.api.Log;
import com.ultralinked.voip.api.MLoginApi;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }


        Bundle data = new Bundle();
        if (remoteMessage.getFrom().startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // Check if message contains a notification payload.
            try{
                if (remoteMessage.getNotification() != null) {
                    Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    // normal downstream message.
                    // when receive the push notification ,we relogin to sip and xmpp server
                    data.putString("message",remoteMessage.getNotification().getBody());
                    data.putString("from",remoteMessage.getFrom());
                }else{
                    String info = remoteMessage.getData().get("alert");
                    Log.d(TAG, "Message Notification Body: " + info);
                    // normal downstream message.
                    // when receive the push notification ,we relogin to sip and xmpp server
                    data.putString("message",info);
                    String chatType = remoteMessage.getData().get("chat_type");
                    if (!TextUtils.isEmpty(chatType)){
                        data.putString("chat_type",chatType);
                    }
                    String senderStr = remoteMessage.getData().get("sender");
                    if (!TextUtils.isEmpty(senderStr)){
                        JSONObject sender = new JSONObject(senderStr);
                        data.putString("sender",sender.optString("sender_id"));
                    }

                    data.putString("from",remoteMessage.getFrom());
                }

            }catch (Exception e){
                Log.i(TAG, "parse gcm push info error:"+android.util.Log.getStackTraceString(e));
            }

            MLoginApi.LoginFromGCM(this,data);

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

}
