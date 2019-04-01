class Moment {
  String id;
  String userId;
  String title;
  String location;
  String lookLimit;
  String publishType;
  String longitude;
  String latitude;
  String content;
  String fileContent;
  num createTime;

  Moment({this.id, this.userId, this.title, this.location, this.lookLimit, this.publishType, this.longitude, this.latitude, this.content, this.fileContent, this.createTime});


  @override
  String toString() {
    return 'Moment{id: $id, userId: $userId, title: $title, location: $location, lookLimit: $lookLimit, publishType: $publishType, longitude: $longitude, latitude: $latitude, content: $content, fileContent: $fileContent, createTime: $createTime}';
  }

  Moment.fromJson(Map<String, dynamic> json) {
    this.id = json['id'];
    this.userId = json['userId'];
    this.title = json['title'];
    this.location = json['location'];
    this.lookLimit = json['lookLimit'];
    this.publishType = json['publishType'];
    this.longitude = json['longitude'];
    this.latitude = json['latitude'];
    this.content = json['content'];
    this.fileContent = json['fileContent'];
    this.createTime = json['createTime'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['userId'] = this.userId;
    data['title'] = this.title;
    data['location'] = this.location;
    data['lookLimit'] = this.lookLimit;
    data['publishType'] = this.publishType;
    data['longitude'] = this.longitude;
    data['latitude'] = this.latitude;
    data['content'] = this.content;
    data['fileContent'] = this.fileContent;
    data['createTime'] = this.createTime;
    return data;
  }

}
