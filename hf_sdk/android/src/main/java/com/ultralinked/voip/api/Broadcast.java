package com.ultralinked.voip.api;

import java.io.Serializable;

public class Broadcast implements Serializable {

    private static final long serialVersionUID = -6665583778491571059L;
    public  final static String TAG="Broadcast";

    /**
     *  message status ok
     */
    public  final static int STATUS_OK=3;

    /**
     *  message status draft
     */

    public  final static int STATUS_DRAFT=0;



    private int status=STATUS_DRAFT;

    private int keyId;

    private String body;

    private int type;

    private  int chatType;

    private int conversionType;

    protected boolean isSender;

    protected String globalMsgTime;

    protected long dateTime = 0L;

    private String senderName;

    private String receiver;

    private boolean isRead;

    /**
     * Get message conversation type
     * @return conversation type
     */
    public int getConversionType() {
        return conversionType;
    }

    /**
     * Set message conversation type
     * @param conversionType
     */
    public void setConversionType(int conversionType) {
        this.conversionType = conversionType;
    }

    /**
     * get the current message read status
     * @return true the message is already read false the message is unread
     */
    public boolean isRead() {
        return isRead;
    }

    protected void setIsRead(boolean isRead) {
        this.isRead = isRead;
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
     * Get message sender name
     * @return sender name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Set message sender name
     * @param senderName
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Get messag receiver name
     * @return receiver name
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Set message receiver name
     * @param receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private int conversationId;

    public Broadcast() {

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
    public boolean isSender() {
        return isSender;
    }

    /**
     * Set messag sender
     * @param isSender
     */
    public void setSender(boolean isSender) {
        this.isSender = isSender;
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


    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }
}
