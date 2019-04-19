import 'package:flutter_app/entity/Message.dart';
import 'package:flutter_app/chat/MessageType.dart';
import 'package:flutter_app/chat/entity/LocationMessage.dart';
import 'package:flutter_app/chat/entity/TextMessage.dart';
import 'dart:convert';

class ChatModule {

  String chatType;

   Message createSendTextMessage(String text,String sendTo){
    TextMessage  message = TextMessage();
    message.text = text;
    message.type = 'TextMessage';
    message.messageTypeValue = MessageType.TextMessage.index;
    message = _covertSendMsgInfoToContent(message,sendTo);
    return message;
  }

  Message createSendLocationMessage(var data,String sendTo){
    LocationMessage  message = LocationMessage();
    message.locationUrl = data['locationUrl'];
    message.subTitle = data['subTitle'];
    message.title = data['title'];
    message.latitude = data['latitude'];
    message.longitude = data['longitude'];
    message.type = 'LocationMessage';
    message.messageTypeValue = MessageType.LocationMessage.index;
    message = _covertSendMsgInfoToContent(message,sendTo);
    return message;
  }



  var _bindId;

   bindListener(var msgStatusChangeCall,var incomingNewMsgCall){
     _bindId = toString();

     var bindItem = {'msgStatusCall':msgStatusChangeCall,'incoming':incomingNewMsgCall};
     bindItem['id']=_bindId;
     _bindMsgCallback.add(bindItem);
   }

   unbindListender(){
      for(var i=0;i<_bindMsgCallback.length;i++){
       if(_bindMsgCallback[i]['id'] == _bindId) {
        var bindItem = _bindMsgCallback.removeAt(i);
        break;
       }
      }
   }

  static List _bindMsgCallback = List();



    Message _covertSendMsgInfoToContent(Message msg,String sendTo) {
    var msgContent = {};
    var data = {};
    if (msg is TextMessage) {
      //data info
      data['content'] = msg.text;
    }else if( msg is LocationMessage){
      data['body'] = msg.locationUrl;
      data['title'] = msg.title;
      data['subTitle'] = msg.subTitle;
      data['longitude'] = msg.longitude;
      data['latitude'] = msg.latitude;
      }

    msg.chatType = chatType;

    msg.senderId = sendTo;
    msg.id = msg.senderId+DateTime.now().millisecondsSinceEpoch.toString();
    msg.timestamp =  DateTime.now().millisecondsSinceEpoch;
    msgContent['messageType'] = msg.type;
    msgContent['messageTypeValue'] = msg.messageTypeValue;
    msgContent['data'] = data;
    msgContent['messageEncrypt'] = "false";
//    var peerInfo = {};
//    peerInfo['userName'] = peerInfo;
//    peerInfo['mobile'] = peerInfo;
//    peerInfo['nickName'] = peerInfo;
//    msgContent['peerInfo'] = peerInfo;
    msg.content = json.encode(msgContent);
     return msg;
  }

  static void insertOrUpdateSendMsg(Message msg) {

  }

  static void updateMsgStatus(String msgId,String chatType, String status) {
      print('消息状态::当前监听个数：${_bindMsgCallback.length}');
   for(var bindItem in _bindMsgCallback){
    var msgStatusCall = bindItem['msgStatusCall'];
    Message  message = Message();
    message.id = msgId;
    message.chatType = chatType;
    message.status = status;
    msgStatusCall(message);
   }

  }

  static void insertOrUpdateIncomingMsg(from, body, type) {
    Message message = new Message();
     message.id =  body['id'];
     message.senderId = from;
    for(var bindItem in _bindMsgCallback){
       var incomingMsgCall = bindItem['incoming'];

       incomingMsgCall(message);
    }

  }

//  static Message

}
