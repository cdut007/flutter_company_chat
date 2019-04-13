package com.ultralinked.voip.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by james on 2015/11/19.
 */


public class SubscribeMessage extends CustomMessage {

    public  static  class  Subscribe implements Serializable {

        public String title;
        public String content;
        public String imgUrl;
        public String linkUrl;
    }

    public static final String TAG = "SubscribeMessage";

    public String dataStr;


    public String title;
    public String content;
    public String imgUrl;
    public String linkUrl;


    public List<Subscribe> subscribes;




    public static String getSubscribeJson(JSONObject jsonObject , Options options) {


        try {

            if (options == null){
                options = new Options();
            }

            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_SUBSCRIBE, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getSubscribeJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        JSONObject data = getJsonData();
        if (data!=null){
            dataStr = data.toString();
        }

    }
}


