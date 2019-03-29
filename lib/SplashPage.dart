import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/login/LoginPage.dart';
import 'package:flutter_app/IndexPage.dart';
import 'dart:io';
import 'dart:convert';

class SplashPage extends StatefulWidget {
  @override
  SplashState createState() => new SplashState();
}

class SplashState extends State<SplashPage> {
  Timer timer;
  WebSocket socket;
  String openId;

  void login() {

    WebSocket.connect( 'ws://39.108.165.171:7070/ws/',headers: {'Sec-WebSocket-Protocol': 'xmpp'}).then((socket) {
      this.socket = socket;
      socket.listen((data) {
      //该方法接收服务器信息
        String info = data.toString();
        print("服务器数据："+info);

        if(info.startsWith("<stream:features xmlns:stream='http://etherx.jabber.org/streams'><mechanisms")){
          auth();
        }else if(info.startsWith('<success xmlns="urn:ietf:params:xml:ns:xmpp-sasl"/>')){
          openBind();
        }else if(info.startsWith('<open from=')){
          var startIndex = info.indexOf('id=')+4;
          var endIndex = info.indexOf('\'',startIndex);
          openId= info.substring(startIndex,endIndex);
          print("服务器openId："+openId);
        }else if (info.startsWith("<stream:features xmlns:stream='http://etherx.jabber.org/streams'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>")){
          bind();
        }else if (info.startsWith('<iq xmlns="jabber:client" type="result"')){

           if(info.contains('<bind xmlns="urn:ietf:params:xml:ns:xmpp-bind">')){
             //出席
             presence();
           }

        }


      });
      socket.done.then((e) {
      //当与服务器连接中断调用
        print('当与服务器连接中断');
      });
    });
  }

  void pingTask(){
    //<iq id='624-71' type='result'></iq>
  }
  //出席
  void presence(){
    var presenceData ='<presence id="'+openId+'"><status>Online</status><priority>1</priority></presence>';
    print("客户端出席："+presenceData);
    socket.add(presenceData);
  }
  //获取session
  void aquireSession(){
    var sessionData = '<iq xmlns="jabber:client" id="'+openId+'" type="set"><session xmlns="urn:ietf:params:xml:ns:xmpp-session"/></iq>';
    print("客户端获取session："+sessionData);
    socket.add(sessionData);
  }
 //绑定
  void bind(){
    var bindData = '<iq id="'+openId+'" type="set"> <bind xmlns="urn:ietf:params:xml:ns:xmpp-bind"><resource>mobile</resource></bind></iq>';
    print("客户端绑定："+bindData);
    socket.add(bindData);
  }

  void openBind(){
    var openBindData = "<open xmlns='jabber:client' to='39.108.165.171' version='1.0'  id='"+openId+"' />";
    print("客户端打开绑定："+openBindData);
    socket.add(openBindData);
  }

  void auth(){
    var userId = 'james';
    var password = 'a123456';
    var datas = List<int>();
    datas.addAll(utf8.encode(userId));
    datas.add(0);// /0
    datas.addAll(utf8.encode(password));
    var encodedToken = base64.encode(datas);
    print("客户端认证base64："+utf8.decode(base64.decode(encodedToken)));
    var authData = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>" + encodedToken + "</auth>";
    print("客户端认证："+authData);
    socket.add(authData);
  }

  @override
  void initState() {
    super.initState();
    ApiManager.init();
      login();
    timer = new Timer(const Duration(milliseconds: 10), () {
      String stream =
          "<open to='39.108.165.171'  xmlns='urn:ietf:params:xml:ns:xmpp-framing'  version='1.0'/>";
//      socket.add(stream);
      _go2Page();
    });

    print("初始化");
  }
  _go2Page() async{
    bool isLogin = await ApiManager.isLoggedIn();
    if(isLogin){
      try {
        Navigator.of(context).pushAndRemoveUntil(new MaterialPageRoute(
            builder: (BuildContext context) => new IndexPage()), (//跳转到主页
            Route route) => route == null);
      } catch (e) {

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
