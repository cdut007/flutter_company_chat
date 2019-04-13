package com.ultralinked.voip.api;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;

import java.lang.reflect.Field;

;


public class MediaManger {
    private Context context;
    private MediaPlayer mediaPlayer;
    public static String TAG = "MediaManger";
    private boolean isLocalmediaPlaying, isIncomingAlertPlaying;
    public AudioManager mAudioManager;
    private Ringtone ringtone;


    private MediaManger() {


    }

    private MediaManger(Context context) {

        this.context = context;

        mAudioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));

        ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

    }

    private static MediaManger instance;


    public synchronized static MediaManger getInstance(Context context) {

        if (instance == null) {

            instance = new MediaManger(context);
        }
        return instance;
    }

    /**
     * play the alarm ring when a incoming call
     */
    public void playAlarmRing() {
        isIncomingAlertPlaying = true;
        // Request audio focus before making any device switch.
        mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        mAudioManager.setMode(AudioManager.MODE_RINGTONE);

        mAudioManager.setSpeakerphoneOn(true);

        if (ringtone != null && !ringtone.isPlaying()) {
            setRingtoneRepeat(ringtone);//设置重复提醒
            ringtone.play();


        } else {

            Log.d(TAG, "Already ringing ....");
        }
    }

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.i(TAG, "------------->AudioManage changed=" + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                CallSession callSession = CallApi.getFgCallSession();

                if (callSession != null && callSession.callState == CallSession.STATUS_CONNECTED) {
                    //					savedIsSpeakerPhoneOn = audioManager.isSpeakerphoneOn();
                    //					callSession.hold();
                    //					EventBus.getDefault().postSticky(new AudioLostFocusEvent());

                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                CallSession callSession = CallApi.getFgCallSession();
                if (callSession != null && callSession.callState == CallSession.STATUS_CONNECTED) {
                    //					callSession.unHold();
                    //					setSpeakerphoneOn(savedIsSpeakerPhoneOn);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

                //				audioManager.abandonAudioFocus(audioFocusChangeListener);
                // Stop playback
            }


        }
    };

    //反射设置闹铃重复播放
    private void setRingtoneRepeat(Ringtone ringtone) {
        Class<Ringtone> clazz = Ringtone.class;
        try {
            Field field = clazz.getDeclaredField("mLocalPlayer");//返回一个 Field 对象，它反映此 Class 对象所表示的类或接口的指定公共成员字段（※这里要进源码查看属性字段）
            field.setAccessible(true);
            MediaPlayer target = (MediaPlayer) field.get(ringtone);//返回指定对象上此 Field 表示的字段的值
            if (target != null) {
                target.setLooping(true);//设置循环
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void stopAlarmRing() {
        stopAlarmRing(true);
    }

    /**
     * stop play the alarm ring when accept or reject the incoming call
     */
    public void stopAlarmRing(boolean endCall) {
        if (!isIncomingAlertPlaying) {
            if (endCall) {
                try {

                    mAudioManager.abandonAudioFocus(audioFocusChangeListener);
                    mAudioManager.setSpeakerphoneOn(false);
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "~ endcall stopAlarmRing ~" + e.getLocalizedMessage());
                }
                return;
            }
        }

        try {
            isIncomingAlertPlaying = false;
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);
            mAudioManager.setSpeakerphoneOn(false);

            if (endCall) {
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
            }

            if (ringtone != null && ringtone.isPlaying()) {

                ringtone.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "~ stopAlarmRing ~" + e.getLocalizedMessage());
        }


    }


    SoundPool soundPool;

    public void playLocalEarlyMedia(int resId) {

        Log.i(TAG, "~ playLocalEarlyMedia ~");


        if (!isLocalmediaPlaying) {

            // Request audio focus before making any device switch.

            if (resId == 0) {

                return;
            }


            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

            soundPool = new SoundPool(10, AudioManager.STREAM_VOICE_CALL, 5);

            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                    if (soundPool != null) {
                        soundPool.play(1, 1, 1, 999999999, -1, 1);
                    }
                    Log.i(TAG, "audioManager.isSpeakerphoneOn(): " + mAudioManager.isSpeakerphoneOn());
                }
            });
            soundPool.load(context, resId, 1);
            isLocalmediaPlaying = true;


        }

    }


    public void playLocalEarlyMedia() {
        int id = context.getResources().getIdentifier("ringout", "raw", context.getPackageName());
        playLocalEarlyMedia(id);
    }


    public void stopLocalEarlyMedia() {
        stopLocalEarlyMedia(true);
    }

    public void stopLocalEarlyMedia(boolean endCall) {

        Log.i(TAG, "~ stopLocalEarlyMedia ~");

        if (isLocalmediaPlaying) {
            try {


                mAudioManager.abandonAudioFocus(null);

                isLocalmediaPlaying = false;

                if (soundPool != null) {
                    soundPool.release();
                    soundPool = null;
                }

                if (endCall) {
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "~ stopLocalEarlyMedia ~" + e.getLocalizedMessage());
            }

        }

    }
}
