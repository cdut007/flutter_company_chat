/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public class NetrtcimCallback {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected NetrtcimCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(NetrtcimCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        imapijJNI.delete_NetrtcimCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    imapijJNI.NetrtcimCallback_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    imapijJNI.NetrtcimCallback_change_ownership(this, swigCPtr, true);
  }

  public NetrtcimCallback() {
    this(imapijJNI.new_NetrtcimCallback(), true);
    imapijJNI.NetrtcimCallback_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

  public void netrtcapi_msgrecv_callback(c_Message msg) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_msgrecv_callback(swigCPtr, this, c_Message.getCPtr(msg), msg); else imapijJNI.NetrtcimCallback_netrtcapi_msgrecv_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, c_Message.getCPtr(msg), msg);
  }

  public void netrtcapi_msg_status_changed_callback(c_Message msg) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_msg_status_changed_callback(swigCPtr, this, c_Message.getCPtr(msg), msg); else imapijJNI.NetrtcimCallback_netrtcapi_msg_status_changed_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, c_Message.getCPtr(msg), msg);
  }

  public void netrtcapi_sockevent_callback(int status) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_sockevent_callback(swigCPtr, this, status); else imapijJNI.NetrtcimCallback_netrtcapi_sockevent_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, status);
  }

  public void netrtcapi_pingtimeout_callback() {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_pingtimeout_callback(swigCPtr, this); else imapijJNI.NetrtcimCallback_netrtcapi_pingtimeout_callbackSwigExplicitNetrtcimCallback(swigCPtr, this);
  }

  public void netrtcapi_friendsync_callback(String chat, eShow show, String status) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_friendsync_callback(swigCPtr, this, chat, show.swigValue(), status); else imapijJNI.NetrtcimCallback_netrtcapi_friendsync_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, chat, show.swigValue(), status);
  }

  public void netrtcapi_Subscribe_callback(String from, boolean isRepeat) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_Subscribe_callback(swigCPtr, this, from, isRepeat); else imapijJNI.NetrtcimCallback_netrtcapi_Subscribe_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, from, isRepeat);
  }

  public void netrtcapi_UnSubscribed_callback(String from) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_UnSubscribed_callback(swigCPtr, this, from); else imapijJNI.NetrtcimCallback_netrtcapi_UnSubscribed_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, from);
  }

  public void netrtcapi_VCardRecv_callback(boolean result, String name, c_VCard vcard) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_VCardRecv_callback(swigCPtr, this, result, name, c_VCard.getCPtr(vcard), vcard); else imapijJNI.NetrtcimCallback_netrtcapi_VCardRecv_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, result, name, c_VCard.getCPtr(vcard), vcard);
  }

  public void netrtcapi_Muc_callback(eMucEvent event, long hmap_id) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_Muc_callback(swigCPtr, this, event.swigValue(), hmap_id); else imapijJNI.NetrtcimCallback_netrtcapi_Muc_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, event.swigValue(), hmap_id);
  }

  public void netrtcapi_Conversation_callback(eConversationEvent event, long hmap_id) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_Conversation_callback(swigCPtr, this, event.swigValue(), hmap_id); else imapijJNI.NetrtcimCallback_netrtcapi_Conversation_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, event.swigValue(), hmap_id);
  }

  public void netrtcapi_FileTransfer_callback(eFileTransferCase event, c_Message msg) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_FileTransfer_callback(swigCPtr, this, event.swigValue(), c_Message.getCPtr(msg), msg); else imapijJNI.NetrtcimCallback_netrtcapi_FileTransfer_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, event.swigValue(), c_Message.getCPtr(msg), msg);
  }

  public void netrtcapi_broadcastrecv_callback(c_Broadcast broadcast) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_broadcastrecv_callback(swigCPtr, this, c_Broadcast.getCPtr(broadcast), broadcast); else imapijJNI.NetrtcimCallback_netrtcapi_broadcastrecv_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, c_Broadcast.getCPtr(broadcast), broadcast);
  }

  public void netrtcapi_histroy_callback(String conversation, int chatType, int count) {
    if (getClass() == NetrtcimCallback.class) imapijJNI.NetrtcimCallback_netrtcapi_histroy_callback(swigCPtr, this, conversation, chatType, count); else imapijJNI.NetrtcimCallback_netrtcapi_histroy_callbackSwigExplicitNetrtcimCallback(swigCPtr, this, conversation, chatType, count);
  }

}
