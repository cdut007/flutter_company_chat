package com.ultralinked.voip.api;

import android.media.AudioManager;
import android.text.TextUtils;

import com.ultralinked.voip.rtcapi.eCALL_STATUS;
import com.ultralinked.voip.rtcapi.rtcapij;

import java.io.Serializable;

/**
 * call  session relative the call
 *
 */
public final class CallSession implements Serializable {

	public static final String TAG = "CallSession";
	/**
	 * call record fail call by audio forcus  cannot requst
	 */
	public static final int STATUS_RECORD_FAIL = -1;
	/**
	 * idle status when call is terminated
	 */
	public static final int STATUS_IDLE = 0;

	/**
	 * incoming call status when a new is incoming
	 */
	protected static final int STATUS_INCOMING = 3;

	/**
	 * altering status when callee is ringing
	 */
	public static final int STATUS_ALERTING = 4;

	/**
	 * connected status when call is connected
	 */
	public static final int STATUS_CONNECTED = 2;

	/**
	 * ice connected status when ice is connected when ice function is open
	 */
	public static final int SATTUS_ICE_CONNECTED = 10;

	/**
	 * processing status  when call is process by server
	 */
	public static final int STATUS_PROCESSING = 1;

	/**
	 * processing status  when call is accept by user
	 */
	protected static final int STATUS_WAIT_AUTO_ACCEPT_BY_SDP_CREATE_OFFER_COMPLETED = 77;



	/**
	 * reconnecting status  when call is disconnect by network status has changed.
	 */
	public static final int STATUS_RECONNECTING = 7;


	/**
	 * audio call type
	 */
	public static final int TYPE_AUDIO = 0;

	/**
	 * video call type
	 */
	public static final int TYPE_VIDEO = 1;
	protected static boolean isICEEnable;

	public long callId = -1;

	public String sipCallid="";
	/**
	 * current call state
	 */
	public int callState;
	/**
	 * when incoming  call get caller name
	 */

	public String callFrom = "";


	/**
	 * when incoming  call get caller DisplayName
	 */
	public String callFromUserDisplayName = "";


	protected String sendCodec = "";

	protected String sdpInformation;

	protected  boolean earlyStartParseSDP;
	/**
	 * the current call type audio or video
	 */
	public int type;

	protected int call_quality;
	protected eCALL_STATUS originStatus;
	protected int failureReason;
	public static final int STATUS_CALL_REQUEST_FAILURE = 19;
	public static final int STATUS_CALL_NO_ANSWER = 20;
	public static final int STATUS_CALL_CANCELED = 21;
	public static final int STATUS_CALL_TIMEOUT = 22;
	public static final int STATUS_CALL_SERVER_FAILURE = 23;
	public static final int STATUS_CALL_MESSAGE_REQUEST_FAILURE = 24;




	protected boolean networkHasChanged;

	public CallSession() {

	}



	public  boolean isIncomingCall;

	public  boolean isAccepted;

	public void accept() {
		accept(false);
		CallApi.muteMedia(false,"accept a call");
	}

	/**
	 * accept the incoming call
	 */
	public void accept(boolean recall) {
		isAccepted = true;
		if(CallApi.isICEEnalbe()&& CallSession.isICEEnable){

		      if(type==TYPE_AUDIO){
                  Log.i(TAG, "audio accept in ice mode");
                  // CallApi.setCurrentSDP(null);  //for test.
					if (TextUtils.isEmpty(CallApi.getCurrentSDP())){
						//maybe need wait
						callState = STATUS_WAIT_AUTO_ACCEPT_BY_SDP_CREATE_OFFER_COMPLETED;
						CallApi.addOrUpdateCallSession(this);
						Log.i(TAG, "audio accept in ice mode,maybe need wait the ice collected complete");
//						CallSession.isICEEnable = false;
//                        CallApi.closeInNonIce(new PeerConnectionClient.PeerConnectionClosedEvent() {
//                            @Override
//                            public void onPeerConnectionClosed() {
//                                Log.i(TAG, " onPeerConnectionClosed  : "+ CallApi.getCurrentCallId());
//
//                                Log.i(TAG, "audio accept in ice mode, but the getCurrentSDP is null,accept by non ice");
//                                rtcapij.grrtc_call_accept(CallApi.getCurrentCallId());
//                            }
//                        });



					}else{
						if (!recall && networkHasChanged){
							networkHasChanged= false;
							Log.i(TAG, "audio accept in ice mode,but the network has Changed, we need recreate SDP info.\n"+sdpInformation);
							//do parse again.
							callState = STATUS_WAIT_AUTO_ACCEPT_BY_SDP_CREATE_OFFER_COMPLETED;

							Log.i(TAG, "incoming call create Answer again");

							//CallApi.peerConnectionClient.createAnswer();
							CallApi.reCreateAnswer(sdpInformation);

							return;
						}

						rtcapij.netrtc_call_accept_sdp(CallApi.getCurrentCallId(), CallApi.getCurrentSDP().trim());

					}

				  if (!recall){
					  MediaManger.getInstance(CallApi.getContext()).stopAlarmRing(false);
				  }

		      }else {

				    Log.i(TAG, "Video call incoming accept as audio ice mode,not use video ,may not accept sccuess") ;

		      }


		}else{

			 Log.i(TAG, "accept in non ice mode");


			 type=TYPE_AUDIO;

			 rtcapij.netrtc_call_accept(CallApi.getCurrentCallId());

			MediaManger.getInstance(CallApi.getContext()).stopAlarmRing(false);
		}

	}


	public void terminate() {
		 terminate(null);
	}

	/**
	 * terminate the connected call or reject the incomng call
	 */
	public void terminate(final String terminateReason) {
		PeerConnectionClient.closeCallId();
      NetRtcSIPCallbackImpl.callReleased = true;
		if (!TextUtils.isEmpty(terminateReason)){
			Log.i(TAG, "terminate call ,reason:"+terminateReason);
		}else {
			Log.i(TAG, "terminate call by ui");
		}


		 if(CallApi.peerConnectionClient!=null){

		   CallApi.peerConnectionClient.isEnd=true;
			 MediaManger.getInstance(CallApi.getContext()).stopLocalEarlyMedia();

		  }

		CallApi.muteMedia(false,"terminate a call", AudioManager.MODE_NORMAL);



		 if(CallApi.getCurrentCallId()==-1 || callId == -1){

			 Log.w(TAG, "current call id is -1");


			 callState=STATUS_IDLE;

			 CallApi.reset();
			 CallApi.sendCallStatusBroadcast(this);

			 return;

		 }




		     CallApi.executor.execute(new Runnable() {
					@Override
					public void run() {
                        long currentCallId = CallApi.getCurrentCallId();

                        Log.i(TAG, "executor user hangUp CallId  is :" + currentCallId);
						int resultCode;
						if (!TextUtils.isEmpty(terminateReason)){
							resultCode = rtcapij.netrtc_call_hangup_withcode(currentCallId,704);
						}else{
							resultCode = rtcapij.netrtc_call_hangup(currentCallId);
						}

                        Log.i(TAG, "user hangUp call id is " + CallApi.getCurrentCallId());
                        if(!CallApi.isICEEnalbe()||!CallSession.isICEEnable){

                            if(CallApi.getCurrentCallId()!=-1){

                                Log.i(TAG, "stop audio call id : " + (int) CallApi.getCurrentCallId());

                                rtcapij.netrtc_call_stopaudio((int) CallApi.getCurrentCallId());
                            }

                        }

                        CallApi.setCurrentCallId(-1);
                        Log.i(TAG, "user hangUp resultCode  is " + resultCode);

						if (resultCode<0){
							callState=STATUS_IDLE;
							CallApi.setCurrentCallId(-1);
							CallApi.reset();
							CallApi.sendCallStatusBroadcast(CallSession.this);
						}
					}
				});


	}
	/**
	 * send dtmf code to server when call is connected
	 */
	public void sendDTMF(char tag){

		rtcapij.netrtc_call_senddtmf(callId, tag, 1);

	}

	/**
	 * mute the current call
	 */

	public void mute() {

		Log.i(TAG, "mute call id is " + CallApi.getCurrentCallId());

		rtcapij.netrtc_call_mutemic(callId, 1);

	}

	/**
	 * unMute the current call ?
	 */
	public void unMute() {

		Log.i(TAG, "unMute call id is " + CallApi.getCurrentCallId());

		rtcapij.netrtc_call_mutemic(callId, 0);

	}

	/**
	 * hold on current call
	 */
	public void hold(){
		Log.i(TAG, "hold call id is " + CallApi.getCurrentCallId());
		rtcapij.netrtc_call_holdon(callId);

	}

	/**
	 * hold off current call
	 */
	public void unHold(){
		Log.i(TAG, "unHold call id is " + CallApi.getCurrentCallId());
		rtcapij.netrtc_call_holdoff(callId);

	 }
}
