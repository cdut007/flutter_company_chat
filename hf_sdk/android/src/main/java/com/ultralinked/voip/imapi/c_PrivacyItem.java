/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public class c_PrivacyItem {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected c_PrivacyItem(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(c_PrivacyItem obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        imapijJNI.delete_c_PrivacyItem(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setJid(String value) {
    imapijJNI.c_PrivacyItem_jid_set(swigCPtr, this, value);
  }

  public String getJid() {
    return imapijJNI.c_PrivacyItem_jid_get(swigCPtr, this);
  }

  public void setAll(boolean value) {
    imapijJNI.c_PrivacyItem_all_set(swigCPtr, this, value);
  }

  public boolean getAll() {
    return imapijJNI.c_PrivacyItem_all_get(swigCPtr, this);
  }

  public void setPush(boolean value) {
    imapijJNI.c_PrivacyItem_push_set(swigCPtr, this, value);
  }

  public boolean getPush() {
    return imapijJNI.c_PrivacyItem_push_get(swigCPtr, this);
  }

  public void setBlock(boolean value) {
    imapijJNI.c_PrivacyItem_block_set(swigCPtr, this, value);
  }

  public boolean getBlock() {
    return imapijJNI.c_PrivacyItem_block_get(swigCPtr, this);
  }

  public void setOrder(long value) {
    imapijJNI.c_PrivacyItem_order_set(swigCPtr, this, value);
  }

  public long getOrder() {
    return imapijJNI.c_PrivacyItem_order_get(swigCPtr, this);
  }

  public c_PrivacyItem() {
    this(imapijJNI.new_c_PrivacyItem(), true);
  }

}
