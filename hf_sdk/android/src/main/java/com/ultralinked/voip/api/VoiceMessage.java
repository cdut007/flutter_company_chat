package com.ultralinked.voip.api;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ultralinked.voip.api.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by LAIWENJIE$ on 2015/11/19.
 * yongjun add play function on 2016/04/28
 */
public class VoiceMessage extends FileMessage {

    public static final String TAG="VoiceMessage";


    public  boolean isPlayed;

    public static String getVoiceInfoJson(String voiceUrl, int during, String fileName, Options options) {


        try {
            JSONObject jsonObject = FileMessage.getFileJsonObject(voiceUrl,fileName);

                jsonObject.put("during", during);
            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_VOICE, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getVoiceInfoJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }


    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        //parse during ....
        JSONObject data = getJsonData();
        during = data.optInt("during");
        isPlayed = data.optBoolean("isPlayed");

    }

    private  int during;//sec

    public int getDuration() {
        return during;
    }


    public boolean seek(int currentPositon) {
        Log.i(TAG, "playStop() keyid:" + getKeyId());
        if (null == mPlayVoice) {
            mPlayVoice = new PlayVoice();
        }
        boolean flag = true;
        if (isPlaying()) {//
            mPlayVoice.seek(currentPositon);
            return flag;
        }

        if (isCurrentPlaying()) {
            // not allow move
            return false;
        }

        mPlayVoice.stop();

        if (!FileUtils.isFileExist(getFilePath()))
            flag = false;
        else {

            mPlayVoice.start(getFilePath(), currentPositon, getKeyId(),
                    false);
            mPlayVoice.setDuration(getDuration() * 1000);
        }
//        if (!msg.isRead()) {
//            msg.read();
//        }
        return flag;
    }

    public interface OnEndPlay{

        public void finish(int msgId, int reason);
    }


    private void setBodyPlayed() throws Exception {
        isPlayed = true;
        String body = getBody();
        JSONObject jsonObject = new JSONObject(body);
        parseData(jsonObject);
        JSONObject data = getJsonData().put("isPlayed",true);
        jsonObject.put(DATA, data);
        MessagingApi.updateMessageBody(getKeyId(),getChatType(),jsonObject.toString());
    }

    transient OnEndPlay mOnEndPlay;
    public boolean playOrStop(OnEndPlay endPlay) {
        mOnEndPlay = endPlay;
        Log.i(TAG, "playStop() keyid:" + getKeyId());
        if (null == mPlayVoice) {
            mPlayVoice = new PlayVoice();
        }
        boolean flag = true;
        if (isPlaying()) {
            mPlayVoice.stop();

        } else {
            if (HasCurrentPlayingID()) {
                mPlayVoice.stop();
            }

            if (TextUtils.isEmpty(getFileName())
                    || !FileUtils.isFileExist(getFilePath()))
                flag = false;
            else {

                mPlayVoice.start(getFilePath(), 0,
                        getKeyId(), true);
                mPlayVoice.setDuration(getDuration() * 1000);

                if (!isSender() && !isPlayed){//received.
                    try {
                        setBodyPlayed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }
//        if (!msg.isRead()) {
//            msg.read();
//        }
        return flag;
    }

    public boolean isPlaying() {
        return mPlayingId == getKeyId();
    }




    public int getDuring() {
        return during;
    }

    public void setDuring(int during) {
        this.during = during;
    }




    public static final int PLAY_STATUS_START = 1;
    public static final int PLAY_STATUS_STOP = 2;
    public static final int PLAY_STATUS_COMPLETION = 3;

    private transient static PlayVoice mPlayVoice = null;
    private static long mPlayingId = -1L;


    public interface RefreshListener {
        void refresh(int pos);
    }






    private static boolean HasCurrentPlayingID() {
        if ((null != mPlayVoice) && (mPlayingId != -1L)) {
            return true;

        }
        return false;
    }

    public static boolean isCurrentPlaying() {
        if ((null != mPlayVoice) && (mPlayingId != -1L)) {
            try {
                return mPlayVoice.isPlaying();
            } catch (IllegalStateException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
        return false;
    }



    public static long getCurrentPlayingId() {
        // TODO Auto-generated method stub
        return mPlayingId;
    }

    public static long getCurrentPlayingDuring() {
        if ((null != mPlayVoice) && (mPlayingId != -1L)) {
            return mPlayVoice.getCurrentPlayingDuring();
        }
        return 0;
    }



    public static void stopCurrentVoice() {
        if ((null != mPlayVoice) && (mPlayingId != -1L))
            mPlayVoice.stop();
    }

    public static boolean pauseOrResume(int pos) {
        if ((null != mPlayVoice) && (mPlayingId != -1L))
            return mPlayVoice.pauseOrResume(pos);
        return true;
    }

    public static void pause() {
        if ((null != mPlayVoice) && (mPlayingId != -1L))
            mPlayVoice.pause();
    }

    public static void resume() {
        if ((null != mPlayVoice) && (mPlayingId != -1L))
            mPlayVoice.goOn(-1);
    }

    public long refreshNow() {
        // if ((null != mPlayVoice) && (mPlayingId != -1L)) {
        // mPlayVoice.refreshNow();
        // }
        return 20;// fix
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void queueNextRefresh(long delay, Handler handler, int whatMsg) {
        if ((null != mPlayVoice) && (mPlayingId != -1L))
            mPlayVoice.queueNextRefresh(delay, handler, whatMsg);
    }

    private class PlayVoice {
        private MediaPlayer mediaPlayer = null;
        boolean mIsRequestAudioFocus = false;
        boolean mIsPause = false;
        final int delayAbandonTime = 500;
        final int WHAT_ABANDON_AUDIO_FOCUS = 1;
        Handler mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case WHAT_ABANDON_AUDIO_FOCUS:
                        Log.d(TAG, "PlayVoice WHAT_ABANDON_AUDIO_FOCUS");
                        VoiceRecord.abandonAudioFocus(MessagingApi.mContext);
                        mIsRequestAudioFocus = false;
                        break;

                }
            }
        };
        private long mDuration;

        public void setDuration(long mDuration) {
            this.mDuration = mDuration;
        }

        public boolean isPlaying() throws IllegalStateException {
            if (mediaPlayer == null)
                return false;
            return mediaPlayer.isPlaying();
        }

        public long getDuring() {
            if (mediaPlayer == null)
                return mDuration;
            return mediaPlayer.getDuration();
        }

        public long getCurrentPlayingDuring() {
            if (mediaPlayer == null)
                return 0;
            return mediaPlayer.getCurrentPosition();
        }

        public void queueNextRefresh(long delay, Handler handler, int whatMsg) {
            android.os.Message msg = handler.obtainMessage(whatMsg);
            handler.removeMessages(whatMsg);
            handler.sendMessageDelayed(msg, delay);

        }

        private long refreshNow() {
            if (mediaPlayer == null)
                return 500;

            long pos = mediaPlayer.getCurrentPosition();
            long remaining = 1000 - (pos % 1000);
            if ((pos >= 0 && pos <= mDuration) && (mDuration > 0)) {

                if (!mediaPlayer.isPlaying()) {
                    remaining = 500;
                }

            } else {
                pos = 0;

            }

            // return the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            return remaining;
        }

        private PlayVoice() {
        }

        public void pause() {
            if ((this.mediaPlayer != null) && (this.mediaPlayer.isPlaying())) {
                this.mediaPlayer.pause();
                this.mIsPause = true;
            }
        }

        public void seek(int pos) {
            if ((this.mediaPlayer != null)) {
                if (pos > -1) {
                    mediaPlayer.seekTo(pos);
                }
                Log.i(TAG, "seek() mPlayingId:" + VoiceMessage.mPlayingId);
                sendBroadcast(PLAY_STATUS_START, VoiceMessage.mPlayingId);
            }
        }

        public boolean pauseOrResume(int pos) {
            if ((this.mediaPlayer != null) && (this.mIsPause)) {
                goOn(pos);
                return false;
            } else {
                pause();
                return true;
            }

        }

        public void goOn(int msec) {
            if ((this.mediaPlayer != null) && (this.mIsPause)) {
                if (msec > -1) {
                    mediaPlayer.seekTo(msec);
                }
                this.mediaPlayer.start();
                this.mIsPause = false;
                Log.d(TAG, "resume() mPlayingId:" + VoiceMessage.mPlayingId);
                sendBroadcast(PLAY_STATUS_START, VoiceMessage.mPlayingId);
            }
        }

        public void stop() {
            if (null == this.mediaPlayer) {
                return;
            }

            Log.d(TAG, "PlayVoice.stop()");
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
            sendBroadcast(PLAY_STATUS_STOP, VoiceMessage.mPlayingId);
            setPlayingId(-1L);
            this.mHandler.sendEmptyMessageDelayed(PLAY_STATUS_START,
                    delayAbandonTime);
        }

        public boolean start(String pcFileName, int currentposition,
                             long keyId, boolean play) {
            Log.d(TAG, "PlayVoice.start()");
            if (null == pcFileName) {
                return false;
            }

            if (!new File(pcFileName).exists()) {
                return false;
            }

            if (null != this.mediaPlayer) {
                return false;
            }

            Log.d(TAG, "PlayVoice.start() pcFileName:" + pcFileName
                    + ", currentposition:" + currentposition
                    + ",mIsRequestAudioFocus:" + this.mIsRequestAudioFocus);

            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer
                    .setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            // TODO Auto-generated method stub

                        }
                    });

            this.mediaPlayer
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {

                            Log.d(TAG, "PlayVoice  MediaPlayer onCompletion()");
                            if (null != VoiceMessage.PlayVoice.this.mediaPlayer) {
                                VoiceMessage.PlayVoice.this.mediaPlayer
                                        .release();
                                VoiceMessage.PlayVoice.this.mediaPlayer = null;
                            }
                            VoiceMessage.PlayVoice.this.sendBroadcast(
                                    PLAY_STATUS_COMPLETION,
                                    VoiceMessage.mPlayingId);
                            VoiceMessage.PlayVoice.this.mHandler
                                    .sendEmptyMessageDelayed(
                                            WHAT_ABANDON_AUDIO_FOCUS,
                                            delayAbandonTime);
                            setPlayingId(-1L);
                        }
                    });
            this.mediaPlayer
                    .setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        public boolean onError(MediaPlayer mp, int what,
                                               int extra) {
                            Log.d(TAG, "PlayVoice  MediaPlayer onError()");

                            VoiceMessage.PlayVoice.this.mediaPlayer = null;
                            VoiceMessage.PlayVoice.this.sendBroadcast(
                                    PLAY_STATUS_STOP, VoiceMessage.mPlayingId);
                            VoiceMessage.PlayVoice.this.mHandler
                                    .sendEmptyMessageDelayed(
                                            WHAT_ABANDON_AUDIO_FOCUS,
                                            delayAbandonTime);
                            setPlayingId(-1L);
                            return false;
                        }
                    });
            try {
                this.mediaPlayer.setDataSource(pcFileName);
                this.mediaPlayer.prepare();
                this.mediaPlayer.seekTo(currentposition);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                VoiceMessage.PlayVoice.this.sendBroadcast(PLAY_STATUS_STOP,
                        keyId);
                setPlayingId(-1L);
                VoiceMessage.PlayVoice.this.mHandler.sendEmptyMessageDelayed(
                        WHAT_ABANDON_AUDIO_FOCUS, delayAbandonTime);
                return false;
            } catch (IllegalStateException e) {
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                e.printStackTrace();
                VoiceMessage.PlayVoice.this.sendBroadcast(PLAY_STATUS_STOP,
                        keyId);
                setPlayingId(-1L);
                VoiceMessage.PlayVoice.this.mHandler.sendEmptyMessageDelayed(
                        WHAT_ABANDON_AUDIO_FOCUS, delayAbandonTime);
                return false;
            } catch (IOException e) {
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                e.printStackTrace();
                Log.i(TAG, "io error msgId =" + keyId);
                VoiceMessage.PlayVoice.this.sendBroadcast(PLAY_STATUS_STOP,
                        keyId);
                setPlayingId(-1L);
                VoiceMessage.PlayVoice.this.mHandler.sendEmptyMessageDelayed(
                        WHAT_ABANDON_AUDIO_FOCUS, delayAbandonTime);
                return false;
            }

            if (play) {
                this.mIsPause = false;
                this.mediaPlayer.start();

            } else {
                this.mIsPause = true;
            }

            setPlayingId(keyId);
            Log.d(TAG, "start() mPlayingId:" + VoiceMessage.mPlayingId);
            sendBroadcast(PLAY_STATUS_START, VoiceMessage.mPlayingId);

            this.mHandler.removeMessages(WHAT_ABANDON_AUDIO_FOCUS);
            if (!this.mIsRequestAudioFocus) {
                VoiceRecord.requestAudioFocus(MessagingApi.mContext);
                this.mIsRequestAudioFocus = true;
            }
            return true;
        }

        private void sendBroadcast(int playStatus, long msgId) {

                if (mOnEndPlay!=null){
                    mOnEndPlay.finish((int)msgId,playStatus);
                }

            Intent intent = new Intent(
                    "com.uluc.message.EVENT_VOICE_PLAY");
            intent.putExtra("status", playStatus);
            intent.putExtra("message_id", msgId);
            LocalBroadcastManager.getInstance(MessagingApi.mContext).sendBroadcast(intent);
        }
    }

    public static void setPlayingId(long keyId) {
        mPlayingId = keyId;

    }

    public  int getRetainPlayingTime() {
        int retainInMsec =  (int) (getDuring() - VoiceMessage
                .getCurrentPlayingDuring()) ;
        if (retainInMsec < 1000 && retainInMsec > 500) {//not reach 1 sec ,we set to 1 sec
            retainInMsec = 1000;
        }
        int retainTime =retainInMsec / 1000;
        if (retainTime < 0) {
            retainTime = 0;
        }

        return retainTime;
    }

    /**
     * @param secs
     *            makeTimeString(pos / 1000)
     * @return
     */
    public static String makeTimeString(long secs) {

        long minute = secs / 60;
        long sec = (secs) % 60;
        String secString = sec < 10 ? "0" + sec : sec + "";
        String minString = minute < 10 ? "0" + minute : minute + "";
        return minString + ":" + secString;

    }

    int playPosition;
}
