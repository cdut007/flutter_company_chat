/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.rtcapi;

public enum eNETRTC_CALL_STATE {
  netrtc_CALL_STATE_IDLE,
  netrtc_CALL_STATE_INCOMING,
  netrtc_CALL_STATE_OUTGOING,
  netrtc_CALL_STATE_ESTABLISHED,
  netrtc_CALL_STATE_HOLDON;

  public final int swigValue() {
    return swigValue;
  }

  public static eNETRTC_CALL_STATE swigToEnum(int swigValue) {
    eNETRTC_CALL_STATE[] swigValues = eNETRTC_CALL_STATE.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (eNETRTC_CALL_STATE swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + eNETRTC_CALL_STATE.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private eNETRTC_CALL_STATE() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private eNETRTC_CALL_STATE(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private eNETRTC_CALL_STATE(eNETRTC_CALL_STATE swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

