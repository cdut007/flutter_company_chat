import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/util/ChatManager.dart';
import 'package:flutter_app/login/LoginPage.dart';
import 'package:flutter_app/chat/ChatPage.dart';
import 'package:flutter_app/IndexPage.dart';

class SplashPage extends StatefulWidget {
  @override
  SplashState createState() => new SplashState();
}

class SplashState extends State<SplashPage> {
  Timer timer;

  @override
  void initState() {
    super.initState();
     ChatManager.init();

    timer = new Timer(const Duration(milliseconds: 2000), () {
      //'39.108.165.171'
      _go2Page();
    });

    print("初始化");
  }
  Future _go2Page() async{
    bool isLogin = await ApiManager.isLoggedIn();
    if(isLogin){
      String token = await ApiManager.getToken();
      //刷新token
       ApiManager.refreshToken({'token': token, 'expireDay': GlobalConfig.Token_expireDay}).then((data){

      },onError: (errorData){
         print(errorData);
       });
      try {
        Navigator.of(context).pushAndRemoveUntil(new MaterialPageRoute(
            builder: (BuildContext context) => new  ChatPage(key:Key('chat'),peerId:'robot',peerName:'robot',peerAvatar:null)), (//跳转到主页
            Route route) => route == null);
      } catch (e) {
          print(e);
      }
    }else{
      try {
        Navigator.of(context).pushAndRemoveUntil(new MaterialPageRoute(
            builder: (BuildContext context) => new LoginPage(fromSplashPage: true,)), (//跳转到登录页面
            Route route) => route == null);
      } catch (e) {

      }
    }

  }

  @override
  void dispose() {
    timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return new Material(
      color: GlobalConfig.themeColor(),
      child: new Padding(
        padding: const EdgeInsets.only(
          top: 150.0,
        ),
        child: new Column(
          children: <Widget>[
            new Text(
              "天下没有难做的供应链",
              style: new TextStyle(
                  color: Colors.white,
                  fontSize: 50.0,
                  fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }
}
