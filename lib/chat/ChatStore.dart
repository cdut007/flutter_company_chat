import 'package:sqflite/sqflite.dart';
import 'package:flutter_app/entity/Message.dart';
import 'package:flutter_app/entity/Conversation.dart';
import 'package:path/path.dart';
import 'package:flutter_app/chat/ChatModule.dart';
import 'dart:convert';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:event_bus/event_bus.dart';
import 'dart:async';
class ChatStore {
  Database db;
  final String tableConversation = 'conversation';
  final String tableMessage = 'message';
  final String columnId = '_id';
  final String columnTitle = 'title';
  final String columnConversationId = 'conversation_id';
  final String columnMessageId = 'message_id';
  final String columnSenderId = 'sender_id';
  final String columnDate = 'local_date';
  final String columnMute = 'mute';
  final String columnSetTop = 'set_top';
  final String columnSetTopTime = 'set_top_time';
  final String columnServerDate = 'server_date';
  final String columnMessageCount = 'message_count';
  final String columnUnreadMessageCount = 'unread_message_count';
  final String columnMessageType = 'type';
  final String columnConversationType = 'conversation_type';
  final String columnStatus = 'status';
  final String columnErrorInfo = 'error_info';
  final String columnJsonData = 'json_data';
  final String columnContent = 'content';
  var _currentUserId;
   checkDBReady(String currentUserId
      ) async{
     _currentUserId = currentUserId;
      if(db!=null && db.isOpen ){
         print('聊天本地数据库已打开准备就绪');
      }else{
        print('正在打开本地数据库...'+_currentUserId);
        open(currentUserId);
      }
  }

  isDbClose(){
    if(db == null || !db.isOpen){
      return true;
    }
    return false;
  }

  openDbIfNeed(String methodName) async{
      if(isDbClose() && _currentUserId!=null ){
        open(_currentUserId);
      }else{
        if( _currentUserId!=null){
          print('数据库ready调用函数:'+methodName);
        }else{
          print('@@@数据库尚未准备好 ，调用函数:'+methodName);
        //ad await  openDbIfNeed(methodName);
        }

      }
  }

  bool initedOpenning=false;
  Future open(String userId) async {
    if(initedOpenning){
       print('数据库正在打开');
       return;
    }
    initedOpenning = true;
    // Get a location using getDatabasesPath
    var databasesPath = await getDatabasesPath();
    String path = join(databasesPath, userId + '_flutter.db');
    print('数据库存储路径：'+path);
    db = await openDatabase(path, version: 2,
        onCreate: (Database db, int version) async {
      await db.execute('''
            create table $tableConversation ( 
              $columnId integer primary key autoincrement, 
              $columnConversationId text,
              $columnTitle text,
              $columnContent text,
              $columnMessageId text,
              $columnSenderId text,
              $columnConversationType text,
              $columnStatus text, 
              $columnJsonData text, 
              $columnMessageCount integer default 0, 
              $columnUnreadMessageCount integer default 0, 
              $columnMute integer default 0, 
              $columnSetTop integer default 0, 
              $columnSetTopTime TimeStamp,
              $columnDate TimeStamp not null default (datetime('now', 'localtime')),
              $columnErrorInfo text)
            ''');

      await db.execute('''
            create table $tableMessage ( 
              $columnId integer primary key autoincrement, 
              $columnMessageId text,
              $columnConversationId text,
              $columnSenderId text,
              $columnContent text,
              $columnMessageType text,
              $columnConversationType text,
              $columnStatus text, 
              $columnJsonData text, 
              $columnDate TimeStamp not null default (datetime('now', 'localtime')),
              $columnServerDate TimeStamp,
              $columnErrorInfo text)
            ''');
      print('数据库表创建完毕');
      initedOpenning=false;
      EventBus eventBus = GlobalConfig.getEventBus();
      eventBus.fire(ChatStore());
    });
  }


  Future<Conversation> insertOrUpdateConversation(Conversation conversation) async {
    Conversation localConversation  = await getConversation(conversation.peerId);
    if(localConversation == null){
      localConversation  = await insertConversation(conversation);
    }else{
      localConversation =   await updateConversation(conversation);
    }
    return localConversation;
  }

  Future<Conversation> insertConversation(Conversation conversation) async {
    var data = setConversationToMap(conversation);
    await openDbIfNeed('insertConversation');
    db.insert(tableConversation, data);
    return conversation;
  }

  Future<List<Conversation>> getAllConversations(String conversationType) async {
    await openDbIfNeed('getAllConversations');
    List<Map> maps ;
    if(conversationType == null){
      maps = await db.rawQuery('SELECT * FROM $tableConversation');
    }else{
      maps = await db.rawQuery('SELECT * FROM $tableConversation where $columnConversationType = "$conversationType"');
    }
    List<Conversation> conversations = List();
    print('获取数据库会话总数：${maps.length}');
    if (maps.length > 0) {
     for(var data in maps){
       Conversation conversation =  await setMapToConversation(data);
       conversations.add(conversation);
     }
      return conversations;
    }
    return conversations;
  }

  Future<Conversation> getConversation(String peerId) async {
    await openDbIfNeed('getConversation');
    List<Map> maps = await db.rawQuery('SELECT * FROM $tableConversation where $columnConversationId = "$peerId"');
    if (maps.length > 0) {
      Conversation conversation = await setMapToConversation(maps.first);
      return conversation;
    }
    return null;
  }

  Future<int> deleteConversation(String peerId) async {
    await openDbIfNeed('deleteConversation');
    return await db.delete(tableConversation, where: '$columnConversationId = ? ', whereArgs: [peerId]);
  }

  setMessageToConversation(Message message){
    Conversation conversation = Conversation();
    conversation.peerId = message.conversationId;
    conversation.senderId = ChatModule.getPeerId(message.conversationId);
    conversation.content = message.content;
    conversation.message = message;
    //conversation.title
    conversation.type = message.chatType;
    conversation.timestamp = message.timestamp;
    return conversation;
  }

  Future<Conversation>setMapToConversation(var mapData) async{
    Conversation conversation = Conversation();
    var data =  mapData;
    conversation.peerId = data[columnConversationId];
    conversation.title = data[columnTitle] == null?conversation.peerId:data[columnTitle];
    conversation.senderId = data[columnSenderId];
    conversation.content = data[columnContent] == null?'':data[columnContent];
    conversation.status = data[columnStatus];
    conversation.type = data[columnConversationType];
    conversation.timestamp = data[columnDate];
    if(data[columnJsonData]!=null){
      conversation.jsonData = json.decode(data[columnJsonData]).cast<String, dynamic>();
    }
    Message message = await getMessage(data[columnMessageId]);
    conversation.message = message;
    return conversation;
  }

  setConversationToMap(Conversation conversation){
    Map<String, dynamic> data = {};
    data[columnConversationId]=conversation.peerId;
    data[columnTitle] = conversation.title;
    data[columnContent] = conversation.content;
    data[columnSenderId] = conversation.senderId;
    data[columnStatus] = conversation.status;
    data[columnDate] = conversation.timestamp;
    data[columnConversationType] = conversation.type;
    if(conversation.jsonData!=null){
      data[columnJsonData] = json.encode(conversation.jsonData);
    }
    if(conversation.message!=null){
      data[columnMessageId] = conversation.message.id;
    }
    return data;
  }

  setMapToMessage(var mapData){
    var data =  mapData;
    Message message = ChatModule.BuilderMessage(data[columnMessageType]);

    message.senderId = data[columnSenderId];
    message.id = data[columnMessageId];
    message.conversationId = data[columnConversationId];
    message.content = data[columnContent];
    message.status = data[columnStatus];
    message.type = data[columnMessageType];
    message.timestamp = data[columnDate];
    if(data[columnJsonData]!=null){
      message.jsonData = json.decode(data[columnJsonData]).cast<String, dynamic>();
    }
    return message;
  }

  setMessageToMap(Message message){
    Map<String, dynamic> data = {};
    data[columnSenderId]=message.senderId;
    data[columnMessageId] = message.id;
    data[columnConversationId] = message.conversationId;
    data[columnStatus] = message.status;
    data[columnMessageType] = message.type;
    data[columnDate] = message.timestamp;
    if(message.jsonData!=null){
      data[columnJsonData] = json.encode(message.jsonData);
    }
    data[columnContent] = message.content;
    return data;
  }


  Future<Conversation> updateConversation(Conversation conversation) async {
    await openDbIfNeed('updateConversation');
    int result = await db.update(tableConversation, setConversationToMap(conversation),
        where: '$columnConversationId = ?', whereArgs: [conversation.peerId]);

    return conversation;
  }



  Future<Message> insertOrUpdateMessage(Message message) async {
    Message localMessage  = await getMessage(message.id);
    if(localMessage == null){
      localMessage  = await insertMessage(message);
    }else{
      localMessage =   await updateMessage(message);
    }
    return localMessage;
  }

  Future<Message> insertMessage(Message message) async {
    var data = setMessageToMap(message);
    db.insert(tableMessage, data);
    Conversation conversation = await getConversation(message.conversationId);
    if(conversation!=null){
       conversation.timestamp = message.timestamp;
       conversation.message = message;
       conversation.content = message.content;
       updateConversation(conversation);
    }else{
       conversation = setMessageToConversation(message);

       insertConversation(conversation);
    }

    return message;
  }

  Future<List<Message>> getMessages(String peerId,String msgId,int count,bool prevOrNext) async {
    await openDbIfNeed('getMessages ,参数peerId: $peerId,msgId: $msgId');
    String msgIdCondition = '';
    if(msgId!=null){
      String beside_ = ">";
      if(prevOrNext){
         beside_="<";
      }
       msgIdCondition = ' and $columnId '+beside_+' (SELECT $columnId from $tableMessage where $columnMessageId = "$msgId") ';
    }
    List<Map> maps = await db.rawQuery('SELECT * FROM $tableMessage where $columnConversationId = "$peerId" '+msgIdCondition+'order by $columnDate desc limit $count');
    List<Message> messages = List();
    print('查询数据库消息getMessages count：${maps.length}');
    if (maps.length > 0) {
      for(var data in maps){
        Message message =  setMapToMessage(data);
        messages.add(message);
      }
      return messages;
    }
    return messages;
  }


  Future<Message> getMessage(String msgId) async {
    await openDbIfNeed('getMessage');
    List<Map> maps = await db.rawQuery('SELECT * FROM $tableMessage where $columnMessageId = "$msgId"');
    print('查询数据库消息是否存在：${maps.length},消息id=${msgId}');
    if (maps.length > 0) {
      Message message =  setMapToMessage(maps.first);
      return message;
    }
    return null;
  }

  Future<int> deleteMessage(String msgId) async {
    await openDbIfNeed('deleteMessage');
    return await db.delete(tableMessage, where: '$columnMessageId = ? ', whereArgs: [msgId]);
  }


  Future<Message> updateMessage(Message message) async {
    await openDbIfNeed('updateMessage');
    int result = await db.update(tableMessage, setMessageToMap(message),
        where: '$columnMessageId = ?', whereArgs: [message.id]);

    return message;
  }


  Future close() async => db.close();


}
