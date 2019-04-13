package com.ultralinked.voip.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LAIWENJIE$ on 2015/11/19.
 */
public class VideoMessage extends FileMessage {


    public static final String TAG="VideoMessage";

    private  int duration;//sec

    public int getDuration() {
        return duration;
    }



    /**
     * get the origin image of the image message
     * @return origin bitmap of image message
     */
    public Bitmap getImage() {

        Log.i(TAG,"getImage from path : "+getFilePath());

        return BitmapUtils.loadBitmap(0, getFilePath());
    }

    public static int getVideoDuration(Context context, String file) {
        int duration = 0;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(file);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Integer.parseInt(durationStr) / 1000;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(TAG, "parse video during time is error: video path=" + file);
        }

        retriever.release();

        return duration;
    }


    public static String getVideoInfoJson(String videoUrl, String fileName, String thumbPath, String thumbUrl, Options options) {


        try {
            JSONObject jsonObject = FileMessage.getFileJsonObject(videoUrl,fileName);
            jsonObject.put("duration", getVideoDuration(MessagingApi.mContext,videoUrl));
            if (options == null){
                options = new Options();
            }
            options.thumbPath = thumbPath;
            return getFormatMessageJson(TAG,jsonObject,thumbUrl, Message.MESSAGE_TYPE_VIDEO, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getVideoInfoJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }


    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        //parse during ....
        JSONObject data = getJsonData();
        duration = data.optInt("duration");
    }
}
