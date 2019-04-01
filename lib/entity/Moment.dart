class Moment {
  String id;
  String userId;
  String cardId;
  String title;
  String location;
  String lookLimit;
  String publishType;
  String content;
  String fileContent;
  String userName;
  String userAvatar;
  double longitude;
  double latitude;
  num createTime;

  Moment({this.id, this.userId, this.cardId, this.title, this.location, this.lookLimit, this.publishType, this.content, this.fileContent, this.userName, this.userAvatar, this.longitude, this.latitude, this.createTime});


  @override
  String toString() {
    return 'Moment{id: $id, userId: $userId, cardId: $cardId, title: $title, location: $location, lookLimit: $lookLimit, publishType: $publishType, content: $content, fileContent: $fileContent, userName: $userName, userAvatar: $userAvatar, longitude: $longitude, latitude: $latitude, createTime: $createTime}';
  }

  Moment.fromJson(Map<String, dynamic> json) {
    this.id = json['id'];
    this.userId = json['userId'];
    this.cardId = json['cardId'];
    this.title = json['title'];
    this.location = json['location'];
    this.lookLimit = json['lookLimit'];
    this.publishType = json['publishType'];
    this.content = json['content'];
    this.fileContent = json['fileContent'];
    this.userName = json['userName'];
    this.userAvatar = json['userAvatar'];
    this.longitude = json['longitude'];
    this.latitude = json['latitude'];
    this.createTime = json['createTime'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['userId'] = this.userId;
    data['cardId'] = this.cardId;
    data['title'] = this.title;
    data['location'] = this.location;
    data['lookLimit'] = this.lookLimit;
    data['publishType'] = this.publishType;
    data['content'] = this.content;
    data['fileContent'] = this.fileContent;
    data['userName'] = this.userName;
    data['userAvatar'] = this.userAvatar;
    data['longitude'] = this.longitude;
    data['latitude'] = this.latitude;
    data['createTime'] = this.createTime;
    return data;
  }

}
