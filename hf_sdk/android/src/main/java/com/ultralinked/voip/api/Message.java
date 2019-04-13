package com.ultralinked.voip.api;


import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    public long burningTime=-1;


    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    protected void setPeerInfo(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }


    public Serializable tag;

    public int getMessageFlag() {
        return messageFlag;
    }

    protected void setMessageFlag(int messageFlag) {
        this.messageFlag = messageFlag;
    }

    public ArrayList<String> getLinkUserIds() {
        return linkUserIds;
    }

    public void setLinkUserIds(ArrayList<String> linkUserIds) {
        this.linkUserIds = linkUserIds;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public  static  final class PeerInfo implements Serializable {
        public String mobile;
        public String userName;
        public String nickName;
        public String icon_url;

        @Override
        public String toString() {
            return "PeerInfo{" +
                    "mobile='" + mobile + '\'' +
                    ", userName='" + userName + '\'' +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "Message{" +
                "errorInfo='" + errorInfo + '\'' +
                ", peerInfo=" + peerInfo +
                ", status=" + status +
                ", keyId=" + keyId +
                ", body='" + body + '\'' +
                ", type=" + type +
                ", chatType=" + chatType +
                ", isSender=" + isSender +
                ", globalMsgTime='" + globalMsgTime + '\'' +
                ", dateTime=" + dateTime +
                ", senderName='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", isRead=" + isRead +
                ", conversationId=" + conversationId +
                '}';
    }

    private static final long serialVersionUID = -6665583778491571059L;
    public  final static String TAG="Message";

    private  int messageFlag;

    private boolean encrypt = true;
    /**
     *  message  type
     */
    public static final int MESSAGE_TYPE_DRAFT = 0;//eMessageType.C_MESSAGE_TYPE_DRAFT.ordinal();
    public static final int MESSAGE_TYPE_TEXT  = 1;//eMessageType.C_MESSAGE_TYPE_TEXT.ordinal();
    public static final int MESSAGE_TYPE_FILE  = 2;//eMessageType.C_MESSAGE_TYPE_FILE_ONLY.ordinal();
    public static final int MESSAGE_TYPE_IMAGE = 3;//eMessageType.C_MESSAGE_TYPE_FILE_WITH_THUMBNAIL.ordinal();
    public static final int MESSAGE_TYPE_CUSTOM = 4;
    public static final int MESSAGE_TYPE_VIDEO = 5;
    public static final int MESSAGE_TYPE_LOCATION = 6;
    public static final int MESSAGE_TYPE_VOICE = 7;
    public static final int MESSAGE_TYPE_VCARD = 8;
    public static final int MESSAGE_TYPE_TIPS = 9;
    public static final int MESSAGE_TYPE_STICKER = 10;
    public static final int MESSAGE_TYPE_EVENT = 11;
    public static final int MESSAGE_TYPE_SUBSCRIBE = 12;
    public static final int MESSAGE_TYPE_VOIP = 13;
    public static final int MESSAGE_TYPE_SYSTEM  = 14;

    public static final int MESSAGE_TYPE_PUSH = 0x21;

    public static final int MESSAGE_TYPE_UNKNOWN = -1;

    /**
     *  message  flag
     */

    public static final int MESSAGE_FLAG_BURNING = 1;

    /**
     *  message conversation type
     */
    public static final int CHAT_TYPE_GROUP = 2;
    public static final int CHAT_TYPE_SINGLE = 1;

    /**
     *  message status ok
     */
    public  final static int STATUS_OK=3;

    /**
     *  message status read
     */
    public  final static int STATUS_READ=12;

    /**
     *  message status draft
     */

    public  final static int STATUS_DRAFT=0;


    public static final int STATUS_DELIVERY_OK = 5;


    public static final int STATUS_FAILURE = 2;

    public static final int STATUS_IN_PROGRESS =15;



    public static final int STATUS_BURNING = 32;


    private String errorInfo;


    private PeerInfo peerInfo;


    public  static  final class Options implements Serializable {
        public int messageFlag;
        public String thumbPath;
        public ArrayList<String> linkUserIds;
        public boolean messageEncrypt = true;//default is true.
    }

//    MESSAGE_STATUS_DRAFT,
//    MESSAGE_STATUS_PROGRESSING,
//    MESSAGE_STATUS_SEND_FAILED,
//    MESSAGE_STATUS_OK,
//    MESSAGE_STATUS_DELIVERY_FAILED,
//    MESSAGE_STATUS_DELIVERY_OK, 5
//    MESSAGE_STATUS_DISPLAY_OK,
//    MESSAGE_STATUS_CANCELLED,
//    MESSAGE_STATUS_PEER_CANCELLED,
//    MESSAGE_STATUS_RECV_INV,
//    MESSAGE_STATUS_RECV_FAILED,
//    MESSAGE_STATUS_READ,
//    MESSAGE_STATUS_RECV_REJECTED,
//    MESSAGE_STATUS_FILE_CREATE,
//    MESSAGE_STATUS_FILE_FAILED,14
//    MESSAGE_STATUS_FILE_PROGRESSING,


    private int status=STATUS_DRAFT;

    private int keyId;

    private String body;

    private ArrayList<String> linkUserIds;

    private int type;

    private  int chatType;

    protected boolean isSender;

    protected String globalMsgTime;

    protected long dateTime = 0L;

    private String sender;

    private String receiver;

    private boolean isRead;




    @Override
    public boolean equals(Object o) {
        Message message = ((Message)o);
        if (message == this){
            return  true;
        }

        int keyId =message.keyId;
        if (keyId == this.keyId && message.chatType == this.chatType){

            return  true;
        }
        return false;
    }

    /**
     * get the current message read status
     * @return true the message is already read false the message is unread
     */
    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }



    /**
     * set the  message burning
     */
    public void burning(){
    Log.i(TAG,"burning msg info :"+toString());
    }


    /**
     * set the unread message status as read status
     */
    public void read() {

        Log.i(TAG,getReceiver()+" read message id : "+keyId);
        if (!isSender ) {
            if ( chatType == CHAT_TYPE_SINGLE) {
                if ( status != STATUS_READ ){
                    MessagingApi.messageRead(conversationId, keyId);
                }
                //check the message flag
//                if (messageFlag == MESSAGE_FLAG_BURNING){
//                    if (conversationId>-1){
//                        MessagingApi.deleteMessage(conversationId, id);
//                    }
//
//                }
            }else {
                MessagingApi.messageRead(conversationId, keyId);
            }
        }

    }

    /**
     * Get  message status
     * @return status of file message
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set message status
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }
    /**
     * Get message sender id
     * @return sender name
     */
    public String getSender() {
        return sender;
    }

    /**
     * Set message sender id
     * @param sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Get messag receiver id
     * @return receiver id
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Set message receiver id
     * @param receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private int conversationId = -1;

    public Message() {

    }

    /**
     * Get message id
     * @return
     */
    public int getKeyId() {
        return keyId;
    }

    /**
     * Set message id
     * @param keyId
     */
    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    /**
     * Get message body
     * @return
     */
    public String getBody() {
        return body;
    }

    /**
     * Set messag body
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get message type
     * @return message type
     */
    public int getType() {
        return type;
    }

    /**
     * Set message type
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * get message is sender
     * @return true is  sender false is reveiver
     */
    public  boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }

    /**
     * Get message send or receive time
     * @return
     */
    public String getGlobalMsgTime() {
        return globalMsgTime;
    }

    /**
     * Set message time
     * @param globalMsgTime
     */
    public void setGlobalMsgTime(String globalMsgTime) {
        this.globalMsgTime = globalMsgTime;
    }


    /**
     * Get conversation id of this message
     * @return
     */
    public int getConversationId() {
        return conversationId;
    }

    /**
     * Set conversation id of this message
     * @param conversationId
     */
    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    /**
     *When message send failure, call this method to resend this message
     */
    public void reSend(){
        if (this instanceof FileMessage){
            MessagingApi.reSendMsg(getConversationId(),getKeyId(),true);

        }else{
            MessagingApi.reSendMsg(getConversationId(),getKeyId(),false);

        }

    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
