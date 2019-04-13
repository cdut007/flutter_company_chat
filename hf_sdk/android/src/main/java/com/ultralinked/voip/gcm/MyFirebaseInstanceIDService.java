package com.ultralinked.voip.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ultralinked.voip.api.Log;
import com.ultralinked.voip.api.MLoginApi;
import com.ultralinked.voip.api.MessagingApi;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (!TextUtils.isEmpty(refreshedToken)){
            sendRegistrationToServer(this,refreshedToken);
        }

    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public static void sendRegistrationToServer(Context context, String token) {
        // TODO: Implement this method to send token to your app server.
        // Add custom implementation, as needed.

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        try{

            sharedPreferences.edit().putString("token",token).commit();

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        }catch (Exception e){
            Log.i(TAG,"register token info  error:"+ android.util.Log.getStackTraceString(e));
        }


        if(MLoginApi.isLogin()){
            if (MessagingApi.GCMProjectNumber==null){
                Log.i(TAG,"send gcm token ,but the GCMProjectNumber is null to xmpp server");
                return;
            }
            Log.i(TAG,"send token : "+token+" to xmpp server");

            MessagingApi.sendAppToken(token,"android");



            try {
                // [END get_token]
                Log.i(TAG, "GCMProjectNumber:" + MessagingApi.GCMProjectNumber + "GCM Registration Token: " + token);


                // [END register_for_gcm]
            } catch (Exception e) {
                Log.d(TAG, "Failed to complete token refresh", e);
                // If an exception happens while fetching the new token or updating our registration data
                // on a third-party server, this ensures that we'll attempt the update at a later time.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            }
            // Notify UI that registration has completed, so the progress indicator can be hidden.
            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(context).sendBroadcast(registrationComplete);
        }else {

            Log.i(TAG,"~ not login ~");
        }
    }
}
