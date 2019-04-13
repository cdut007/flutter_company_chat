/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public class c_Broadcast {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected c_Broadcast(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(c_Broadcast obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        imapijJNI.delete_c_Broadcast(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setMsg_status(int value) {
    imapijJNI.c_Broadcast_msg_status_set(swigCPtr, this, value);
  }

  public int getMsg_status() {
    return imapijJNI.c_Broadcast_msg_status_get(swigCPtr, this);
  }

  public void setIsRead(int value) {
    imapijJNI.c_Broadcast_isRead_set(swigCPtr, this, value);
  }

  public int getIsRead() {
    return imapijJNI.c_Broadcast_isRead_get(swigCPtr, this);
  }

  public void setMessageID(int value) {
    imapijJNI.c_Broadcast_messageID_set(swigCPtr, this, value);
  }

  public int getMessageID() {
    return imapijJNI.c_Broadcast_messageID_get(swigCPtr, this);
  }

  public void setMessageType(int value) {
    imapijJNI.c_Broadcast_messageType_set(swigCPtr, this, value);
  }

  public int getMessageType() {
    return imapijJNI.c_Broadcast_messageType_get(swigCPtr, this);
  }

  public void setConversationID(int value) {
    imapijJNI.c_Broadcast_conversationID_set(swigCPtr, this, value);
  }

  public int getConversationID() {
    return imapijJNI.c_Broadcast_conversationID_get(swigCPtr, this);
  }

  public void setMsg_sender(String value) {
    imapijJNI.c_Broadcast_msg_sender_set(swigCPtr, this, value);
  }

  public String getMsg_sender() {
    return imapijJNI.c_Broadcast_msg_sender_get(swigCPtr, this);
  }

  public void setMsg_receiver(String value) {
    imapijJNI.c_Broadcast_msg_receiver_set(swigCPtr, this, value);
  }

  public String getMsg_receiver() {
    return imapijJNI.c_Broadcast_msg_receiver_get(swigCPtr, this);
  }

  public void setMsg_body(byte[] value) {
    imapijJNI.c_Broadcast_msg_body_set(swigCPtr, this, value);
  }

  public byte[] getMsg_body() {
    return imapijJNI.c_Broadcast_msg_body_get(swigCPtr, this);
}

  public void setMsg_date(String value) {
    imapijJNI.c_Broadcast_msg_date_set(swigCPtr, this, value);
  }

  public String getMsg_date() {
    return imapijJNI.c_Broadcast_msg_date_get(swigCPtr, this);
  }

  public void setChatType(int value) {
    imapijJNI.c_Broadcast_chatType_set(swigCPtr, this, value);
  }

  public int getChatType() {
    return imapijJNI.c_Broadcast_chatType_get(swigCPtr, this);
  }

  public c_Broadcast() {
    this(imapijJNI.new_c_Broadcast(), true);
  }

}
