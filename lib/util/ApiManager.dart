import 'dart:convert';
import 'package:meta/meta.dart';
import 'dart:async';
import 'package:http/http.dart' as Http;
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:flutter_app/entity/ResponseEntity.dart';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
var BASE_URL = "http://39.96.161.237:9090/api";
var BASE_STAGE_URL = "https://ucstage.sealedchat.com/api";

class ApiManager {
  static var refresh_tag = 'refreshUserInfo';



  ///
  /// 注册请求url
  ///
  static Future register(var data) async {
    String regist_url = BASE_URL + "/user/register";
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
    String resetPassword_url = BASE_URL + "/pwd/reset";
    var requestData = await putPublicParams(data);
    Response response =
        await reuqest(resetPassword_url, GlobalConfig.PATCH, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  ///
  /// 获取七牛token信息
  ///
  static Future uploadFile(var fileName, var filePath) async {
    var token = await getToken();
    Map<String, dynamic> qiNiuJson = await getQiNiuToken({'token': token});
    String qiniu_upload_url = qiNiuJson['domain'];
    var qiNiuToken = qiNiuJson['token'];
    Dio dio = new Dio();
    print('qiniu_upload_url=' + qiniu_upload_url);
    print('upload file info=' + fileName + ';filePath=' + filePath);
    FormData formData = new FormData.from({
      "upload_token": qiNiuToken,
      "fileName": fileName,
      "fileBinaryData": new UploadFileInfo(new File(filePath), fileName)
    });
    Response response = await dio.post(qiniu_upload_url, data: formData);
    ResponseEntity responseErrorEntity = await responseQiuniuError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }

  static Future responseQiuniuError(Response response) async {
    var responseData = ResponseEntity();
    if (response.statusCode == 200) {
      var data = response.data;
      var code = data['code'];
      var msg = data['message'];
      if (code == null) {
        code = '-100';
      }
      if (msg == null) {
        msg = '未知格式';
      }
      print(code);
      if (code != '100000') {
        responseData.code = code;
        responseData.msg = msg;
        return new Future.error(responseData);
      } else {
        return null;
      }
    } else {
      responseData.code = response.statusCode;
      responseData.msg = response.toString();
      return new Future.error(responseData);
    }
  }

  ///
  /// 获取七牛token信息
  ///
  static Future getQiNiuToken(var data) async {
    String qiniu_token_url = BASE_URL + "/qiniu/token";
    var requestData = await putPublicParams(data);
    Response response =
        await reuqest(qiniu_token_url, GlobalConfig.GET, requestData);
//    ResponseEntity responseErrorEntity = await responseError(response);
//    if (responseErrorEntity != null) {
//      return new Future.error(responseErrorEntity);
//    }
    var responseData = getResponseData(response);
    return new Future.value(responseData);
  }

  ///
  /// 更新用户个人信息
  ///
  static Future updateUserProfile(var data) async {
    String user_profile_url = BASE_URL + "/user/profile";
    var requestData = await putPublicParams(data);
    Response response =
        await reuqest(user_profile_url, GlobalConfig.PUT, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }

  ///
  /// 获取申请用户名片信息列表
  ///
  static Future getApplyVcardList(var data) async {
    String user_profile_url = BASE_URL + "/card/findCard";
    var requestData = await putPublicParams(data);
    Response response =
    await reuqest(user_profile_url, GlobalConfig.GET, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData['data']));
  }

  ///
  /// 获取用户名片信息
  ///
  static Future getUserVcardList(var data) async {
    String user_profile_url = BASE_URL + "/card/findCard";
    var requestData = await putPublicParams(data);
    Response response =
    await reuqest(user_profile_url, GlobalConfig.GET, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData['data']));
  }

  ///
  /// 获取用户个人信息
  ///
  static Future getUserProfile(var data) async {
    String user_profile_url = BASE_URL + "/user/profile";
    var requestData = await putPublicParams(data);
    Response response =
        await reuqest(user_profile_url, GlobalConfig.GET, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData['data']));
  }

  ///
  /// 获取sms code
  ///
  static Future otp(var data) async {
    String otp_url = BASE_URL + "/otp/send";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(otp_url, GlobalConfig.GET, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(UserInfo.fromJson(responseData));
  }

  ///
  /// 发布动态 url
  ///
  static Future postMoments(var data) async {

    String url = BASE_URL + "/post/moment";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(url, GlobalConfig.POST, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  ///
  /// 获取动态 url
  ///
  static Future getPostMomentsList(var data) async {
    if(true){
      return new Future.value([]);
    }
    String url = BASE_URL + "/post/moment";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(url, GlobalConfig.GET, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }


  ///
  /// 刷新token url
  ///
  static Future refreshToken(var data) async {
    String url = BASE_URL + "/user/token/refresh";
    Response response = await reuqest(url, GlobalConfig.GET, data);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  static getResponseData(Response response) {
    return response.data;
  }


  ///
  /// 申请发送名片请求url
  ///
  static Future applyCard(var data) async {
    String url = BASE_URL + "/card/apply";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(url, GlobalConfig.POST, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  ///
  /// 接收申请名片请求url
  ///
  static Future acceptedCard(var data) async {
    String url = BASE_URL + "/card/"+data['applyId']+"/exchange";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(url, GlobalConfig.POST, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  ///
  /// 新增名片请求url
  ///
  static Future updateCard(var data) async {
    String url = BASE_URL + "/card/updateCard";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(url, GlobalConfig.PUT, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  ///
  /// 更新名片请求url
  ///
  static Future createCard(var data) async {
    String url = BASE_URL + "/card/addCard";
    var requestData = await putPublicParams(data);
    Response response = await reuqest(url, GlobalConfig.POST, requestData);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    return new Future.value(responseData['data']);
  }

  ///
  /// 登录请求url
  ///
  static Future login(var data) async {
    String url = BASE_URL + "/user/login";
    Response response = await reuqest(url, GlobalConfig.POST, data);
    ResponseEntity responseErrorEntity = await responseError(response);
    if (responseErrorEntity != null) {
      return new Future.error(responseErrorEntity);
    }
    var responseData = getResponseData(response);
    var token = responseData['data'];
    var refreshTokeInfo =
        await refreshToken({'token': token, 'expireDay': '15'});

    if (refreshTokeInfo is ResponseEntity) {
      return new Future.error(refreshTokeInfo);
    } else {
      token = refreshTokeInfo;
      print('refresh token:' + token);
    }

    return new Future.value(token);
  }

  static Future<UserInfo> getUserInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var user_result = await prefs.getString('user_info');
    if (user_result == null || user_result.isEmpty) {
      print('获取本地用户信息失败');
      var responseErrorEntity = ResponseEntity();
      responseErrorEntity.code = "-10002";
      responseErrorEntity.msg = "no user found in local";
      return new Future.error(responseErrorEntity);
    }
    print('获取本地用户信息：' + user_result);
    var decodedJson = json.decode(user_result).cast<String, dynamic>();
    return new Future.value(UserInfo.fromJson(decodedJson));
  }

  static Future<UserInfo> getVcardListInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var user_result = await prefs.getString('vcard_list_info');
    if (user_result == null || user_result.isEmpty) {
      print('获取本地用户名片夹信息失败');
      var responseErrorEntity = ResponseEntity();
      responseErrorEntity.code = "-10002";
      responseErrorEntity.msg = "no user vcardInfo found in local";
      return new Future.error(responseErrorEntity);
    }
    print('获取本地用户名片夹信息：' + user_result);
    var decodedJson = json.decode(user_result).cast<String, dynamic>();
    return new Future.value(UserInfo.fromJson(decodedJson));
  }

  static Future<bool> saveVcardListInfo(var vcardListInfo) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    print('保存本地用户名片夹信息：' + vcardListInfo);
    var save_result = await prefs.setString('vcard_list_info', vcardListInfo);
    return save_result;
  }

  static Future<bool> saveUserInfo(var userInfo) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    print('保存本地用户信息：' + userInfo);
    var save_result = await prefs.setString('user_info', userInfo);
    return save_result;
  }

  static Future<bool> logout() async {
    var logout = await clearUserInfo();
    return logout;
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

  static Future<bool> isLoggedIn() async {
    var token = await getToken();
    return token != null;
  }

  static Future<String> getToken() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var access_token = prefs.getString('access_token');
    return access_token;
  }

  static parseErrorInfo( errorData) {
    if (errorData is DioError) {
      DioError dioError = errorData;
      ResponseEntity responseEntity = ResponseEntity();
      if (dioError.response == null) {
        //check network.
        responseEntity.code = 0;
        responseEntity.msg = dioError.message;
        if (dioError.type == DioErrorType.CONNECT_TIMEOUT) {
          responseEntity.msg = '服务不可用，连接超时，请检查网络';
        } else if (dioError.type == DioErrorType.RECEIVE_TIMEOUT) {
          responseEntity.msg = '请求服务响应超时，请检查网络';
        }
        print('no response found');
        print(dioError);
        return responseEntity;
      }
      responseEntity.code = dioError.response.statusCode;
      responseEntity.msg = dioError.message;
      print(dioError.type);
      if (dioError.type == DioErrorType.CONNECT_TIMEOUT) {
        responseEntity.msg = '服务不可用，连接超时，请检查网络';
      } else if (dioError.type == DioErrorType.RECEIVE_TIMEOUT) {
        responseEntity.msg = '请求服务响应超时，请检查网络';
      } else if (dioError.type == DioErrorType.RESPONSE) {
        responseEntity.msg = dioError.response.toString();
        var data = dioError.response.data;
        if(data!=null){
          if(dioError.response.statusCode == 500){
            try{
              responseEntity.code ='500';
              responseEntity.msg = '服务不可用，请稍后再试';
            }catch (e){
              print(e);
            }
          }else{
            try{
              responseEntity.code = data['code'];
              responseEntity.msg = data['message'];
            }catch (e){
              print(e);
            }
          }

          //如果 token 过期 跳到登录页面。
//          if(code == "-100021"){
//            responseEntity.msg = "用户信息已过期，请重新登录";
//          }
        }

      } else {
        if (dioError.response.data != null) {
          var msg = dioError.response.data['message'];
          if (msg == null) {
            msg = dioError.response.data.toString();
          }

          if (msg != null) {
            responseEntity.msg = msg;
          }
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
      var msg = data['message'];
      if (code == null) {
        code = '-100';
      }
      if (msg == null) {
        msg = '未知格式';
      }
      print(code);
      if (code != '100000') {
        responseData.code = code;
        responseData.msg = msg;
        return new Future.error(responseData);
      } else {
        return null;
      }
    } else {
      responseData.code = response.statusCode;
      responseData.msg = response.toString();
      print('****** parse http request response error ******');
      print(responseData.msg);
      var data = response.data;
      if(data!=null){
        responseData.code = data['code'];
        responseData.msg = data['message'];
      }

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
    var token = data['token'];
    if (token != null) {
      options.headers['Authorization'] = 'Bearer ' + token;
    }

    Response response;
    print('***************请求url参数地址*************');
    print(url);
    print(data);

    if (httpRequsetType == GlobalConfig.GET) {
      response = await dio.get(url, options: options, data: data);
    } else if (httpRequsetType == GlobalConfig.POST) {
      response = await dio.post(url, options: options, data: data);
    } else if (httpRequsetType == GlobalConfig.PATCH) {
      response = await dio.patch(url, options: options, data: data);
    } else if (httpRequsetType == GlobalConfig.PUT) {
      response = await dio.put(url, options: options, data: data);
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
