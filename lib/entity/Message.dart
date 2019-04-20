class Message{
  String id;
  String senderId;
  String conversationId;
  String type; //TextMessage,....
  String chatType;//single ,group,private
  String status;//sent ,delivered,read,fail
  int messageTypeValue;
  String content;
  int timestamp;
  var jsonData;

  @override
  String toString() {
    return 'Message{id: $id, senderId: $senderId, type: $type, content: $content, timestamp: $timestamp}';
  }


}