import 'package:flutter_app/entity/Message.dart';
class Conversation extends Object {
  var peerId;
  var peerAvatar;
  var senderId;
  var title;
  var type;
  var status;
  var unreadMsgCount;
  Message message;
  var content;
  var jsonData;

  @override
  String toString() {
    return 'Conversation{peerId: $peerId, peerAvatar: $peerAvatar}';
  }

}