package com.ultralinked.voip.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 2015/11/19.
 */

public class SystemMessage extends CustomMessage {

    public static final String TAG = "SystemMessage";



    public String contet;

    public static String getSystemJson(String msgContent, Options options)  {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content",msgContent);
            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_SYSTEM, options);
        } catch (JSONException e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("TextMessageJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;

    }

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        JSONObject data = getJsonData();
        if (data!=null){
            contet = data.optString("content");

        }

    }
}


