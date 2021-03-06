/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.ultralinked.voip.rtcapi;

public class rtcapij implements rtcapijConstants {
  public static void netrtc_init(String lib_license, String useragentname) {
    rtcapijJNI.netrtc_init(lib_license, useragentname);
  }

  public static void netrtc_init_done() {
    rtcapijJNI.netrtc_init_done();
  }

  public static void netrtc_setcallback(eCALLBACK_TYPE callback_type, long pCallBack) {
    rtcapijJNI.netrtc_setcallback(callback_type.swigValue(), pCallBack);
  }

  public static String netrtc_version() {
    return rtcapijJNI.netrtc_version();
  }

  public static String netrtc_log(int level, String logstr) {
    return rtcapijJNI.netrtc_log(level, logstr);
  }

  public static void netrtc_set_context(String contextname, Object context) {
    rtcapijJNI.netrtc_set_context(contextname, context);
  }

  public static void netrtc_set_context2(String contextname, SWIGTYPE_p_void jvm, Object context) {
    rtcapijJNI.netrtc_set_context2(contextname, SWIGTYPE_p_void.getCPtr(jvm), context);
  }

  public static void netrtc_set_config(String config_key, String config_value) {
    rtcapijJNI.netrtc_set_config(config_key, config_value);
  }

  public static String netrtc_get_config(String config_key) {
    return rtcapijJNI.netrtc_get_config(config_key);
  }

  public static String netrtc_get_codeclist() {
    return rtcapijJNI.netrtc_get_codeclist();
  }

  public static long netrtc_hashmap_init() {
    return rtcapijJNI.netrtc_hashmap_init();
  }

  public static int netrtc_hashmap_set(long hmap_id, String key, String value) {
    return rtcapijJNI.netrtc_hashmap_set(hmap_id, key, value);
  }

  public static long netrtc_hashmap_get(long hmap_id, String key) {
    return rtcapijJNI.netrtc_hashmap_get(hmap_id, key);
  }

  public static String netrtc_hashmap_getstr(long hmap_id, String key) {
    return rtcapijJNI.netrtc_hashmap_getstr(hmap_id, key);
  }

  public static long netrtc_hashmap_first(long hmap_id) {
    return rtcapijJNI.netrtc_hashmap_first(hmap_id);
  }

  public static long netrtc_hashmap_next(long hmap_index_id) {
    return rtcapijJNI.netrtc_hashmap_next(hmap_index_id);
  }

  public static String netrtc_hashmap_this_key(long hmap_index_id) {
    return rtcapijJNI.netrtc_hashmap_this_key(hmap_index_id);
  }

  public static long netrtc_hashmap_this_value(long hmap_index_id) {
    return rtcapijJNI.netrtc_hashmap_this_value(hmap_index_id);
  }

  public static long netrtc_hashmap_count(long hmap_id) {
    return rtcapijJNI.netrtc_hashmap_count(hmap_id);
  }

  public static void netrtc_hashmap_clear(long hmap_id) {
    rtcapijJNI.netrtc_hashmap_clear(hmap_id);
  }

  public static void netrtc_hashmap_free(long hmap_id) {
    rtcapijJNI.netrtc_hashmap_free(hmap_id);
  }

  public static long netrtc_acc_add(String acc_name, String username, String password, long acc_params) {
    return rtcapijJNI.netrtc_acc_add(acc_name, username, password, acc_params);
  }

  public static int netrtc_acc_count() {
    return rtcapijJNI.netrtc_acc_count();
  }

  public static long netrtc_acc_names(SWIGTYPE_p_p_char acc_names, int accs_count) {
    return rtcapijJNI.netrtc_acc_names(SWIGTYPE_p_p_char.getCPtr(acc_names), accs_count);
  }

  public static int netrtc_acc_set_config(String acc_name, String config_key, String config_value) {
    return rtcapijJNI.netrtc_acc_set_config(acc_name, config_key, config_value);
  }

  public static String netrtc_acc_get_config(String acc_name, String config_key) {
    return rtcapijJNI.netrtc_acc_get_config(acc_name, config_key);
  }

  public static long netrtc_acc_get_configs(String acc_name) {
    return rtcapijJNI.netrtc_acc_get_configs(acc_name);
  }

  public static int netrtc_acc_del(String acc_name) {
    return rtcapijJNI.netrtc_acc_del(acc_name);
  }

  public static int netrtc_send_subscribe(String acc_name, String event) {
    return rtcapijJNI.netrtc_send_subscribe(acc_name, event);
  }

  public static int netrtc_acc_register(String acc_name) {
    return rtcapijJNI.netrtc_acc_register(acc_name);
  }

  public static void netrtc_acc_tryconnect(String acc_name) {
    rtcapijJNI.netrtc_acc_tryconnect(acc_name);
  }

  public static int netrtc_acc_unregister(String acc_name) {
    return rtcapijJNI.netrtc_acc_unregister(acc_name);
  }

  public static int netrtc_acc_reset_network(String acc_name) {
    return rtcapijJNI.netrtc_acc_reset_network(acc_name);
  }

  public static int netrtc_acc_reset_network_cause(String acc_name, String cause_str) {
    return rtcapijJNI.netrtc_acc_reset_network_cause(acc_name, cause_str);
  }

  public static int netrtc_acc_get_register_status(String acc_name) {
    return rtcapijJNI.netrtc_acc_get_register_status(acc_name);
  }

  public static int netrtc_acc_get_connect_status(String acc_name) {
    return rtcapijJNI.netrtc_acc_get_connect_status(acc_name);
  }

  public static eNETRTC_CONNECTION_MODE netrtc_acc_get_connectionmode(String acc_name) {
    return eNETRTC_CONNECTION_MODE.swigToEnum(rtcapijJNI.netrtc_acc_get_connectionmode(acc_name));
  }

  public static String netrtc_call_getsipcallid(long call_id) {
    return rtcapijJNI.netrtc_call_getsipcallid(call_id);
  }

  public static String netrtc_call_audio(String acc_name, String call_number, long call_params) {
    return rtcapijJNI.netrtc_call_audio(acc_name, call_number, call_params);
  }

  public static String netrtc_call_audio_sdp(String acc_name, String call_number, long call_params, String sdp) {
    return rtcapijJNI.netrtc_call_audio_sdp(acc_name, call_number, call_params, sdp);
  }

  public static int netrtc_call_recall(long callid) {
    return rtcapijJNI.netrtc_call_recall(callid);
  }

  public static int netrtc_call_recall_sdp(long callid, String sdp) {
    return rtcapijJNI.netrtc_call_recall_sdp(callid, sdp);
  }

  public static int netrtc_acc_message(String acc_name, String message_to, String message) {
    return rtcapijJNI.netrtc_acc_message(acc_name, message_to, message);
  }

  public static String netrtc_call_acc_name(long callid) {
    return rtcapijJNI.netrtc_call_acc_name(callid);
  }

  public static int netrtc_call_hangup(long callid) {
    return rtcapijJNI.netrtc_call_hangup(callid);
  }

  public static int netrtc_call_hangup_withcode(long callid, int apphangupcode) {
    return rtcapijJNI.netrtc_call_hangup_withcode(callid, apphangupcode);
  }

  public static int netrtc_call_ring_sdp(long callid, String sdp) {
    return rtcapijJNI.netrtc_call_ring_sdp(callid, sdp);
  }

  public static int netrtc_call_accept(long callid) {
    return rtcapijJNI.netrtc_call_accept(callid);
  }

  public static int netrtc_call_accept_sdp(long call_id, String sdp) {
    return rtcapijJNI.netrtc_call_accept_sdp(call_id, sdp);
  }

  public static int netrtc_call_reject(long callid) {
    return rtcapijJNI.netrtc_call_reject(callid);
  }

  public static eNETRTC_CALL_STATE netrtc_call_getstatus(long callid) {
    return eNETRTC_CALL_STATE.swigToEnum(rtcapijJNI.netrtc_call_getstatus(callid));
  }

  public static void netrtc_call_senddtmf(long callid, char dtmfDigit, int inBand) {
    rtcapijJNI.netrtc_call_senddtmf(callid, dtmfDigit, inBand);
  }

  public static void netrtc_call_mutemic(long callid, int mute) {
    rtcapijJNI.netrtc_call_mutemic(callid, mute);
  }

  public static int netrtc_call_quality(long callid) {
    return rtcapijJNI.netrtc_call_quality(callid);
  }

  public static int netrtc_call_holdon(long callid) {
    return rtcapijJNI.netrtc_call_holdon(callid);
  }

  public static int netrtc_call_holdoff(long callid) {
    return rtcapijJNI.netrtc_call_holdoff(callid);
  }

  public static void netrtc_call_setmicVol(long callid, int vol) {
    rtcapijJNI.netrtc_call_setmicVol(callid, vol);
  }

  public static int netrtc_call_getmicVol(long callid) {
    return rtcapijJNI.netrtc_call_getmicVol(callid);
  }

  public static void netrtc_call_setspkVol(long callid, int vol) {
    rtcapijJNI.netrtc_call_setspkVol(callid, vol);
  }

  public static int netrtc_call_getspkVol(long callid) {
    return rtcapijJNI.netrtc_call_getspkVol(callid);
  }

  public static int getfileinfo(String file) {
    return rtcapijJNI.getfileinfo(file);
  }

  public static int convertFile(String src_file, String dst_file, int channels, int bits, int rate) {
    return rtcapijJNI.convertFile(src_file, dst_file, channels, bits, rate);
  }

  public static int netrtc_startRecordingMicrophone(String fileNameUTF8) {
    return rtcapijJNI.netrtc_startRecordingMicrophone(fileNameUTF8);
  }

  public static int netrtc_stopRecordingMicrophone() {
    return rtcapijJNI.netrtc_stopRecordingMicrophone();
  }

  public static int netrtc_startRecordingPlayout(long callid, String filename) {
    return rtcapijJNI.netrtc_startRecordingPlayout(callid, filename);
  }

  public static int netrtc_stopRecordingPlayout(long callid) {
    return rtcapijJNI.netrtc_stopRecordingPlayout(callid);
  }

  public static int netrtc_startPlayFileAsMicrophone(long callid, String fileName, int loop, int mixwithmacirophone) {
    return rtcapijJNI.netrtc_startPlayFileAsMicrophone(callid, fileName, loop, mixwithmacirophone);
  }

  public static int netrtc_stopPlayingFileAsMicrophone(long callid) {
    return rtcapijJNI.netrtc_stopPlayingFileAsMicrophone(callid);
  }

  public static int netrtc_startPlayFileLocally(long callid, String fileName, int loop) {
    return rtcapijJNI.netrtc_startPlayFileLocally(callid, fileName, loop);
  }

  public static int netrtc_stopPlayingFileLocally(long callid) {
    return rtcapijJNI.netrtc_stopPlayingFileLocally(callid);
  }

  public static int netrtc_call_startaudio(long callid) {
    return rtcapijJNI.netrtc_call_startaudio(callid);
  }

  public static int netrtc_call_stopaudio(long callid) {
    return rtcapijJNI.netrtc_call_stopaudio(callid);
  }

  public static void logEncrypt(SWIGTYPE_p_unsigned_char before) {
    rtcapijJNI.logEncrypt(SWIGTYPE_p_unsigned_char.getCPtr(before));
  }

  public static void logFileDecrypt(String filename) {
    rtcapijJNI.logFileDecrypt(filename);
  }

  public static int c_netrtcapi_acc_callback(String acc_name, eACC_STATUS nState, long acc_datas) {
    return rtcapijJNI.c_netrtcapi_acc_callback(acc_name, nState.swigValue(), acc_datas);
  }

  public static int c_netrtcapi_call_callback(long callid, eCALL_STATUS nState, long call_datas) {
    return rtcapijJNI.c_netrtcapi_call_callback(callid, nState.swigValue(), call_datas);
  }

  public static void setRegisteredCallbackObject(NetrtcCallback value) {
    rtcapijJNI.registeredCallbackObject_set(NetrtcCallback.getCPtr(value), value);
  }

  public static NetrtcCallback getRegisteredCallbackObject() {
    long cPtr = rtcapijJNI.registeredCallbackObject_get();
    return (cPtr == 0) ? null : new NetrtcCallback(cPtr, false);
  }

  public static void setCallbackObject(NetrtcCallback callback) {
    rtcapijJNI.setCallbackObject(NetrtcCallback.getCPtr(callback), callback);
  }

}
