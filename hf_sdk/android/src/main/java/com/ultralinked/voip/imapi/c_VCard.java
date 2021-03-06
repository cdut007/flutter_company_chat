/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public class c_VCard {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected c_VCard(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(c_VCard obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        imapijJNI.delete_c_VCard(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setJid(byte[] value) {
    imapijJNI.c_VCard_jid_set(swigCPtr, this, value);
  }

  public byte[] getJid() {
    return imapijJNI.c_VCard_jid_get(swigCPtr, this);
}

  public void setFamilyName(byte[] value) {
    imapijJNI.c_VCard_familyName_set(swigCPtr, this, value);
  }

  public byte[] getFamilyName() {
    return imapijJNI.c_VCard_familyName_get(swigCPtr, this);
}

  public void setNickName(byte[] value) {
    imapijJNI.c_VCard_nickName_set(swigCPtr, this, value);
  }

  public byte[] getNickName() {
    return imapijJNI.c_VCard_nickName_get(swigCPtr, this);
}

  public void setURL(byte[] value) {
    imapijJNI.c_VCard_URL_set(swigCPtr, this, value);
  }

  public byte[] getURL() {
    return imapijJNI.c_VCard_URL_get(swigCPtr, this);
}

  public void setBirthday(String value) {
    imapijJNI.c_VCard_birthday_set(swigCPtr, this, value);
  }

  public String getBirthday() {
    return imapijJNI.c_VCard_birthday_get(swigCPtr, this);
  }

  public void setRole(byte[] value) {
    imapijJNI.c_VCard_role_set(swigCPtr, this, value);
  }

  public byte[] getRole() {
    return imapijJNI.c_VCard_role_get(swigCPtr, this);
}

  public void setHomeAddr(byte[] value) {
    imapijJNI.c_VCard_homeAddr_set(swigCPtr, this, value);
  }

  public byte[] getHomeAddr() {
    return imapijJNI.c_VCard_homeAddr_get(swigCPtr, this);
}

  public void setLocality(byte[] value) {
    imapijJNI.c_VCard_locality_set(swigCPtr, this, value);
  }

  public byte[] getLocality() {
    return imapijJNI.c_VCard_locality_get(swigCPtr, this);
}

  public void setDescription(byte[] value) {
    imapijJNI.c_VCard_description_set(swigCPtr, this, value);
  }

  public byte[] getDescription() {
    return imapijJNI.c_VCard_description_get(swigCPtr, this);
}

  public void setTelKey(String[] value) {
    imapijJNI.c_VCard_telKey_set(swigCPtr, this, value);
  }

  public String[] getTelKey() {
    return imapijJNI.c_VCard_telKey_get(swigCPtr, this);
  }

  public void setTelValue(String[] value) {
    imapijJNI.c_VCard_telValue_set(swigCPtr, this, value);
  }

  public String[] getTelValue() {
    return imapijJNI.c_VCard_telValue_get(swigCPtr, this);
  }

  public void setTelLength(int value) {
    imapijJNI.c_VCard_telLength_set(swigCPtr, this, value);
  }

  public int getTelLength() {
    return imapijJNI.c_VCard_telLength_get(swigCPtr, this);
  }

  public void setEmailKey(String[] value) {
    imapijJNI.c_VCard_emailKey_set(swigCPtr, this, value);
  }

  public String[] getEmailKey() {
    return imapijJNI.c_VCard_emailKey_get(swigCPtr, this);
  }

  public void setEmailValue(SWIGTYPE_p_p_signed_char value) {
    imapijJNI.c_VCard_emailValue_set(swigCPtr, this, SWIGTYPE_p_p_signed_char.getCPtr(value));
  }

  public SWIGTYPE_p_p_signed_char getEmailValue() {
    long cPtr = imapijJNI.c_VCard_emailValue_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_signed_char(cPtr, false);
  }

  public void setEmailLength(int value) {
    imapijJNI.c_VCard_emailLength_set(swigCPtr, this, value);
  }

  public int getEmailLength() {
    return imapijJNI.c_VCard_emailLength_get(swigCPtr, this);
  }

  public c_VCard() {
    this(imapijJNI.new_c_VCard(), true);
  }

}
