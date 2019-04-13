package com.ultralinked.voip.api;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LAIWENJIE$ on 2015/11/19.
 */
public class ImageMessage extends FileMessage {

    public static final String TAG="ImageMessage";


    public  int width;
    public  int height;


    public static String getImageInfoJson(String imageUrl, String fileName, String thumbPath, int imgWidth, int imgHeight, String thumbUrl, Options options) {


        try {
            JSONObject jsonObject = FileMessage.getFileJsonObject(imageUrl,fileName);
            if (options == null){
                options = new Options();
            }
            options.thumbPath = thumbPath;
            jsonObject.put("width", imgWidth);
            jsonObject.put("height", imgHeight);
            return getFormatMessageJson(TAG,jsonObject,thumbUrl, Message.MESSAGE_TYPE_IMAGE, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getImageInfoJson error e:")
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

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);
        //parse width height ....
        JSONObject data = getJsonData();
        width = data.optInt("width");
        height = data.optInt("height");

    }

   }


