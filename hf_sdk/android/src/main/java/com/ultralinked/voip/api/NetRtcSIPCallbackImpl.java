package com.ultralinked.voip.api;

import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;

import com.ultralinked.voip.rtcapi.NetrtcCallback;
import com.ultralinked.voip.rtcapi.eACC_STATUS;
import com.ultralinked.voip.rtcapi.eCALL_STATUS;
import com.ultralinked.voip.rtcapi.rtcapij;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class NetRtcSIPCallbackImpl extends NetrtcCallback {
    public static String TAG = "NetRtcSIPCallbackImpl";
    private static final int ON_CALL_STATE = 2;
    private Handler msgHandler;
    private CallSession session;
    private Timer timerUpdateNetworkStatus = new Timer();
    public String callFrom = "";
    public String sipCallId = "";
    private TimerTask updateTimerValuesTask;
    protected static boolean mutex = false;
    //private int audioChannel=-1;
    private Lock lock;
    private boolean canRecall;
    //rtcapij.grrtc_call_getsipcallid(call_id)

    protected static SparseArray<Boolean> CallIds = new SparseArray<Boolean>(5);

    public static boolean callReleased;

    String releaseReason = null;
    private long lastTerminateTime;

    @Override
    public void netrtcapi_call_callback(final long callid, eCALL_STATUS nState, final long call_datas) {


        Log.i(TAG, "rtcapi call callback  CallId=" + callid + " status=" + nState);

        if (nState == eCALL_STATUS.CALL_STATUS_CALL_RELEASED) {
            releaseReason = rtcapij.netrtc_hashmap_getstr(call_datas, "releaseReason");
            Log.i(TAG, "rtcapi call callback releaseReason, CallId=" + callid + " releaseReason=" + releaseReason);

        }

        String callFrom = rtcapij.netrtc_hashmap_getstr(call_datas, "FromUserName");

        if (TextUtils.isEmpty(callFrom)) {
            callFrom = rtcapij.netrtc_hashmap_getstr(call_datas, "FromUserId");
        }

        String callFromUserDisplayName = rtcapij.netrtc_hashmap_getstr(call_datas, "FromUserDisplayName");


        String sipcallid = rtcapij.netrtc_hashmap_getstr(call_datas, "sipCallId");

        if (CallApi.getCurrentConnectMode().equals("TNUNEL")) {

            sipcallid = rtcapij.netrtc_call_getsipcallid(callid);
        }


        if (checkisNeedToEndCall(nState, callid, callFrom) && !callReleased) {// &&
            if (nState == eCALL_STATUS.CALL_STATUS_CALL_RELEASED) {
                Log.i(TAG, "end success another call");
            }
            if (nState == eCALL_STATUS.CALL_STATUS_INCOMING_CALL) {
                //end the call
                CallApi.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        rtcapij.netrtc_call_reject(callid);
                    }
                });
            }

            return;
        }

        if (session != null) {
            session = null;
        }

        session = new CallSession();

        session.callId = callid;

        session.originStatus = nState;


        if (!TextUtils.isEmpty(sipcallid)) {

            session.sipCallid = sipcallid;

            this.sipCallId = sipcallid;

        }

        if (!TextUtils.isEmpty(callFrom)) {

            Log.i(TAG, "callFrom : " + callFrom);

            session.callFrom = callFrom;

            this.callFrom = callFrom;

        }

        if (!TextUtils.isEmpty(callFromUserDisplayName)) {

            Log.i(TAG, "callFromUserDisplayName : " + callFromUserDisplayName);

            session.callFromUserDisplayName = callFromUserDisplayName;

        }


        String sendCodec = rtcapij.netrtc_hashmap_getstr(call_datas, "sendCodec");

        if (sendCodec != null) session.sendCodec = sendCodec;


        String answerSDP = rtcapij.netrtc_hashmap_getstr(call_datas, "AnsweredSdp");

        String incomingSDP = rtcapij.netrtc_hashmap_getstr(call_datas, "InComingSdp");

        String ringingSDP = rtcapij.netrtc_hashmap_getstr(call_datas, "RingingSdp");

        if (!TextUtils.isEmpty(answerSDP)) {


            session.sdpInformation = answerSDP;

        }

        if (!TextUtils.isEmpty(incomingSDP)) {

            session.sdpInformation = incomingSDP;

        }

        if (!TextUtils.isEmpty(ringingSDP)) {
            session.sdpInformation = ringingSDP;
        }

        CallApi.executor.execute(new Runnable() {
            @Override
            public void run() {
                if (session != null) {
                    callbackProcess(session, call_datas);
                } else {
                    Log.i(TAG, "session is null.......");
                }
            }
        });

    }

    @Override
    public void netrtcapi_acc_callback(String acc_name, eACC_STATUS nState, long acc_datas) {

        Log.i(TAG, "netrtcapi_acc_callback:" + nState.toString());
        LoginApi.isDiconnecting = false;
        int sipLoginStatus = LoginApi.STATUS_REGISTER_ACCOUNT_FAILURE;
        switch (nState) {

            case ACC_STATUS_REG_OK:
            /*	CallApi.networkChange=false;*/
                Log.i(TAG, "acc reg ok");
                sipLoginStatus = LoginApi.STATUS_REGISTER_OK;

                break;
            case ACC_STATUS_REG_TIMEOUT:
                sipLoginStatus = LoginApi.STATUS_REGISTER_TIME_OUT;
                LoginApi.isDiconnecting = true;

                Log.i(TAG, "acc reg timeout");

                break;
            case ACC_STATUS_CONNECT_ERROR:
                sipLoginStatus = LoginApi.STATUS_CONNECTING_ERROR;
                LoginApi.isDiconnecting = true;
                Log.i(TAG, "acc connect error");

		/*		  if(CallApi.getCurrentCallId()!=-1){

	                 Log.i(TAG, "hangUp call id is " + CallApi.getCurrentCallId());

			         CallApi.executor.execute(new Runnable() {

					      @Override
					         public void run() {

						      rtcapij.grrtc_call_hangup(CallApi.getCurrentCallId());

					   }
				     });

				  }*/

                break;

            case ACC_STATUS_REG_ACCOUNT_ERROR:
                LoginApi.isDiconnecting = true;
                sipLoginStatus = LoginApi.STATUS_REGISTER_ACCOUNT_ERROR;
                Log.i(TAG, "acc reg account error");
                break;
            case ACC_STATUS_LOGOUT:
                sipLoginStatus = LoginApi.STATUS_SERVER_FORCE_LOGOUT;
                LoginApi.isDiconnecting = true;

                // rtcapij.grrtc_acc_del(CallApi.configName);

                Log.i(TAG, "acc logout");
                break;
            case ACC_STATUS_REG_ACCOUNT_FAILURE:
                LoginApi.isDiconnecting = true;
                sipLoginStatus = LoginApi.STATUS_REGISTER_ACCOUNT_FAILURE;
                Log.i(TAG, "acc reg account failure");

                break;
            case ACC_STATUS_CONNECT_SUCCESS:
                sipLoginStatus = LoginApi.STATUS_REGISTER_OK;
                LoginApi.isDiconnecting = false;
                if (CallApi.networkChange && CallApi.getCurrentCallId() > -1) {

                    if (canRecall) {
                        if ((!CallApi.isICEEnalbe() || !CallSession.isICEEnable)) {
                            Log.i(TAG, "recall in non ice mode ");

                            rtcapij.netrtc_call_recall(CallApi.getCurrentCallId());
                        } else {
                            CallSession callSession = CallApi.getFgCallSession();
                            if (callSession != null && (callSession.callState != CallSession.STATUS_INCOMING
                                    && callSession.callState != CallSession.STATUS_WAIT_AUTO_ACCEPT_BY_SDP_CREATE_OFFER_COMPLETED
                            )) {
                                recallOfferInIce();
                                Log.i(TAG, "recall in ice mode :" + CallApi.isICEEnalbe() + "----:" + CallSession.isICEEnable);
                                CallApi.isRecall = true;
                            }
                        }


                    } else {

                        //when is outgoing call is calling . when send reinvite
//                        CallSession callSession = CallApi.getFgCallSession();
//                        if (callSession != null && (callSession.callState == CallSession.STATUS_PROCESSING ||
//                                callSession.callState == CallSession.STATUS_ALERTING
//                        )) { //test
//                            Log.i(TAG, "recall in ringing mode ");
//                            if ((!CallApi.isICEEnalbe() || !CallSession.isICEEnable)) {
//                                Log.i(TAG, "recall in non ice mode ");
//
//                                rtcapij.grrtc_call_recall(CallApi.getCurrentCallId());
//                            } else {
//                                recallOfferInIce();
//                                Log.i(TAG, "recall in ice mode :" + CallApi.isICEEnalbe() + "----:" + CallSession.isICEEnable);
//                            }
//                            CallApi.isRecall = true;
//                        }

                    }


                }

                Log.i(TAG, "acc connect success ");

                break;

            case ACC_STATUS_NOTIFY_MESSAGE:

                String notifyBody = rtcapij.netrtc_hashmap_getstr(acc_datas, "notifyBody");

                Log.i(TAG, "notifyBody : " + notifyBody);

                if (!TextUtils.isEmpty(notifyBody) && notifyBody.contains("<unregister")) {


                    //rtcapij.grrtc_acc_del(CallApi.configName);

                    LoginApi.sendLoginStatusBroadcast(LoginApi.STATUS_SERVER_FORCE_LOGOUT, notifyBody);
                    return;

                }

                return;
            case ACC_STATUS_MESSAGE_MESSAGE:


                String from = rtcapij.netrtc_hashmap_getstr(acc_datas, "FromUserName");

                String message = rtcapij.netrtc_hashmap_getstr(acc_datas, "message");

                CustomMessageApi.sendCustomMessageBroadcast(from, message);


                return;


            default:
                break;
        }

        LoginApi.sendLoginStatusBroadcast(sipLoginStatus);

    }

    private void recallOfferInIce() {

        CallApi.reCreateOffer();

        CallSession callSession = CallApi.getFgCallSession();
        if (callSession != null && callSession.callId > -1) {
            callSession.callState = CallSession.STATUS_RECONNECTING;
            CallApi.sendCallStatusBroadcast(callSession);
        } else {
            Log.i(TAG, "recallOfferInIce  call id is -1 or null");
        }

    }

    private void recallAnswerInIce(String sdpInfo) {

        CallApi.reCreateAnswer(sdpInfo);
        CallSession callSession = CallApi.getFgCallSession();
        if (callSession != null && callSession.callId > -1) {
            callSession.callState = CallSession.STATUS_RECONNECTING;
            CallApi.sendCallStatusBroadcast(callSession);
        } else {
            Log.i(TAG, "recallAnswerInIce  call id is -1 or null");
        }


    }


    private void startTimer(final CallSession callSession) {


        updateTimerValuesTask = new TimerTask() {

            @Override
            public void run() {

                if (callSession == null) {
                    return;
                }


                Log.i(TAG, "~ netrtc call quality ~");

                int status = rtcapij.netrtc_call_quality(callSession.callId);

                if (!CallApi.isICEEnalbe() || !CallSession.isICEEnable) {
                    Log.i(TAG, "~ netrtc call quality ~CallSession.isICEEnable ===" + CallSession.isICEEnable);
                    CallApi.sendCallQosBroadcast(CallApi.NON_ICE_TAG, null, status);

                }


            }
        };


        timerUpdateNetworkStatus.schedule(updateTimerValuesTask, 1000, 5000);
    }


    boolean is_non_ice_answer = false;

    private void cancelTimer() {

        if (updateTimerValuesTask != null) {

            Log.i(TAG, "~ cancel call quality timetask ~");

            updateTimerValuesTask.cancel();

        }

        if (timerUpdateNetworkStatus != null) {

            Log.i(TAG, "~ cancel call quality timer ~");

            timerUpdateNetworkStatus.purge();

        }

    }

    protected synchronized void callbackProcess(CallSession callSession, long acc_datas) {

        lock = new ReentrantLock();

        lock.lock();

        switch (callSession.originStatus) {

            case CALL_STATUS_INCOMING_CALL:

                if (System.currentTimeMillis() - lastTerminateTime < 1000){
                    rtcapij.netrtc_call_reject(callSession.callId);
                    Log.i(TAG, "the end call time is too short, just reject the call.");
                    return;
                }

                callReleased = false;

                Log.i(TAG, "incomingSDP : " + callSession.sdpInformation);
                CallApi.muteMedia(true,"incoming a voip call");
                // incoming video call

                if (!TextUtils.isEmpty(callSession.sdpInformation) && callSession.sdpInformation.contains(CallApi.VIDEO_TAG)) {

                    boolean isLogin = LoginApi.isLogin();

                    Log.i(TAG, "login status : " + isLogin + " disconnecting : " + LoginApi.isDiconnecting);
//                    /**
//                     * here is avoid incoming call when is registering sip
//                     */
//                    if (!isLogin || LoginApi.isDiconnecting) {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if (!isLogin || LoginApi.isDiconnecting) {
//                        //
//
//                        rtcapij.grrtc_call_reject(callSession.callId);
//
//                        Log.w(TAG, "user not login  reject the new call id : " + callSession.callId);
//                        lock.unlock();
//                        return;
//                    }

                    if (CallApi.getCurrentCallId() != -1) {

                        Log.w(TAG, "already incall reject the new call id : " + callSession.callId);

                        rtcapij.netrtc_call_reject(callSession.callId);
                        lock.unlock();
                        return;
                    }

                    CallApi.setCurrentCallId(callSession.callId);

                    CallIds.put((int) callSession.callId, true);

                    Log.i(TAG, "incoming video call from " + callSession.callFrom + " sdp : " + callSession.sdpInformation);

                    callSession.type = CallSession.TYPE_VIDEO;

                    callSession.callState = CallSession.STATUS_INCOMING;

                    if (!TextUtils.isEmpty(session.sdpInformation)) {

                        CallSession.isICEEnable = true;

                    }
                    callSession.isIncomingCall = true;
                    CallApi.addOrUpdateCallSession(callSession);

                    MediaManger.getInstance(CallApi.getContext()).playAlarmRing();

                    CallApi.sendCallInviteBroadcast(CallApi.getFgCallSession());


                } else {

                    // incoming audio call

                    boolean isLogin = LoginApi.isLogin();

                    Log.i(TAG, "login status : " + isLogin + " disconnecting : " + LoginApi.isDiconnecting);

                    if (!isLogin || LoginApi.isDiconnecting) {

                        Log.w(TAG, "user not login  reject the new call id : " + callSession.callId);

                        rtcapij.netrtc_call_reject(callSession.callId);
                        lock.unlock();
                        return;
                    }
                    if (CallApi.getCurrentCallId() != -1) {

                        rtcapij.netrtc_call_reject(callSession.callId);

                        Log.w(TAG, "already incall reject the new call id : " + CallApi.getCurrentCallId());
                        lock.unlock();
                        return;
                    }

                    CallIds.put((int) callSession.callId, true);

                    Log.i(TAG, "incoming audio call from " + callSession.callFrom);

                    callSession.type = CallSession.TYPE_AUDIO;

                    callSession.callState = CallSession.STATUS_INCOMING;

                    CallApi.addOrUpdateCallSession(callSession);

                    if (!TextUtils.isEmpty(session.sdpInformation)) {

                        CallSession.isICEEnable = true;


                    } else {

                        CallSession.isICEEnable = false;
                    }

                    Log.i(TAG, "ICE Enable :" + CallSession.isICEEnable + " button control :" + CallApi.isICEEnalbe());

                    if (CallApi.isICEEnalbe()) {

                        if (!CallSession.isICEEnable) {

                            //incoming audio call in non ice mode

                            CallApi.sendCallInviteBroadcast(CallApi.getFgCallSession());

                            MediaManger.getInstance(CallApi.getContext()).playAlarmRing();


                        } else {
                            // incoming audio call in ice mode


                            CallApi.peerConnectionClient = CallApi.PeerConnectionFactoryInit(false, false, new PeerConnectionClient.PeerConnectionEvents() {
                                @Override
                                public void onLocalDescription(SessionDescription sdp) {

                                }

                                @Override
                                public void onIceCandidate(IceCandidate candidate) {

                                }

                                @Override
                                public void onIceConnected() {

                                }

                                @Override
                                public void onIceDisconnected() {

                                }

                                @Override
                                public void onPeerConnectionClosed() {

                                }

                                @Override
                                public void onPeerConnectionStatsReady(StatsReport[] reports) {

                                }

                                @Override
                                public void onPeerConnectionError(String description) {
                                    Log.i(TAG, "incoming audio onPeerConnectionError=" + description);
                                    if (session != null && CallSession.isICEEnable) {
                                        session.terminate("incoming audio onPeerConnectionError");
                                    } else {
                                        if (CallApi.peerConnectionClient != null && CallSession.isICEEnable) {
                                            CallApi.peerConnectionClient.close();
                                            CallApi.peerConnectionClient = null;
                                        }
                                    }
                                }
                            });

                            //do earlier parse first.
                            CallApi.parseSDPInformation(CallApi.peerConnectionClient, callSession.sdpInformation, SessionDescription.Type.OFFER);

                            MediaManger.getInstance(CallApi.getContext()).playAlarmRing();
                            CallApi.sendCallInviteBroadcast(CallApi.getFgCallSession());
                        }

                    } else {

					/*		if (CallApi.peerConnectionClient == null) {

								CallApi.peerConnectionClient = PeerConnectionClient.getInstance();

								CallApi.peerConnectionClient.createPeerConnectionFactory(CallApi.getContext(), null, false);

								CallApi.peerConnectionClient.createPeerConnection(null, null);
							}
							*/
                        MediaManger.getInstance(CallApi.getContext()).playAlarmRing();

                        CallApi.sendCallInviteBroadcast(CallApi.getFgCallSession());

                    }

                }

                Log.i(TAG, "incoming call from " + callSession.callFrom);


                break;

            case CALL_STATUS_CALL_REINVITE:

                Log.i(TAG, "call reinvite");
                if ((!CallApi.isICEEnalbe() || !CallSession.isICEEnable)) {
                    Log.i(TAG, "recall in non ice mode ");
                } else {
                    Log.i(TAG, "recall in ice mode ");
                    recallAnswerInIce(callSession.sdpInformation);
                }

                break;

            case CALL_STATUS_PROCEEDING:

                if (CallApi.isRecall) {

                    Log.i(TAG, "CALL_STATUS_PROCEEDING recall return");
                    lock.unlock();
                    return;
                }

                CallIds.put((int) callSession.callId, true);

                callSession.callState = CallSession.STATUS_PROCESSING;

                CallApi.sendCallStatusBroadcast(callSession);

                Log.i(TAG, "call proceeding");
                break;

            case CALL_STATUS_CALL_CLOSED:

                Log.i(TAG, "call closed");
                break;
            case CALL_STATUS_RINGING:

                if (CallApi.isRecall) {
                    Log.i(TAG, "CALL_STATUS_RINGING recall return");
                    lock.unlock();
                    return;
                }

                Log.i(TAG, "call ring");

//                CallSession currentCallSession = CallApi.getFgCallSession();
//                if (currentCallSession!=null &&(
//                        currentCallSession.callState == CallSession.STATUS_CONNECTED
//                        || currentCallSession.callState == CallSession.SATTUS_ICE_CONNECTED
//                        )){
//                    Log.i(TAG, "call ring already connected, just return.");
//                    return;
//                }

                callSession.callState = CallSession.STATUS_ALERTING;
                CallSession initCallSession = CallApi.getFgCallSession();
                if (!callSession.sendCodec.equals("")) {

                    if (!CallApi.isICEEnalbe() || !CallSession.isICEEnable) {

                        rtcapij.netrtc_call_startaudio(callSession.callId);

                        Log.i(TAG, "call ringing start audio call id : " + (int) callSession.callId);

                    }

                    if (initCallSession != null && initCallSession.type == CallSession.TYPE_AUDIO) {
                        MediaManger.getInstance(CallApi.getContext()).stopLocalEarlyMedia(false);
                    }

                } else {
                    if (initCallSession != null && initCallSession.type == CallSession.TYPE_AUDIO) {

                        MediaManger.getInstance(CallApi.getContext()).playLocalEarlyMedia();
                    }


                }

                CallApi.sendCallStatusBroadcast(callSession);

                Log.i(TAG, "call ringing:" + callSession.sendCodec);

                //outoging call answer done by peer ringing has
                if (!TextUtils.isEmpty(session.sdpInformation)) {

                    Log.i(TAG, "ringing callee sdp: " + session.sdpInformation);

                    CallSession.isICEEnable = true;

                    if (session.sdpInformation.contains(CallApi.VIDEO_TAG)) {

                        callSession.type = CallSession.TYPE_VIDEO;

                    } else {

                        callSession.type = CallSession.TYPE_AUDIO;

                    }
                    CallApi.parseSDPInformation(CallApi.peerConnectionClient, callSession.sdpInformation, SessionDescription.Type.ANSWER);

                }

                break;

            case CALL_STATUS_OUTGOING_CALL_ANSWERED:


                canRecall = true;


                if (CallApi.isRecall) {
                    Log.i(TAG, "_OUTGOING_CALL_ANSWERED recall return");
                    if (CallApi.isICEEnalbe() && CallSession.isICEEnable) {
                        Log.i(TAG, "ice check _OUTGOING_CALL_ANSWERED recall ,parse sdp info.");
                        CallApi.parseSDPInformation(CallApi.peerConnectionClient, callSession.sdpInformation, SessionDescription.Type.ANSWER);

                    }
                    lock.unlock();
                    return;
                }

                MediaManger.getInstance(CallApi.getContext()).stopLocalEarlyMedia(false);

                CallApi.muteMedia(false,"call answered by caller");

                Log.i(TAG, "call answered");

                //outoging call answer done by peer 200OK

                if (!TextUtils.isEmpty(session.sdpInformation)) {

                    Log.i(TAG, "callee sdp: " + session.sdpInformation);

                    CallSession.isICEEnable = true;

                    if (session.sdpInformation.contains(CallApi.VIDEO_TAG)) {

                        callSession.type = CallSession.TYPE_VIDEO;

                    } else {

                        callSession.type = CallSession.TYPE_AUDIO;

                    }


                } else {//sdp info not found , mark as non ice
                    //
                    if (callSession.type == CallSession.TYPE_VIDEO) {
                        Log.i(TAG, "there is non-ice can not allow to call");
                        callSession.terminate("there is non-ice can not allow to call");
                    }
                    CallSession.isICEEnable = false;

                }

                if (!CallApi.isICEEnalbe() || !CallSession.isICEEnable) {

                    if (!CallApi.isVideoICEEnable()) {//not video call
                        //if is ice call , need to stop ice ...
                        rtcapij.netrtc_call_startaudio(callSession.callId);

                        Log.i(TAG, "outgoing call answer start audio call id : " + (int) callSession.callId);

                        callSession.type = CallSession.TYPE_AUDIO;
                    }

                }

                callSession.callState = CallSession.STATUS_CONNECTED;

                startTimer(callSession);

                boolean parseSDP = true;
                if (CallApi.isICEEnalbe() && CallSession.isICEEnable) {

                    CallSession currentCallSession = CallApi.getFgCallSession();
                    if (currentCallSession != null) {
                        if (currentCallSession.callState == CallSession.SATTUS_ICE_CONNECTED) {
                            parseSDP = false;
                            Log.i(TAG, "call answer already media ice connected, just return.");

                        } else if (currentCallSession.callState == CallSession.STATUS_ALERTING && !TextUtils.isEmpty(currentCallSession.sdpInformation)) {
                            Log.i(TAG, "call ring already start parse sdp, just return.");
                            parseSDP = false;
                        }
                    }

                    if (parseSDP) {
                        CallApi.parseSDPInformation(CallApi.peerConnectionClient, callSession.sdpInformation, SessionDescription.Type.ANSWER);

                    }

                }



                CallApi.sendCallStatusBroadcast(callSession);


                break;


            case CALL_STATUS_CALL_ACK:
                //incoming accept done
                //because sdk return two ack so ui limit it
                if (mutex) {
                    lock.unlock();
                    return;
                }

                canRecall = true;

                if (CallApi.isRecall) {
                    Log.i(TAG, "ACK recall return");

                    if (CallApi.isICEEnalbe() && CallSession.isICEEnable) {
                        Log.i(TAG, "ice check callACK recall ,parse sdp info.");
                        CallSession currentCallSession = CallApi.getFgCallSession();
                        if (currentCallSession != null) {
                            currentCallSession.callState = CallSession.SATTUS_ICE_CONNECTED;
                            CallApi.sendCallStatusBroadcast(currentCallSession);
                        }

                    }
                    lock.unlock();
                    return;
                }

                Log.i(TAG, "call ack start ,callid:" + (int) callSession.callId);

                MediaManger.getInstance(CallApi.getContext()).stopAlarmRing(false);

                if (!CallApi.isICEEnalbe() || !CallSession.isICEEnable) {

                    rtcapij.netrtc_call_startaudio(callSession.callId);

                    Log.i(TAG, "call ack start audio call id : " + (int) callSession.callId);

                }
                int callType = CallSession.TYPE_AUDIO;
                if (CallApi.getFgCallSession() != null && callSession.callId == CallApi.getFgCallSession().callId) {
                    callType = CallApi.getFgCallSession().type;
                    Log.i(TAG, "getFgCallSession : " + (int) callSession.callId + ";callType:" + callType);

                } else {
                    Log.i(TAG, "not match current exsit getFgCallSession call, the callsession : " + (int) callSession.callId + "; FgCall is null?" + (CallApi.getFgCallSession() == null));

                }

                if (callType == CallSession.TYPE_AUDIO) {

                    CallApi.initiateVoiceAudio();

                    Log.i(TAG, "audio ack");

                    startTimer(callSession);

                    callSession.callState = CallSession.STATUS_CONNECTED;
                    callSession.callFrom = callFrom;
                    callSession.sipCallid = sipCallId;
                    callSession.type = callType;
                    CallApi.sendCallStatusBroadcast(callSession);

                } else {

                    CallApi.initiateVideoAudio();
                }


                mutex = true;
                Log.i(TAG, "call ack end");
                break;

            case CALL_STATUS_CALL_REQUESTFAILURE:

                callSession.failureReason = CallSession.STATUS_CALL_REQUEST_FAILURE;

                Log.i(TAG, "call requestfailure");
                break;

            case CALL_STATUS_CALL_NOANSWER:

                callSession.failureReason = CallSession.STATUS_CALL_NO_ANSWER;

                Log.i(TAG, "call noanswer");

                break;

            case CALL_STATUS_CALL_REDIRECTED:

                Log.i(TAG, "call redirected");

                break;

            case CALL_STATUS_CALL_CANCELLED:

                callSession.failureReason = CallSession.STATUS_CALL_CANCELED;

                Log.i(TAG, "call cancelled");

                break;

            case CALL_STATUS_CALL_TIMEOUT:

                callSession.failureReason = CallSession.STATUS_CALL_TIMEOUT;

                Log.i(TAG, "call timeout");

                break;

            case CALL_STATUS_CALL_SERVERFAILURE:

                callSession.failureReason = CallSession.STATUS_CALL_SERVER_FAILURE;

                Log.i(TAG, "call serverfailuer");

                break;

            case CALL_STATUS_CALL_MESSAGE_REQUESTFAILURE:

                callSession.failureReason = CallSession.STATUS_CALL_MESSAGE_REQUEST_FAILURE;

                Log.i(TAG, "call message requestfailure");

                break;
            case CALL_STATUS_CALL_AUDIO_CLOSE:
                Log.i(TAG, "call message CALL_STATUS_CALL_AUDIO_CLOSE");
                is_non_ice_answer = false;
                CallApi.reset();
                break;
            case CALL_STATUS_CALL_RELEASED:
                lastTerminateTime = System.currentTimeMillis();
                CallApi.muteMedia(false,"CALL_STATUS_CALL_RELEASED call", AudioManager.MODE_NORMAL);
                callReleased = true;
                for (int i = 0; i < CallIds.size(); i++) {
                    int key = CallIds.keyAt(i);
                    // get the object by the key.
                    boolean value = CallIds.get(key);

                    Log.i(TAG, "call id : " + key + " value : " + value);
                }
                if (CallIds.get((int) callSession.callId) != null && CallIds.get((int) callSession.callId)) {

                    CallIds.delete((int) callSession.callId);

                    canRecall = false;


                } else {
//						if(CallApi.getCurrentCallId()>-1) {
//							hasAnotherCall = false;
//							Log.w(TAG, "don't release the already call");
//							lock.unlock();
//							return;
//
//						}
                }


                Log.i(TAG, "releaseReason : " + releaseReason);


                if (!CallApi.isICEEnalbe() || !CallSession.isICEEnable) {

                    if ((int) callSession.callId != -1) {

                        Log.i(TAG, "stop audio call id : " + (int) callSession.callId);

                        rtcapij.netrtc_call_stopaudio((int) callSession.callId);

                        is_non_ice_answer = true;
                    }

                }

                MediaManger.getInstance(CallApi.getContext()).stopAlarmRing();

                MediaManger.getInstance(CallApi.getContext()).stopLocalEarlyMedia();

                if (!is_non_ice_answer) {
                    CallApi.reset();
                } else {
                    CallApi.setCurrentCallId(-1);
                }

                cancelTimer();

                callFrom = "";

                sipCallId = "";

                callSession.callState = CallSession.STATUS_IDLE;

                callSession.callId = -1;

                CallApi.sendCallStatusBroadcast(callSession, releaseReason);

                Log.i(TAG, "call released");

                mutex = false;

                break;

            case CALL_STATUS_GLOBAL_FAILURE:

                Log.i(TAG, "call global failure");

                break;

            default:
                break;

        }

        lock.unlock();
    }


    private boolean checkisNeedToEndCall(eCALL_STATUS call_status, long callId, String callName) {

        CallSession currentCallSession = CallApi.getFgCallSession();

        if (currentCallSession != null && currentCallSession.callId != -1) {
            Log.i(TAG, "call status:" + currentCallSession.callState);
            //test reincoming flow
            if (call_status == eCALL_STATUS.CALL_STATUS_INCOMING_CALL && callName != null && callName.equals(currentCallSession.callFrom)) {
                return false;
            }

            if (callId != currentCallSession.callId) {
                if (currentCallSession.callState == CallSession.STATUS_CONNECTED ||
                        currentCallSession.callState == CallSession.SATTUS_ICE_CONNECTED ||
                        currentCallSession.callState == CallSession.STATUS_WAIT_AUTO_ACCEPT_BY_SDP_CREATE_OFFER_COMPLETED ||
                        currentCallSession.callState == CallSession.STATUS_INCOMING ||
                        currentCallSession.callState == CallSession.STATUS_ALERTING ||
                        currentCallSession.callState == CallSession.STATUS_PROCESSING
                        ) {
                    Log.i(TAG, "currentCallSession.callId:" + currentCallSession.callId + ";currentCallSession.callFrom:" + currentCallSession.callFrom + ":from another call not the same with current user,the name is:" + callName + ";and callid:" + callId);
                    return true;
                }
            }

        }


        return false;
    }



}
