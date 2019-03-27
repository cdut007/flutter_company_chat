import 'package:flutter/material.dart';
import 'package:flutter_app/widget/LoginFormCode.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/util/ApiManager.dart';
/**
 * @Description  找回密码页面
 * @Author  james
 * @Date 2019/03/22  19:00
 * @Version  1.0
 */
class ForgetPasswordPage extends StatefulWidget {


  @override
  State<StatefulWidget> createState() {
    return new _ForgetPasswordPageState();
  }
}

class _ForgetPasswordPageState extends State<ForgetPasswordPage> {
  var leftRightPadding = 30.0;
  var topBottomPadding = 4.0;
  var textTips = new TextStyle(fontSize: 16.0, color: Colors.black);
  var hintTips = new TextStyle(fontSize: 15.0, color: Colors.black26);
  var avaliable = true;
//  static const LOGO = "images/oschina.png";

  var _userPassController = new TextEditingController();
  var _userPhoneController = new TextEditingController();
  var _userSMSCodeController = new TextEditingController();
  var countdown = 60;
  resetSuccess(){

    showToast(context,'注册重置密码成功');
    //设置变量
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("找回密码", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
        ),
        body: new Column(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[

            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 50.0, leftRightPadding, topBottomPadding),
              child: new TextField(
                style: hintTips,
                keyboardType:TextInputType.phone,
                controller: _userPhoneController,
                decoration: new InputDecoration(
                    hintText: "手机号（中国）", prefixIcon: Icon(Icons.phone_iphone)),
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
                    hintText: "密码", prefixIcon: Icon(Icons.lock)),
                obscureText: true, //是否隐藏正在编辑的文本
              ),
            ),
            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 50.0, leftRightPadding, topBottomPadding),
              child: Row(children: <Widget>[
                Expanded(child: new TextField(
                  enabled: avaliable,
                  style: hintTips,
                  controller: _userSMSCodeController,
                  decoration: new InputDecoration(
                      hintText: "验证码", prefixIcon: Icon(Icons.sms)),

                ),flex: 1,),
                 LoginFormCode(type: 'RESET_PWD', phone:  _userPhoneController.text, countdown: countdown, available: avaliable, onTapCallback: (){


                },)
              ],),
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

                      if( _userSMSCodeController.text.isEmpty){
                        showToast(context,'请输入验证码');
                        return;
                      }

                      print("the pass is" + _userPassController.text);
                      showLoadingDialog(context);
                      var data ={"phoneNumber":_userPhoneController.text,"otp":_userSMSCodeController.text,"newPwd":_userPassController.text};
                      final future = ApiManager.resetPassword(data);
                      future.then((data){
                        closeLoadingDialog();
                        print('*********resetPassword callback*********');
                        print(data);
                        resetSuccess();
                      },onError: (errorData){
                       var error =  ApiManager.parseErrorInfo(errorData);
                        closeLoadingDialog();
                        showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
                        print('*********resetPassword callback error*********');
                       //
                      });
                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '重置密码',
                        style:
                        new TextStyle(color: Colors.white, fontSize: 16.0),
                      ),
                    )),
              ),
            ),



          ],
        ));
  }


}
