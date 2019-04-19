/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.rtcapi;

public enum eACC_STATUS {
  ACC_STATUS_REG_OK,
  ACC_STATUS_REG_TIMEOUT,
  ACC_STATUS_REG_ACCOUNT_ERROR,
  ACC_STATUS_REG_ACCOUNT_FAILURE,
  ACC_STATUS_LOGOUT,
  ACC_STATUS_LOGOUT_FAILURE,
  ACC_STATUS_CONNECT_ERROR,
  ACC_STATUS_CONNECT_SUCCESS,
  ACC_STATUS_MESSAGE_MESSAGE,
  ACC_STATUS_NOTIFY_MESSAGE;

  public final int swigValue() {
    return swigValue;
  }

  public static eACC_STATUS swigToEnum(int swigValue) {
    eACC_STATUS[] swigValues = eACC_STATUS.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (eACC_STATUS swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + eACC_STATUS.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private eACC_STATUS() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private eACC_STATUS(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private eACC_STATUS(eACC_STATUS swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}
