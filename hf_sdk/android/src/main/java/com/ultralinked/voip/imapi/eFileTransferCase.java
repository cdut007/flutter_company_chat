/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.imapi;

public enum eFileTransferCase {
  FILE_GET_URL_TIMEOUT,
  FILE_GET_URL_SUCCEED,
  FILE_ERROR_TOO_LARGE,
  FILE_ERROR_RESOURCE_CONSTRAINT,
  FILE_ERROR_NOT_ALLOWED,
  FILE_WAIT_UPLOAD,
  FILE_UPLOAD_SUCCEED;

  public final int swigValue() {
    return swigValue;
  }

  public static eFileTransferCase swigToEnum(int swigValue) {
    eFileTransferCase[] swigValues = eFileTransferCase.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (eFileTransferCase swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + eFileTransferCase.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private eFileTransferCase() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private eFileTransferCase(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private eFileTransferCase(eFileTransferCase swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

