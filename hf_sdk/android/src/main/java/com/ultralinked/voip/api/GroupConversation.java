package com.ultralinked.voip.api;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/1.
 */
public class GroupConversation extends Conversation {


    private Message.Options options;

    public static final String TAG="GroupConversation";

    private String groupTopic;

    /**
     * Get group id of this group  chat conversation
     * @return group id
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Set group id of this group chat conversation
     * @param groupID
     */
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    private String groupID;

    /**
     * Get group topic of this group chat conversation
     * @return group topic of this group conversation
     */
    public String getGroupTopic() {
        return groupTopic;
    }

    /**
     * Set group topic of this group conversation
     * @param groupTopic
     */
    public void setGroupTopic(String groupTopic) {
        this.groupTopic = groupTopic;
    }

    /**
     * invite the member to join the current group conversaton
     * @param member
     */
    public void inviteToGroup(String member){

        Log.i(TAG,"invite : "+member+" to join  : "+getGroupID());

        MessagingApi.inviteToGroup(getGroupID(), member);

    }

    /**
     * modify the group title(only the group owner can change the title)
     * @param title
     */
    public void modifyTitle(String title){
        if (TextUtils.isEmpty(title)|| TextUtils.isEmpty(title.trim())){
            return;
        }
        MessagingApi.modifyTitle(getGroupID(), title);
    }

    /**
     *invite the members to join the current group conversaton
     * @param members
     */
    public void invitesToGroup(List<String> members){


        for (String member:members) {
            if (member!=null&&member.equals(MLoginApi.currentAccount.id)){
                Log.i(TAG,"can not invite youself.");
                continue;
            }
            Log.i(TAG,"invite : "+member+" to  join  : "+getGroupID());
            if (!TextUtils.isEmpty(member)){
                MessagingApi.inviteToGroup(getGroupID(),member);
            }

        }

    }
    private List<String> removeDuplicateMember(List<String> originMembers){

        Set set  =   new HashSet();
        List<String> newList  =   new ArrayList<String>();

        if(originMembers==null){

            return newList;
        }
        for  (Iterator iter = originMembers.iterator(); iter.hasNext();)   {
            Object element  =  iter.next();
            if  (set.add(element))
                newList.add((String) element);
        }
        return newList;

    }

    /**
     * send file message in current group conversaton
     * @param filePath
     */
    public Message sendFile(String filePath){
        return sendFile(filePath,null);

    }

    public Message sendFile(String filePath, Message.Options options){

        return MessagingApi.sendFileChat(getGroupID(), filePath, Message.CHAT_TYPE_GROUP, options);

    }


    /**
     * send video message in current group conversaton
     * @param filePath
     */
    public Message sendVideo(String filePath){
        return  sendVideo(filePath,null);

    }

    public Message sendVideo(String filePath, Message.Options options){

        return   MessagingApi.sendVideoChat(getGroupID(), filePath, Message.CHAT_TYPE_GROUP, options);

    }


    /**
     * send sticker message in current  conversaton
     * @param filePath
     */
    public Message sendSticker(String stickerName, String filePath){

        return  sendSticker(stickerName,filePath,null);

    }

    public Message sendSticker(String stickerName, String filePath, Message.Options options){

        return  MessagingApi.sendStickerChat(getGroupID(), stickerName,filePath, Message.CHAT_TYPE_GROUP,options);

    }

    /**
     * send image message in current group conversaton
     * @param filePath
     */
     public Message sendImage(String filePath){
         return  sendImage(filePath,null);

     }

    public Message sendImage(String filePath, Message.Options options){

       return   MessagingApi.sendImageChat(getGroupID(), filePath, Message.CHAT_TYPE_GROUP, options);

     }

    /**
     * check all group member in current group conversaton on server
     */
    public  void checkGroupMember(){

        MessagingApi.checkGroupMember(getGroupID());
    }

    /**
     * Send a text message in current group conversaton
     * @param text
     * @return
     */
    public Message sendText(String text){

        return  sendText(text,null);
    }

    public Message sendText(String text, Message.Options options){
        return  MessagingApi.sendText(getGroupID(), text, Message.CHAT_TYPE_GROUP, options);

    }

    /**
     * exit current group conversation
     */
    public  void exitGroup(){
        MessagingApi.exitGroup(conversationId,getGroupID());
    }

    /**
     * Get the group  chat conversation by group id
     * @param groupId
     * @return
     */
     public static GroupConversation getConversationByGroupId(String groupId){

         GroupConversation groupConversation= MessagingApi.getConversationByGroupId(groupId);

       return groupConversation;
    }


    /**
     * Send a location message in current group conversation
     * @param locationhHashMap must have follow key LocationMessage.
    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String ACCURACY = "accuracy";

    public static final String TITLE = "title";

    public static final String SUBTITLE = "subTitle";
     */
    public Message sendLocation(HashMap<String,String> locationhHashMap){

        return sendLocation(locationhHashMap, null);
    }

    public Message sendLocation(HashMap<String,String> locationhHashMap, Message.Options options){

        return MessagingApi.sendLocationMessage(getGroupID(), locationhHashMap, Message.CHAT_TYPE_GROUP, options);

    }


    public Message sendVoice(String voiceUrl, int during){

        return sendVoice(voiceUrl,during,null);

    }

    /**
     * Send a voice message in current group conversation
     * @param voiceUrl
     */
    public Message sendVoice(String voiceUrl, int during, Message.Options options){

        return MessagingApi.sendVoiceMessage(getGroupID(), during, voiceUrl, Message.CHAT_TYPE_GROUP, options);

    }


    /**
     * Send a vcard message in current group conversation
     * @param vcardUrl
     */
    public Message sendVcard(String vcardUrl){
        return sendVcard(vcardUrl,null);

    }

    public Message sendVcard(String vcardUrl, Message.Options options){

        return MessagingApi.sendVcardMessage(getGroupID(), vcardUrl, Message.CHAT_TYPE_GROUP, options);

    }

    /**
     * Send a custom message in current single conversation
     * @param jsonData
     */

    public Message sendCustomMessage(JSONObject jsonData){
        return sendCustomMessage(jsonData,null);

    }

    public Message sendCustomMessage(JSONObject jsonData, Message.Options options){

        return MessagingApi.sendCustomTypeMessage(getGroupID(), jsonData, Message.CHAT_TYPE_GROUP, options);

    }

    /**
     * Join the current group chat conversation
     */
    public  void JoinGroup(){

        MessagingApi.JoinGroup(getGroupID());

    }

    public  void kickMember(String memberName){
       MessagingApi.kickMember(getGroupID(),memberName);
    }


    public List<GroupMember> getGroupMember(){
        return MessagingApi.getGroupMemembers(getGroupID(),getChairMan());
    }

    /**
     * true is group conversation creator false is just member
     */
    public boolean isOwner;




    /**
     * isSetMute conversation
     * @param isSetMute
     */
    public void setMute(boolean isSetMute){

        MessagingApi.setConversationMute(getGroupID(), true, isSetMute);

    }

    public boolean isMute(){

        return MessagingApi.getConversationIsMute(getGroupID());

    }


    public void setProperties(HashMap<String ,String> propertiesMap){
        if (propertiesMap == null){
            Log.i("GroupConversation","setProperties has a null map");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            Set set = propertiesMap.entrySet();
            Iterator i = set.iterator();
            while(i.hasNext()){
                Map.Entry<String, String> entry1=(Map.Entry<String, String>)i.next();
                jsonObject.put(entry1.getKey(),entry1.getValue());
            }

            MessagingApi.setConversationProperty(getGroupID(),true, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("GroupConversation","setProperty error:"+e.getLocalizedMessage()+";map="+propertiesMap.toString());
        }

    }

    public void setProperty(String key, String value){

        HashMap<String,String> map = new HashMap<String,String>();
        map.put(key,value);
        setProperties(map);

    }


    public HashMap<String,String> getProperties(){
        String props = MessagingApi.getConversationProperties(getGroupID(),true);
        HashMap<String,String> map = new HashMap<String,String>();
        if (TextUtils.isEmpty(props)){
            return  map;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(props);
            Iterator iterator = jsonObject.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                String value = jsonObject.optString(key);
                map.put(key,value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("GroupConversation", "getProperties error:" + e.getLocalizedMessage());
        }

        return  map;
    }



}
