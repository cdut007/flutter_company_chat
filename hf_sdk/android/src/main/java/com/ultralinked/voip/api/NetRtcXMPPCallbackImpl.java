package com.ultralinked.voip.api;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ultralinked.voip.imapi.NetrtcimCallback;
import com.ultralinked.voip.imapi.c_Broadcast;
import com.ultralinked.voip.imapi.c_Message;
import com.ultralinked.voip.imapi.c_VCard;
import com.ultralinked.voip.imapi.eConversationEvent;
import com.ultralinked.voip.imapi.eFileTransferCase;
import com.ultralinked.voip.imapi.eMucEvent;
import com.ultralinked.voip.imapi.eShow;
import com.ultralinked.voip.imapi.imapij;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class NetRtcXMPPCallbackImpl extends NetrtcimCallback {
	public static String TAG	= "NetRtcXMPPCallbackImpl";
	private static final int	ON_CALL_STATE	= 2;
	private Handler msgHandler;
	private CallSession session;
	private Timer timerUpdateNetworkStatus=new Timer();
	public static String callFrom="";
	public static String sipCallId="";
	private TimerTask updateTimerValuesTask;
	public static boolean mutex=false;
	private int audioChannel=-1;
	private Lock lock = new ReentrantLock();

	@Override
	public void netrtcapi_msg_status_changed_callback(c_Message msg) {

		super.netrtcapi_msg_status_changed_callback(msg);

		MessagingApi.sendMessageStatusChangeBroadcast(MessageConvertImp.convert2Message(msg));



	}



	@Override
	public void netrtcapi_histroy_callback(String conversation, int chatType, int count) {
		super.netrtcapi_histroy_callback(conversation,chatType, count);
		MessagingApi.sendHistroyCallbackBroadcast(conversation,chatType, count);
	}

	@Override
	public void netrtcapi_broadcastrecv_callback(c_Broadcast broadcast) {
		super.netrtcapi_broadcastrecv_callback(broadcast);
	   //
		try {
			BroadcastApi.sendIncomingBroadcast(BroadcastApi.convert2Broadcast(broadcast));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i(TAG, "rtcapi_broadcastrecv_callback : " + e.getLocalizedMessage());
		}
	}

	@Override
	public void netrtcapi_Conversation_callback(eConversationEvent event, long hmap_id) {
		super.netrtcapi_Conversation_callback(event, hmap_id);
		Log.i(TAG, "rtcapi_Conversation_callback : " + event);
		if(event== eConversationEvent.CONV_CONFIG_UPDATE_EVENT){
//			#define PARAM_CONV_EVENT_TYPE                           "PARAM_CONV_EVENT_TYPE"
//			#define PARAM_CONV_CHAT_TYPE                            "PARAM_CONV_CHAT_TYPE"
			String chatId = imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_CONV_ID");
			String chatType = imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_CONV_CHAT_TYPE");
			int conversationType = Message.CHAT_TYPE_SINGLE;
			if (!"chat".equals(chatType)){//group
				chatId = chatId.toLowerCase();
				conversationType = Message.CHAT_TYPE_GROUP;
			}
			String eventCase= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_CONV_EVENT_TYPE");
			int action = MessagingApi.CONVERSATION_ACTION_MUTE;
			switch (eventCase){
				case "mute":
					action = MessagingApi.CONVERSATION_ACTION_MUTE;
					break;
				case "setProperties":
					action = MessagingApi.CONVERSATION_ACTION_PROPERTIES;
					break;
			}
			MessagingApi.sendConversationChangedBroadcast(chatId,conversationType,action);

		}
	}

	@Override
		public void netrtcapi_Muc_callback(eMucEvent event, long hmap_id) {

			super.netrtcapi_Muc_callback(event, hmap_id);

			Log.i(TAG,"rtcapi_Muc_callback : "+event);

			if(event== eMucEvent.MUC_MEMBER_INIT){

				String groupId= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_MEMBER_INIT_MUC_ID");

				Log.i(TAG, "group init successful groupId : "+groupId);
				if (TextUtils.isEmpty(groupId)){
					return;
				}
				MessagingApi.sendGroupMemberBroadcast(groupId.toLowerCase());


			}else if(event== eMucEvent.MUC_MEMBER_UPDATE){

				String groupId= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_MEMBER_UPDATE_MUC_ID");

				MessagingApi.sendGroupMemberBroadcast( groupId.toLowerCase());

				Log.i(TAG, "group memember update groupId : "+groupId);

			}else if(event== eMucEvent.MUC_INVITE_RECV){


				String invite= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_ISINVITE");

				String inviteFrom= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_FROM");

				String inviteGroupId= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_MUC_ID");

				String inviteGroupTitle= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_MUC_TITLE");

				String inviteGroupReason= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_REASON");

				Log.i(TAG, "group invite reveive inviteGroupTitle : "+inviteGroupTitle+" invite : "+invite);

				if(!TextUtils.isEmpty(invite)&&invite.equalsIgnoreCase("true")){

					MessagingApi.checkGroupMember(inviteGroupId);
					MessagingApi.sendGroupInviteBroadcast(inviteFrom,inviteGroupId.toLowerCase(),inviteGroupTitle, MessagingApi.RECV_INVITE_GROUP_SUCCESS);

				}else if(!TextUtils.isEmpty(invite)&&invite.equalsIgnoreCase("false")){

					Log.i(TAG, "group invite reveive  inviteGroupTitle : " + inviteGroupReason + " invite : " + invite);
				}
			}else if(event== eMucEvent.MUC_INVITE_SUCCESS){

				String invite= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_ISINVITE");

				String inviteFrom= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_FROM");

				String inviteGroupId= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_MUC_ID");

				String inviteGroupTitle= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_INVITE_RECV_MUC_TITLE");

				Log.i(TAG, "group invite success reveive inviteGroupTitle : " + inviteGroupTitle+";invite result:"+invite);
				if(!TextUtils.isEmpty(invite)&&invite.equalsIgnoreCase("true")){

					MessagingApi.checkGroupMember(inviteGroupId);
					MessagingApi.sendGroupInviteBroadcast(inviteFrom,inviteGroupId.toLowerCase(),inviteGroupTitle, MessagingApi.INVITE_GROUP_SUCCESS);

				}else if(!TextUtils.isEmpty(invite)&&invite.equalsIgnoreCase("false")){
					MessagingApi.sendGroupInviteBroadcast(inviteFrom, inviteGroupId.toLowerCase(), inviteGroupTitle, MessagingApi.INVITE_GROUP_FAILURE);
				}

			}else if(eMucEvent.MUC_JOIN_EVENT==event){

				String groupId= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_JOIN_EVENT_MUC_ID");

				String eventCase= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_JOIN_EVENT_CASE");

				Log.i(TAG, groupId + " event : " + eventCase);

				if(TextUtils.isEmpty(eventCase)){
                       return;
				}


				int status=-1;

				switch (eventCase){
					case "PARAM_MUC_JOIN_EVENT_CASE_VALUE_BE_KICKED":

						status= MessagingApi.KICK_OUT_BY_GROUP;

						break;
					case "PARAM_MUC_JOIN_EVENT_CASE_VALUE_JOIN_FAILED":

						status= MessagingApi.JOIN_GROUP_FAILURE;

						break;
					case "PARAM_MUC_JOIN_EVENT_CASE_VALUE_JOIN_SUCCEED":

						status= MessagingApi.JOIN_GROUP_SUCCESS;

						MessagingApi.checkGroupMember(groupId);
						break;
					case "PARAM_MUC_JOIN_EVENT_CASE_VALUE_LEAVE_SUCCEED":

						status= MessagingApi.LEAVE_GROUP_SUCCESS;

						break;
					case "PARAM_MUC_JOIN_EVENT_CASE_VALUE_LEAVE_FAILED":

						status= MessagingApi.LEAVE_GROUP_FAILURE;

						break;

				}
				Log.i(TAG,groupId+" status : "+status);
              MessagingApi.sendGroupInfoChangeBroadcast(groupId,status);

			}else if(eMucEvent.MUC_CONFIG_UPDATE_EVENT==event){

				String groupId= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_CONFIG_UPDATE_MUC_ID");

				String eventCase= imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_CONFIG_UPDATE_TYPE");

				Log.i(TAG, groupId + " event : " + eventCase);

				if(TextUtils.isEmpty(eventCase)){
					return;
				}

				int status=-1;
				String success = imapij.netrtcim_hashmap_getstr(hmap_id, "PARAM_MUC_CONFIG_UPDATE_SUCCED");
				switch (eventCase){
					case "title":
						if (success!=null&&success.equals("true")) {
							status = MessagingApi.TITLE_CHANGE_GROUP_SUCCESS;
						}else{
							status = MessagingApi.TITLE_CHANGE_GROUP_FAILURE;
						}
						break;


				}
				Log.i(TAG,groupId+" status : "+status);
				MessagingApi.sendGroupInfoChangeBroadcast(groupId,status);

			}
		}

		@Override
		public void netrtcapi_msgrecv_callback(c_Message msg) {

			if(msg==null){return;}

			super.netrtcapi_msgrecv_callback(msg);

			Log.i(TAG,"msg receiver : "+msg.getMsg_receiver()+" sender : "+msg.getMsg_sender());


			Message message= MessageConvertImp.convert2Message(msg);
			if (message == null){
				Log.i(TAG,"parse message is null ");
				return;
			}
			MessagingApi.sendIncomingMsgBroadcast(message);

		}
		@Override
		public void netrtcapi_Subscribe_callback(String from, boolean isReapt) {
			// receive the add friend request
			//  true accept  false reject
			Log.i(TAG, "rtcapi_Subscribe_callback  from : " + from + " isReapt : " + isReapt);

//			if(!isReapt){
//
//				ContactHelper.sendAddFriendBroadcast(from);
//
//			}else{
//
//				ContactHelper.sendAddFriendFeedbackBroadcast(from,true);
//
//				imapij.grAgreeToBeFriend(from, true);
//
//			}

		}

		@Override
		public void netrtcapi_UnSubscribed_callback(String from) {
			// TODO Auto-generated method stub

			// add friend been reject
			Log.i(TAG, "rtcapi_UnSubscribed_callback from : "+from);

			ContactHelper.sendAddFriendFeedbackBroadcast(from, false);
		}

	private  boolean isMainThread(){
		return  Looper.myLooper()
				== Looper.getMainLooper();
	}

		@Override
		public void netrtcapi_sockevent_callback(int status) {

			super.netrtcapi_sockevent_callback(status);



			Log.i(TAG, "~ sockevent callback : " + status+";is Main thread:"+isMainThread());

			if(status!=1){

				MLoginApi.isConnecting=false;

			}

			MLoginApi.sendLoginStatusBroadcast(status);

		}

		@Override
		public void netrtcapi_pingtimeout_callback() {


			super.netrtcapi_pingtimeout_callback();
			Log.i(TAG, "rtcapi_pingtimeout_callback callback ");
			MLoginApi.isConnecting=false;
			MLoginApi.sendLoginStatusBroadcast(MLoginApi.STATUS_DISCONNECTED);
		}

		@Override
		public void netrtcapi_friendsync_callback(String chat, eShow show, String status) {

			super.netrtcapi_friendsync_callback(chat, show,status);

			ContactHelper.sendFriendStatusChangeBroadcast(chat, status);

			Log.i(TAG, "~ friendSync callback : " + status + "  from : " + chat + " show : " + show.ordinal());
		}

	@Override
	public void netrtcapi_VCardRecv_callback(boolean result, String name, c_VCard vcard) {
		Log.i(TAG, "vCardRecv callback result : " + result + " name : " + name);
        if(result){

			ContactHelper.sendFriendInfoChangeBroadcast(ContactHelper.convertVcard2Contact(name,vcard));
		}
		super.netrtcapi_VCardRecv_callback(result, name, vcard);
	}

/*	PARAM_GET_FILE_URL_FILE_URL_VALUE_FILE_TOO_LARGE
	PARAM_GET_FILE_URL_FILE_URL_VALUE_RESOURCE_CONSTRAINT
	PARAM_GET_FILE_URL_FILE_URL_VALUE_NOT_ALLOWED*/




	@Override
	public void netrtcapi_FileTransfer_callback(eFileTransferCase event, c_Message msg) {

		super.netrtcapi_FileTransfer_callback(event, msg);

		Log.i(TAG,"FileTransfer_callback : "+event +" conversationId : "+msg.getConversationID()+" msgId : "+msg.getMessageID());

		switch (event){

			case  FILE_GET_URL_SUCCEED:
			//get file upload address successful from the server

					MessagingApi.FileUrlCallback(MessageConvertImp.convert2Message(msg));


				break;
			case  FILE_ERROR_TOO_LARGE: //get file upload address failure because the the file is out of size

				break;
			case  FILE_ERROR_RESOURCE_CONSTRAINT://file type is not validate
				imapij.netimSendFileMsg(msg.getConversationID(), msg.getMessageID(), false);
				break;
			case  FILE_ERROR_NOT_ALLOWED: // file not allowed
				imapij.netimSendFileMsg(msg.getConversationID(), msg.getMessageID(), false);
				break;
			case FILE_WAIT_UPLOAD: //file is already in uploading
				if (msg!=null&&msg.getFileURL()!=null) {
					MessagingApi.FileUrlCallback(MessageConvertImp.convert2Message(msg));
				}else{
					Log.i(TAG,"FILE_WAIT_UPLOAD msg url not exsit : "+event +" conversationId : "+msg.getConversationID()+" msgId : "+msg.getMessageID());

				}
				break;
			case FILE_UPLOAD_SUCCEED:
			//file already in the server you can send it now
				imapij.netimSendFileMsg(msg.getConversationID(), msg.getMessageID(), true);

				break;
			case FILE_GET_URL_TIMEOUT:

				imapij.netimSendFileMsg(msg.getConversationID(), msg.getMessageID(), false);
				break;

		}
	}

}
