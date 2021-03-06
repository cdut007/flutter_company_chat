import 'dart:async';
import 'dart:io';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:flutter_app/entity/Message.dart';
import 'package:flutter_app/util/ChatManager.dart';
import 'package:flutter_app/chat/ConversationType.dart';
import 'package:flutter_app/chat/ChatModule.dart';
import 'package:flutter_app/chat/entity/TextMessage.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/chat/ConversationType.dart';
import 'package:flutter_app/widget/LoadingWidget.dart';

import 'package:flutter_app/widget/HeaderListView.dart';

//import 'package:image_picker/image_picker.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ChatPage extends StatelessWidget {
  final String peerId;
  final String peerAvatar;
  final String peerName;

  ChatPage({Key key, @required this.peerId, @required this.peerName, @required this.peerAvatar})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(peerName, style: new TextStyle(color: Colors.white)),
        iconTheme: new IconThemeData(color: Colors.white),
        centerTitle: true,
      ),
      body: new ChatScreen(
        peerId: peerId,
        peerAvatar: peerAvatar,
      ),
    );
  }
}

class ChatScreen extends StatefulWidget {
  final String peerId;
  final String peerAvatar;


  ChatScreen({Key key, @required this.peerId, @required this.peerAvatar})
      : super(key: key);

  @override
  State createState() =>
      new ChatScreenState(peerId: peerId, peerAvatar: peerAvatar);
}

class ChatScreenState extends State<ChatScreen> {
  ChatScreenState({Key key, @required this.peerId, @required this.peerAvatar});

  String peerId;
  String peerAvatar;
  String id;

  LoadingType loadingType = LoadingType.Loading;
  ChatModule chatModule;
  List<Message> listMessage = [];

  File imageFile;
  bool isLoading;
  bool isShowSticker;
  String imageUrl;

  final TextEditingController textEditingController =
      new TextEditingController();
  final ScrollController listScrollController = new ScrollController();
  final FocusNode focusNode = new FocusNode();
  double chatListViewOffset=0.0;
  @override
  void initState() {
    super.initState();
    focusNode.addListener(onFocusChange);

    chatModule = new ChatModule();
    chatModule.bindListener(msgStatusChangeCall, incomingNewMsgCall);
    chatModule.chatType = ConversationType.Single.toString();
    isLoading = false;
    isShowSticker = false;
    imageUrl = '';

    id = ChatManager.currentUserId;

    readLocal();
  }



  msgStatusChangeCall(Message message){
    print('【聊天页面收到消息状态】');
    print(message);
  }
  incomingNewMsgCall(Message message){
    print('【聊天页面收到新消息】');
    print(message);
    ChatManager.readMsg(message);
  }


  @override
  void dispose() {
    chatModule.unbindListender();
    super.dispose();
  }

  void onFocusChange() {
    if (focusNode.hasFocus) {
      // Hide sticker when keyboard appear
      setState(() {
        isShowSticker = false;
      });
    }
  }

  readLocal() async {

    List<Message> messages = await chatModule.getMessages(peerId);

    setState(() {
      listMessage.addAll(messages);
      if (listMessage.length > 0) {
        loadingType = LoadingType.End;
      } else {
        loadingType = LoadingType.Empty;
      }
    });
  }

  Future<Null> pullToRefresh() async {
    _loadMoreMessages();
    return null;
  }

  Future<void> _loadMoreMessages() async {
    String msgId = null;
    if(listMessage.length>0){
      msgId = listMessage[0].id;
    }
    List<Message> messages = await chatModule.getMessages(peerId,msgId: msgId);

    setState(() {
      listMessage.insertAll(0,messages);
      if (listMessage.length > 0) {
        loadingType = LoadingType.End;
      } else {
        loadingType = LoadingType.Empty;
      }
    });
  }

  Future getImage() async {
    ChatManager.changeImServer();
//    imageFile = await ImagePicker.pickImage(source: ImageSource.gallery);
//
//    if (imageFile != null) {
//      setState(() {
//        isLoading = true;
//      });
//      uploadFile();
//    }
  }

  void getSticker() {
    // Hide keyboard when sticker appear
    focusNode.unfocus();
    setState(() {
      isShowSticker = !isShowSticker;
    });
  }

  Future uploadFile() async {
    String fileName = DateTime.now().millisecondsSinceEpoch.toString();
//    StorageReference reference = FirebaseStorage.instance.ref().child(fileName);
//    StorageUploadTask uploadTask = reference.putFile(imageFile);
//    StorageTaskSnapshot storageTaskSnapshot = await uploadTask.onComplete;
//    storageTaskSnapshot.ref.getDownloadURL().then((downloadUrl) {
//      imageUrl = downloadUrl;
//      setState(() {
//        isLoading = false;
//        onSendMessage(imageUrl, 1);
//      });
//    }, onError: (err) {
//      setState(() {
//        isLoading = false;
//      });
//      Fluttertoast.showToast(msg: 'This file is not an image');
//    });
  }

  void onSendMessage(String content, int type) {
    // type: 0 = text, 1 = image, 2 = sticker
    if (content.trim() != '') {
      textEditingController.clear();
      //send Text
       var message = chatModule.createSendTextMessage(content, peerId);
       //send location
//       var data = {};
//            data['body'] = 'http:\/\/maps.google.com\/?q=30.54145496656533,104.05876168946345&version=1.0';
//            data['title'] = '位置分享';
//            data['longitude'] = '104.05876168946345';
//            data['latitude'] = '30.54145496656533';
//            data['subTitle'] = '中国四川省成都市武侯区吉泰路';
//      message =  chatModule.createSendLocationMessage(data, peerId);


      ChatManager.sendMessage(message);

       setState(() {
         listMessage.add(message);
       });

      listScrollController.animateTo(0.0,
          duration: Duration(milliseconds: 300), curve: Curves.easeOut);
    } else {
      Fluttertoast.showToast(msg: 'Nothing to send');
    }
  }

  Widget buildItem(BuildContext context, int index  ) {
    Message message = listMessage[index];
    if (message.senderId == id) {
      // Right (my message)
      return Row(
        children: <Widget>[
          message.type == 'TextMessage'
              // Text
              ? Container(
                  child: Text(
          ( message as TextMessage).text ,
                    style: TextStyle(color: GlobalConfig.themeColor()),
                  ),
                  padding: EdgeInsets.fromLTRB(15.0, 10.0, 15.0, 10.0),
                  width: 200.0,
                  decoration: BoxDecoration(
                      color: Colors.grey,
                      borderRadius: BorderRadius.circular(8.0)),
                  margin: EdgeInsets.only(
                      bottom: isLastMessageRight(index) ? 20.0 : 10.0,
                      right: 10.0),
                )
              : message.type == 'ImageMessage'
                  // Image
                  ? Container(
                      child: Material(
                        child: CachedNetworkImage(
                          placeholder: Container(
                            child: CircularProgressIndicator(
                              valueColor: AlwaysStoppedAnimation<Color>(
                                  GlobalConfig.themeColor()),
                            ),
                            width: 200.0,
                            height: 200.0,
                            padding: EdgeInsets.all(70.0),
                            decoration: BoxDecoration(
                              color: Colors.grey,
                              borderRadius: BorderRadius.all(
                                Radius.circular(8.0),
                              ),
                            ),
                          ),
                          errorWidget: Material(
                            child: Image.asset(
                              'images/img_not_available.jpeg',
                              width: 200.0,
                              height: 200.0,
                              fit: BoxFit.cover,
                            ),
                            borderRadius: BorderRadius.all(
                              Radius.circular(8.0),
                            ),
                            clipBehavior: Clip.hardEdge,
                          ),
                          imageUrl: message.content,
                          width: 200.0,
                          height: 200.0,
                          fit: BoxFit.cover,
                        ),
                        borderRadius: BorderRadius.all(Radius.circular(8.0)),
                        clipBehavior: Clip.hardEdge,
                      ),
                      margin: EdgeInsets.only(
                          bottom: isLastMessageRight(index) ? 20.0 : 10.0,
                          right: 10.0),
                    )
                  // Sticker
                  : Container(
                      child: new Image.asset(
                        message.content,
                        width: 100.0,
                        height: 100.0,
                        fit: BoxFit.cover,
                      ),
                      margin: EdgeInsets.only(
                          bottom: isLastMessageRight(index) ? 20.0 : 10.0,
                          right: 10.0),
                    ),
        ],
        mainAxisAlignment: MainAxisAlignment.end,
      );
    } else {
      // Left (peer message)
      return Container(
        child: Column(
          children: <Widget>[
            Row(
              children: <Widget>[
                isLastMessageLeft(index)
                    ? Material(
                        child: CommonUI.getAvatarWidget(peerAvatar,size: 35),
                        borderRadius: BorderRadius.all(
                          Radius.circular(18.0),
                        ),
                        clipBehavior: Clip.hardEdge,
                      )
                    : Container(width: 35.0),
                message.type == 'TextMessage'
                    ? Container(
                        child: Text(
                          ( message as TextMessage).text,
                          style: TextStyle(color: Colors.white),
                        ),
                        padding: EdgeInsets.fromLTRB(15.0, 10.0, 15.0, 10.0),
                        width: 200.0,
                        decoration: BoxDecoration(
                            color: GlobalConfig.themeColor(),
                            borderRadius: BorderRadius.circular(8.0)),
                        margin: EdgeInsets.only(left: 10.0),
                      )
                    : message.type == 'ImageMessage'
                        ? Container(
                            child: Material(
                              child: CachedNetworkImage(
                                placeholder: Container(
                                  child: CircularProgressIndicator(
                                    valueColor: AlwaysStoppedAnimation<Color>(
                                        GlobalConfig.themeColor()),
                                  ),
                                  width: 200.0,
                                  height: 200.0,
                                  padding: EdgeInsets.all(70.0),
                                  decoration: BoxDecoration(
                                    color: Colors.grey,
                                    borderRadius: BorderRadius.all(
                                      Radius.circular(8.0),
                                    ),
                                  ),
                                ),
                                errorWidget: Material(
                                  child: Image.asset(
                                    'images/img_not_available.jpeg',
                                    width: 200.0,
                                    height: 200.0,
                                    fit: BoxFit.cover,
                                  ),
                                  borderRadius: BorderRadius.all(
                                    Radius.circular(8.0),
                                  ),
                                  clipBehavior: Clip.hardEdge,
                                ),
                                imageUrl: message.content,
                                width: 200.0,
                                height: 200.0,
                                fit: BoxFit.cover,
                              ),
                              borderRadius:
                                  BorderRadius.all(Radius.circular(8.0)),
                              clipBehavior: Clip.hardEdge,
                            ),
                            margin: EdgeInsets.only(left: 10.0),
                          )
                        : Container(
                            child: new Image.asset(
                              message.content,
                              width: 100.0,
                              height: 100.0,
                              fit: BoxFit.cover,
                            ),
                            margin: EdgeInsets.only(
                                bottom: isLastMessageRight(index) ? 20.0 : 10.0,
                                right: 10.0),
                          ),
              ],
            ),

            // Time
            isLastMessageLeft(index)
                ? Container(
                    child: Text(
                      DateFormat('dd MMM kk:mm').format(
                          DateTime.fromMillisecondsSinceEpoch(
                              message.timestamp)),
                      style: TextStyle(
                          color: Colors.blueGrey,
                          fontSize: 12.0,
                          fontStyle: FontStyle.italic),
                    ),
                    margin: EdgeInsets.only(left: 50.0, top: 5.0, bottom: 5.0),
                  )
                : Container()
          ],
          crossAxisAlignment: CrossAxisAlignment.start,
        ),
        margin: EdgeInsets.only(bottom: 10.0),
      );
    }
  }

  bool isLastMessageLeft(int index) {
    if ((index > 0 &&
            listMessage != null &&
            listMessage[index - 1].senderId == id) ||
        index == 0) {
      return true;
    } else {
      return false;
    }
  }

  bool isLastMessageRight(int index) {
    if ((index > 0 &&
            listMessage != null &&
            listMessage[index - 1].senderId != id) ||
        index == 0) {
      return true;
    } else {
      return false;
    }
  }

  Future<bool> onBackPress() {
    if (isShowSticker) {
      setState(() {
        isShowSticker = false;
      });
    } else {
      Navigator.pop(context);
    }

    return Future.value(false);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      child: Stack(
        children: <Widget>[
          Column(
            children: <Widget>[
              // List of messages
              buildListMessage(),

              // Sticker
              (isShowSticker ? buildSticker() : Container()),

              // Input content
              buildInput(),
            ],
          ),

          // Loading
          buildLoading()
        ],
      ),
      onWillPop: onBackPress,
    );
  }

  Widget buildSticker() {
    return Container(
      child: Column(
        children: <Widget>[
          Row(
            children: <Widget>[
              FlatButton(
                onPressed: () => onSendMessage('mimi1', 2),
                child: new Image.asset(
                  'images/mimi1.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              ),
              FlatButton(
                onPressed: () => onSendMessage('mimi2', 2),
                child: new Image.asset(
                  'images/mimi2.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              ),
              FlatButton(
                onPressed: () => onSendMessage('mimi3', 2),
                child: new Image.asset(
                  'images/mimi3.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              )
            ],
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          ),
          Row(
            children: <Widget>[
              FlatButton(
                onPressed: () => onSendMessage('mimi4', 2),
                child: new Image.asset(
                  'images/mimi4.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              ),
              FlatButton(
                onPressed: () => onSendMessage('mimi5', 2),
                child: new Image.asset(
                  'images/mimi5.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              ),
              FlatButton(
                onPressed: () => onSendMessage('mimi6', 2),
                child: new Image.asset(
                  'images/mimi6.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              )
            ],
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          ),
          Row(
            children: <Widget>[
              FlatButton(
                onPressed: () => onSendMessage('mimi7', 2),
                child: new Image.asset(
                  'images/mimi7.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              ),
              FlatButton(
                onPressed: () => onSendMessage('mimi8', 2),
                child: new Image.asset(
                  'images/mimi8.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              ),
              FlatButton(
                onPressed: () => onSendMessage('mimi9', 2),
                child: new Image.asset(
                  'images/mimi9.gif',
                  width: 50.0,
                  height: 50.0,
                  fit: BoxFit.cover,
                ),
              )
            ],
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          )
        ],
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      ),
      decoration: new BoxDecoration(
          border:
              new Border(top: new BorderSide(color: Colors.grey, width: 0.5)),
          color: Colors.white),
      padding: EdgeInsets.all(5.0),
      height: 180.0,
    );
  }

  Widget buildLoading() {
    return Positioned(
      child: isLoading
          ? Container(
              child: Center(
                child: CircularProgressIndicator(
                    valueColor: AlwaysStoppedAnimation<Color>(
                        GlobalConfig.themeColor())),
              ),
              color: Colors.white.withOpacity(0.8),
            )
          : Container(),
    );
  }

  Widget buildInput() {
    return Container(
      child: Row(
        children: <Widget>[
          // Button send image
          Material(
            child: new Container(
              margin: new EdgeInsets.symmetric(horizontal: 1.0),
              child: new IconButton(
                icon: new Icon(Icons.image),
                onPressed: getImage,
                color: GlobalConfig.themeColor(),
              ),
            ),
            color: Colors.white,
          ),
          Material(
            child: new Container(
              margin: new EdgeInsets.symmetric(horizontal: 1.0),
              child: new IconButton(
                icon: new Icon(Icons.face),
                onPressed: getSticker,
                color: GlobalConfig.themeColor(),
              ),
            ),
            color: Colors.white,
          ),

          // Edit text
          Flexible(
            child: Container(
              child: TextField(
                style:
                    TextStyle(color: GlobalConfig.themeColor(), fontSize: 15.0),
                controller: textEditingController,
                decoration: InputDecoration.collapsed(
                  hintText: '输入聊天内容...',
                  hintStyle: TextStyle(color: Colors.blueGrey),
                ),
                focusNode: focusNode,
              ),
            ),
          ),

          // Button send message
          Material(
            child: new Container(
              margin: new EdgeInsets.symmetric(horizontal: 8.0),
              child: new IconButton(
                icon: new Icon(Icons.send),
                onPressed: () => onSendMessage(textEditingController.text, 0),
                color: GlobalConfig.themeColor(),
              ),
            ),
            color: Colors.white,
          ),
        ],
      ),
      width: double.infinity,
      height: 50.0,
      decoration: new BoxDecoration(
          border:
              new Border(top: new BorderSide(color: Colors.grey, width: 0.5)),
          color: Colors.white),
    );
  }

  Widget loadLocalMessageList() {

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
//                setState(() {
//                  loadingType = LoadingType.Loading;
//                });
              },
            )
          ],
        ),
      );
    }


//    return ListView.builder(
//      padding: EdgeInsets.all(10.0),
//      itemBuilder: (context, index) =>
//          buildItem(index, listMessage[index]),
//      itemCount:listMessage.length,
//      reverse: true,
//      controller: listScrollController,
//    );
    Widget content = new HeaderListView(
      listMessage,
      headerList: [1],
      itemWidgetCreator: buildItem,
      headerCreator: (BuildContext context, int position) {
        if (position == 0) {
          return new Container();
        }
      },
      usePullToRefresh: true,
      onHeaderRefresh: pullToRefresh,
      scrollOffset: chatListViewOffset,
      setScrollOffset: (offset){
        print('offset changed---->$offset');
        chatListViewOffset = offset;
      },
    );


   return content;
  }



  Widget buildListMessage() {
//    return Flexible(child: Center(
//    child: CircularProgressIndicator(valueColor: AlwaysStoppedAnimation<Color>(GlobalConfig.themeColor()))));

    return Flexible(
      child: loadLocalMessageList(),
    );
  }
}
