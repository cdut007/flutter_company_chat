import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/entity/Conversation.dart';
import 'package:flutter_app/chat/ChatPage.dart';
import 'package:flutter_app/widget/LoadingWidget.dart';
class ConversationsPage extends StatefulWidget{
  @override
  _ConversationsPageState createState() => new _ConversationsPageState();
}
class _ConversationsPageState extends State{

  List<Conversation> _conversations = [];

  LoadingType loadingType = LoadingType.Loading;
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
      body: _buildListView(),
    );
  }

  @override
  void initState() {
    super.initState();
    _loadConversations();
  }

  Future<void> _loadConversations() async {
    var conersations = List<Conversation>();
    for(var index = 0; index < 10; index++) {
      Conversation value = Conversation();
      value.name = 'ssss$index';
      value.content='sssss222';
      conersations.add(value);
    }
    print('convesation  load length：${_conversations.length}');
    setState(() {
      _conversations = conersations;
    });
  }


  void _navigateToConversationDetails(Conversation conversation, Object avatarTag) {
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (c) {
          conversation.peerId='james';
          conversation.peerAvatar='https://pic3.zhimg.com/50/2b8be8010409012e7cdd764e1befc4d1_s.jpg';
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
          child: CommonUI.getAvatarWidget('https://pic3.zhimg.com/50/2b8be8010409012e7cdd764e1befc4d1_s.jpg',size: 24),//conversation.peerAvatar
        ),
        title: new Text(conversation.name),
        subtitle: new Text(conversation.content),
        trailing: new Text("9:00"),
      ),
      new Divider(height: 1,)
    ],);
  }

  Widget getConversationList() {
    Widget content;

    print('convesation length：${_conversations.length}');
    if (_conversations.isEmpty) {
      content = new Center(
        child: new CircularProgressIndicator(),
      );
    } else {
      content = new ListView.builder(
        itemCount: _conversations.length,
        itemBuilder: _buildConversationListTile,
      );
    }

    return content;
  }

  Container _buildListView(){
    return Container(child: getConversationList(),);
  }
}