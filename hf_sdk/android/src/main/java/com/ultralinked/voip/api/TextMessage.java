package com.ultralinked.voip.api;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/11/4.
 */
public class TextMessage extends CustomMessage {


    public static  final String TAG="TextMessage";

    private static final String CONTENT_KEY = "content";


    private String content;
    /**
     * Get message text
     * @return
     */
    public String getText() {
        if (TextUtils.isEmpty(content)){
            return getBody();
        }
        return content;
    }

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);
        parseTextInfo(getJsonData());
    }

    public  void parseTextInfo(JSONObject data) throws JSONException {
        content = data.optString(CONTENT_KEY);
    }

    public static String getTextJson(String msgContent, Options options)  {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(CONTENT_KEY,msgContent);
            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_TEXT, options);
        } catch (JSONException e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("TextMessageJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;

    }


}
