class Message{
  String id;
  String senderId;
  int type;
  String content;
  int timestamp;

  @override
  String toString() {
    return 'Message{id: $id, senderId: $senderId, type: $type, content: $content, timestamp: $timestamp}';
  }


}