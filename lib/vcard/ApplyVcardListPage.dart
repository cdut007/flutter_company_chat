import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/entity/Conversation.dart';
import 'package:flutter_app/chat/ChatPage.dart';
class ApplyVcardListPage extends StatefulWidget{
  @override
  _ApplyVcardListPageState createState() => new _ApplyVcardListPageState();
}
class _ApplyVcardListPageState extends State{

  List<Conversation> _conversations = [];

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("申请名片列表", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
        ),
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
          conversation.peerId='11';
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
          tag: 'vcard apply ${index}',
          child: new CircleAvatar(
            backgroundImage: new NetworkImage('https://pic3.zhimg.com/50/2b8be8010409012e7cdd764e1befc4d1_s.jpg'),//conversation.peerAvatar
          ),
        ),
        title: new Text(conversation.name),
        subtitle: new Text(conversation.content),
        trailing: new Container(child: new Card(
          color: GlobalConfig.themeColor(),
          elevation: 2.0,
          child: new FlatButton(
              onPressed: () {
                print("apply vcard call");
              },
              child: new Padding(
                padding: new EdgeInsets.all(0.0),
                child: new Text(
                  '同 意',
                  style: new TextStyle(
                    color: Colors.white,
                    fontSize: 14.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              )),
        ),height: 44,width: 72,),
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