package com.ultralinked.voip.api;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ultralinked.
 */
public class StickerMessage extends FileMessage {

    public static final String TAG="StickerMessage";



    public static String getStickerInfoJson(String imageUrl, String stickerName, String fileName, String thumbPath, String thumbUrl, Options options) {


        try {
            JSONObject jsonObject = FileMessage.getFileJsonObject(imageUrl,fileName);
            if (options == null){
                options = new Options();
            }
            options.thumbPath = thumbPath;
            jsonObject.put("sitckerNmae", stickerName);
            return getFormatMessageJson(TAG,jsonObject,thumbUrl, Message.MESSAGE_TYPE_STICKER, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getStickerInfoJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }

    /**
     * get the origin image of the image message
     * @return origin bitmap of image message
     */
    public Bitmap getImage() {

            Log.i(TAG,"getImage from path : "+getFilePath());

            return BitmapUtils.loadBitmap(0, getFilePath());
    }

    private String stickerName;

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        //parse stickerName ....
        JSONObject data = getJsonData();
        stickerName = data.optString("sitckerNmae");
    }

    public String getStickerName() {
        return stickerName;
    }

    public void setStickerName(String stickerName) {
        this.stickerName = stickerName;
    }
}


