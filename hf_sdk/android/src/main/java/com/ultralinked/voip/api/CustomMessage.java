package com.ultralinked.voip.api;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAIWENJIE$ on 2015/11/19.
 */
public class CustomMessage extends Message {

    public static final String

            TAG = "CustomMessage",
            DATA = "data",
            MESSAGE_TYPE_TAG = "messageType",
            MESSAGE_TYPE_VALUE_TAG = "messageTypeValue",
            PEERINFO_TAG = "peerInfo",
            DESC = "desc",
            THUMB_URL = "thumb_url",
            THUMB_PATH = "thumb_path",
            LINK_USERIDS = "linkUserIds",
            MESSAGE_ENCRYPT_TAG = "messageEncrypt",
            BURNING_TIME = "burning_time",
            OPTION_TAG = "option";



    private boolean hasFileTag;

    private String customMsgTypeStr;

    private int customMsgType;
    private transient JSONObject data;

    public void setCustomMsgTypeStr(String customMsgTypeStr) {
        this.customMsgTypeStr = customMsgTypeStr;
    }

    public String getCustomMsgTypeStr() {
        return customMsgTypeStr;
    }

    public boolean hasFileTag() {
        return hasFileTag;
    }

    public void setHasFileTag(boolean hasFileTag) {
        this.hasFileTag = hasFileTag;
    }

    public void setCustomMsgType(int customMsgType) {
        this.customMsgType = customMsgType;
    }

    public int getCustomMsgType() {

        return customMsgType;
    }

    public JSONObject getJsonData() {
        return data;
    }



    public  static String getFormatMessageJson(String customMsgType, JSONObject data, String thumbUrl, int customMsgTypeValue, Options options) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGE_TYPE_TAG, customMsgType);
        jsonObject.put(MESSAGE_TYPE_VALUE_TAG, customMsgTypeValue);
        jsonObject.put(DATA, data);
//        if (thumbData!=null){
//            jsonObject.put(DESC, thumbData);
//        }

        if (thumbUrl != null) {
            jsonObject.put(THUMB_URL, thumbUrl);
        }


        if (options!=null){

            if (!options.messageEncrypt){
                jsonObject.put(MESSAGE_ENCRYPT_TAG, "false");
            }else{
                jsonObject.put(MESSAGE_ENCRYPT_TAG, "true");
            }

            //has @ link.
            if (options.linkUserIds!=null){
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < options.linkUserIds.size(); i++) {
                    jsonArray.put(i,options.linkUserIds.get(i));
                }
                jsonObject.put(LINK_USERIDS,jsonArray);
            }


            if (options.thumbPath!= null) {
                jsonObject.put(THUMB_PATH, options.thumbPath);
            }


            if (options.messageFlag == MESSAGE_FLAG_BURNING){
                //burning message.
                JSONObject optionObject = new JSONObject();
                optionObject.put(MESSAGE_FLAG,MESSAGE_FLAG_BURNING_VALUE);
                jsonObject.put(OPTION_TAG, optionObject);
            }

        }

        JSONObject peerinfoObject = new JSONObject();
        MLoginApi.initAccount();
        peerinfoObject.put(USER_NAME, MLoginApi.currentAccount.userName);
        peerinfoObject.put(MOBILE, MLoginApi.currentAccount.mobile);
        peerinfoObject.put(NICK_NAME, MLoginApi.currentAccount.nickName);
        jsonObject.put(PEERINFO_TAG, peerinfoObject);
        return  jsonObject.toString();
    }

    public  static String getFormatMessageJson(String customMsgType, JSONObject data, int customMsgTypeValue, Options options) throws JSONException {

        return getFormatMessageJson(customMsgType,data,null,customMsgTypeValue,options);
    }

    protected  static final String MESSAGE_FLAG="messageFlag";
    protected  static final String MESSAGE_FLAG_BURNING_VALUE="burning";
    protected  static final String MOBILE="mobile";
    protected  static final String USER_NAME="userName";
    protected  static final String NICK_NAME="nickName";



    public void parseData(JSONObject json) throws JSONException {
        boolean encrypt = json.optBoolean(CustomMessage.MESSAGE_ENCRYPT_TAG,true);

        setEncrypt(encrypt);

         data = json.optJSONObject(CustomMessage.DATA);

        burningTime = data.optLong(BURNING_TIME);

        JSONArray jsonArray = json.optJSONArray(CustomMessage.LINK_USERIDS);
        if (jsonArray!=null){
            ArrayList<String> userIds = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String userId = jsonArray.optString(i);
                if (!TextUtils.isEmpty(userId)){
                    userIds.add(userId);
                }
            }
            if (userIds.size()>0){
                setLinkUserIds(userIds);
            }
        }

        String customType = json.optString(CustomMessage.MESSAGE_TYPE_TAG);
        int customMsgTypeValue = json.optInt(CustomMessage.MESSAGE_TYPE_VALUE_TAG);
        setCustomMsgType(customMsgTypeValue);
        setCustomMsgTypeStr(customType);

        JSONObject peerInfoObj = json.optJSONObject(CustomMessage.PEERINFO_TAG);
        if (peerInfoObj!=null) {
            PeerInfo peerInfo = new PeerInfo();
            peerInfo.mobile = peerInfoObj.optString(MOBILE);
            peerInfo.userName = peerInfoObj.optString(USER_NAME);
            peerInfo.nickName = peerInfoObj.optString(NICK_NAME);
            setPeerInfo(peerInfo);
        }

        JSONObject optionObject = json.optJSONObject(CustomMessage.OPTION_TAG);
        if (optionObject!=null) {
            String msgFlagStr = optionObject.optString(MESSAGE_FLAG);
            if (msgFlagStr!=null){
                if (msgFlagStr.equals(MESSAGE_FLAG_BURNING_VALUE)){
                    setMessageFlag(MESSAGE_FLAG_BURNING);
                }
            }
        }

    }


    @Override
    public final void burning() {
        super.burning();
        try {
            updateBurningTime(System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void updateBurningTime(long time) throws Exception {

        String body = getBody();
        JSONObject jsonObject = new JSONObject(body);
        parseData(jsonObject);
        JSONObject data = getJsonData().put(BURNING_TIME,time);
        jsonObject.put(DATA, data);
        burningTime = time;
        Log.i(TAG,"current burning time is :"+burningTime);
        MessagingApi.updateMessageBody(getKeyId(),getChatType(),jsonObject.toString());
    }



}


