
import 'dart:io';
import 'dart:convert';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:flutter_app/util/ApiManager.dart';
class ChatManager {

  static WebSocket socket;
  static String openId;


  static init(){


  }




  static login()  async{
    UserInfo userInfo = await ApiManager.getUserInfo();
//uc.aitelian.cn  ws://39.108.165.171:7070/ws/ headers: {'Sec-WebSocket-Protocol': 'xmpp'}
    WebSocket.connect( 'ws://uc.aitelian.cn:5280/websocket/',headers: {'Sec-WebSocket-Protocol': 'xmpp'}).then((webSocket) {
      socket = webSocket;

      String stream =
          "<open to='ul'  xmlns='urn:ietf:params:xml:ns:xmpp-framing'  version='1.0'/>";
      socket.add(stream);


      socket.listen((data) {
        //该方法接收服务器信息
        String info = data.toString();
        print("服务器数据："+info);

        if(info.startsWith("<stream:features xmlns:stream='http://etherx.jabber.org/streams'>") && info.contains("<mechanisms")){
          _auth(userInfo.id,userInfo.passwd);
        }else if(info.startsWith('<success xmlns=') && info.contains("xmpp-sasl")){
          openBind();
        }else if(info.startsWith('<open ')){
          var startIndex = info.indexOf('id=')+4;
          var endIndex = info.indexOf('\'',startIndex);
          openId= info.substring(startIndex,endIndex);
          print("服务器openId："+openId);
        }else if (info.startsWith("<stream:features xmlns:stream='http://etherx.jabber.org/streams'>") && info.contains("<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>")){
          bind();
        }else if (info.startsWith('<iq xmlns="jabber:client" type="result"') || info.startsWith("<iq xmlns='jabber:client' type='result'")){

          if(info.contains('<bind xmlns="urn:ietf:params:xml:ns:xmpp-bind">')||info.contains("<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>")){
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

  static pingTask(){
    //<iq id='624-71' type='result'></iq>
  }
  //出席
  static presence(){
    var presenceData ='<presence id="'+openId+'"><status></status><priority>0</priority></presence>';
    print("客户端出席："+presenceData);
    socket.add(presenceData);
  }
  //获取session
  static aquireSession(){
    var sessionData = '<iq xmlns="jabber:client" id="'+openId+'" type="set"><session xmlns="urn:ietf:params:xml:ns:xmpp-session"/></iq>';
    print("客户端获取session："+sessionData);
    socket.add(sessionData);
  }
  //绑定
  static bind(){
    var bindData = '<iq id="'+openId+'" type="set"> <bind xmlns="urn:ietf:params:xml:ns:xmpp-bind"><resource>call</resource></bind></iq>';
    print("客户端绑定："+bindData);
    socket.add(bindData);
  }

  static openBind(){
    //to='39.108.165.171'
    var openBindData = "<open xmlns='jabber:client' to='ul' version='1.0'  id='"+openId+"' />";
    print("客户端打开绑定："+openBindData);
    socket.add(openBindData);
  }

  static _auth(var userId,var password){
    var _userId = userId+"_uc";//james
    var _password = password;
    var datas = List<int>();
    //如果是ejabberd 需要加一个，如果是openfire就不需要
    datas.add(0);
    datas.addAll(utf8.encode(_userId));
    datas.add(0);// /0
    datas.addAll(utf8.encode(_password));
    var encodedToken =base64.encode(datas);//'AHpxenE0ZmM2NzE0ZTEzOTQ0YTc1OWY2MjUxOGFjM2MyOGFlYl91YwBhYWFhYWFh';// base64.encode(datas);
    print("客户端认证base64："+utf8.decode(base64.decode(encodedToken)));
    var authData = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>" + encodedToken + "</auth>";
    print("客户端认证："+authData);
    socket.add(authData);
  }


}
