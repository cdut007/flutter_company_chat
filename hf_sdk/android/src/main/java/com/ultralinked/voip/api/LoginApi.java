package com.ultralinked.voip.api;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ultralinked.voip.api.utils.VersionUtil;
import com.ultralinked.voip.rtcapi.rtcapij;

import org.json.JSONObject;

public class LoginApi {

	/**
	 *Current login account
	 */
	public static String currentAccont="";

	public static final String TAG = "LoginApi";
	/**
	 * login status change action
	 */
	public static final String EVENT_LOGIN_STATUS_CHANGE = "com.ultralinked.voip.loginStatusChange";
	/**
	 * login status  key
	 */
	public static final String PARAM_LOGIN_STATUS = "login_status";

	public static final String PARAM_KICK_OUT_REASON = "reason";

	/**
	 * login status register ok
	 */
	public static final int STATUS_REGISTER_OK = 0;

	/**
	 * login status register time out
	 */
	public static final int STATUS_REGISTER_TIME_OUT = 1;

	public static final int STATUS_REGISTER_ACCOUNT_ERROR = 2;

	public static final int STATUS_REGISTER_ACCOUNT_FAILURE = 3;

	public static final int STATUS_USER_LOGOUT = 4;

	public static final int STATUS_SERVER_FORCE_LOGOUT=7;

	public static final int STATUS_CONNECTING = 6;

	public static final int STATUS_CONNECTING_ERROR = 10;

    protected static boolean isDiconnecting;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	protected static void sendLoginStatusBroadcast(int status) {

		Intent callHandlerIntent = new Intent(EVENT_LOGIN_STATUS_CHANGE);

		callHandlerIntent.putExtra(PARAM_LOGIN_STATUS, status);

		if (CallApi.getContext() != null) {

			Log.i(TAG, "send login status change broadcast --> " + status);

			LocalBroadcastManager.getInstance(CallApi.getContext()).sendBroadcast(callHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}

	}
	protected static void sendLoginStatusBroadcast(int status,String reason) {

		Intent callHandlerIntent = new Intent(EVENT_LOGIN_STATUS_CHANGE);

		callHandlerIntent.putExtra(PARAM_LOGIN_STATUS, status);
		callHandlerIntent.putExtra(PARAM_KICK_OUT_REASON, reason);

		if (CallApi.getContext() != null) {

			Log.i(TAG, "send login status change broadcast --> " + status);

			LocalBroadcastManager.getInstance(CallApi.getContext()).sendBroadcast(callHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}

	}
	/**
	 * get the sdk version of current sdk
	 * @return sdk version
	 */
	public static String getSDKVersion(){

		String version= rtcapij.netrtc_version().substring(0, rtcapij.netrtc_version().indexOf("-"));

		return version.replace("0.0", VersionUtil.Version);
	}

	/**
	 * set  audio  codes of current user
	 * @param codecs
	 */
	public static void  setAudioCodecs(String codecs){

		PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).edit().putString("codecs", codecs).commit();
	}
	/**
	 * login to sip server with username and password
	 * @param username
	 * @param password
	 */
	public static void login(final String username, final String password) {
			//check config exsit
		if (MessagingApi.mContext == null){
			Log.i(TAG, "context not init ,maybe in sub proress");
			return;
		}

		isDiconnecting = false;
		//for sonnect
	   new Thread(new Runnable() {
		   @Override
		   public void run() {
			   CustomEventApi.connect();
			   login2server(username, password);
		   }
	   }).start();




	}


	public  static String getDomain(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MessagingApi.mContext);
		String userConfigToken = preferences.getString("sipdomain","caas.grcaassip.com");
		return userConfigToken;
	}

	public  static String getAccessToken(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MessagingApi.mContext);
		String userConfigToken = preferences.getString("access_token",null);
		return userConfigToken;
	}

	private static void login2server(String username, String password) {

		currentAccont=username;

	/*	if(LoginApi.isLogin()||LoginApi.isConnecting()) {
			Log.i(TAG, "need not login again ");
			return;
		}*/

		Log.i(TAG, "login username : " + username );
		Log.i(TAG, "login password : " + password );
		if (TextUtils.isEmpty(username) ){
			Log.i(TAG, "login username  is null ,return " );
			return;
		}

		if (TextUtils.isEmpty(password) ){
			Log.i(TAG, "login password  is null ,return " );
			return;
		}


		SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(CallApi.getContext());

		long acc_param = rtcapij.netrtc_hashmap_init();

		Log.i(TAG, "set audio codec : " + PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("audio_codecs", "iLBC,opus,PCMU,PCMA,G729,G722"));

		rtcapij.netrtc_set_config("support_codec", PreferenceManager.getDefaultSharedPreferences(CallApi.getContext()).getString("audio_codecs", "iLBC,opus,PCMU,PCMA,G729,G722"));
		rtcapij.netrtc_set_config("connect_mode", preferences.getString("connect_mode", "tcp")); // tcp
		rtcapij.netrtc_set_config("X-Access-Token", password+""); // sip head
		rtcapij.netrtc_init_done();

		rtcapij.netrtc_hashmap_set(acc_param, "sipdomain", preferences.getString("sipdomain", "uc"));//
		rtcapij.netrtc_hashmap_set(acc_param, "sipproxyip", preferences.getString("sipproxyip","voip.uc.sealedchat.com" )); //203.117.31.251
		rtcapij.netrtc_hashmap_set(acc_param, "sipproxyport",preferences.getString("sipproxyport", "5060"));
		rtcapij.netrtc_hashmap_set(acc_param, "siptunnelip", preferences.getString("siptunnelip", "voip.uc.sealedchat.com"));
		rtcapij.netrtc_hashmap_set(acc_param, "siptunnelport",preferences.getString("siptunnelport", "443"));
		rtcapij.netrtc_hashmap_set(acc_param, "httpproxyip", preferences.getString("httpproxyip", "voip.uc.sealedchat.com"));
		rtcapij.netrtc_hashmap_set(acc_param, "httpproxyport",preferences.getString("httpproxyport", "3128"));

		try{
			String info = preferences.getString("UserInfo","");
			JSONObject jsonObject = new JSONObject(info);

			String userName = jsonObject.optString("userName");
			if (TextUtils.isEmpty(userName)){
				userName = jsonObject.optString("nickName");
			}
			if (TextUtils.isEmpty(userName)){
				userName = jsonObject.optString("mobile");
			}

			if (!TextUtils.isEmpty(userName)){
				Log.i(TAG, "set user displayname  : " + userName);
				rtcapij.netrtc_hashmap_set(acc_param, "displayname", userName);
			}

		}catch (Exception e){
			Log.i(TAG, "set user displayname failed : " + e.getLocalizedMessage() );
		}



		rtcapij.netrtc_acc_add(CallApi.configName, username, password, acc_param);

		rtcapij.netrtc_acc_register(CallApi.configName);

		Log.i(TAG, "account register : " + CallApi.configName);

	}

	/**
	 * get user register status
	 * @return true user have login to sip server false user have disconected from sip server
	 */
	public static boolean isLogin(){

		int status= rtcapij.netrtc_acc_get_register_status(CallApi.configName);

		//Log.i(TAG, "login status : "+status);

		return (status==1?true:false);
	}

	/**
	 * get user connecting status
	 * @return true user is connecting to sip server false not doing
	 */
	public static boolean isConnecting(){

		int status= rtcapij.netrtc_acc_get_register_status(CallApi.configName);

		Log.i(TAG, "login status : "+status);

		return status==0?true:false;
	}


	public static boolean hasAddAccount(){

	    int status= rtcapij.netrtc_acc_get_register_status(CallApi.configName);

		Log.i(TAG, "login status : " + status);

		return status!=-2?true:false;
	}

	/**
	 * user logout from the sip server
	 */
	public static void logout() {

		Log.i(TAG, "user logout");

		isDiconnecting=true;

		currentAccont="";

		new Thread(new Runnable() {

			@Override
			public void run() {

			    rtcapij.netrtc_acc_unregister(CallApi.configName);
				rtcapij.netrtc_acc_del(CallApi.configName);
			    if(CallApi.peerConnectionClient!=null){

		         CallApi.peerConnectionClient.isEnd=true;

				 CallApi.peerConnectionClient.isICECompleted=true;
			    }


			}
		}).start();
	}

	/**
	 * check the device does support the gcm function or not
	 * @param context
	 * @return
	 */
	public static boolean checkPlayServices(Activity context) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {

				android.util.Log.i(TAG, "This device do not have the google paly service.");
				/*
				apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			*/ }else {
				android.util.Log.i(TAG, "This device is not supported.");
			}
			return false;
		}
		return true;
	}

    public static void qrLogin(String sip) {
        if (isLogin()) {
            CustomEventApi.qrConnect(sip, getAccessToken());
        }
    }
}
