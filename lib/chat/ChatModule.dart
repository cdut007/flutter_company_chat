import 'package:flutter_app/entity/Message.dart';
import 'package:flutter_app/chat/MessageType.dart';
import 'package:flutter_app/entity/Conversation.dart';
import 'package:flutter_app/chat/ChatStore.dart';
import 'package:flutter_app/util/ChatManager.dart';
import 'package:flutter_app/chat/entity/LocationMessage.dart';
import 'package:flutter_app/chat/entity/TextMessage.dart';
import 'package:flutter_app/chat/ConversationType.dart';
import 'dart:convert';
import 'dart:async';

class ChatModule {
  String chatType;

  Message createSendTextMessage(String text, String sendTo) {
    TextMessage message = TextMessage();
    message.text = text;
    message.type = 'TextMessage';
    message.messageTypeValue = MessageType.TextMessage.index;
    message = _covertSendMsgInfoToContent(message, sendTo);
    return message;
  }

  Message createSendLocationMessage(var data, String sendTo) {
    LocationMessage message = LocationMessage();
    message.locationUrl = data['locationUrl'];
    message.subTitle = data['subTitle'];
    message.title = data['title'];
    message.latitude = data['latitude'];
    message.longitude = data['longitude'];
    message.type = 'LocationMessage';
    message.messageTypeValue = MessageType.LocationMessage.index;
    message = _covertSendMsgInfoToContent(message, sendTo);
    return message;
  }

  int msgCount = 10;

  Future<List<Message>> getMessages(String peerId, {String msgId}) async {
    List<Message> messages = await ChatManager.getChatStore()
        .getMessages(peerId, msgId, msgCount, true);
    for (Message msg in messages) {
      msg = parseMsgInfo(msg);
    }
    //从本地用户查询用户信息赋值
    messages.sort((left, right) => left.timestamp - right.timestamp);
    return messages;
  }

  Future<Message> getMessage(String msgId) async {
    Message message = await ChatManager.getChatStore().getMessage(msgId);
    message = parseMsgInfo(message);
    return message;
  }

  Future<Conversation> getConversation(String peerId) async {
    Conversation conversation =
        await ChatManager.getChatStore().getConversation(peerId);
    conversation = parseConversationInfo(conversation);
    return conversation;
  }

  parseConversationInfo(Conversation conversation) {}

  Future<List<Conversation>> getConversations({String conversationType}) async {
    List<Conversation> conversations =
        await ChatManager.getChatStore().getAllConversations(conversationType);
    //从本地用户查询用户信息赋值
    conversations.sort((left, right) => right.timestamp - left.timestamp);
    return conversations;
  }

  var _bindId;

  bindListener(var msgStatusChangeCall, var incomingNewMsgCall) {
    _bindId = DateTime.now().millisecondsSinceEpoch.toString();

    var bindItem = {
      'msgStatusCall': msgStatusChangeCall,
      'incoming': incomingNewMsgCall
    };
    bindItem['id'] = _bindId;
    print('binding id=' + _bindId);
    _bindMsgCallback.add(bindItem);
  }

  unbindListender() {
    for (var i = 0; i < _bindMsgCallback.length; i++) {
      if (_bindMsgCallback[i]['id'] == _bindId) {
        var bindItem = _bindMsgCallback.removeAt(i);
        break;
      }
    }
  }

  static List _bindMsgCallback = List();

  static getPeerId(String sendTo){
    int index = sendTo.lastIndexOf("_");
    if(index == -1){
      return sendTo;
    }
    return sendTo.substring(0,sendTo.lastIndexOf("_"));

  }

  static getSendTo(String peerId,String type){
    if (type!= ConversationType.Group.toString()) {
      peerId = peerId + '_' + type;
    } else {

    }
    return peerId;
  }


  Message _covertSendMsgInfoToContent(Message msg, String sendTo) {
    var msgContent = {};
    var data = {};
    if (msg is TextMessage) {
      //data info
      data['content'] = msg.text;
      msg.content = msg.text;
    } else if (msg is LocationMessage) {
      data['body'] = msg.locationUrl;
      data['title'] = msg.title;
      data['content'] = msg.title + msg.subTitle;
      data['subTitle'] = msg.subTitle;
      data['longitude'] = msg.longitude;
      data['latitude'] = msg.latitude;
      msg.content = msg.title + msg.subTitle;
    }

    msg.chatType = chatType;
    msg.conversationId = sendTo;
    msg.senderId = ChatManager.currentUserId;
    msg.id = msg.senderId + DateTime.now().millisecondsSinceEpoch.toString();
    msg.timestamp = DateTime.now().millisecondsSinceEpoch;
    msgContent['messageType'] = msg.type;
    msgContent['messageTypeValue'] = msg.messageTypeValue;
    msgContent['data'] = data;
    msgContent['messageEncrypt'] = "false";
//    var peerInfo = {};
//    peerInfo['userName'] = peerInfo;
//    peerInfo['mobile'] = peerInfo;
//    peerInfo['nickName'] = peerInfo;
//    msgContent['peerInfo'] = peerInfo;
    msg.jsonData = msgContent;
    return msg;
  }

  static void insertOrUpdateSendMsg(Message msg) async {
    print('sending insert msg to db');
    msg = await ChatManager.getChatStore().insertOrUpdateMessage(msg);
  }

  static void updateMsgStatus(
      String msgId, String chatType, String status) async {
    print('消息状态::当前监听通知个数：${_bindMsgCallback.length}');
    Message message = await ChatManager.getChatStore().getMessage(msgId);
    if (message == null) {
      print('该消息本地没有找到，可能已被删除，或者是因为来了新消息，还没插入数据库，对方回执状态本地没有来得及更新');
      return;
    }
    message.status = status;
    ChatManager.getChatStore().updateMessage(message);
    for (var bindItem in _bindMsgCallback) {
      var msgStatusCall = bindItem['msgStatusCall'];
      msgStatusCall(message);
    }
  }

  static Message parseMsgInfo(Message msg) {
    var jsonData = msg.jsonData;
    msg.type = jsonData['messageType'];
    msg.messageTypeValue = jsonData['messageTypeValue'];

    if (jsonData['data'] != null) {
      msg.content = jsonData['data']['content'];
      if (msg is TextMessage) {
        msg.text = jsonData['data']['content'];
        if (msg.text == null) {
          msg.text = '';
        }
      } else {

      }
    }

    return msg;
  }

  static Message BuilderMessage(String messageType) {
    if (messageType == 'TextMessage') {
      return new TextMessage();
    } else {
      return new TextMessage();
    }
    return new Message();
  }

static void callIncomingMsg( message){
  for (var bindItem in _bindMsgCallback) {
    var incomingMsgCall = bindItem['incoming'];

    incomingMsgCall(message);
  }
}


  static void insertOrUpdateIncomingMsg(from, body, type) async {
    print('message from==' + from);
    var jsonData = body['body']['\$t'];
    var jsonDataMap = {};
    var messageType = "CustomMessage";
    try {
      jsonDataMap = json.decode(jsonData).cast<String, dynamic>();
      messageType = jsonDataMap['messageType'];
    } catch (e) {
      print(e);
    }
    Message message = ChatModule.BuilderMessage(messageType);
    message.id = body['id'];
    message.senderId = from;
    message.jsonData = jsonDataMap;

    if (type == 'chat') {
      message.chatType = ConversationType.Single.toString();
      message.conversationId = from + '_' + ConversationType.Single.toString();
    } else {
      message.conversationId = from;
      message.chatType = ConversationType.Group.toString();
    }
    message.timestamp = DateTime.now().millisecondsSinceEpoch;
    message = parseMsgInfo(message);
    message = await ChatManager.getChatStore().insertOrUpdateMessage(message);
    for (var bindItem in _bindMsgCallback) {
      var incomingMsgCall = bindItem['incoming'];

      incomingMsgCall(message);
    }
  }

//  static Message

}
