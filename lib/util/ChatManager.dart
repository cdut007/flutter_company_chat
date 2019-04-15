
import 'dart:io';
import 'dart:convert';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/entity/Message.dart';
import 'package:xml2json/xml2json.dart';
class ChatManager {

  static WebSocket socket;
  static String openId;
  static int loginStatus;
  static String currentUserId;

  static init(){


  }

  static List<Message> cacheMsg = new List();
  static List<Message> cacheReadMsg = new List();

  static readMsg(Message message){
    if(_checkIMStatusIsOk()){
      _readMessage(message);
    }else{
      //insert in native, when ready resend it.
      print('等待im登录回执');
      if(!cacheReadMsg.contains(message)){
        cacheReadMsg.add(message);
      }
    }
  }

  static _getUserJidFromNode(String jidNode){
     return jidNode.substring(0,jidNode.lastIndexOf('/'));
  }

  static _getCurrentJid([bool source]){
    if(source){
      return currentUserId +_JIdNode()+_getDomain()+"/call";
    }else{
      return currentUserId +_JIdNode()+_getDomain();
    }
  }

  static _readMessage(Message msg){
    cacheReadMsg.remove(msg);
   var messageData ='<message to="'+msg.senderId+_JIdNode()+_getDomain()+'" type="chat" xmlns="jabber:client"><x xmlns="jabber:x:event"><read/>'
        +'<msgid>'+msg.id+
            '</msgid><timestamp/></x></message>';
    print("*****客户端发送已读消息状态*****" );
    print(messageData);
    print(msg);
    socket.add(messageData);
  }

  static _deliveryMsg(var jid ,var msgId){
    var messageData ='<message to="'+jid+'" type="chat" xmlns="jabber:client"><x xmlns="jabber:x:event"><delivered/>'
        +'<msgid>'+msgId+
        '</msgid><timestamp/></x></message>';
    print("*****客户端发送回执消息状态*****" );
    print(messageData);
    socket.add(messageData);
  }

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
   // msg.senderId='james@james';
    msg.id = msg.senderId+DateTime.now().millisecondsSinceEpoch.toString();
        msg.content = '{"messageType":"TextMessage","messageTypeValue":1,"data":{"content":"'+msg.content+'"},"messageEncrypt":"false","peerInfo":{"userName":"username","mobile":"","nickName":"阿童木"}}';
        var messageData ='<message to="'+msg.senderId+_JIdNode()+_getDomain()+'" id="'+msg.id+'" type="chat" xmlns="jabber:client">'
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

  static _parseXMLToJson(var info){
    // Create a client transformer
    final Xml2Json myTransformer = Xml2Json();

    // Parse a simple XML string
    myTransformer.parse(info);
    // Transform to JSON using GData
    String _result = myTransformer.toGData();

    print('');
    print('解析XML to JSON use GData');
    print(_result);
    print('');
   return  json.decode(_result).cast<String, dynamic>();

  }

  static login()  async{
    if(socket!=null){
      try{
        socket.close(-1001,'可能由于网络问题，客户端主动关闭');
        socket =null;
      }catch (e){
        print('关闭socket 异常');
        print(e);
      }
    }

    UserInfo userInfo = await ApiManager.getUserInfo();
    currentUserId = userInfo.id;
    loginStatus  = WebSocket.connecting;
    //ws://uc.aitelian.cn:5280/websocket/
//ws://39.108.165.171:7070/ws/ headers: {'Sec-WebSocket-Protocol': 'xmpp'}
    var im_server = 'ws://uc.aitelian.cn:5280/websocket/';
    if(openfireServer){
      im_server =  'ws://39.108.165.171:7070/ws/';
      userInfo.id='testchat';
      userInfo.passwd='123456';
    }

    WebSocket.connect(im_server,headers: {'Sec-WebSocket-Protocol': 'xmpp'}).then((webSocket) {
      socket = webSocket;

      String stream =
          "<open to='ul'  xmlns='urn:ietf:params:xml:ns:xmpp-framing'  version='1.0'/>";
      socket.add(stream);


      socket.listen((data) {
        //该方法接收服务器信息
        String info = data.toString();
        print("服务器数据："+info);
       var xmlData= _parseXMLToJson(info);



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
            loginStatus  = WebSocket.open;
            //出席
            presence();
            //
            resendLocalMessages();
          }

        }else if (xmlData['failure']!=null){
          var failure = xmlData['failure'];
          if(failure['not-authorized']!=null){
            loginStatus = WebSocket.closed;
          }
        }else if (xmlData['message']!=null){

          var message = xmlData['message'];
          var received = message['received'];
          if(received!=null){
            if(received['xmlns']=='urn:xmpp:receipts'){
              print('状态回执消息已到达【#聊天服务器#】');
            }else{
              print('发送消息已到达【聊天服务器】，消息id='+received['msgid']);
            }

          }
          var from = message['from'];
          var type = message['type'];
          var xNode = message['x'];
          var body = message['body'];

          if(type == 'error'){
            print(message['error']);
          }else if(type == 'chat'){
            if(xNode!=null&&xNode['delivered']!=null){
              print('发送消息【已到】对端，消息id='+xNode['msgid']['\$t']);
            }

            if(xNode!=null&&xNode['read']!=null){
              print('发送消息对端【已读】，消息id='+xNode['msgid']['\$t']);
            }

            if(body!=null){
              if(from != _getCurrentJid(true)){
                print('收到新消息');
                _deliveryMsg(_getUserJidFromNode(from), message['id']);
              }else{

              }
            }else{
            }
          }






        }


      },onError: (error){
        loginStatus  = WebSocket.closed;
        print('websokect服务器侦听失败');
        print(error);
      },cancelOnError: true);
      socket.done.then((e) {
        //当与服务器连接中断调用
        loginStatus  = WebSocket.closed;
        print('当与服务器连接中断');
        print(e);
      });
    },onError: (errorInfo){
      loginStatus  = WebSocket.closed;
      print('websokect服务器连接失败');
      print(errorInfo);
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

  static bool openfireServer = true;

  static changeImServer(){

    openfireServer = !openfireServer;
    loginStatus = WebSocket.closed;
    print('修改聊天服务器环境：$openfireServer');
    _relogin();
  }

  static _getDomain(){
    if(openfireServer){
      return '@james';
    }
    return '@ul';
  }
  static _JIdNode(){
    if(openfireServer){
      return '';
    }
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
