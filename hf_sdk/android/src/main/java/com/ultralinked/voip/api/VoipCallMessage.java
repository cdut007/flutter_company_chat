package com.ultralinked.voip.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 2015/11/19.
 */


public class VoipCallMessage extends CustomMessage {

    public static final String TAG = "VoipCallMessage";


    private static  final String CALLER = "caller";
    private static  final String CALLEE = "callee";
    private static  final String DURING = "during";
    private static  final String CALL_TYPE = "call_type";

    public String dataStr;

    public String caller;
    public String callee;
    public  int during;
    public  int callType;



    public static String getVoipCallInfoJson(String caller, String callee, int during, int callType , Options options) {


        try {
            JSONObject jsonObject = new JSONObject();
            if (options == null){
                options = new Options();
            }
            jsonObject.put(CALLEE,callee);
            jsonObject.put(CALLER,caller);
            jsonObject.put(DURING,during);

            if (callType == CallSession.TYPE_VIDEO){
                jsonObject.put(CALL_TYPE,"video");
            }else {
                jsonObject.put(CALL_TYPE,"audio");
            }

            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_VOIP, options);
        } catch (Exception e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getVoipCallInfoJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }


    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        JSONObject data = getJsonData();
        if (data!=null){
            caller = data.optString(CALLER);
            callee = data.optString(CALLEE);
            during = data.optInt(DURING);

            String callTypeStr = data.optString(CALL_TYPE);
            if ("video".equals(callTypeStr)){
                callType = CallSession.TYPE_VIDEO;
            }else {
                callType = CallSession.TYPE_AUDIO;
            }
        }

    }
}


