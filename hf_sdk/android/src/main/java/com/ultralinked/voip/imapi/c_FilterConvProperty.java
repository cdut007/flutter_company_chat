/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public class c_FilterConvProperty {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected c_FilterConvProperty(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(c_FilterConvProperty obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        imapijJNI.delete_c_FilterConvProperty(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setFlag(int value) {
    imapijJNI.c_FilterConvProperty_flag_set(swigCPtr, this, value);
  }

  public int getFlag() {
    return imapijJNI.c_FilterConvProperty_flag_get(swigCPtr, this);
  }

  public void setContains(boolean value) {
    imapijJNI.c_FilterConvProperty_contains_set(swigCPtr, this, value);
  }

  public boolean getContains() {
    return imapijJNI.c_FilterConvProperty_contains_get(swigCPtr, this);
  }

  public void setMatchKey(String value) {
    imapijJNI.c_FilterConvProperty_matchKey_set(swigCPtr, this, value);
  }

  public String getMatchKey() {
    return imapijJNI.c_FilterConvProperty_matchKey_get(swigCPtr, this);
  }

  public c_FilterConvProperty() {
    this(imapijJNI.new_c_FilterConvProperty(), true);
  }

}
