package com.ultralinked.voip.api;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ultralinked.voip.imapi.FriendInfo;
import com.ultralinked.voip.imapi.c_VCard;
import com.ultralinked.voip.imapi.eShow;
import com.ultralinked.voip.imapi.imapij;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * contact reletive api class
 *
 */
public class ContactHelper {

	public static final String TAG = "ContactApi";

	public static final String EVENT_FRIEND_INFO_CHANGE= "com.ultralinked.voip.friendInfoChange";

	public static final String EVENT_FRIEND_LIST_INFO_CHANGE= "com.ultralinked.voip.friendListInfoChange";

	public static final String EVENT_ADD_FRIEND_REQUEST = "com.ultralinked.voip.addFriendRequest";

	public static final String EVENT_ADD_FRIEND_FEEDBACK = "com.ultralinked.voip.addFriendFeedback";

	public static final String EVENT_FRIEND_STATUS_CHANGE= "com.ultralinked.voip.friendStatusChange";

	public static final String PARAM_FROM_TO = "from_to";

	public static final String PARAM_DATA = "data";

	public static final String PARAM_IS_ACCEPT = "accept";

	public static final String PARAM_STATUS = "status";

	public static final String PARAM_FRIEND = "friend";

	public static final String PROFILE_STATUS_KEY = "profile_status";

	public static final String PROFILE_PHONENUMBERS_KEY = "profile_phone_numbers";

	public static final String PROFILE_NICKNAME_KEY = "status";

	public static final String PROFILE_URL_KEY = "url";

	public static final String PHONE_HOME = "HOME1";

	public static final String PHONE_WORK = "WORK1";

	public static final String PHONE_MOBILE = "MOBILE1";

	public static final String PHONE_OTHER = "OTHER1";




	/**
	 * send a local broadcast to when contact info changed
	 */
	protected static void sendContactInfoChangeBroadcast(String peopleEntityStr){

		Intent callHandlerIntent = new Intent(EVENT_FRIEND_INFO_CHANGE);

		callHandlerIntent.putExtra(PARAM_DATA, peopleEntityStr);
		if (MessagingApi.mContext != null) {

			Log.i(TAG, "send friend status change broadcast --> " + peopleEntityStr);

			LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}
	}

	/**
	 * get all contacts of the current user
	 * @return contact list of the current user
	 */

	public static List<Contact> getAllContacts()  {

		List<Contact> friends=new ArrayList<Contact>();

		FriendInfo[] tmp= imapij.netimGetRoster();

		Log.i(TAG, "get friend list size : "+tmp.length);

		try {

			for (FriendInfo friendInfo : tmp) {

				Contact contact = new Contact();
				contact.setNickName(new String(friendInfo.getFriend_nickname(), "UTF-8"));
				contact.setName(friendInfo.getFriend_name());
				contact.setStatus(new String(friendInfo.getFriend_status(), "UTF-8"));
				friends.add(contact);

			}
		}catch (Exception e){
			e.printStackTrace();
		}
		if(tmp!=null&&tmp.length>0) {
			imapij.netimReleaseRoster(tmp[0], tmp.length);
		}

		return friends;

	}

	/**
	 * remove a contact by name
	 * @param  friendName the friend name
	 */

	public static void removeFriend(String friendName){

		Log.i(TAG, "remove friend name : "+friendName);

		imapij.netimRemoveFriend(friendName);
	}


	/**
	 * accept invitation  with the friend which name is friendName to be friend relationship
	 * @param  friendName the friend name
	 */

	public static void acceptFriendInvite(String friendName){


		Log.i(TAG, "accept friend invite name : "+friendName);

		imapij.netimAgreeToBeFriend(friendName, false);

	}





	/**
	 * reject invitation with the friend which name is friendName to be friend relationship
	 * @param  friendName the friend name
	 */

	public static void rejectFriendInvite(String friendName){

		Log.i(TAG, "reject friend invite name : "+friendName);

		imapij.netimRejectToBeFriend(friendName);


	}

	/**
	 * invite the friend  which name is friendName to be friend relationship
	 * @param friendName
	 */
	public static void InviteFriend(String friendName){

			if(TextUtils.isEmpty(friendName)){
				return;
			}

		Log.i(TAG, "grInviteFriend : " + friendName);

		imapij.netimInviteFriend(friendName);



	}



	/**
	 * send a local broadcast to when friend list is received
	 */

	protected static void sendFriendListBroadcast(String from) {

		Intent contactHandlerIntent = new Intent(EVENT_FRIEND_LIST_INFO_CHANGE);

		contactHandlerIntent.putExtra(PARAM_FROM_TO, from);

		if (MessagingApi.mContext != null) {

			Log.i(TAG, "send login status change broadcast --> " + from);

			LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(contactHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}

	}

	/**
	 * send a local broadcast to when friend invitation is received
	 */

	protected static void sendAddFriendBroadcast(String from) {

		Intent callHandlerIntent = new Intent(EVENT_ADD_FRIEND_REQUEST);

		callHandlerIntent.putExtra(PARAM_FROM_TO, from);

		if (MessagingApi.mContext != null) {

			Log.i(TAG, "send login status change broadcast --> " + from);

			LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}

	}

	/**
	 * send a local broadcast to when friend invitation result is feedback
	 */
	protected static void sendAddFriendFeedbackBroadcast(String from, boolean isAccept) {

		Intent callHandlerIntent = new Intent(EVENT_ADD_FRIEND_FEEDBACK);

		callHandlerIntent.putExtra(PARAM_IS_ACCEPT, isAccept);
		callHandlerIntent.putExtra(PARAM_FROM_TO, from);

		if (MessagingApi.mContext != null) {

			Log.i(TAG, "send login status change broadcast --> " + from);

			LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}

	}
	/**
	 * send a local broadcast to when friend info changed
	 */
	protected static void sendFriendStatusChangeBroadcast(String from, String status){

		Intent callHandlerIntent = new Intent(EVENT_FRIEND_STATUS_CHANGE);
		callHandlerIntent.putExtra(PARAM_STATUS, status);
		callHandlerIntent.putExtra(PARAM_FROM_TO, from);

		if (MessagingApi.mContext != null) {

			Log.i(TAG, "send frien status change broadcast --> " + from);

			LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);

		} else {

			Log.i(TAG, "TestApplication is not Running()");
		}
	}

	/**
	 * check the specified friend's latest information
	 * @param name
	 */
	public  static void checkFriend(String name){

		Log.i(TAG,"checkFriend : "+name);

		imapij.netimGetVCard(name);
	}

	/**
	 * send a local broadcast when friend info changed
	 *
	 */
	protected static void sendFriendInfoChangeBroadcast(Contact contact){

//		Intent callHandlerIntent = new Intent(EVENT_FRIEND_INFO_CHANGE);
//
//		callHandlerIntent.putExtra(PARAM_FRIEND, contact);
//
//		if (MessagingApi.mContext != null) {
//
//			Log.i(TAG, "send frien info change broadcast --> " + contact.getName());
//
//			LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(callHandlerIntent);
//
//		} else {
//
//			Log.i(TAG, "TestApplication is not Running()");
//		}


	}

	/**
	 * save the contact information to server
	 * @param profiles the contact key value pairs
	 */
	public static void saveProfile(HashMap<String,Object> profiles) {

		if(profiles==null||profiles.isEmpty()){
			Log.w(TAG, "profiles is null can not save");
            return;
		}

		Log.i(TAG, "save profile");

        try{

		c_VCard vcard=new c_VCard();

		if (!TextUtils.isEmpty((String) profiles.get(PROFILE_NICKNAME_KEY))) {

			String nickName= (String)profiles.get(PROFILE_NICKNAME_KEY);

		    Log.i(TAG,"set NickName : "+nickName);

			vcard.setNickName(nickName.getBytes("UTF-8"));
		}
		if (!TextUtils.isEmpty((String) profiles.get(PROFILE_STATUS_KEY))) {

			String status= (String)profiles.get(PROFILE_STATUS_KEY);

			Log.i(TAG,"set status : "+status);

			vcard.setDescription(status.getBytes("UTF-8"));
		}
		if (!TextUtils.isEmpty((String) profiles.get(PROFILE_URL_KEY))) {

			String url= (String)profiles.get(PROFILE_URL_KEY);

			Log.i(TAG,"set url : "+url);

			vcard.setURL(url.getBytes("UTF-8"));
		}
		HashMap<String,String> phones=(HashMap<String,String>)profiles.get(PROFILE_PHONENUMBERS_KEY);

		vcard.setTelLength(4);

		vcard.setTelKey(new String[]{PHONE_MOBILE, PHONE_HOME, PHONE_WORK, PHONE_OTHER});

		String[] phoneValues=new String[4];

		if (phones != null && !phones.isEmpty()) {

			phoneValues[0] = (phones.get(PHONE_MOBILE) == null ? "" : phones.get(PHONE_MOBILE));

			phoneValues[1] = (phones.get(PHONE_HOME) == null ? "" : phones.get(PHONE_HOME));

			phoneValues[2] = (phones.get(PHONE_WORK) == null ? "" : phones.get(PHONE_WORK));

			phoneValues[3] = (phones.get(PHONE_OTHER) == null ? "" : phones.get(PHONE_OTHER));
		}
			for (int i = 0; i <phoneValues.length ; i++) {

				Log.i(TAG,"number : "+phoneValues[i]);

			}

		vcard.setTelValue(phoneValues);

		vcard.setEmailLength(0);

			imapij.netimSetVCard(vcard);


		}catch (UnsupportedEncodingException e){

			e.printStackTrace();
		}

	}

	//convert c vcard to java contact
	protected static Contact convertVcard2Contact(String name, c_VCard vCard){

		Contact contact=new Contact();

		try {

			contact.setName(name);

			contact.setNickName(new String(vCard.getNickName(), "UTF-8"));

			contact.setStatus(new String(vCard.getDescription(), "UTF-8"));

			contact.setUrl(new String(vCard.getURL(),"UTF-8"));

        if(vCard.getTelLength()>0){

			contact.setMobilePhone(vCard.getTelValue()[0]);
		}

		}catch (UnsupportedEncodingException e){

			e.printStackTrace();
		}

		return  contact;
	}
	public  static  boolean getFriendOnlineStatus(String friendName){

		return  imapij.netimGetFriendStatus(friendName)== eShow.SHOW_ONLINE;
	}

}
