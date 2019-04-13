package com.ultralinked.voip.api;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * chat conversation
 */
public class Conversation implements Serializable {

    private static final long serialVersionUID = -2899003067247543731L;
    protected int conversationId=-1;
    private String time;
    protected String contactNumber;
    protected String contactName;
    protected String contactId;
    private String draft;
    private boolean isGroup;
    private boolean hasFlag;
    private boolean hasDraft;
    public  static  final int CONVERSATION_FLAG_NONE = 0;
    public  int        conversationFlag = CONVERSATION_FLAG_NONE;
    public  static  final int  CONVERSATION_FLAG_PRIVATE = 1;

    public Message.PeerInfo peerInfo;
    /**
     * Group conversation type
     */
    public final static int GROUP_CHAT = 2;

    /**
     * single conversation type
     */
    public final static int SINGLE_CHAT = 1;

    private int msgCount;

    private String chairMan;
    private String convProperties;

    private Message lastMessage;

    private Serializable tag;



    public Serializable getTag() {
        return tag;
    }

    public void setTag(Serializable tag) {

        this.tag = tag;
    }

    protected   static SparseArray<String> flagtypes;

    static {
        flagtypes = new SparseArray<String>();
        flagtypes.put(CONVERSATION_FLAG_PRIVATE,"[private]");
        //
    }



    private static int findKeyOfValue(String props) {
        int mSize = flagtypes.size();
        for (int i = 0; i < mSize; i++) {
            if (flagtypes.valueAt(i).equals(props)) {
                return flagtypes.keyAt(i);
            }
        }
        return CONVERSATION_FLAG_NONE;

    }


    @Override
    public boolean equals(Object o) {
        Conversation conversation = ((Conversation)o);
        int ConvId =conversation.conversationId;
        if (ConvId<0 && !conversation.isGroup()){
            String contactId =conversation.contactNumber;
            return  contactNumber.equals(contactId);
        }
        return conversationId == ConvId;
    }

    protected  static String getFlagStr(int propType){


        return flagtypes.get(propType);
    }


    public void setProperties(HashMap<String ,String> propertiesMap){
        if (propertiesMap == null){
            Log.i("converastion","setProperties has a null map");
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

            MessagingApi.setConversationProperty(contactNumber, false, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("conversation","setProperty error:"+e.getLocalizedMessage()+";map="+propertiesMap.toString());
        }

    }

    public void setProperty(String key, String value){

           HashMap<String,String> map = new HashMap<String,String>();
           map.put(key,value);
           setProperties(map);

    }


    public HashMap<String,String> getProperties(){
      String props = MessagingApi.getConversationProperties(contactNumber,false);
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
            Log.i("conversation", "getProperties error:" + e.getLocalizedMessage());
        }

        return  map;
    }

    public String getDraft() {
        return draft;
    }

    protected void setDraft(String draft) {

        if (!TextUtils.isEmpty(draft)){
            hasDraft = true;
            this.draft = draft;
        }
    }

    private  static boolean parseConvFlag(Conversation conversation){//split

       if (conversation.contactNumber == null){

           return  false;
       }
       conversation.contactName = conversation.contactNumber;
        conversation.contactId = conversation.contactNumber;
         int pos = conversation.contactNumber.lastIndexOf("[");
         String props  =null;
       if (pos>0){
           props = conversation.contactNumber.substring(pos);
       }

         if (TextUtils.isEmpty(props)){

             return  false;
         }

       conversation.contactName = conversation.contactNumber.substring(0, pos);

        conversation.contactId = conversation.contactNumber.substring(0, pos);

         int key = findKeyOfValue(props);
         if (key == CONVERSATION_FLAG_PRIVATE){
             conversation.conversationFlag = CONVERSATION_FLAG_PRIVATE;
             return true;
         }
         return  false;
   }



    protected void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return contactId;
    }



    protected void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return contactName;
    }

    public boolean hasDraft() {
        return hasDraft;
    }




    /**
     * Get this conversation type
     * @return true is group chat false is single chat
     */
    public boolean isGroup() {
        return isGroup;
    }

    /**
     * Set conversation type
     * @param isGroup
     */
    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }


    /**
     * Get conversation id
     * @return
     */
    public int getConversationId() {
        return conversationId;
    }

    /**
     * Set conversation id
     * @param conversationId
     */
    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }


    /**
     * Get all message count of this conversation
     * @return
     */
    public int getMsgCount() {
        return msgCount;
    }

    /**
     * Set message count of this conversation
     * @param msgCount
     */
    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    /**
     * Get latest msg time of this conversation
     * @return
     */
    public String getTime() {
        return time;
    }

    /**
     * Set latest msg time of this conversation
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Get contact number of single chat conversation
     * @return
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Set contact number of single chat conversation
     * @param contactNumber
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        hasFlag = parseConvFlag(this);
    }

    public Conversation() {


    }

    /**
     * get the unread message counts of the current conversation
     * @return unread message counts of current conversation
     */
    private  int  getUnreadCount = -1;

    public int getUnReadMsgCount() {

//        if (getUnreadCount == -1){
//            getUnreadCount = MessagingApi.getConversationUnreadMessageCounts(conversationId);
//        }

        return getUnreadCount;
    }

    public void setUnreadCount(int getUnreadCount) {
        this.getUnreadCount = getUnreadCount;
    }

    /**
     * set current conversation all unread messages as read status
     */
    public void read(){

       MessagingApi.conversationRead(conversationId);
        getUnreadCount = 0;

   }

    /**
     * delete the specified message
     * @param id
     */
    public  void deleteMessage(int id){
         MessagingApi.deleteMessage(conversationId, id);

    }

    /**
     * delete all message of current conversation
     */
    public  void deleteAllMessages(){

        MessagingApi.deleteAllMessages(conversationId);
    }

    /**
     * delete the id array of mutiple message
     * @param ids ids array
     */
    public void deleteMutipleMessage(int[] ids){

        MessagingApi.deleteMutipleMessage(conversationId, ids);
    }

    /**
     * delete the current conversation
     */
    public void delete(){

        MessagingApi.deleteConversation(conversationId);
    }
    /**
     * send file message in current group conversaton
     * @param filePath
     */
    public Message sendFile(String filePath){

       return sendFile(filePath,null);

    }

    public Message sendFile(String filePath, Message.Options options){

        return  MessagingApi.sendFileChat(getContactNumber(), filePath, Message.CHAT_TYPE_SINGLE,options);

    }

    /**
     * isSetMute conversation
     * @param isSetMute
     */
    public void setMute(boolean isSetMute){

        MessagingApi.setConversationMute(getContactNumber(),false,isSetMute);

    }

    public boolean isMute(){

        return MessagingApi.getConversationIsMute(getContactNumber());

    }

    /**
     * send video message in current  conversaton
     * @param filePath
     */
    public Message sendVideo(String filePath){

        return  sendVideo(filePath,null);

    }

    public Message sendVideo(String filePath, Message.Options options){

        return  MessagingApi.sendVideoChat(getContactNumber(), filePath, Message.CHAT_TYPE_SINGLE,options);

    }

    /**
     * set the draft
     * @param content
     */
    public Conversation saveDraft(String content){
        if (content == null){
            content = "";
        }

        if (conversationId<0){
            //create.
            conversationId= MessagingApi.createConversation(contactNumber,false);
            if (conversationId>=0) {
                MessagingApi.sendConversationCreatedBroadcast(this);
            }
        }

        int succ = MessagingApi.setConversationDraftArray(conversationId, content.trim().getBytes());
        if (succ==1){
            setDraft(content);
        }
        return  this;

    }

    /**
     * remove the draft
     * @param
     */
    public void removeDraft(){
        int succ = MessagingApi.setConversationDraftArray(conversationId, null);
        if (succ==1){
            hasDraft = false;
            this.draft = "";
            //send borda cast
        }

    }


    /**
     * send sticker message in current  conversaton
     * @param filePath
     */
    public Message sendSticker(String stickerName, String filePath){

        return  sendSticker(stickerName,filePath,null);

    }

    public Message sendSticker(String stickerName, String filePath, Message.Options options){

        return  MessagingApi.sendStickerChat(getContactNumber(), stickerName,filePath, Message.CHAT_TYPE_SINGLE,options);

    }


    /**
     * send image message in current  conversaton
     * @param filePath
     */
    public Message sendImage(String filePath){

        return  sendImage(filePath,null);

    }

    public Message sendImage(String filePath, Message.Options options){

        return  MessagingApi.sendImageChat(getContactNumber(), filePath, Message.CHAT_TYPE_SINGLE,options);

    }

    /**
     * Send a text message in current single conversation
     * @param text
     */
    public Message sendText(String text){

        return sendText(text, null);

    }

    public Message sendText(String text, Message.Options options){

        return  MessagingApi.sendText(getContactNumber(), text, Message.CHAT_TYPE_SINGLE,options);

    }

    /**
     * Send a location message in current single conversation
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

        return MessagingApi.sendLocationMessage(contactNumber, locationhHashMap, Message.CHAT_TYPE_SINGLE,options);

    }

    /**
     * Send a custom message in current single conversation
     * @param jsonData
     */
    public Message sendCustomMessage(JSONObject jsonData){

        return sendCustomMessage(jsonData,null);

    }

    public Message sendCustomMessage(JSONObject jsonData, Message.Options options){

        return MessagingApi.sendCustomTypeMessage(contactNumber, jsonData, Message.CHAT_TYPE_SINGLE,options);

    }

    protected static final void stopTyping(Runnable onTypingStop){
        if (mTypingHandler == null){
            HandlerThread mTypingThreadHandler = new HandlerThread("sendComposing");
            mTypingThreadHandler.start();
            mTypingHandler = new Handler(mTypingThreadHandler.getLooper());
        }
        mTypingHandler.postDelayed(onTypingStop, TYPING_TIMEOUT);//3 sec for stop typing
    }


    /**
     * Send a paint draw broadcast in current single conversation
     *
     */
    public  final void  sendPaintInfo(JSONObject drawInfo){
        String jsonData = BroadcastApi.getPaintStatusJson(drawInfo);
        MessagingApi.sendCustomBroadcast(contactNumber,jsonData, Message.CHAT_TYPE_SINGLE);

    }


    /**
     * Send a custom broadcast in current single conversation
     *
     */
    public  final void  sendCustomBroadcast(String tag, JSONObject broadcast){
        String jsonData = BroadcastApi.getCustomJson(tag,broadcast);
        MessagingApi.sendCustomBroadcast(contactNumber,jsonData, Message.CHAT_TYPE_SINGLE);

    }


    private  static  final  int TYPING_TIMEOUT = 3000;
    /**
     * Send a composing broadcast in current single conversation
     *
     */
    public  final void  sendComposing(){
        if (!mTyping) {
            mTyping = true;
            MessagingApi.sendComposingBroadcast(contactNumber, true, Message.CHAT_TYPE_SINGLE);

            if (mTypingHandler == null){
                HandlerThread mTypingThreadHandler = new HandlerThread("sendComposing");
                mTypingThreadHandler.start();
                mTypingHandler = new Handler(mTypingThreadHandler.getLooper());
            }
            mTypingHandler.removeCallbacks(onTypingTimeout);//worker handler
            mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMEOUT);//3 sec for stop typing
        }

    }

    public  void  release(){
        if (mTypingHandler!=null){
            mTypingHandler.removeCallbacksAndMessages(null);
            mTypingHandler.getLooper().quit();
            mTypingHandler = null;
        }
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;
            mTyping = false;//stop typing
            MessagingApi.sendComposingBroadcast(contactNumber, false, Message.CHAT_TYPE_SINGLE);
        }
    };

    private static Handler mTypingHandler = null;
    private boolean mTyping = false;
    /**
     * Send a voice message in current single conversation
     * @param voiceUrl
     */
    public Message sendVoice(String voiceUrl, int during){

        return sendVoice(voiceUrl,during,null);

    }

    public Message sendVoice(String voiceUrl, int during, Message.Options options){

        return  MessagingApi.sendVoiceMessage(getContactNumber(), during, voiceUrl, Message.CHAT_TYPE_SINGLE,options);

    }
    /**
     * Send a vcard message in current single conversation
     * @param vcardUrl
     */
    public Message sendVcard(String vcardUrl){

        return sendVcard(vcardUrl,null);

    }

    public Message sendVcard(String vcardUrl, Message.Options options){

        return  MessagingApi.sendVcardMessage(getContactNumber(), vcardUrl, Message.CHAT_TYPE_SINGLE ,options);

    }
    /**
     * Get all messages in this conversation
     * @return all messages in this conversation
     */
    public List<Message> getAllMessageList(){

        return  MessagingApi.getAllMessages(getConversationId());
    }



    /**
     * Get the messages from position message to before,has the current message
     * @param msgId
     * @param count
     * @return
     */
    public List<Message> getMessageListWithPrev(int msgId , int count) {
        return MessagingApi.getMessagesByMessageId(getConversationId(), msgId, count, false, true);
    }


    /**
     * Get the messages from position message to front,has the current message
     * @param msgId
     * @param count
     * @return
     */
    public List<Message> getMessageListWithFront(int msgId , int count) {
        return MessagingApi.getMessagesByMessageId(getConversationId(), msgId, count, true, true);
    }


    /**
     * Get the messages from position message to before
     * @param msgId
     * @param count
     * @return
     */
    public List<Message> getPrevMessageList(int msgId , int count) {
        return MessagingApi.getMessagesByMessageId(getConversationId(), msgId, count, false,false);
    }


    /**
     * Get the messages from position message to front
     * @param msgId
     * @param count
     * @return
     */
    public List<Message> getFrontMessageList(int msgId , int count) {
        return MessagingApi.getMessagesByMessageId(getConversationId(), msgId, count, true, false);
    }
    /**
     * Get the messages from position start to end
     * @param start start position
     * @param end  end position
     * @return
     */
    public List<Message> getMessageList(int start, int end){

        return  MessagingApi.getMessages(getConversationId(), start, end);
    }

     public void topUp(boolean isTopUp){
         MessagingApi.setConversationPriority(getConversationId(),isTopUp);
     }

    public boolean isTopUp;


    protected void setChairMan(String chairMan) {
        this.chairMan = chairMan;
    }

    public String getChairMan() {

        return chairMan;
    }

    public boolean hasConversationFlag() {
        return hasFlag;
    }


    protected void setConvProperties(String convProperties) {
        this.convProperties = convProperties;
    }


    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
