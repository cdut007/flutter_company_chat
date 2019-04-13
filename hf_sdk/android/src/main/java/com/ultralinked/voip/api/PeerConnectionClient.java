/*
 * libjingle
 * Copyright 2014 Google Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * specified, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ultralinked.voip.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.holdingfuture.flutterapp.hfsdk.BuildConfig;
import com.ultralinked.voip.rtcapi.rtcapij;

import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaCodecVideoEncoder;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Peer connection client implementation.
 * <p>
 * <p>All public methods are routed to local looper thread.
 * All PeerConnectionEvents callbacks are invoked from the same looper thread.
 * This class is a singleton.
 */
public class PeerConnectionClient {
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    private static final String TAG = "PeerConnectionClient";
    private static final String FIELD_TRIAL_VP9 = "WebRTC-SupportVP9/Enabled/";
    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String VIDEO_CODEC_H264 = "H264";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;
    private static final int MAX_VIDEO_WIDTH = 1280;
    private static final int MAX_VIDEO_HEIGHT = 1280;
    private static final int MAX_VIDEO_FPS = 30; //30
    public final static int ICE_TIMEOUT_TIME = 1200;

    private static final int maxICEtryCount = 3;

    private static final int maxVIDEOICEtryCount = 7;

    private volatile static PeerConnectionClient instance = new PeerConnectionClient();
    private final PCObserver pcObserver = new PCObserver();
    private final SDPObserver sdpObserver = new SDPObserver();
    public final LooperExecutor executor;

    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    PeerConnectionFactory.Options options = null;
    private VideoSource videoSource;
    private boolean videoCallEnabled;
    private boolean preferIsac;
    private boolean preferH264;
    private boolean videoSourceStopped;
    private boolean isError;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private MediaConstraints pcConstraints;
    private MediaConstraints videoConstraints;
    private MediaConstraints audioConstraints;
    private MediaConstraints sdpMediaConstraints;
    private LinkedList<IceCandidate> queuedRemoteCandidates;
    private boolean isInitiator;
    private SessionDescription localSdp;
    private MediaStream mediaStream;
    private int numberOfCameras;
    private VideoCapturerAndroid videoCapturer;
    private boolean renderVideo;
    private VideoTrack localVideoTrack;
    private VideoTrack remoteVideoTrack;

    public StringBuffer localCandidates;

    public StringBuffer originSdp;

    private boolean isOutGoingCall = true;

    public boolean isICECompleted;

    private Handler handler;

    public boolean isEnd;

    private SharedPreferences sharedPreferences;

    private Context mContext;

    private int iceCheckCount = 0;

    private PeerConnectionParameters peerConnectionParameters;

    //new added
    private Timer statsTimer;
    private AppRTCClient.SignalingParameters signalingParameters;
    private ParcelFileDescriptor aecDumpFileDescriptor;
    private PeerConnectionEvents events;

    public int getMaxICERetryCount() {
        if (videoCallEnabled) {
            return maxVIDEOICEtryCount;
        }
        return maxICEtryCount;
    }


    /**
     * Peer connection parameters.
     */
    public static class PeerConnectionParameters {
        public final boolean videoCallEnabled;
        public final boolean loopback;
        public final boolean tracing;
        public final int videoWidth;
        public final int videoHeight;
        public final int videoFps;
        public final int videoStartBitrate;
        public final String videoCodec;
        public final boolean videoCodecHwAcceleration;
        public final boolean captureToTexture;
        public final int audioStartBitrate;
        public final String audioCodec;
        public final boolean noAudioProcessing;
        public final boolean aecDump;
        public final boolean useOpenSLES;

        public PeerConnectionParameters(
                boolean videoCallEnabled, boolean loopback, boolean tracing,
                int videoWidth, int videoHeight, int videoFps, int videoStartBitrate,
                String videoCodec, boolean videoCodecHwAcceleration, boolean captureToTexture,
                int audioStartBitrate, String audioCodec,
                boolean noAudioProcessing, boolean aecDump, boolean useOpenSLES) {
            this.videoCallEnabled = videoCallEnabled;
            this.loopback = loopback;
            this.tracing = tracing;
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            this.videoFps = videoFps;
            this.videoStartBitrate = videoStartBitrate;
            this.videoCodec = videoCodec;
            this.videoCodecHwAcceleration = videoCodecHwAcceleration;
            this.captureToTexture = captureToTexture;
            this.audioStartBitrate = audioStartBitrate;
            this.audioCodec = audioCodec;
            this.noAudioProcessing = noAudioProcessing;
            this.aecDump = aecDump;
            this.useOpenSLES = useOpenSLES;
        }
    }


    /**
     * Peer connection events.
     */
    public static interface PeerConnectionEvents {
        /**
         * Callback fired once local SDP is created and set.
         */
        public void onLocalDescription(final SessionDescription sdp);

        /**
         * Callback fired once local Ice candidate is generated.
         */
        public void onIceCandidate(final IceCandidate candidate);

        /**
         * Callback fired once connection is established (IceConnectionState is
         * CONNECTED).
         */
        public void onIceConnected();

        /**
         * Callback fired once connection is closed (IceConnectionState is
         * DISCONNECTED).
         */
        public void onIceDisconnected();

        /**
         * Callback fired once peer connection is closed.
         */
        public void onPeerConnectionClosed();

        /**
         * Callback fired once peer connection statistics is ready.
         */
        public void onPeerConnectionStatsReady(final StatsReport[] reports);

        /**
         * Callback fired once peer connection error happened.
         */
        public void onPeerConnectionError(final String description);
    }

    public static interface PeerConnectionClosedEvent {

        /**
         * Callback fired once peer connection is closed.
         */
        public void onPeerConnectionClosed();
    }


    private PeerConnectionClient() {

        executor = new LooperExecutor();

        executor.requestStart();

        handler = new Handler(Looper.getMainLooper());

        localCandidates = new StringBuffer();

    }

    public static PeerConnectionClient getInstance() {
        return getInstance(false);
    }

    public static PeerConnectionClient getInstance(boolean reset) {

        if (reset) {
            instance = null;
        }

        if (instance == null) {
            synchronized (PeerConnectionClient.class) {
                if (instance == null) {
                    instance = new PeerConnectionClient();
                }
            }
        }


        return instance;
    }

    public void setPeerConnectionFactoryOptions(PeerConnectionFactory.Options options) {
        this.options = options;
    }

    public void createPeerConnectionFactory(
            final Context context,
            final PeerConnectionParameters peerConnectionParameters,
            final PeerConnectionEvents events
    ) {

        isEnd = false;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.mContext = context;

        // Reset variables to initial states.
        factory = null;
        peerConnection = null;
        preferIsac = false;
        preferH264 = false;
        videoSourceStopped = false;
        isError = false;
        queuedRemoteCandidates = null;
        localSdp = null; // either offer or answer SDP
        mediaStream = null;
        videoCapturer = null;
        renderVideo = true;
        localVideoTrack = null;
        remoteVideoTrack = null;

        this.peerConnectionParameters = peerConnectionParameters;
        this.events = events;
        videoCallEnabled = peerConnectionParameters.videoCallEnabled;

        statsTimer = new Timer();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                createPeerConnectionFactoryInternal(context);
            }
        });

    }

    public void createPeerConnection(
            final EglBase.Context renderEGLContext,
            final VideoRenderer.Callbacks localRender,
            final VideoRenderer.Callbacks remoteRender,
            final AppRTCClient.SignalingParameters signalingParameters
    ) {
        if (peerConnectionParameters == null) {
            Log.e(TAG, "Creating peer connection without initializing factory.");
            return;
        }

        this.localRender = localRender;
        this.remoteRender = remoteRender;
        this.signalingParameters = signalingParameters;

        executor.execute(new Runnable() {
            @Override
            public void run() {

                createMediaConstraintsInternal();

                createPeerConnectionInternal(renderEGLContext);
            }
        });

        setVideoEnabled(true);
    }


    private PeerConnectionClosedEvent connectionClosedEvent;

    public void close() {
        close(null);
    }


    public void close(PeerConnectionClosedEvent closedEvent) {

        this.connectionClosedEvent = closedEvent;

        new Thread(new Runnable() {
            @Override
            public void run() {
                closeInternal();
            }
        }).start();
//    executor.execute(new Runnable() {
//      @Override
//      public void run() {
//        closeInternal();
//      }
//    });
    }


    public void closeOtherIce() {
        QosStatusCount = 0;
        if (factory != null && peerConnectionParameters.aecDump) {
            factory.stopAecDump();
        }
        Log.d(TAG, "closeOtherIce peer connection.");
        try {
            statsTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        options = null;
        isEnd = true;
        isICECompleted = true;
        Log.d(TAG, "closeNonIce peer connection done.");
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
        Log.i(TAG, "closeNonIce peer connection done.");

    }

    public void closeNonIce(final PeerConnectionClosedEvent peerConnectionClosedEvent) {
        QosStatusCount = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (factory != null && peerConnectionParameters.aecDump) {
                    factory.stopAecDump();
                }

                Log.d(TAG, "closeNonIce peer connection.");
                try {
                    statsTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (peerConnection != null) {
                    peerConnection.dispose();
                    peerConnection = null;
                }
                Log.d(TAG, "closeNonIce video source.");
                if (videoSource != null) {
                    videoSource.dispose();
                    videoSource = null;
                }
                Log.d(TAG, "closeNonIce peer connection factory.");
                if (factory != null) {
                    factory.dispose();
                    factory = null;
                }
                options = null;
                isEnd = false;
                isICECompleted = false;
                Log.d(TAG, "closeNonIce peer connection done.");
                PeerConnectionFactory.stopInternalTracingCapture();
                PeerConnectionFactory.shutdownInternalTracer();
                Log.i(TAG, "closeNonIce peer connection done.");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "closeNonIce notify onPeerConnectionClosed.");
                        if (peerConnectionClosedEvent != null)
                            peerConnectionClosedEvent.onPeerConnectionClosed();
                    }
                }, 500);


            }
        }).start();

    }

    public boolean isVideoCallEnabled() {
        return videoCallEnabled;
    }

    private void createPeerConnectionFactoryInternal(
            Context context) {
        if (peerConnectionParameters.tracing) {
            PeerConnectionFactory.startInternalTracingCapture(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                            + "webrtc-trace.txt");
        }
        Log.d(TAG, "Create peer connection factory." + " Use video: "
                + peerConnectionParameters.videoCallEnabled);

        isError = false;

        boolean useVP9 = true;

        boolean useH264 = true;

        // Check if VP9 is used by default.
        if (videoCallEnabled && useVP9) {
            PeerConnectionFactory.initializeFieldTrials(FIELD_TRIAL_VP9);
        } else {
            PeerConnectionFactory.initializeFieldTrials(null);
        }
        // Check if H.264 is used by default.
        preferH264 = false;

        if (videoCallEnabled && useH264) {
            preferH264 = true;
        }
        // Check if ISAC is used by default.
        preferIsac = false;

        boolean useISAC = false;


        if (!PeerConnectionFactory.initializeAndroidGlobals(context, true, true,
                peerConnectionParameters.videoCodecHwAcceleration)) {
        }
        if (useISAC) {
            preferIsac = true;
        }

        factory = new PeerConnectionFactory();
        if (options != null) {
            Log.d(TAG, "Factory networkIgnoreMask option: " + options.networkIgnoreMask);
            factory.setOptions(options);
        }
        Log.d(TAG, "Peer connection factory created.");
    }

    private void createMediaConstraintsInternal() {

        boolean videoCodecHwAcceleration = true;
        // Create peer connection constraints.
        pcConstraints = new MediaConstraints();
        // Enable DTLS for normal calls and disable for loopback calls.

        pcConstraints.optional.add(new KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
        String codecList = rtcapij.netrtc_get_codeclist();
        if (TextUtils.isEmpty(codecList)) {
            Log.i(TAG, "codec list is null,read from the prefs");
            codecList = sharedPreferences.getString("audio_codecs", "iLBC,opus,PCMU,PCMA,G729,G722");

        }
        pcConstraints.optional.add(new KeyValuePair("UserCodec", codecList));


        // Check if there is a camera on device and disable video call if not.
        numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
        if (numberOfCameras == 0) {
            Log.w(TAG, "No camera on device. Switch to audio only call.");
            videoCallEnabled = false;
        }
        // Create video constraints if video call is enabled.
        if (videoCallEnabled) {
            videoConstraints = new MediaConstraints();
            int videoWidth = peerConnectionParameters.videoWidth;
            int videoHeight = peerConnectionParameters.videoHeight;

            // If VP8 HW video encoder is supported and video resolution is not
            // specified force it to HD.
            if ((videoWidth == 0 || videoHeight == 0)
                    && MediaCodecVideoEncoder.isVp8HwSupported() && videoCodecHwAcceleration) {
                videoWidth = HD_VIDEO_WIDTH;
                videoHeight = HD_VIDEO_HEIGHT;
            }

            // Add video resolution constraints.
            if (videoWidth > 0 && videoHeight > 0) {
                videoWidth = Math.min(videoWidth, MAX_VIDEO_WIDTH);
                videoHeight = Math.min(videoHeight, MAX_VIDEO_HEIGHT);
                videoConstraints.mandatory.add(new KeyValuePair(
                        MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
                videoConstraints.mandatory.add(new KeyValuePair(
                        MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
                videoConstraints.mandatory.add(new KeyValuePair(
                        MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
                videoConstraints.mandatory.add(new KeyValuePair(
                        MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
            }

            // Add fps constraints.
            int videoFps = peerConnectionParameters.videoFps;

            if (videoFps <= 0) {
                videoFps = MAX_VIDEO_FPS;
            }

            if (videoFps > 0) {
                videoFps = Math.min(videoFps, MAX_VIDEO_FPS);
                videoConstraints.mandatory.add(new KeyValuePair(
                        MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(videoFps)));
                videoConstraints.mandatory.add(new KeyValuePair(
                        MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(videoFps)));
            }
        }

        // Create audio constraints.
        audioConstraints = new MediaConstraints();
        // added for audio performance measurements

        boolean onlyVideo = videoCallEnabled;//false by default.
        if (onlyVideo) {
            Log.d(TAG, "Disabling audio processing");
            audioConstraints.mandatory.add(new KeyValuePair(
                    AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(new KeyValuePair(
                    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(new KeyValuePair(
                    AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(new KeyValuePair(
                    AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
        }
        // Create SDP constraints.
        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new KeyValuePair(
                "OfferToReceiveAudio", "true"));

        if (videoCallEnabled) {
            sdpMediaConstraints.mandatory.add(new KeyValuePair(
                    "OfferToReceiveVideo", "true"));
        } else {
            sdpMediaConstraints.mandatory.add(new KeyValuePair(
                    "OfferToReceiveVideo", "false"));
        }
    }


    private void createPeerConnectionInternal(EglBase.Context renderEGLContext) throws NullPointerException {


        if (factory == null || isError) {
            Log.e(TAG, "Peerconnection factory is not created");
            return;
        }

        Log.d(TAG, "Create peer connection");

        Log.d(TAG, "PCConstraints: " + pcConstraints.toString());

        if (videoConstraints != null) {

            Log.d(TAG, "VideoConstraints: " + videoConstraints.toString());

        }
        queuedRemoteCandidates = new LinkedList<IceCandidate>();

        if (videoCallEnabled) {
            Log.d(TAG, "EGLContext: " + renderEGLContext);
            factory.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext);
        }

        LinkedList<PeerConnection.IceServer> ice_servers = new LinkedList<PeerConnection.IceServer>();

        String stunStr = "stun:" + sharedPreferences.getString("ice_server_ip", "61.5.195.41") + ":" + sharedPreferences.getString("ice_server_port", "3478");
        String turnStr = "turn:" + sharedPreferences.getString("ice_server_ip", "61.5.195.41") + ":" + sharedPreferences.getString("ice_server_port", "3478") + "?transport=tcp";

        ice_servers.add(new PeerConnection.IceServer(stunStr, "", ""));

        ice_servers.add(new PeerConnection.IceServer(turnStr, sharedPreferences.getString("turn_username", "ultralinked"), sharedPreferences.getString("turn_password", "ultralinked")));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(ice_servers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;

        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;

        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;

        peerConnection = factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);

        isInitiator = false;

        mediaStream = factory.createLocalMediaStream("ARDAMS");
        if (videoCallEnabled) {

            String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(0);
            String frontCameraDeviceName =
                    CameraEnumerationAndroid.getNameOfFrontFacingDevice();
            if (numberOfCameras > 1 && frontCameraDeviceName != null) {
                cameraDeviceName = frontCameraDeviceName;
            }
            Log.d(TAG, "Opening camera: " + cameraDeviceName);
            videoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null);
            if (videoCapturer == null) {
                reportError("Failed to open camera");
                return;
            }
            mediaStream.addTrack(createVideoTrack(videoCapturer));
        }

        mediaStream.addTrack(factory.createAudioTrack(
                AUDIO_TRACK_ID,
                factory.createAudioSource(audioConstraints)));
        peerConnection.addStream(mediaStream);

        Log.d(TAG, "Peer connection created.");

        rtcapij.netrtc_set_config("enable_webrtcicelog", "true");
    }


    private String newSDP() {

        String origin_sdp = "";

        if (originSdp != null && originSdp.toString().contains(CallApi.VIDEO_TAG)) {

            if (!isVideoCallEnabled()) {

                origin_sdp = originSdp.toString().substring(0, originSdp.toString().indexOf(CallApi.VIDEO_TAG));

            } else {

                origin_sdp = originSdp.toString();
            }

        } else if (originSdp != null) {

            origin_sdp = originSdp.toString();
        }

        String newSDP = origin_sdp.replace(CallApi.ICE_TAG, localCandidates.toString() + CallApi.ICE_TAG);
        return newSDP;
    }

    long checkTime = 0;

    private synchronized void callProcess(String newSDP) {


        if (System.currentTimeMillis() - checkTime < 500) {
            Log.i(TAG, "callProcess is frequety just ignore~~~~");
            return;
        }
        checkTime = System.currentTimeMillis();

        boolean noNewSDP = false;
        if (TextUtils.isEmpty(newSDP)) {
            Log.i(TAG, "grrtc call newSDP is empty : " + isOutGoingCall);
            noNewSDP = true;

        }

        if (isOutGoingCall) {

            Log.i(TAG, "grrtc call audio sdp : \n " + newSDP.trim());

            long callId = -1;

            String sipCallid = "";
            String callResult = "";
            if (noNewSDP) {
                if (!videoCallEnabled) {//
                    CallSession.isICEEnable = false;
                    CallApi.closeInNonIce(new PeerConnectionClient.PeerConnectionClosedEvent() {
                        @Override
                        public void onPeerConnectionClosed() {
                            Log.i(TAG, " onPeerConnectionClosed  : " + CallApi.getCurrentCallId());

                            Log.i(TAG, "grrtc_call_audio  by non ice");
                            CallApi.initiateAudioCall(CallApi.getCallDest(), true);

                        }
                    });

                    Log.i(TAG, "grrtc call audio callResult : \n " + callResult);
                    return;
                } else {//video call
                    Log.i(TAG, "outgoing video end ,maybe need to check ANR callResult");
                    CallSession session = new CallSession();
                    MediaManger.getInstance(CallApi.getContext()).stopAlarmRing();
                    session.callState = CallSession.STATUS_IDLE;
                    CallApi.setCurrentCallId(-1);
                    CallApi.reset();
                    CallApi.sendCallStatusBroadcast(session);
                    return;
                }
            } else {

                if (videoCallEnabled && !newSDP.contains(CallApi.VIDEO_TAG)) {
                    Log.i(TAG, "error not contain: \n " + newSDP.trim());
                    return;
                }

                if (CallApi.isRecall) {
                    Log.i(TAG, "recall in ice model: \n ");
                    if (CallApi.getCurrentCallId() >= 0) {
                        rtcapij.netrtc_call_recall_sdp(CallApi.getCurrentCallId(), newSDP.trim());
                    }

                } else {
                    if (!TextUtils.isEmpty(CallApi.getCallDest())) {
                        callResult = rtcapij.netrtc_call_audio_sdp(CallApi.configName, CallApi.getCallDest(), 0, newSDP.trim());

                    } else {
                        Log.i(TAG, "call maybe hangup ");
                        close();
                        return;
                    }

                }
            }

            if (!TextUtils.isEmpty(callResult) && callResult.contains(":") && callResult.contains(",")) {

                callId = Long.parseLong(callResult.substring(callResult.indexOf(":") + 1, callResult.indexOf(",")));

                sipCallid = callResult.substring(callResult.lastIndexOf(":") + 1, callResult.length());

            } else {

                if (!CallApi.isRecall) {
                    Log.i(TAG, "illeage call result : " + callResult);
                    CallApi.terminateCall();
                    return;
                }

            }

            if (!CallApi.isRecall) {
                CallSession session = new CallSession();

                session.callId = callId;

                session.sipCallid = sipCallid;

                session.callState = CallSession.STATUS_PROCESSING;

                NetRtcSIPCallbackImpl.CallIds.put((int) callId, true);

                CallApi.sendCallStatusBroadcast(session);

                Log.i(TAG, "grrtc_call_audio_sdp callId : " + callId + " ~ sipCallid :" + sipCallid);
            } else {
                Log.i(TAG, "recall in ice model~~~~~callid:" + CallApi.getCurrentCallId());
            }


        } else {

            if (CallApi.getFgCallSession() != null && CallApi.getFgCallSession().type == CallSession.TYPE_AUDIO && CallApi.getCurrentCallId() > -1) {

                CallSession callSession = CallApi.getFgCallSession();

                if (!noNewSDP) {
                    CallApi.setCurrentSDP(newSDP);
                }


                if (callSession.callState != CallSession.STATUS_RECONNECTING) {
                    //if it is incoming call ?
                    if (callSession.callState == CallSession.STATUS_WAIT_AUTO_ACCEPT_BY_SDP_CREATE_OFFER_COMPLETED) {
                        if (!noNewSDP) {
                            rtcapij.netrtc_call_accept_sdp(CallApi.getCurrentCallId(), newSDP.trim());
                            Log.i(TAG, "incoming call parse succ call id,just accept it : " + CallApi.getFgCallSession().callId);
                        } else {
                            CallSession.isICEEnable = false;
                            rtcapij.netrtc_call_accept(CallApi.getCurrentCallId());
                            Log.i(TAG, "incoming call no new sdp found,just accept by non-ice: " + CallApi.getFgCallSession().callId);
                        }
                    } else {
                        Log.i(TAG, "incoming call not accepted, we wait it.");
                        if (callSession.callState  == CallSession.STATUS_INCOMING){
                            Log.i(TAG, "incoming call start media candidate early.");
                            if (!noNewSDP) {
                                rtcapij.netrtc_call_ring_sdp(CallApi.getCurrentCallId(), newSDP.trim());
                                Log.i(TAG, "incoming call start media parse succ call id,just accept it : " + CallApi.getFgCallSession().callId);
                            } else {

                                Log.i(TAG, "incoming call start media no new sdp found,just ignore " + CallApi.getFgCallSession().callId);
                            }
                        }
                    }

                } else {
                    callSession.accept(true);
                    Log.i(TAG, "incoming call maybe a recall just accecpt it, callSession.callState : " + callSession.callState);
                }

            } else {
                Log.i(TAG, "accept call call id : " + CallApi.getCurrentCallId() + "videoCallEnabled:" + videoCallEnabled);
                if (CallApi.getCurrentCallId() < 0) {
                    Log.i(TAG, "incoming call id is -1");
                    localCandidates.setLength(0);
                    return;
                }


                if (!videoCallEnabled) {//audio call

                    CallApi.getFgCallSession().type = CallSession.TYPE_AUDIO;
                    if (noNewSDP) {
                        CallSession.isICEEnable = false;
                        rtcapij.netrtc_call_accept(CallApi.getCurrentCallId());
                    } else {
                        rtcapij.netrtc_call_accept_sdp(CallApi.getCurrentCallId(), newSDP);
                    }
                    MediaManger.getInstance(CallApi.getContext()).stopAlarmRing(false);
                } else {//video call

                    if (noNewSDP) {//video call no sdp  notify

                        CallSession session = CallApi.getFgCallSession();
                        if (session != null) {

                            MediaManger.getInstance(CallApi.getContext()).stopAlarmRing();
                            Log.i(TAG, "noNewSDP  no new sdp collected");
                            session.terminate("no new sdp collected");
                        } else {
                            Log.i(TAG, "noNewSDP  but callsession is null ");
                        }

                    } else {
                        rtcapij.netrtc_call_accept_sdp(CallApi.getCurrentCallId(), newSDP);
                        MediaManger.getInstance(CallApi.getContext()).stopAlarmRing(false);
                    }

                }


            }

        }

        localCandidates.setLength(0);

        Log.i(TAG, " new sdp length : " + newSDP.length() + "new sdp : \n" + newSDP);
    }


    private void closeInternal() {
        QosStatusCount = 0;

        if (peerConnection == null) {
            Log.d(TAG, "already Closed peer connection.");
            if (connectionClosedEvent != null) {
                connectionClosedEvent.onPeerConnectionClosed();
            }
            return;
        }

        PeerConnection peerConnectionTemp = peerConnection;
        peerConnection = null;


        if (factory != null && peerConnectionParameters.aecDump) {
            factory.stopAecDump();
        }

        Log.d(TAG, "Closing peer connection.");
        try {
            statsTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (peerConnectionTemp != null) {
            peerConnectionTemp.dispose();
        }
        Log.d(TAG, "Closing video source.");
        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }
        Log.d(TAG, "Closing peer connection factory.");
        if (factory != null) {
            factory.dispose();
            factory = null;
        }
        options = null;
        isEnd = false;
        isICECompleted = false;
        Log.d(TAG, "Closing peer connection done.");
        if (events != null) {
            events.onPeerConnectionClosed();
            instance = null;
        }

        if (connectionClosedEvent != null) {
            connectionClosedEvent.onPeerConnectionClosed();
        }

        try {
            PeerConnectionFactory.stopInternalTracingCapture();
            PeerConnectionFactory.shutdownInternalTracer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Closing peer connection done.");
    }

    public boolean isHDVideo() {
        if (!videoCallEnabled) {
            return false;
        }
        int minWidth = 0;
        int minHeight = 0;
        for (KeyValuePair keyValuePair : videoConstraints.mandatory) {
            if (keyValuePair.getKey().equals("minWidth")) {
                try {
                    minWidth = Integer.parseInt(keyValuePair.getValue());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Can not parse video width from video constraints");
                }
            } else if (keyValuePair.getKey().equals("minHeight")) {
                try {
                    minHeight = Integer.parseInt(keyValuePair.getValue());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Can not parse video height from video constraints");
                }
            }
        }
        if (minWidth * minHeight >= 1280 * 720) {
            return true;
        } else {
            return false;
        }
    }


    int QosStatusCount = 0;

    private void getStats() throws Exception {
        if (peerConnection == null || isError) {
            Log.e(TAG, "peerConnection is null" + peerConnection + ";isError=" + isError);
            return;
        }

        AudioTrack audioTrack = null;
        if (mediaStream != null && mediaStream.audioTracks.size() > 0) {//always have one.
            audioTrack = mediaStream.audioTracks.get(0);
            Log.i(TAG, "getStats mediaStream audioTracks  size:" + mediaStream.audioTracks.size());

            Log.i(TAG, "getStats mediaStream audioTrack[0]:" + audioTrack.id() + ";kind=" + audioTrack.kind());
        }

        boolean success = peerConnection.getStats(new StatsObserver() {
            @Override
            public void onComplete(final StatsReport[] reports) {
                if (events != null) {
                    events.onPeerConnectionStatsReady(reports);
                    if (reports != null && reports.length > 0) {
                        try {
                            boolean getGoogCandidatePair = false;
                            for (int i = 0; i < reports.length; i++) {
                                if (BuildConfig.DEBUG) {
                                    Log.i(TAG, "getStats events onComplete  info:" + reports[i].toString());
                                }

                                StatsReport statsReport = reports[i];


                                if (!getGoogCandidatePair && "googCandidatePair".equals(statsReport.type)) {

                                    StatsReport.Value[] values = statsReport.values;
                                    for (int j = 0; j < values.length; j++) {
                                        if ("googActiveConnection".equals(values[j].name) && "true".equals(values[j].value)) {
                                            getGoogCandidatePair = true;
                                            String candidateInfo = statsReport.toString();
                                            Log.i(TAG, "getStats googCandidatePair status  info:" + candidateInfo);
                                            CallApi.sendCallQosBroadcast(CallApi.ICE_TAG, candidateInfo, CallApi.QOS_QUALITY_INVALID);
                                            break;
                                        }
                                    }


                                }

                                if ("ssrc".equals(statsReport.type)) {
                                    StatsReport.Value[] values = statsReport.values;
                                    for (int j = 0; j < values.length; j++) {
                                        if ("googExpandRate".equals(values[j].name)) {
                                            QosStatusCount++;
                                            if (QosStatusCount >= 5) {
                                                QosStatusCount = 0;
                                                float status = Float.parseFloat(values[j].value);
                                                Log.i(TAG, "getStats events status  info:" + status);
                                                CallApi.sendCallQosBroadcast(CallApi.ICE_TAG, null, (int) status);
                                            }
                                            break;
                                        }
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "getStats events error :" + android.util.Log.getStackTraceString(e));
                        }
                    } else {
                        Log.i(TAG, "getStats events onComplete  null");
                    }

                }
            }
        }, null);

        Log.i(TAG, "getStats success: " + success + ";events=" + events);
        if (!success) {
            Log.e(TAG, "getStats() returns false!");
        }
    }

    static Timer closeCallTimer;

    static void cancelCloseCallTimer() {
        try {
            if (closeCallTimer != null) {
                closeCallTimer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initCallCloseTimer() {

        cancelCloseCallTimer();
        closeCallTimer = new Timer();
        try {
            closeCallTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (closeCallId != -1 && CallApi.getCurrentCallId() == closeCallId) {
                        CallSession callSession = CallApi.getFgCallSession();
                        if (callSession != null) {
                            rtcapij.netrtc_call_hangup_withcode(CallApi.getCurrentCallId(), 704);
                            Log.i(TAG, "stop call timer,caused by 15 sec no connected,the call id is:" + closeCallId);
                            closeCallId = -1;
                        } else {
                            Log.i(TAG, "ignore stop call timer,cause the :" + closeCallId);
                        }

                    }
                }
            }, 15 * 1000);
        } catch (Exception e) {
            Log.e(TAG, "Can not schedule CloseCallTimer(); timer", e);
        }
    }


    public void enableStatsEvents(boolean enable, int periodMs) {
        Log.i(TAG, "enableStatsEvents~~~~~~~~~~~~");
        try {
            if (statsTimer != null) {
                statsTimer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (enable) {
            statsTimer = new Timer();
            try {
                statsTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    getStats();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "get status error," + android.util.Log.getStackTraceString(e));
                                }
                            }
                        });
                    }
                }, 0, periodMs);
            } catch (Exception e) {
                Log.e(TAG, "Can not schedule statistics timer", e);
            }
        } else {
            try {
                statsTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setVideoEnabled(final boolean enable) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                renderVideo = enable;
                if (localVideoTrack != null) {
                    localVideoTrack.setEnabled(renderVideo);
                }
                if (remoteVideoTrack != null) {
                    remoteVideoTrack.setEnabled(renderVideo);
                }
            }
        });
    }


    public void createOffer() {
        iceCheckCount = 0;
        isICECompleted = false;
        isEnd = false;

        executor.execute(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "peerConnection : " + peerConnection);
                if (peerConnection != null && !isError) {
                    Log.d(TAG, "PC Create OFFER");
                    isInitiator = true;
                    isOutGoingCall = true;
                    peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
                }
            }
        });
        checkICEDelayHander("createOffer");
    }

    private void checkICEDelayHander(final String tag) {


        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                String newSDP = newSDP();
                Log.i(TAG, "handler " + tag + " isICECompleted : " + isICECompleted + " isEnd : " + isEnd + ";iceCheckCount==" + iceCheckCount + "\n" + "sdp:" + newSDP);

                if (!isICECompleted && !isEnd) {

                    if (!TextUtils.isEmpty(newSDP) && newSDP.contains(CallApi.ICE_CANDIDATE_TAG)) {
                        callProcess(newSDP);
                        isICECompleted = true;
                    } else {

                        iceCheckCount++;

                        if (iceCheckCount >= getMaxICERetryCount()) {
                            if (!videoCallEnabled) {//audio call
                                callProcess("");//go by non ice
                            } else {
                                callProcess("");//end try
                            }
                           //
                        } else {

                            checkICEDelayHander(tag);

                        }
                    }

                } else {
                    if (isICECompleted || isEnd) {
                        Log.i(TAG, "completed or is end handler " + tag + " isICECompleted : " + isICECompleted + " isEnd : " + isEnd + ";iceCheckCount==" + iceCheckCount);
                        return;
                    }
                    iceCheckCount++;
                    if (iceCheckCount < getMaxICERetryCount()) {
                        checkICEDelayHander(tag);
                    }
                }

            }
        }, ICE_TIMEOUT_TIME);
    }


    public void createAnswer() {

        iceCheckCount = 0;

        isICECompleted = false;

        isOutGoingCall = false;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "handler createAnswer peerConnection is null : " + (peerConnection == null) + " isError : " + isError);
                if (peerConnection != null && !isError) {

                    Log.d(TAG, "PC create ANSWER");
                    isInitiator = false;

                    peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);

                }
            }
        });

        checkICEDelayHander("createAnswer");

    }

    public void addRemoteIceCandidate(final IceCandidate candidate) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (peerConnection != null && !isError) {
                    if (queuedRemoteCandidates != null) {
                        queuedRemoteCandidates.add(candidate);
                    } else {
                        peerConnection.addIceCandidate(candidate);
                    }
                }
            }
        });
    }

    public void setRemoteDescription(final SessionDescription sdp) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (peerConnection == null || isError) {
                    return;
                }
                String sdpDescription = sdp.description;
                if (preferIsac) {
                    sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
                }
                if (videoCallEnabled && preferH264) {
                    sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264, false);
                }
                int startVidieoBitrate = 1000;
                if (videoCallEnabled) {
                    sdpDescription = setStartBitrate(VIDEO_CODEC_VP8, true,
                            sdpDescription, startVidieoBitrate);
                    sdpDescription = setStartBitrate(VIDEO_CODEC_VP9, true,
                            sdpDescription, startVidieoBitrate);
                    sdpDescription = setStartBitrate(VIDEO_CODEC_H264, true,
                            sdpDescription, startVidieoBitrate);
                }
                int startAudioBitrate = 32;
                sdpDescription = setStartBitrate(AUDIO_CODEC_OPUS, false,
                        sdpDescription, startAudioBitrate);

                Log.d(TAG, "Set remote SDP.");
                SessionDescription sdpRemote = new SessionDescription(
                        sdp.type, sdpDescription);
                if (peerConnection!=null){
                    peerConnection.setRemoteDescription(sdpObserver, sdpRemote);
                }else{
                    Log.d(TAG, "peerConnection is null ,can not Set remote SDP.");
                }

            }
        });
    }




    public void stopVideoSource() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (videoSource != null && !videoSourceStopped) {
                    Log.d(TAG, "Stop video source.");
                    videoSource.stop();
                    videoSourceStopped = true;
                }
            }
        });
    }

    public void startVideoSource() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (videoSource != null && videoSourceStopped) {
                    Log.d(TAG, "Restart video source.");
                    videoSource.restart();
                    videoSourceStopped = false;
                }
            }
        });
    }

    private void reportError(final String errorMessage) {
        Log.e(TAG, "Peerconnection error: " + errorMessage);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    if (events != null)
                        events.onPeerConnectionError(errorMessage);
                    isError = true;
                }
            }
        });
    }

    private VideoTrack createVideoTrack(VideoCapturerAndroid capturer) {

        Log.i(TAG, "~ createVideoTrack ~");
        videoSource = factory.createVideoSource(capturer, videoConstraints);

        localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(renderVideo);
        localVideoTrack.addRenderer(new VideoRenderer(localRender));
        return localVideoTrack;
    }

    private static String setStartBitrate(String codec, boolean isVideoCodec,
                                          String sdpDescription, int bitrateKbps) {
        String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap
                + " at " + lines[rtpmapLineIndex]);

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                Log.d(TAG, "Found " + codec + " " + lines[i]);
                if (isVideoCodec) {
                    lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE
                            + "=" + bitrateKbps;
                } else {
                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE
                            + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }

        StringBuilder newSdpDescription = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            newSdpDescription.append(lines[i]).append("\r\n");
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " "
                            + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " "
                            + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }

        }
        return newSdpDescription.toString();
    }

    private static String preferCodec(
            String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length)
                && (mLineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                continue;
            }
        }
        if (mLineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at "
                + lines[mLineIndex]);
        String[] origMLineParts = lines[mLineIndex].split(" ");
        if (origMLineParts.length > 3) {
            StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            for (; origPartIndex < origMLineParts.length; origPartIndex++) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
            }
            lines[mLineIndex] = newMLine.toString();
            Log.d(TAG, "Change media description: " + lines[mLineIndex]);
        } else {
            Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
        }
        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }

    private void drainCandidates() {
        try {
            if (queuedRemoteCandidates != null) {
                Log.d(TAG, "Add " + queuedRemoteCandidates.size() + " remote candidates");
                for (IceCandidate candidate : queuedRemoteCandidates) {
                    peerConnection.addIceCandidate(candidate);
                }
                queuedRemoteCandidates = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "drainCandidates error:" + android.util.Log.getStackTraceString(e));
        }
    }

    private void switchCameraInternal() {
        if (!videoCallEnabled || numberOfCameras < 2 || isError || videoCapturer == null) {
            Log.e(TAG, "Failed to switch camera. Video: " + videoCallEnabled + ". Error : "
                    + isError + ". Number of cameras: " + numberOfCameras);
            return;  // No video is sent or only one camera is available or error happened.
        }
        Log.d(TAG, "Switch camera");
        videoCapturer.switchCamera(null);
    }

    public void switchCamera() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                switchCameraInternal();
            }
        });
    }

    public void changeCaptureFormat(final int width, final int height, final int framerate) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                changeCaptureFormatInternal(width, height, framerate);
            }
        });
    }


    private void changeCaptureFormatInternal(int width, int height, int framerate) {
        if (!videoCallEnabled || isError || videoCapturer == null) {
            Log.e(TAG, "Failed to change capture format. Video: " + videoCallEnabled + ". Error : "
                    + isError);
            return;
        }
        videoCapturer.onOutputFormatRequest(width, height, framerate);
    }


    public static long closeCallId = -1;

    public static void closeCallId() {
        closeCallId = -1;
        cancelCloseCallTimer();

    }

    // Implementation detail: observe ICE & stream changes and react accordingly.
    private class PCObserver implements PeerConnection.Observer {
        @Override
        public void onIceCandidate(final IceCandidate candidate) {


            Log.i(TAG, "collect ice candidate : " + candidate.sdp);

            if (!localCandidates.toString().contains(candidate.sdp)) {

                localCandidates.append("a=").append(candidate.sdp).append("\r\n");

            }
        }

        @Override
        public void onSignalingChange(
                PeerConnection.SignalingState newState) {
            Log.d(TAG, "SignalingState: " + newState);
        }

        @Override
        public void onIceConnectionChange(
                final IceConnectionState newState) {
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    Log.i(TAG, "IceConnectionState: " + newState);

                    if (newState == IceConnectionState.CONNECTED) {

                        enableStatsEvents(true, 1000);

                        closeCallId();//maybe reconnected.
                        CallSession callSession = CallApi.getFgCallSession();
                        if (callSession == null) {
                            Log.i(TAG, "the call sesson not exsit ,maybe hangup by user, " + newState);
                            close(null);
                            return;
                        }


                        callSession.callState = CallSession.SATTUS_ICE_CONNECTED;

                        CallApi.sendCallStatusBroadcast(callSession);


                    } else if (newState == IceConnectionState.DISCONNECTED) {

                        if (CallApi.getCurrentCallId() != -1) {

                            Log.i(TAG, "ice disconnect hang up current call id: " + CallApi.getCurrentCallId());
                            CallSession callSession = CallApi.getCallSessionById(CallApi.getCurrentCallId());
                            if (callSession != null && (callSession.callState == CallSession.SATTUS_ICE_CONNECTED
                                    || callSession.callState == CallSession.STATUS_CONNECTED
                                    || callSession.callState == CallSession.STATUS_RECONNECTING
                            )) {
                                Log.i(TAG, "try recall ,ignore hangup :" + CallApi.getCurrentCallId());
                                //release the peerinfo.
                                if (callSession.callState != CallSession.STATUS_RECONNECTING) {
                                    Log.i(TAG, "release the ice peerinfo:" + CallApi.getCurrentCallId());
                                    close();
                                }

                            } else {
                                Log.i(TAG, "hangup call :" + CallApi.getCurrentCallId() + ";call status:" + callSession.callState);
                                rtcapij.netrtc_call_hangup_withcode(CallApi.getCurrentCallId(), 704);
                            }

                        }

                    } else if (newState == IceConnectionState.FAILED) {
                        reportError("ICE connection failed.");
                    } else if (newState == IceConnectionState.CHECKING) {

                    } else if (newState == IceConnectionState.CLOSED) {
                        if (CallApi.getCurrentCallId() != -1) {
                            closeCallId = CallApi.getCurrentCallId();
                            Log.i(TAG, "call will be close in 10 sec,callid:" + closeCallId);
                            initCallCloseTimer();
                        }
                    }
                }
            });
        }

        @Override
        public void onIceGatheringChange(IceGatheringState newState) {

            Log.d(TAG, "IceGatheringState: " + newState + " is end : " + isEnd);

            Log.i(TAG, "localCandidates : " + localCandidates);


            if (newState == IceGatheringState.COMPLETE) {

                if (!isICECompleted && !isEnd) {
                    String newSDP = newSDP();
                    if (!TextUtils.isEmpty(newSDP) && newSDP.contains(CallApi.ICE_CANDIDATE_TAG)) {
                        handler.removeCallbacksAndMessages(null);
                        isICECompleted = true;
                        Log.i(TAG, "onIceGatheringChange COMPLETE: " + newSDP);
                        callProcess(newSDP);

                    } else {
                        Log.i(TAG, "localCandidates not contain candidate: " + newSDP);
                    }

                } else if (isEnd) {

                    close();

                }
            }
        }


        @Override
        public void onAddStream(final MediaStream stream) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection == null || isError) {
                        return;
                    }
                    if (stream.audioTracks.size() > 1 || stream.videoTracks.size() > 1) {
                        reportError("Weird-looking stream: " + stream);
                        return;
                    }

                    Log.i(TAG, "onAddStream  audio track size:" + stream.audioTracks.size());
                    if (stream.audioTracks.size() == 1) {
                        Log.i(TAG, "onAddStream  audio track id:" + stream.audioTracks.get(0).id());
                    }

                    if (stream.videoTracks.size() == 1) {
                        remoteVideoTrack = stream.videoTracks.get(0);
                        remoteVideoTrack.setEnabled(renderVideo);
                        remoteVideoTrack.addRenderer(new VideoRenderer(remoteRender));
                    }
                }
            });
        }

        @Override
        public void onRemoveStream(final MediaStream stream) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection == null || isError) {
                        return;
                    }
                    Log.i(TAG, "onRemoveStream");

                    remoteVideoTrack = null;
                    stream.videoTracks.get(0).dispose();
                }
            });
        }

        @Override
        public void onDataChannel(final DataChannel dc) {
            reportError("AppRTC doesn't use data channels, but got: " + dc.label()
                    + " anyway!");
        }

        @Override
        public void onRenegotiationNeeded() {
            // No need to do anything; AppRTC follows a pre-agreed-upon
            // signaling/negotiation protocol.
        }

        @Override
        public void onIceConnectionReceivingChange(boolean receiving) {
            // TODO Auto-generated method stub

        }
    }

    // Implementation detail: handle offer creation/signaling and answer setting,
    // as well as adding remote ICE candidates once the answer SDP is set.
    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(final SessionDescription origSdp) {
            if (localSdp != null) {
                reportError("Multiple SDP create.");
                return;
            }

            Log.d(TAG, "sdp create success");
            originSdp = new StringBuffer(origSdp.description);

            String sdpDescription = origSdp.description;
            if (preferIsac) {
                sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
            }
            if (videoCallEnabled && preferH264) {
                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264, false);
            }
            final SessionDescription sdp = new SessionDescription(origSdp.type, sdpDescription);
            localSdp = sdp;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection != null && !isError) {

                        Log.d(TAG, "Set local SDP from " + sdp.type);

                        String origin_sdp = "";

                        if (sdp.description.contains(CallApi.VIDEO_TAG)) {

                            if (!isVideoCallEnabled()) {

                                origin_sdp = sdp.description.substring(0, sdp.description.indexOf(CallApi.VIDEO_TAG));

                            } else {

                                origin_sdp = sdp.description;
                            }

                        } else {

                            origin_sdp = sdp.description;
                        }
                        final SessionDescription sdp = new SessionDescription(origSdp.type, origin_sdp);

                        Log.i(TAG, "generate sdp :" + origin_sdp);

                        peerConnection.setLocalDescription(sdpObserver, sdp);
                    }
                }
            });
        }

        @Override
        public void onSetSuccess() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection == null || isError) {
                        return;
                    }
                    if (isInitiator) {
                        // For offering peer connection we first create offer and set
                        // local SDP, then after receiving answer set remote SDP.
                        if (peerConnection.getRemoteDescription() == null) {
                            // We've just set our local SDP so time to send it.


                            Log.d(TAG, "Local SDP set succesfully");

                        } else {
                            // We've just set remote description, so drain remote
                            // and send local ICE candidates.
                            Log.d(TAG, "Remote SDP set succesfully");
                            drainCandidates();
                        }
                    } else {
                        // For answering peer connection we set remote SDP and then
                        // create answer and set local SDP.
                        if (peerConnection.getLocalDescription() != null) {
                            // We've just set our local SDP so time to send it, drain
                            // remote and send local ICE candidates.
                            Log.d(TAG, "Local SDP set succesfully");

                            drainCandidates();
                        } else {
                            // We've just set remote SDP - do nothing for now -
                            // answer will be created soon.
                            Log.d(TAG, "Remote SDP set succesfully");
                        }
                    }
                }
            });
        }

        @Override
        public void onCreateFailure(final String error) {
            reportError("createSDP error: " + error);
        }

        @Override
        public void onSetFailure(final String error) {
            reportError("setSDP error: " + error);
        }
    }
}
