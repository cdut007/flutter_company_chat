class Message{
  String id;
  String senderId;
  String type; //TextMessage,....
  String chatType;//single ,group
  String status;//sent ,delivered,read,fail
  int messageTypeValue;
  String content;
  int timestamp;

  @override
  String toString() {
    return 'Message{id: $id, senderId: $senderId, type: $type, content: $content, timestamp: $timestamp}';
  }


}