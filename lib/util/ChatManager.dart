
import 'dart:io';
import 'dart:convert';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/entity/Message.dart';
class ChatManager {

  static WebSocket socket;
  static String openId;
  static int loginStatus;

  static init(){


  }

  static List<Message> cacheMsg = new List();

  static sendMessage(Message message){
      if(_checkIMStatusIsOk()){
        _sendMessage(message);
      }else{
        //insert in native, when ready resend it.
        print('等待im登录回执');
        insertLocalMessages(message);
      }
  }

  static _sendMessage(Message msg){
        cacheMsg.remove(msg);
//    <message xmlns="jabber:client" to="dummy@dummy.example" id="cxyRA-132" type="chat">
//    <thread>60AI1F</thread>
//    <body>ssa</body>
//    <x xmlns="jabber:x:event">
//    <offline/>
//    <composing/>
//    </x>
//    <active xmlns="http://jabber.org/protocol/chatstates"/>
//    </message>

    msg.id = msg.senderId+DateTime.now().millisecondsSinceEpoch.toString();
        msg.content = '{"messageType":"TextMessage","messageTypeValue":1,"data":{"content":"'+msg.content+'"},"messageEncrypt":"false","peerInfo":{"userName":"username","mobile":"","nickName":"阿童木"}}';
        var messageData ='<message to="'+msg.senderId+_JIdNode()+_getDomain()+'"id="'+msg.id+'" type="chat" xmlns="jabber:client">'
            + '\n'+
            '<body>'+msg.content+'</body>'
            +'\n'+
            '<x type="4" xmlns="jabber:x:data"/>'
            +'\n'+
            '</message>';
        print("*****客户端发送消息*****" );
        print(messageData);
        print(msg);
        socket.add(messageData);
  }

  static insertLocalMessages(Message message){
    if(!cacheMsg.contains(message)){
      cacheMsg.add(message);
    }

  }
  static resendLocalMessages(){
     for(Message msg in cacheMsg){
       sendMessage(msg);
     }
  }

  static _checkIMStatusIsOk(){
     if(_isIMLogin()){
       return true;
     }
     _relogin();
     return false;
  }

  static _isIMLogin(){
    if(socket!=null){
       if(loginStatus  == WebSocket.open ){
         return true;
       }
    }
    return false;
  }

  static  _relogin(){
    print('relogin im server');
    if(socket!=null){
      if( loginStatus  == WebSocket.connecting ){
        print('current is connecting mode ');
        return ;
      }
      login();
    }else{
      login();
    }
  }



  static login()  async{
    if(socket!=null){
      try{
        socket.close(-1001,'可能由于网络问题，客户端主动关闭');
        socket =null;
      }catch (e){
        print(e);
      }
    }

    UserInfo userInfo = await ApiManager.getUserInfo();
    loginStatus  = WebSocket.connecting;
//uc.aitelian.cn  ws://39.108.165.171:7070/ws/ headers: {'Sec-WebSocket-Protocol': 'xmpp'}
    WebSocket.connect( 'ws://uc.aitelian.cn:5280/websocket/',headers: {'Sec-WebSocket-Protocol': 'xmpp'}).then((webSocket) {
      socket = webSocket;
      loginStatus  = WebSocket.open;
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
            //
            resendLocalMessages();
          }

        }


      });
      socket.done.then((e) {
        //当与服务器连接中断调用
        loginStatus  = WebSocket.closed;
        print('当与服务器连接中断');
        print(e);
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

  static _getDomain(){
    return '@ul';
  }
  static _JIdNode(){
    return '_uc';
  }

  static _auth(var userId,var password){
    var _userId = userId+_JIdNode();//james
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
