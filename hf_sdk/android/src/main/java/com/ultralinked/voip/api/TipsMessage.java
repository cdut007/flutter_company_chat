package com.ultralinked.voip.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAIWENJIE$ on 2015/11/19.
 */
public class TipsMessage extends Message {

    public static final String TAG = "TipsMessage";


    public List<String> members;

    private String inviter;

    private String actionType;

//{"action":"kicked","data":{"members":[{"name":"b1"}]}}
    //{"action":"join","data":{"inviter":"yongjun","members":[{"name":"alex"}]}}
    public static TipsMessage parseJson(JSONObject json, TipsMessage message) {
        String action = json.optString("action");
        JSONObject data = json.optJSONObject("data");

        JSONArray members = data.optJSONArray("members");
        message.members = new ArrayList<String>();
        if (members!=null){

        for (int i = 0; i < members.length(); i++) {
            JSONObject jsonObj = members.optJSONObject(i);
            if (jsonObj!=null) {
                message.members.add(jsonObj.optString("name"));
            }
        }
        }

        message.setActionType(action);
        if ("join".equals(action)){
            message.setType(MESSAGE_TYPE_TIPS);
            message.setInviter(data.optString("inviter"));
        }else if ("left".equals(action)){
            message.setType(MESSAGE_TYPE_TIPS);
        }else if ("kicked".equals(action)){
            message.setType(MESSAGE_TYPE_TIPS);
        }
        return  message;

    }

    public static int parseSystemMessageType(String msgContent) throws JSONException {
        JSONObject json = new JSONObject(msgContent);
        String action = json.optString("action");
        if ("join".equals(action)){
            return  MESSAGE_TYPE_TIPS;
        }else if ("left".equals(action)){
            return  MESSAGE_TYPE_TIPS;

        }else if ("kicked".equals(action)){
            return  MESSAGE_TYPE_TIPS;
        }
        return MESSAGE_TYPE_UNKNOWN;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }


    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}


