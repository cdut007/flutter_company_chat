package com.ultralinked.voip.api;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ultralinked.voip.api.utils.CommonUtils;
import com.ultralinked.voip.api.utils.FileUtils;
import com.ultralinked.voip.rtcapi.eNETRTC_CONNECTION_MODE;
import com.ultralinked.voip.rtcapi.rtcapij;

import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * call relative api class
 */
public class CallApi {


    public static final String TAG = "CallApi";


    public static final int QOS_QUALITY_INVALID = -2;

    /**
     * Good network quality in call
     */
    public static final int QOS_QUALITY_GOOD = 4;

    /**
     * Average network quality in call
     */
    public static final int QOS_QUALITY_AVERAGE = 3;

    /**
     * Low network quality in call
     */
    public static final int QOS_QUALITY_LOW = 2;

    /**
     * Very low network quality in call
     */
    public static final int QOS_QUALITY_VERY_LOW = 1;

    /**
     * Worst network quality in call
     */
    public static final int QOS_QUALITY_WORST = 0;
    public static final String PARAM_CANDIDATE_PAIR = "candidatePair";
    public static final String PARAM_REPORT_TYPE = "report_type";

    public static EglBase rootEglBase;
    public static boolean disableLogToFile;


    /**
     * static method to load the share library
     */
    static {

        try {


            System.loadLibrary("ulrtcapi");

            //System.loadLibrary("jingle_peerconnection_so");

        } catch (UnsatisfiedLinkError e) {

            Log.i(TAG, "we have a problem to load Netrtc lib");

        } catch (Exception e) {
            e.printStackTrace();

            Log.i(TAG, "we have a problem to load Netrtc lib");
        }

    }


    /**
     * Call invite action
     */
    public static final String EVENT_CALL_INVITATION = "com.ultralinked.voip.callInviation";

    /**
     * Call status change action
     */
    public static final String EVENT_CALL_STATUS_CHANGE = "com.ultralinked.voip.callStatusChange";

    /**
     * Call quality change action
     */
    public static final String EVENT_CALL_QOS_REPORT = "com.ultralinked.voip.callQosStatusChange";


    /**
     * record status action
     */
    public static final String EVENT_CALL_RECORD_STATUS = "com.ultralinked.voip.record_status";

    /**
     * Call session key
     */
    public static final String PARAM_CALL_SESSION = "call_session";

    /**
     * Call end reason
     */
    public static final String PARAM_SIP_REASON_TEXT = "sip_reason_text";

    protected static final String ICE_CANDIDATE_TAG = "a=candidate:";


    public static final String NON_ICE_TAG = "non_ice";
    public final static String ICE_TAG = "a=ice-ufrag";

    protected final static String VIDEO_M_LINE = "m=video 0 UDP/TLS/RTP/SAVPF 0";

    protected final static String VIDEO_TAG = "m=video";
    /**
     * Call quality key
     */
    public final static String PARAM_CALL_QOS = "call_quality";

    protected static String configName = "uluc_prod";

    private static Context mContext;

    private static HashMap<Long, CallSession> callSessionsHash;

    private static long currentCallId = -1;

    private static String callDest;

    public static PeerConnectionClient peerConnectionClient;

    private static String currentSDP;

    private static AppRTCAudioManager appRTCAudioManager;

    private static NetRtcSIPCallbackImpl netRtcSipCallback;

    private static SharedPreferences preferences;

    protected static LooperExecutor executor;

    protected static boolean isRecall;

    protected static boolean networkChange;

    public static String logfilePath;

    public static String imLogfilePath;

    /**
     * Local video renderer in video call
     */
    public static SurfaceViewRenderer mLocalRender;

    /**
     * Remote video renderer in video call
     */
    public static SurfaceViewRenderer mRemoteRender;

    /**
     * Get current connected call id
     */
    public static long getCurrentCallId() {
        return currentCallId;
    }


    protected static String getCurrentSDP() {
        return currentSDP;
    }


    protected static void setCurrentSDP(String currentSDP) {
        CallApi.currentSDP = currentSDP;
    }


    protected static void setCurrentCallId(long currentCallId) {
        if (currentCallId == -1) {
            if (CallApi.currentCallId != -1) {
                removeCallSessionById(CallApi.currentCallId);
                Log.i(TAG, "remove the current call id : " + CallApi.currentCallId);
            }
        }
        CallApi.currentCallId = currentCallId;

        Log.i(TAG, "current call id : " + CallApi.currentCallId);


    }


    /**
     * init the Call Api class when application start
     *
     * @param context
     */
    public static void init(Application context) {

        Log.i(TAG, "~ init ~");
        if (!CommonUtils.isMainPid(context)) {

            return;
        }
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);//for init.
        mContext = context;


        String appName = ConfigApi.appName;

        logfilePath = FileUtils.getSDPath() + File.separator + appName + File.separator + "Logs";
        Log.setLogPath(logfilePath);

        imLogfilePath = FileUtils.getSDPath() + File.separator + appName + File.separator + "IMLogs";

        FileUtils.createFileDir(logfilePath);

        Log.i(TAG, "file log path : " + logfilePath);
        //init the xutils

/*		x.Ext.setDebug(true);*/

        if (executor == null) {
            executor = new LooperExecutor();
            executor.requestStart();

        }

        executor.execute(new Runnable() {

            @Override
            public void run() {

                callSessionsHash = new HashMap<Long, CallSession>();

                netRtcSipCallback = new NetRtcSIPCallbackImpl();

                rtcapij.setCallbackObject(netRtcSipCallback);

                NetRtcFactory.InitNetRtc(mContext);

            }
        });

        context.registerReceiver(new ConnectionChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.deleteZipFiles(FileUtils.getSDPath());
            }
        }).start();

    }


    protected static void closeInNonIce(PeerConnectionClient.PeerConnectionClosedEvent peerConnectionClosedEvent) {

        Log.i(TAG, "~ closeInNonIce ~");

        if (appRTCAudioManager != null) {
            appRTCAudioManager.close();

        }


        if (CallApi.peerConnectionClient != null) {//
            CallApi.peerConnectionClient.closeNonIce(peerConnectionClosedEvent);

            CallApi.peerConnectionClient = null;

        }
        if (CallApi.mLocalRender != null) {
            CallApi.mLocalRender.release();
            CallApi.mLocalRender = null;
        }
        if (CallApi.mRemoteRender != null) {
            CallApi.mRemoteRender.release();
            CallApi.mRemoteRender = null;
        }

    }


    /**
     * reset the call api when call session end
     */
    protected static void reset() {

        Log.i(TAG, "~ reset ~");
        CallApi.setCallDest("");

        if (appRTCAudioManager != null) {
            appRTCAudioManager.close();

        }
        PeerConnectionClient.closeCallId();

        isRecall = false;

        networkChange = false;

        CallApi.setCurrentCallId(-1);

        CallApi.setCurrentSDP("");


        if (CallApi.peerConnectionClient != null) {//
            if (CallSession.isICEEnable) {
                CallApi.peerConnectionClient.close(new PeerConnectionClient.PeerConnectionClosedEvent() {
                    @Override
                    public void onPeerConnectionClosed() {
                        Log.i(TAG, " ice peerConnect onPeerConnectionClosed  set the audio model to normal.");
                        muteMedia(false, "reset call", AudioManager.MODE_NORMAL);
                    }
                });
            } else {
                try {
                    CallApi.peerConnectionClient.closeOtherIce();
                } catch (Exception e) {
                    Log.i(TAG, "peerConnectionClient non ice close error." + android.util.Log.getStackTraceString(e));
                }
                Log.i(TAG, "non ice peerConnect do nothing.");
            }


            CallApi.peerConnectionClient = null;

        } else {

            Log.i(TAG, "reset call peerConnectionClient already release.");
        }

        if (CallApi.mLocalRender != null) {
            CallApi.mLocalRender.release();
            CallApi.mLocalRender = null;
        }
        if (CallApi.mRemoteRender != null) {
            CallApi.mRemoteRender.release();
            CallApi.mRemoteRender = null;
        }


        muteMedia(false, "reset call", AudioManager.MODE_NORMAL);
    }


    /**
     * init the audio in android device
     */
    protected static void initiateVoiceAudio() {

        appRTCAudioManager = AppRTCAudioManager.create(mContext);

        appRTCAudioManager.init();

        appRTCAudioManager.setSpeakerphoneOn(false);


    }

    /**
     * init the video  in android device
     */
    protected static void initiateVideoAudio() {

        appRTCAudioManager = AppRTCAudioManager.create(mContext);

        appRTCAudioManager.init();

        appRTCAudioManager.setSpeakerphoneOn(true);


    }

    /**
     * get ICE function status
     *
     * @return true ICE function is open  false ICE function is close
     */
    public static boolean isICEEnalbe() {
        if (preferences == null) {
            return false;
        }
        boolean configICE = preferences.getBoolean("ICE_CONTROL", true);
        if (!configICE) {//from the config ice is false.
            //check current video ice is open.
            if (videoICEEnable) {
                return true; //the video maybe calling.
            }

        }
        return configICE;
    }

    public static boolean isVideoICEEnable() {
        return videoICEEnable;
    }

    /**
     * set ICE function status
     *
     * @param enable true open the ICE function false close the ICE function
     *               in video call the ICE function must be open
     */
    public static void setICEEnable(boolean enable) {
        if (preferences == null) {
            return;
        }
        preferences.edit().putBoolean("ICE_CONTROL", enable).commit();

    }

    private static boolean videoICEEnable;

    public static void setVideoICEEnable(boolean enable) {
        videoICEEnable = enable;
    }


    protected static PeerConnectionClient PeerConnectionFactoryInit(boolean reset, boolean isVideo, PeerConnectionClient.PeerConnectionEvents events) {
        if (peerConnectionClient != null && !reset) {
            Log.w(TAG, "peerConnectionClient already created");
            return peerConnectionClient;
        }
        peerConnectionClient = PeerConnectionClient.getInstance(reset);
        PeerConnectionClient.PeerConnectionParameters peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(isVideo,
                false,
                false, 0, 0, 0, 0, null, true, false, 0, null, false, false, false);
        peerConnectionClient.createPeerConnectionFactory(mContext, peerConnectionParameters, events);

        peerConnectionClient.createPeerConnection(null, null, null, null);
        return peerConnectionClient;
    }


    protected static PeerConnectionClient PeerConnectionVideoFactoryInit(Context context, boolean reset, PeerConnectionClient.PeerConnectionEvents events, PeerConnectionClient.PeerConnectionParameters peerConnectionParameters) {
        if (peerConnectionClient != null && !reset) {
            Log.w(TAG, "peerConnectionClient already created");
            return peerConnectionClient;
        }
        peerConnectionClient = PeerConnectionClient.getInstance();


        CallApi.peerConnectionClient.createPeerConnectionFactory(context, peerConnectionParameters, events);

        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), mLocalRender, mRemoteRender, null);
        return peerConnectionClient;
    }


    /**
     * initate a ip 2 phone call
     *
     * @param contact
     * @return call session object of the current call
     */
    public static CallSession initiateIp2PhoneCall(String contact) {

        Log.i(TAG, "initiateIp2PhoneCall : " + contact);

        CallApi.initiateVoiceAudio();

        final CallSession session = new CallSession();

        //"callid:xxx,sipcallid:xxx"

        Log.i(TAG, "netrtc_call_audio : " + contact);

        String callResult = rtcapij.netrtc_call_audio(CallApi.configName, contact, 0);

        Log.i(TAG, "callResult : " + callResult);

        long callId = -1;

        String sipCallid = "";

        if (!TextUtils.isEmpty(callResult) && callResult.contains(":") && callResult.contains(",")) {

            callId = Long.parseLong(callResult.substring(callResult.indexOf(":") + 1, callResult.indexOf(",")));

            sipCallid = callResult.substring(callResult.lastIndexOf(":") + 1, callResult.length());

        } else {


            Log.w(TAG, "illeage call result : " + callResult);
            terminateCall();
            return session;
        }

        session.callId = callId;

        session.sipCallid = sipCallid;

        session.isICEEnable = false;

        Log.i(TAG, "call aduio callId : " + callId + " ~ sipCallid :" + sipCallid);

        NetRtcSIPCallbackImpl.CallIds.put((int) callId, true);

        CallApi.setCurrentCallId(callId);

        return session;

    }


    public static CallSession initiateAudioCall(String contact) {

        return initiateAudioCall(contact, false);
    }


    /**
     * initate a audio call if you open the ICE function , the system will use priority
     * launch a ice call.if your phoneNumber start with "0090#" will launch a ip2phone call
     * no mater ice function is open or close.
     *
     * @param contact
     * @return call session object of the current call
     */
    public static CallSession initiateAudioCall(String contact, boolean forceNonIce) {

        Log.i(TAG, "initiateAudioCall : " + contact);
        muteMedia(true, "initcall maybe from outgoing audio call");
        CallApi.initiateVoiceAudio();

        final CallSession session = new CallSession();

        if (isICEEnalbe() && !contact.startsWith("0090#") && !forceNonIce) {

            setCallDest(contact);

            peerConnectionClient = CallApi.PeerConnectionFactoryInit(false, false, new PeerConnectionClient.PeerConnectionEvents() {
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
                    Log.i(TAG, "audio onPeerConnectionError=" + description);
                    if (session != null && CallSession.isICEEnable) {
                        session.terminate("audio onPeerConnectionError");
                    } else {
                        if (CallApi.peerConnectionClient != null && CallSession.isICEEnable) {
                            CallApi.peerConnectionClient.close();
                            CallApi.peerConnectionClient = null;
                        }
                    }
                }
            });
            peerConnectionClient.createOffer();


        } else {

            // ice call  close lead to stop the native audio, so we should restart again when start non ice call

/*			if (peerConnectionClient == null) {

				peerConnectionClient = PeerConnectionClient.getInstance();

				peerConnectionClient.createPeerConnectionFactory(mContext, null, false);

				peerConnectionClient.createPeerConnection(null, null);

			}*/

            //"callid:xxx,sipcallid:xxx"

            Log.i(TAG, "netrtc_call_audio : " + contact);

            String callResult = rtcapij.netrtc_call_audio(CallApi.configName, contact, 0);

            Log.i(TAG, "callResult : " + callResult);

            long callId = -1;

            String sipCallid = "";

            if (!TextUtils.isEmpty(callResult) && callResult.contains(":") && callResult.contains(",")) {

                callId = Long.parseLong(callResult.substring(callResult.indexOf(":") + 1, callResult.indexOf(",")));

                sipCallid = callResult.substring(callResult.lastIndexOf(":") + 1, callResult.length());

            } else {


                Log.w(TAG, "illeage call result : " + callResult);
                terminateCall();
                return session;
            }

            session.callId = callId;

            session.sipCallid = sipCallid;

            Log.i(TAG, "call aduio callId : " + callId + " ~ sipCallid :" + sipCallid);

            NetRtcSIPCallbackImpl.CallIds.put((int) callId, true);

            CallApi.setCurrentCallId(callId);


        }

        return session;

    }


    /**
     * initate a video call must open the ICE fucntion both the caller and callee.
     *
     * @param context
     * @param contact
     * @param isIncoming               true is callee fase is the caller
     * @param peerConnectionParameters
     * @return call session object of the current video  call
     */
    public static CallSession initiateVideoCall(final Context context, String contact, final boolean isIncoming, PeerConnectionClient.PeerConnectionParameters peerConnectionParameters) {
        Log.i(TAG, "initiateVideoCall : " + contact);
        muteMedia(true, "init call maybe from video call");
        CallApi.initiateVideoAudio();

        setCallDest(contact);

        CallSession session = null;

        if (isIncoming) {

            session = CallApi.getFgCallSession();

            final String sdpString = session.sdpInformation;

            initVideo(session, context, peerConnectionParameters, isIncoming, sdpString);

        } else {

            session = new CallSession();
            session.type = CallSession.TYPE_VIDEO;
            initVideo(session, context, peerConnectionParameters, isIncoming, "");
        }

        return session;
    }

    private static void initVideo(final CallSession session, final Context context, final PeerConnectionClient.PeerConnectionParameters peerConnectionParameters, final boolean isIncoming, final String sdpString) {


        CallApi.peerConnectionClient = CallApi.PeerConnectionVideoFactoryInit(context, true, new PeerConnectionClient.PeerConnectionEvents() {
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
//				if (!isIncoming) {
//
//					CallApi.peerConnectionClient.createOffer();
//
//				} else {
//
//
//					CallApi.parseSDPInformation(CallApi.peerConnectionClient, sdpString, SessionDescription.Type.OFFER);
//
//				}
            }

            @Override
            public void onPeerConnectionError(String description) {

                Log.i(TAG, "video onPeerConnectionError=" + description);
                if (session != null && CallSession.isICEEnable) {
                    session.terminate("audio onPeerConnectionError");
                } else {
                    if (CallApi.peerConnectionClient != null && CallSession.isICEEnable) {
                        CallApi.peerConnectionClient.close();
                        CallApi.peerConnectionClient = null;
                    }
                }

            }
        }, peerConnectionParameters);

        if (!isIncoming) {

            CallApi.peerConnectionClient.createOffer();

        } else {


            CallApi.parseSDPInformation(CallApi.peerConnectionClient, sdpString, SessionDescription.Type.OFFER);

        }
    }


    /**
     * get the call session object by session id
     *
     * @param sessionId call session id
     * @return call session object of the id
     */
    public static CallSession getCallSessionById(long sessionId) {
        if (null == callSessionsHash) {
            Log.d(TAG, "CallApi getCallSessionById callSessionsHash is null");
            return null;
        }
        return (CallSession) callSessionsHash.get(Long.valueOf(sessionId));
    }

    /**
     * get the current connection mode of the sip server
     * <p>
     * include the UPD mode, TCP mode , TUNNEL mode.
     *
     * @return the connection mode value contain one of the "UDP","TCP","TUNNEL"
     */
    public static String getCurrentConnectMode() {

        String mode = "";

        eNETRTC_CONNECTION_MODE eMode = rtcapij.netrtc_acc_get_connectionmode(configName);

        Log.i(TAG, "current connection mode : " + eMode.ordinal());

        switch (eMode) {

            case ACC_CONNECTION_MODE_UDP:
                mode = "UDP";
                break;
            case ACC_CONNECTION_MODE_TCP:
                mode = "TCP";
                break;

            case ACC_CONNECTION_MODE_TUNNEL:
                mode = "TUNNEL";
                break;

            default:
                break;
        }
        return mode;
    }

    /**
     * @param session add or update the call session object in current app
     */

    protected static void addOrUpdateCallSession(CallSession session) {
        if (null == callSessionsHash) {
            Log.d(TAG, "CallApi getCallSessionById callSessionsHash is null");
            return;
        }


        Log.i(TAG, "update call session  call id : " + session.callId);

        CallApi.setCurrentCallId(session.callId);

        callSessionsHash.put(session.callId, session);

    }

    /**
     * removte the call session by session id
     *
     * @param sessionId
     * @return
     */
    protected static CallSession removeCallSessionById(long sessionId) {
        if (null == callSessionsHash) {
            Log.d(TAG, "CallApi getCallSessionById callSessionsHash is null");
            return null;
        }
        return (CallSession) callSessionsHash.remove(Long.valueOf(sessionId));
    }

    /**
     * get the  call session of the current call
     *
     * @return the call session of the call
     */
    public static CallSession getFgCallSession() {
        if (null == callSessionsHash) {
            Log.d(TAG, "CallApi getFgCallSession callSessionsHash is null");
            return null;
        }
        return (CallSession) callSessionsHash.get(currentCallId);
    }

    protected static Context getContext() {
        return mContext;
    }

    protected static String getCallDest() {

        return callDest;

    }

    /**
     * set callee name
     *
     * @param callDest
     */
    protected static void setCallDest(String callDest) {

        CallApi.callDest = callDest;

    }

    public static void muteMedia(boolean isMute, String tag) {
        muteMedia(isMute, tag, AudioManager.MODE_IN_COMMUNICATION);
    }

    public static void muteMedia(boolean isMute, String tag, int audioModel) {


        AudioManager audioManager = (AudioManager) CallApi.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(audioModel);
        audioManager.setMicrophoneMute(isMute);

        Log.i(TAG, "set mute media is mute:" + isMute + ";tag=" + tag);
    }

    protected static void terminateCall() {
        muteMedia(false, "terminate a call", AudioManager.MODE_NORMAL);
        CallSession callSession = new CallSession();
        callSession.callState = CallSession.STATUS_IDLE;
        CallApi.setCurrentCallId(-1);
        CallApi.sendCallStatusBroadcast(callSession);
    }


    /**
     * send the local broadcast  when call status changed
     */

    public static void sendCallRecordErrorBroadcast(CallSession callSession) {

        sendCallStatusBroadcast(callSession);

    }


    /**
     * send the local broadcast  when call status changed
     */

    protected static void sendCallStatusBroadcast(CallSession callSession) {

        sendCallStatusBroadcast(callSession, null);

    }

    protected static void sendCallStatusBroadcast(CallSession callSession, String reason) {

        addOrUpdateCallSession(callSession);

        Intent callHandlerIntent = new Intent(CallApi.EVENT_CALL_STATUS_CHANGE);

        callHandlerIntent.putExtra(CallApi.PARAM_CALL_SESSION, callSession);

        if (!TextUtils.isEmpty(reason)) {

            callHandlerIntent.putExtra(CallApi.PARAM_SIP_REASON_TEXT, reason);

        }


        if (CallApi.getContext() != null) {

            Log.i(TAG, "send call status change broadcast -->   " + callSession.callState);

            LocalBroadcastManager.getInstance(CallApi.getContext()).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "TestApplication is not Running()");
        }

    }

    /**
     * send the local broadcast  when have a incoming call
     */

    protected static void sendCallInviteBroadcast(CallSession callSession) {

        Intent callHandlerIntent = new Intent(CallApi.EVENT_CALL_INVITATION);

        callHandlerIntent.putExtra(CallApi.PARAM_CALL_SESSION, callSession);


        if (CallApi.getContext() != null) {

            Log.i(TAG, "send call sendCallInviteBroadcast -->   " + callSession.callState);

            LocalBroadcastManager.getInstance(CallApi.getContext()).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "TestApplication is not Running()");
        }

    }


    /**
     * send the local broadcast  when the network quality changed
     */
    protected static void sendCallQosBroadcast(String type, String info, int callQuality) {

        Intent callHandlerIntent = new Intent(CallApi.EVENT_CALL_QOS_REPORT);


        callHandlerIntent.putExtra(CallApi.PARAM_REPORT_TYPE, type);
        callHandlerIntent.putExtra(CallApi.PARAM_CANDIDATE_PAIR, info);
        callHandlerIntent.putExtra(CallApi.PARAM_CALL_QOS, callQuality);


        if (CallApi.getContext() != null) {

            Log.i(TAG, "send call qos change broadcast -->  : " + callQuality);

            LocalBroadcastManager.getInstance(CallApi.getContext()).sendBroadcast(callHandlerIntent);

        } else {

            Log.i(TAG, "TestApplication is not Running()");
        }

    }

    /**
     * ice sdp information process
     */
    protected static void parseSDPInformation(PeerConnectionClient pc, String sdpDescription, SessionDescription.Type type) {

        parseSDPInformation(pc, sdpDescription, type, false);
    }

    /**
     * ice sdp information process
     */
    protected static void parseSDPInformation(PeerConnectionClient pc, String sdpDescription, SessionDescription.Type type, boolean video2audio) {

        if (TextUtils.isEmpty(sdpDescription) || pc == null) {
            return;

        }

        Log.i(TAG, "~ parseSDPInformation ~");

        String[] lines = sdpDescription.split("\r\n");

        StringBuffer newSdpDescription = new StringBuffer();

        List<IceCandidate> candidates = new ArrayList<IceCandidate>();

        for (int i = 0; i < lines.length; i++) {

            if (lines[i].contains(ICE_CANDIDATE_TAG)) {

                candidates.add(new IceCandidate("audio", 0, lines[i]));
                continue;

            }

            newSdpDescription.append(lines[i]).append("\r\n");

        }

        if (video2audio) {

            String audioSdp = newSdpDescription.toString().substring(0, newSdpDescription.toString().indexOf(VIDEO_TAG));


            pc.setRemoteDescription(new SessionDescription(type, audioSdp));


        } else {

            if (pc.isVideoCallEnabled() && !newSdpDescription.toString().contains(VIDEO_TAG)) {

                peerConnectionClient.stopVideoSource();

                pc.setVideoEnabled(false);

                Log.i(TAG, "~ set video disable ~");

                String mSdp = newSdpDescription.append(VIDEO_M_LINE).toString();

                pc.setRemoteDescription(new SessionDescription(type, mSdp));


            } else {


                pc.setRemoteDescription(new SessionDescription(type, newSdpDescription.toString()));

            }

        }

        if (type == SessionDescription.Type.OFFER) {

            Log.i(TAG, "incoming call create Answer");

            pc.createAnswer();
        }

        for (int i = 0; i < candidates.size(); i++) {

            Log.i(TAG, "add remote ice candidate : " + candidates.get(i).sdp);

            pc.addRemoteIceCandidate(candidates.get(i));

        }
    }

    /**
     * When speaker open status set this value
     */
    public final static int MODE_SPEAKER = 0x1;
    /**
     * When speaker close status set this value
     */
    public final static int MODE_MICROPHONE = 0x2;
    /**
     * When use headset set this value
     */

    public final static int MODE_HEADSET = 0x3;
    /**
     * When user headphone set this value
     */
    public final static int MODE_HEADPHONE = 0x4;

    /**
     * Set mode in call
     *
     * @param audioMode
     */
    public static void setAudioAECMode(int audioMode) {
        switch (audioMode) {

            case MODE_SPEAKER:
                rtcapij.netrtc_set_config("aec", "handsfree");
                break;
            case MODE_MICROPHONE:
                rtcapij.netrtc_set_config("aec", "handset");
                break;
            case MODE_HEADSET:
                rtcapij.netrtc_set_config("aec", "headset");
                break;
            case MODE_HEADPHONE:
                rtcapij.netrtc_set_config("aec", "headphone");
                break;
        }
    }


    public static void reCreateAnswer(final String sdpDescription) {

        if (TextUtils.isEmpty(sdpDescription)) {
            Log.i(TAG, "reCreateAnswer but the sdp info is null~~~~~~~");
            return;
        }
        //video type deal later .
        if (peerConnectionClient != null) {
            peerConnectionClient.close(new PeerConnectionClient.PeerConnectionClosedEvent() {
                @Override
                public void onPeerConnectionClosed() {
                    Log.i(TAG, "startReCreateAnswer~~~~~~~" + getCurrentCallId());
                    if (getCurrentCallId() > -1) {
                        peerConnectionClient = CallApi.PeerConnectionFactoryInit(true, false, new PeerConnectionClient.PeerConnectionEvents() {
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
                                Log.i(TAG, "audio onPeerConnectionError=" + description);
                                CallSession session = CallApi.getFgCallSession();
                                if (session != null && CallSession.isICEEnable) {
                                    session.terminate("audio onPeerConnectionError");
                                } else {
                                    if (CallApi.peerConnectionClient != null && CallSession.isICEEnable) {
                                        CallApi.peerConnectionClient.close();
                                        CallApi.peerConnectionClient = null;
                                    }
                                }
                            }
                        });

                        CallApi.parseSDPInformation(CallApi.peerConnectionClient, sdpDescription, SessionDescription.Type.OFFER);
                    }
                }
            });
            peerConnectionClient = null;
        }
        Log.i(TAG, "reCreateAnswer~~~~~~~" + sdpDescription);


    }

    public static void reCreateOffer() {

        //video type deal later .
        if (peerConnectionClient != null) {
            peerConnectionClient.close(new PeerConnectionClient.PeerConnectionClosedEvent() {
                @Override
                public void onPeerConnectionClosed() {
                    Log.i(TAG, "startReCreateOffer~~~~~~~" + getCurrentCallId());

                    if (getCurrentCallId() > -1) {

                        peerConnectionClient = CallApi.PeerConnectionFactoryInit(true, false, new PeerConnectionClient.PeerConnectionEvents() {
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
                                Log.i(TAG, "audio onPeerConnectionError=" + description);
                                CallSession session = CallApi.getFgCallSession();
                                if (session != null && CallSession.isICEEnable) {
                                    session.terminate("audio onPeerConnectionError");
                                } else {
                                    if (CallApi.peerConnectionClient != null && CallSession.isICEEnable) {
                                        CallApi.peerConnectionClient.close();
                                        CallApi.peerConnectionClient = null;
                                    }
                                }
                            }
                        });
                        peerConnectionClient.createOffer();
                    } else {

                    }
                }
            });
            peerConnectionClient = null;
        }
        Log.i(TAG, "reCreateOffer~~~~~~~");

    }
}


