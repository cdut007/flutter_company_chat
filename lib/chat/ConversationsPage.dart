import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/entity/Conversation.dart';
import 'package:flutter_app/chat/ChatPage.dart';
import 'package:flutter_app/chat/ChatModule.dart';
import 'package:flutter_app/widget/HeaderListView.dart';
import 'package:flutter_app/entity/Message.dart';
import 'package:flutter_app/widget/LoadingWidget.dart';
class ConversationsPage extends StatefulWidget{
  @override
  _ConversationsPageState createState() => new _ConversationsPageState();
}
class _ConversationsPageState extends State{

  List<Conversation> _conversations = [];

  ChatModule chatModule = ChatModule();

  LoadingType loadingType = LoadingType.Loading;
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
      body: _buildListView(),
    );
  }

  Timer timer;

  @override
  void initState() {
    super.initState();
    chatModule.bindListener(msgStatusChangeCall, incomingNewMsgCall);
    timer = new Timer(const Duration(milliseconds: 1000), () {
      _loadConversations();
    });


  }

  msgStatusChangeCall(Message message){
    print('【聊天列表页面收到消息状态】');
    _loadConversations();
  }
  incomingNewMsgCall(Message message){
    print('【聊天列表页面收到新消息】');
    _loadConversations();
  }
  @override
  void dispose() {
    //chatModule.unbindListender();
    timer.cancel();
    super.dispose();
  }


   _loadConversations()  {
   chatModule.getConversations().then((conversations){
   print('convesation  load length：${_conversations.length}');
   setState(() {
   _conversations = conversations;
   if (_conversations.length > 0) {
   loadingType = LoadingType.End;
   } else {
   loadingType = LoadingType.Empty;
   }
   });
    },onError: (error){
     print('加载会话错误');
     print(error);
      setState(() {
        if (_conversations.length > 0) {
          loadingType = LoadingType.End;
        } else {
          loadingType = LoadingType.Error;
        }
      });
    });

  }


  void _navigateToConversationDetails(Conversation conversation, Object avatarTag) {
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (c) {
          return new ChatPage(key:Key('chat'),peerId:conversation.peerId,peerAvatar:conversation.peerAvatar);
        },
      ),
    );
  }

  Widget _buildConversationListTile(BuildContext context, int index) {
    var conversation = _conversations[index];

    return new Column(children: <Widget>[
      new ListTile(
        onTap: () => _navigateToConversationDetails(conversation, index),
        leading: new Hero(
          tag: index,
          child: CommonUI.getAvatarWidget(conversation.peerAvatar,size: 24),//conversation.peerAvatar
        ),
        title: new Text(conversation.title),
        subtitle: new Text(conversation.content),
        trailing: new Text("9:00"),
      ),
      new Divider(height: 1,)
    ],);
  }

  Widget getConversationList() {
    Widget content;

    if (loadingType == LoadingType.Loading) {
      return Center(
        child: Column(
          children: <Widget>[
            new LoadingWidget(
              loadingType: LoadingType.Loading,
            )
          ],
        ),
      );
    }


    if (loadingType == LoadingType.Empty) {
      return Center(
        child: Column(
          children: <Widget>[
            new LoadingWidget(
              loadingType: LoadingType.Empty,
              clickCallback: () {
                print('click empty ui');
                setState(() {
                  loadingType = LoadingType.Loading;
                });
                _loadConversations();
              },
            )
          ],
        ),
      );
    }


    if (loadingType == LoadingType.Error) {
      return Center(
        child: Column(
          children: <Widget>[
            new LoadingWidget(
              loadingType: LoadingType.Error,
              clickCallback: () {
                setState(() {
                  loadingType = LoadingType.Loading;
                });
                _loadConversations();
              },
            )
          ],
        ),
      );
    }

    content = new ListView.builder(
      itemCount: _conversations.length,
      itemBuilder: _buildConversationListTile,
    );

    return content;
  }

  Container _buildListView(){
    return Container(child: getConversationList(),);
  }
}