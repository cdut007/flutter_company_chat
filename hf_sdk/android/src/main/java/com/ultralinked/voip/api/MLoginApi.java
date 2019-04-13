package com.ultralinked.voip.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ultralinked.voip.gcm.MyFirebaseInstanceIDService;

public class MLoginApi {


    private static final String USER_PREF_NAME = "user_account_info";

    protected static void initAccount() {
        if (MLoginApi.currentAccount == null || TextUtils.isEmpty(MLoginApi.currentAccount.id)) {
            Log.i(TAG, "currnet id is null ,should never happend this");
            MLoginApi.currentAccount = new Account(MLoginApi.getFromLocal(MessagingApi.mContext));
        }

    }

    public static final class Account {
        public String id;
        public String userName;
        public String password;
        public String nickName;
        public String mobile;

        public Account() {

        }

        Account(Account a) {
            id = a.id;
            userName = a.userName;
            password = a.password;
            nickName = a.nickName;
            mobile = a.mobile;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "id='" + id + '\'' +
                    ", userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", mobile='" + mobile + '\'' +
                    '}';
        }


        public void delete() {
            Log.i(TAG, "delete current account:" + toString());
            id = "";
            userName = "";
            nickName = "";
            mobile = "";
            password = "";
            //clear all this username and account push need use to pull up the app
            SharedPreferences.Editor editor = MessagingApi.mContext.getApplicationContext().getSharedPreferences(USER_PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.remove("id").remove("password").remove("mobile").remove("nickname").remove("username").commit();
        }


    }


    private static void save(Account account) {
        //save this username and account push need use to pull up the app
        MessagingApi.mContext.getApplicationContext().getSharedPreferences(USER_PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString("id", account.id)
                .putString("mobile", account.mobile).putString("nickname", account.nickName).putString("username", account.userName)
                .putString("password", account.password).commit();
        //read other info

    }

    protected static Account getFromLocal(Context context) {

        SharedPreferences preferences = MessagingApi.mContext.getApplicationContext().getSharedPreferences(USER_PREF_NAME, Context.MODE_PRIVATE);

        Account account = new Account();
        account.id = preferences.getString("id", "");

        account.password = preferences.getString("password", "");
        account.userName = preferences.getString("username", "");
        account.mobile = preferences.getString("mobile", "");
        account.nickName = preferences.getString("nickname", "");
        Log.i(TAG, "account info:" + account.toString());
        return account;

    }


    public static final String TAG = "MLoginApi";

    public static final String PARAM_LOGIN_STATUS = "mlogin_status";

    public static final String EVENT_LOGIN_STATUS_CHANGE = "com.ultralinked.voip.mloginStatusChange";

    public static Account currentAccount = new Account();

    public static final int STATUS_REGISTER_OK = 2;

    public static final int STATUS_CONNECTING = 1;

    public static final int STATUS_REGISTER_ACCOUNT_ERROR = 5;

    public static final int STATUS_USER_LOGOUT = 0;

    public static final int STATUS_SERVER_FORCE_LOGOUT = 3;

    public static final int STATUS_RECONNECT = 4;

    public static final int STATUS_DISCONNECTED = 8;

    public static boolean isConnecting;


    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Get user xmpp server register status
     *
     * @return true register to xmpp server false disconected from the xmpp server
     */
    public static boolean isLogin() {

        boolean isLogin = MessagingApi.isLogin();

        return isLogin;
    }

    /**
     * relogin to the xmpp server
     */
    public static void reLogin() {

        Log.i(TAG, "relogin");

        if (currentAccount != null && TextUtils.isEmpty(currentAccount.id)) {
            Log.i(TAG, "relogin return ,maybe in sub proress");
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {

                //grwork is destroyed, call login
                Log.i(TAG, "call relogin by login");
                if (!TextUtils.isEmpty(currentAccount.id)) {
                    isConnecting = true;
                    int result = MessagingApi.relogin();
                    if (result == 0) {
                        isConnecting = false;
                    }
                } else {
                    Log.i(TAG, "call relogin by login, but the id not exsit.");
                }

            }
        });


    }

    static boolean userLoginFlag = false;


    static Runnable loginTask = null;

    public synchronized static void login(final Account account) {

        if (MessagingApi.mContext == null) {
            Log.i(TAG, "context not init ,maybe in sub proress");
            return;
        }

        if (account != null && TextUtils.isEmpty(account.id)) {
            Log.i(TAG, "login return ,maybe in sub proress");
            return;
        }

        if (isConnecting) {
            Log.i(TAG, "login id : " + account.id + " is isConnecting");
            return;
        }

        Log.i(TAG, "login id : " + account.id);

        if (TextUtils.isEmpty(account.id)) {
            Log.i(TAG, "login id  is not has. " + account.id);
            return;
        }

        currentAccount = new Account(account);
        userLoginFlag = true;
        isConnecting = true;

//		grrtcXmppcallback = new NetRtcXMPPCallbackImpl();
//		imapij.setIMCallbackObject(grrtcXmppcallback);
        MessagingApi.setConfig("IM_HOST", PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("imserver", "im.uc.sealedchat.com"));

        String port = PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("im_tls_port", "");
        if (TextUtils.isEmpty(port)) {
            port = PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("import", "5222");
        }
        MessagingApi.setConfig("IM_PORT", port);

        MessagingApi.setConfig("IM_DOMAIN", PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("sipdomain", "uc"));

        handler.removeCallbacks(loginTask);
        loginTask = new Runnable() {
            @Override
            public void run() {
                if (currentAccount != null && !TextUtils.isEmpty(currentAccount.id)) {
                    MessagingApi.LoginXmpp(currentAccount);

                }
            }
        };
        handler.postDelayed(loginTask, 100);


    }

    /**
     * logout from the xmpp server
     */
    public static void logout() {
        //		imapij.setIMCallbackObject(null);
        if (currentAccount != null && TextUtils.isEmpty(currentAccount.id)) {
            Log.i(TAG, "logout return ,maybe in sub proress");
            return;
        }
        isConnecting = false;
        currentAccount.delete();
        clearLoginLooperCheck();

        if (MessagingApi.GCMProjectNumber != null) {
            MessagingApi.sendAppToken("delete", "android");
        } else {

            MessagingApi.sendAppToken("delete", "umeng");

        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessagingApi.disconnect();
            }
        }, 1000);


    }

    public static boolean LoginFromPush(Context context) {


        Account account = new Account(getFromLocal(context));

        if (account != null && TextUtils.isEmpty(account.id)) {
            Log.i(TAG, "LoginFromPush return ,maybe in sub proress");
            return false;
        }

        currentAccount = account;
        android.util.Log.i(TAG, "receive push login username : " + currentAccount.id + " isConnecting : " + MLoginApi.isConnecting + " islogin : " + MLoginApi.isLogin());

        boolean hasUserName = !TextUtils.isEmpty(currentAccount.id);
        if (hasUserName && !MLoginApi.isConnecting && !MLoginApi.isLogin()) {

            MLoginApi.login(currentAccount);

        }

        if (!LoginApi.isLogin()) {
            LoginApi.login(currentAccount.id, currentAccount.password);

        }
        return hasUserName;
    }

    public static void LoginFromGCM(Context context, Bundle data) {

        //send bordcast to user.
        boolean hasUserName = LoginFromPush(context);

        String message = data.getString("message");
        if (message == null) {
            Log.i(TAG, "send push broadcast is null ");
            return;
        }

        if (!hasUserName) {
            Log.i(TAG, "user maybe already logout no need to login");
            return;
        }

        Intent callHandlerIntent = new Intent(MessagingApi.EVENT_MESSAGE_PUSH);
        Message pushMessage = new Message();
        pushMessage.setType(Message.MESSAGE_TYPE_PUSH);
        pushMessage.setSender(data.getString("sender"));

        int chatType = Message.CHAT_TYPE_SINGLE;

        String chatTypeStr = data.getString("chat_type");

        if (TextUtils.isEmpty(chatTypeStr)){
            chatType = -1;
        }else{
            if ("groupchat".equals(chatTypeStr)){

                chatType = Message.CHAT_TYPE_GROUP;
            }
        }


        pushMessage.setChatType(chatType);
        callHandlerIntent.putExtra(MessagingApi.PARAM_MESSAGE, pushMessage);
        callHandlerIntent.putExtra("data", data);

        if (MessagingApi.mContext != null) {

            Log.i(TAG, "send push broadcast --> ");

            LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "loginGCM is not Running()");
        }

    }


    static int retryCount = 0;

    static Runnable loginRunnable = null;


    protected static void reloginByNetwork(Context context) {

            if (TextUtils.isEmpty(currentAccount.id)) {
                Log.i("reloginByNetwork", "not find im relogin account info ,maybe in sub proess");
                return;
            }
            if (isConnecting) {
                Log.i(TAG, "current is connecting...");
                return;
            }
            //reset network ,because network is changed no need to check is logined in.

            handler.removeCallbacks(loginRunnable);
            loginRunnable = new Runnable() {
                @Override
                public void run() {

                    if (isConnecting) {
                        Log.i("Login", "im is isConnecting.");
                        return;
                    }
                    retryCount++;
                    if (retryCount > 5) {//reset network.
                        retryCount = 0;
                    }
                    if (TextUtils.isEmpty(currentAccount.id)) {

                        Log.i("reloginByNetwork", "im relogin ,not find current account info.");

                        return;
                    }
                    Log.i("reloginByNetwork", "im relogin by network changed , must do it.");
                    MLoginApi.reLogin();


                }
            };

            handler.postDelayed(loginRunnable, 1000);


    }


    public static void checkLoginStatus() {

            if (currentAccount != null && TextUtils.isEmpty(currentAccount.id)) {
                Log.i(TAG, "check Login status return ,maybe in sub proress");
                return;
            }

            boolean isLogin = isLogin();

            if (!isLogin && !isConnecting) {

                handler.removeCallbacks(loginRunnable);
                loginRunnable = new Runnable() {
                    @Override
                    public void run() {

                        if (isConnecting) {
                            Log.i("Login", "im is isConnecting.");
                            return;
                        }
                        retryCount++;
                        if (retryCount > 5) {//reset network.
                            retryCount = 0;
                            Log.i("Login", "im is isConnecting reach max retry count.");
                            return;
                        }
                        if (TextUtils.isEmpty(currentAccount.id)) {

                            Log.i("Login", "im relogin ,not find current account info.");

                            return;
                        }
                        Log.i("Login", "im relogin");
                        if (!isLogin()) {
                            isConnecting = true;
                            MLoginApi.reLogin();
                            if (!LoginApi.isLogin()) {
                                LoginApi.login(currentAccount.id, currentAccount.password);

                            }
                        }
                    }
                };

                handler.postDelayed(loginRunnable, 1000);
            } else {
                Log.i(TAG, "the login status is logged on.");
            }

    }


    private static void clearLoginLooperCheck() {
        handler.removeCallbacksAndMessages(null);
    }

    protected static void sendLoginStatusBroadcast(int status) {

        if (STATUS_REGISTER_OK == status) {
            //for gcm
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "login & Refreshed token: " + refreshedToken);

            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            if (MessagingApi.GCMProjectNumber != null) {
                if (TextUtils.isEmpty(refreshedToken)) {
                    refreshedToken = PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("token", "");
                    Log.d(TAG, "read native file Refreshed token: " + refreshedToken);
                }

                if (!TextUtils.isEmpty(refreshedToken)) {
                    Log.i(TAG, "send gcm token : " + refreshedToken + " to xmpp server");
                    MessagingApi.sendAppToken(refreshedToken,"android");
                } else {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(MessagingApi.mContext, MyFirebaseInstanceIDService.class);
                    MessagingApi.mContext.startService(intent);
                }
            }


        }


        if (currentAccount != null && TextUtils.isEmpty(currentAccount.id)) {
            Log.i(TAG, "sendLoginStatus return ,maybe in sub proress");
            return;
        }

        //reset login status
        if (STATUS_REGISTER_OK == status) {
            retryCount = 0;
            if (userLoginFlag) {//user login success
                userLoginFlag = false;
                    save(currentAccount);
                    if (MessagingApi.mContext != null) {
                        Account account = getFromLocal(MessagingApi.mContext);
                        if (!TextUtils.isEmpty(account.id)) {
                            currentAccount = new Account(account);
                        } else {
                            Log.i(TAG, "getFromLocal failed :" + account.toString());
                        }
                    }

            }

        }

        //reset
        if (status != STATUS_CONNECTING) {
            isConnecting = false;
        }

        if (STATUS_SERVER_FORCE_LOGOUT == status) {
            //force logout.
            currentAccount.delete();
        }

        Intent callHandlerIntent = new Intent(EVENT_LOGIN_STATUS_CHANGE);

        callHandlerIntent.putExtra(PARAM_LOGIN_STATUS, status);

        if (MessagingApi.mContext != null) {

            Log.i(TAG, "send login status change broadcast --> " + status);

            LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "TestApplication is not Running()");
        }

    }


}
