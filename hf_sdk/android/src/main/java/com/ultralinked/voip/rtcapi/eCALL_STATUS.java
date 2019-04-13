/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.rtcapi;

public enum eCALL_STATUS {
  CALL_STATUS_INCOMING_CALL,
  CALL_STATUS_REINVITE,
  CALL_STATUS_PROCEEDING,
  CALL_STATUS_CALL_CLOSED,
  CALL_STATUS_OUTGOING_CALL_ANSWERED,
  CALL_STATUS_RINGING,
  CALL_STATUS_CALL_ACK,
  CALL_STATUS_CALL_REQUESTFAILURE,
  CALL_STATUS_CALL_NOANSWER,
  CALL_STATUS_CALL_REDIRECTED,
  CALL_STATUS_CALL_CANCELLED,
  CALL_STATUS_CALL_TIMEOUT,
  CALL_STATUS_CALL_SERVERFAILURE,
  CALL_STATUS_CALL_MESSAGE_REQUESTFAILURE,
  CALL_STATUS_CALL_RELEASED,
  CALL_STATUS_INVITE_RESULT,
  CALL_STATUS_GLOBAL_FAILURE,
  CALL_STATUS_CALL_REINVITE,
  CALL_STATUS_CALL_OPPOSITE_HOLDON,
  CALL_STATUS_CALL_OPPOSITE_HOLDOFF,
  CALL_STATUS_CALL_OPPOSITE_MUTE,
  CALL_STATUS_CALL_OPPOSITE_UNMUTE,
  CALL_STATUS_CALL_AUDIO_CLOSE;

  public final int swigValue() {
    return swigValue;
  }

  public static eCALL_STATUS swigToEnum(int swigValue) {
    eCALL_STATUS[] swigValues = eCALL_STATUS.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (eCALL_STATUS swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + eCALL_STATUS.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private eCALL_STATUS() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private eCALL_STATUS(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private eCALL_STATUS(eCALL_STATUS swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

