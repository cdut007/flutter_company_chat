package com.ultralinked.voip.api;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;

import java.io.IOException;

/**
 * Created by yongjun on 4/26/16.
 */
public class VoiceRecord {

    private VoiceRecord(Context context){
        mediaRecorder = null;
        mContext = context;
    }


    public static VoiceRecord getInstance(Context context){
        if (null == _VoiceRecord){
            _VoiceRecord = new VoiceRecord(context);
        }
        return  _VoiceRecord;
    }


    public static  void abandonAudioFocus(Context ctx){
        if (ctx == null){
            Log.i("VoiceRecord","abandonAudioFocus context is null");

        }else{
            Log.i("VoiceRecord","abandonAudioFocus");
            AudioManager audioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.abandonAudioFocus(null);
        }
    }

    public static  void requestAudioFocus(Context ctx){
        if (ctx == null){
            Log.i("VoiceRecord","requestAudioFocus context is null");

        }else{
            Log.i("VoiceRecord","requestAudioFocus");
            AudioManager audioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.requestAudioFocus(null, AudioManager.STREAM_RING, AudioManager.STREAM_RING);
            audioManager.setMicrophoneMute(false);
        }
    }



    public boolean startRecord(String pcFileName) {
        Log.i(TAG, "start record voice..");
        if (null == pcFileName) {
            Log.i(TAG, "ptt filename == null");
            return false;
        }
        stopAndRelease();
        requestAudioFocus(mContext);
        mediaRecorder = new MediaRecorder();
        if (null == mediaRecorder) {
            Log.i(TAG, "media recorder == null");
            return false;
        }
        mediaRecorder
                .setOnErrorListener(new MediaRecorder.OnErrorListener() {

                    public void onError(MediaRecorder mr, int what, int extra) {
                        Log.i(TAG, "onErrorListener start..");
                        stopAndRelease();
                    }

                });
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP){
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }else{
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
        mediaRecorder.setOutputFile(pcFileName);
        Log.i(TAG, (new StringBuilder())
                .append("record preparing .. fileName = ").append(pcFileName)
                .toString());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.i(TAG, "record prepare error illegalstate.");
            Log.i(TAG, e.toString());
            stopAndRelease();
            return false;
        } catch (IOException e) {
            Log.i(TAG, "record prepare error ioexception.");
            Log.i(TAG, e.toString());
            stopAndRelease();
            return false;
        }
        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.i(TAG, "record start error ioexception.");
            Log.i(TAG, e.toString());
            abandonAudioFocus(mContext);
        }
        Log.i(TAG, "start record ptt over..");
        return true;
    }

    public static boolean canPlayVoice(String pcFileName) {
        //if sdcard not exsit.
        return false;
    }

    public void stop() {
        stopAndRelease();
    }

    private void stopAndRelease() {
        if (null != mediaRecorder) {
            abandonAudioFocus(mContext);
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                Log.i(TAG,"stop or release error");
                Log.i(TAG, e.toString());
            }
            mediaRecorder = null;
        }
    }

    public void stopAndDelete(String recordFilePath) {
        stopAndRelease();
        //deleteFile(recordFilePath);
    }

    public int getMaxAmplitude() {
        return mediaRecorder != null ? mediaRecorder.getMaxAmplitude() : -1;
    }

    private static final String TAG = "VoiceRecord";
    private Context mContext;
    private static VoiceRecord _VoiceRecord;
    private MediaRecorder mediaRecorder;


}
