import 'dart:convert';
import 'package:meta/meta.dart';
import 'dart:async';
import 'package:http/http.dart' as Http;
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:flutter_app/global_config.dart';
import 'package:flutter_app/entity/ResponseEntity.dart';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:shared_preferences/shared_preferences.dart';

var BASE_URL = "https://uc.aitelian.cn/api";
var BASE_STAGE_URL = "https://ucstage.sealedchat.com/api";

class ApiManager {
  ///
  /// 注册请求url
  ///
  static Future register(var data) async {
    String regist_url = BASE_URL + "/op/otp_regist";
    Response response = await reuqest(regist_url, GlobalConfig.POST, data);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }

  static putPublicParams(var data) async {
    String token = await getToken();
    data['token'] = token;
    data['device'] = 'android';
    data['app_ver'] = '';
    return data;
  }

  ///
  /// 忘记密码url
  ///
  static Future resetPassword(var data) async {
    String resetPassword_url = BASE_URL + "/op/forgetPassword";
    var requestData = await putPublicParams(data);
    Response response =
        await reuqest(resetPassword_url, GlobalConfig.POST, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }


  ///
  /// 获取用户个人信息
  ///
  static Future getUserProfile(var data) async {
    String user_profile_url = BASE_URL + "/profile";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(user_profile_url, GlobalConfig.POST,  requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }


  ///
  /// 获取sms code
  ///
  static Future otp(var data) async {
    String otp_url = BASE_URL + "/op/otp";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(otp_url, GlobalConfig.POST,  requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }

  ///
  /// 登录请求url
  ///
  static Future login(var data) async {
    String login_url = BASE_URL + "/authuser";
    Response response = await reuqest(login_url, GlobalConfig.POST, data);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }

  static getResponseData(Response response) {
    return response.data;
  }

  static Future<UserInfo> getUserInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var user_result = await prefs.getString('user_info');
    if (user_result == null || user_result.isEmpty) {
      var responseErrorEntity = ResponseEntity();
      return new Future.error(responseErrorEntity);
    }
    var decodedJson = json.decode(user_result).cast<String, dynamic>();
    return new Future.value(UserInfo.fromJson(decodedJson));
  }

  static Future<bool> saveUserInfo(var userInfo) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var save_result = await prefs.setString('user_info', userInfo);
    return save_result;
  }

  static Future<bool> clearUserInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    bool rm_access_token = await prefs.remove('access_token');
    if (rm_access_token) {
      rm_access_token = await prefs.remove('user_info');
    }
    return rm_access_token;
  }

  static Future<bool> saveToken(var token) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var success = await prefs.setString('access_token', token);
    return success;
  }

  static Future<String> getToken() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var access_token = prefs.getString('access_token');
    return access_token;
  }

  static parseErrorInfo(errorData) {
    if (errorData is DioError) {
      DioError dioError = errorData;
      ResponseEntity responseEntity = ResponseEntity();
      if (dioError.response == null) {
        //check network.
        responseEntity.code = 0;
        responseEntity.msg = dioError.message;
        print(dioError);
        return responseEntity;
      }
      responseEntity.code = dioError.response.statusCode;
      responseEntity.msg = dioError.message;
      if (dioError.response.data != null) {
        var msg = dioError.response.data['description'];
        if (msg == null) {
          msg = dioError.response.data['message'];
        }
        if (msg == null) {
          msg = dioError.response.data.toString();
        }

        if (msg != null) {
          responseEntity.msg = msg;
        }
      }
      print(dioError);
      return responseEntity;
    } else {
      return errorData;
    }
  }

  static Future responseError(Response response) async {
    var responseData = ResponseEntity();
    if (response.statusCode == 200) {
      var data = response.data;
      var code = data['code'];
      var msg = data['msg'];
      if (code == null) {
        code = -100;
      }
      if (msg == null) {
        msg = '未知格式';
      }
      if (code != 200) {
        responseData.code = code;
        responseData.msg = msg;
        return new Future.error(responseData);
      } else {
        return null;
      }
    } else {
      responseData.code = response.statusCode;
      //responseData.msg = response.toString();
      return new Future.error(responseData);
    }
  }

  static Future<Response> reuqest(
      var url, var httpRequsetType, var data) async {
    Dio dio = new Dio();
    Options options = new Options(
//        baseUrl:"https://www.xx.com/api",
        connectTimeout: 7000,
        receiveTimeout: 3000,
        contentType: ContentType.json);

    Response response;
    print('***************请求url参数地址*************');
    print(url);
    print(data);

    if (httpRequsetType == GlobalConfig.GET) {
      response = await dio.get(url, options: options, data: data);
    } else if (httpRequsetType == GlobalConfig.POST) {
      response = await dio.post(url, options: options, data: data);
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
