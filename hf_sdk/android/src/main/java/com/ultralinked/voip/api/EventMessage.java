package com.ultralinked.voip.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 2015/11/19.
 */

//body:{"peerInfo": {"userName": "anxunzhushou", "mobile": "86123456789", "nickName": "sealchat"},
//        "messageTypeValue": 101,
//        "data": {"content": "hi", "event": "invite_friend"}, "messageType": "EventMessage"}

public class EventMessage extends CustomMessage {

    public static final String TAG = "EventMessage";

    public String dataStr;


    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);

        JSONObject data = getJsonData();
        if (data!=null){
            dataStr = data.toString();
        }

    }
}


