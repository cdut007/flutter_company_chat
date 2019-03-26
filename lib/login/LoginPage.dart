import 'package:flutter/material.dart';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/login/RegisterPage.dart';
import 'package:flutter_app/login/ForgetPasswordPage.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'dart:convert';
/**
 * @Description  登录页面
 * @Author  james
 * @Date 2019/03/22  19:00
 * @Version  1.0
 */
class LoginPage extends StatefulWidget {


  @override
  State<StatefulWidget> createState() {
    return new _LoginPageState();
  }
}

class _LoginPageState extends State<LoginPage> {
  var leftRightPadding = 30.0;
  var topBottomPadding = 4.0;
  var textTips = new TextStyle(fontSize: 16.0, color: Colors.black);
  var hintTips = new TextStyle(fontSize: 15.0, color: Colors.black26);

//  static const LOGO = "images/oschina.png";
  var phone;
  var _userPassController = new TextEditingController();
  var _userPhoneController = new TextEditingController();


  @override
  void initState() {
    super.initState();
     Future userInfoFuture = ApiManager.getUserInfo();
     userInfoFuture.then((userInfo){
       setState(() {
         UserInfo userInfoData = userInfo as UserInfo;
         phone = userInfoData.phoneNumber;
         _userPhoneController.text = phone;
       });
     });
  }

  _go2HomePage(){
    //设置变量
    Navigator.pop(context,'refreshUserInfo');
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("登录", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
        ),
        body: new Column(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 50.0, leftRightPadding, 10.0),
//              child: new Image.asset(LOGO),
            ),
            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 50.0, leftRightPadding, topBottomPadding),
              child: new TextField(
                keyboardType:TextInputType.phone,
                style: hintTips,
                controller: _userPhoneController,
                decoration: new InputDecoration(
                    hintText: "请输入手机号", prefixIcon: Icon(Icons.phone_iphone)),
                autofocus: true,
              ),
            ),
            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 30.0, leftRightPadding, topBottomPadding),
              child: new TextField(
                style: hintTips,
                controller: _userPassController,
                maxLength: 32,
                decoration: new InputDecoration(
                    hintText: "请输入密码", prefixIcon: Icon(Icons.lock)),
                obscureText: true, //是否隐藏正在编辑的文本
              ),
            ),
            new Container(
              width: 360.0,
              margin: new EdgeInsets.fromLTRB(10.0, 40.0, 10.0, 0.0),
              padding: new EdgeInsets.fromLTRB(10,
                  topBottomPadding, 10, topBottomPadding),
              child: new Card(
                color: new Color.fromARGB(255, 0, 215, 198),
                elevation: 6.0,
                child: new FlatButton(
                    onPressed: () {

                      if( _userPhoneController.text.isEmpty){
                        showToast(context,'请输入手机号');
                        return;
                      }
                      if(!StringUtil.isChinaPhoneLegal(_userPhoneController.text)){
                        showToast(context,'请输入正确的手机号码');
                        return;
                      }



                      if(_userPassController.text.isEmpty){
                        showToast(context,'请输入密码');
                        return;
                      }

                      print("the pass is" + _userPassController.text);

                      showLoadingDialog(context);
                      var data ={"account":_userPhoneController.text,"password":_userPassController.text,"loginType":"PHONE_PWD",'deviceInfo':'android'};
                      final future = ApiManager.login(data);
                      future.then((data){
                        print('*********login callback*********');
                        closeLoadingDialog();
                        print(data);
                         Future<bool> saved = ApiManager.saveToken(data);
                         saved.then((success){
                           var userProfileData = {};
                           final userInfoFuture = ApiManager.getUserProfile(userProfileData);
                           userInfoFuture.then((data){
                             var userInfo = data as UserInfo;
                             ApiManager.saveUserInfo(json.encode(userInfo));
                             _go2HomePage();
                           });
                         });


                      },onError: (errorData){
                        print('*********login callback error print*********');
                        var error =  ApiManager.parseErrorInfo(errorData);
                        closeLoadingDialog();
                        showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
                        print('*********login callback error print end*********');
                        //
                      });

                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '马上登录',
                        style:
                        new TextStyle(color: Colors.white, fontSize: 16.0),
                      ),
                    )),
              ),
            ),
            Row(
              children: <Widget>[
                Expanded(
                  child: buildForgetPasswordText(context),
                  flex: 1,
                ),
                Expanded(
                  child:  buildRegisterTipsText(context),
                  flex: 1,
                ),
              ],
            )


          ],
        ));
  }

  Padding buildRegisterTipsText(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 8.0, right: 10),
      child: Align(
        alignment: Alignment.centerRight,
        child: FlatButton(
          child: Text(
            '没有账号？注册',
            style: TextStyle(fontSize: 14.0, color: new Color.fromARGB(255, 0, 215, 198)),
          ),
          onPressed: () {
            Navigator.push(context,
                new MaterialPageRoute(builder: (context) => new RegisterPage()));
          },
        ),
      ),
    );
  }

  Padding buildForgetPasswordText(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 8.0, right: 10),
      child: Align(
        alignment: Alignment.centerLeft,
        child: FlatButton(
          child: Text(
            '忘记密码？',
            style: TextStyle(fontSize: 14.0, color: Colors.grey),
          ),
          onPressed: () {
            Navigator.push(context,
                new MaterialPageRoute(builder: (context) => new ForgetPasswordPage()));
          },
        ),
      ),
    );
  }
}
