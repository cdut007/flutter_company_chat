package com.ultralinked.voip.api;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.ultralinked.voip.rtcapi.rtcapij;

// network type change broadcast
public class ConnectionChangeReceiver extends BroadcastReceiver {
    private final static String TAG = "ConnectionChangeReceiver";
    private Handler handler = new Handler(Looper.getMainLooper());
    private final static String LocalLoopAddress = "127.0.0.1";

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //WIFI

            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);//MOBILE


            boolean hasAddAccount = LoginApi.hasAddAccount();
            // network change to wifi
            if (wifiNetInfo != null && wifiNetInfo.isConnected()) {

                MLoginApi.reloginByNetwork(context);
                // regist to sip server when ip address change
                if (hasAddAccount) {

                    String ipAddress = NetworkManager.getWIFILocalIpAdress(context);

                    Log.i(TAG, "reset network in wifi : " + ipAddress);

                    if (!LocalLoopAddress.equals(ipAddress)) {

                        rtcapij.netrtc_acc_reset_network(CallApi.configName);

                        CallApi.networkChange = true;
                    }

                }

                handler.removeCallbacksAndMessages(null);

                notifyCallSessionNetwokHasChanged();

            }

            //network change to mobile network
            if (mobNetInfo != null && mobNetInfo.isConnected()) {

                MLoginApi.reloginByNetwork(context);

                // regist to sip server when ip address change
                if (hasAddAccount) {

                    String ipAddress = NetworkManager.getGPRSLocalIpAddress();

                    Log.i(TAG, "reset network in 3g : " + ipAddress);

                    if (!LocalLoopAddress.equals(ipAddress)) {

                        rtcapij.netrtc_acc_reset_network(CallApi.configName);

                        CallApi.networkChange = true;
                    }
                }
                handler.removeCallbacksAndMessages(null);

                notifyCallSessionNetwokHasChanged();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void notifyCallSessionNetwokHasChanged() {
        CallSession callSession = CallApi.getFgCallSession();
        if (callSession != null) {
            callSession.networkHasChanged = true;
            CallApi.addOrUpdateCallSession(callSession);

        }
    }
}

