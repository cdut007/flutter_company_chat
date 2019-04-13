/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public enum eMessageType {
  C_MESSAGE_TYPE_DRAFT,
  C_MESSAGE_TYPE_TEXT,
  C_MESSAGE_TYPE_FILE_ONLY,
  C_MESSAGE_TYPE_FILE_WITH_THUMBNAIL,
  C_MESSAGE_TYPE_CUSTOM_TEXT,
  C_MESSAGE_TYPE_TIPS;

  public final int swigValue() {
    return swigValue;
  }

  public static eMessageType swigToEnum(int swigValue) {
    eMessageType[] swigValues = eMessageType.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (eMessageType swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + eMessageType.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private eMessageType() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private eMessageType(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private eMessageType(eMessageType swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

