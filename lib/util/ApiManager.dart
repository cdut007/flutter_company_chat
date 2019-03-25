import 'dart:convert';
import 'package:meta/meta.dart';
import 'dart:async';
import 'package:dio/dio.dart';
import 'package:flutter_app/global_config.dart';
import 'package:flutter_app/entity/ResponseEntity.dart';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:shared_preferences/shared_preferences.dart';

var BASE_URL = "https://uc.aitelian.cn/api";
var BASE_STAGE_URL = "https://ucstage.sealedchat.com/api";

class ApiManager {
  /**
   * 登录请求url
   */
  static Future login(var data) async {
    String login_url = BASE_URL + "/authuser";
    Response response = await reuqest(login_url, GlobalConfig.POST, data);
    ResponseEntity responseErrorEntity = await responseError(response);
    if(responseErrorEntity!=null){
      return  new Future.error(responseErrorEntity);
    }
    var responseData = response.data;
    return new Future.value(UserInfo.fromJson(responseData));
  }

  static Future<UserInfo> getUserInfo(var userId) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var user_result = await prefs.getString('user_info_'+userId) ;
    if(user_result == null || user_result.isEmpty){
      var responseErrorEntity = ResponseEntity();
      return  new Future.error(responseErrorEntity);
    }
    var decodedJson = json.decode(user_result).cast<String, dynamic>();
    return new Future.value(UserInfo.fromJson(decodedJson));
  }
  

  static Future<bool> saveUserInfo(var userId,var userInfo) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var save_result = await prefs.setString('user_info_'+userId,userInfo) ;
    return save_result;
  }

  static Future<bool> clearUserInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    bool rm_access_token = await prefs.remove('access_token') ;
    if(rm_access_token){
      rm_access_token =await prefs.remove('user_info') ;
    }
    return rm_access_token;
  }

  static Future<bool> saveToken(var token) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var success = await prefs.setString('access_token',token) ;
    return success;
  }

  static Future<String> getToken() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var access_token =prefs.getString('access_token') ;
    return access_token;
  }

  /**
   * 注册请求url
   */
  static Future register(var data) async {
    String login_url = BASE_URL + "/op/otp_regist";
    Response response = await reuqest(login_url, GlobalConfig.POST, data);
    ResponseEntity responseErrorEntity = await responseError(response);
    if(responseErrorEntity!=null){
      return  new Future.error(responseErrorEntity);
    }
    var responseData = response.data;
    return new Future.value(UserInfo.fromJson(responseData));
  }


  static Future responseError(Response response) async{
    var responseData = ResponseEntity();
    if (response.statusCode == 200) {
      var data = response.data;
      if(data['code']!=200) {
        responseData.code = data['code'];
        responseData.msg = data['msg'];
        return new Future.error(responseData);
      }else{
        return null;
      }
    } else {

      responseData.code = response.statusCode;
      //responseData.msg = response.toString();
      return new Future.error(responseData);
    }
  }

  static Future<Response> reuqest(var url, var httpRequsetType, var data) async {
    Dio dio = new Dio();
    Response response;
    print('***************请求url参数地址*************');
    print(url);
    print(data);
    if (httpRequsetType == GlobalConfig.GET) {
      response = await dio.get(url, data: data);
    } else if (httpRequsetType == GlobalConfig.POST) {
      response = await dio.post(url, data: data);
    }
    print('***************请求url参数地址结果START*************' + url);
    print(response.data);
    print(response.headers);
    print(response.request);
    print(response.statusCode);
    print('***************请求url参数地址结果END*************' + url);
    return response;
  }
}