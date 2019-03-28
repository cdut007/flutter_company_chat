class ApplyVcardEntity {
  String id;
  String applyUser;
  String respondUser;
  String applyCard;
  String respondCard;
  String applyStatus;
  String userName;
  String userCompany;
  String userJob;
  String userAvatar;
  String phoneNumber;
  num creatTime;
  num updateTime;


  @override
  String toString() {
    return 'ApplyVcardEntity{id: $id, applyUser: $applyUser, respondUser: $respondUser, applyCard: $applyCard, respondCard: $respondCard, applyStatus: $applyStatus, userName: $userName, userCompany: $userCompany, userJob: $userJob, userAvatar: $userAvatar, phoneNumber: $phoneNumber, creatTime: $creatTime, updateTime: $updateTime}';
  }

  ApplyVcardEntity({this.id, this.applyUser, this.respondUser, this.applyCard, this.respondCard, this.applyStatus, this.userName, this.userCompany, this.userJob, this.userAvatar, this.phoneNumber, this.creatTime, this.updateTime});

  ApplyVcardEntity.fromJson(Map<String, dynamic> json) {    
    this.id = json['id'];
    this.applyUser = json['applyUser'];
    this.respondUser = json['respondUser'];
    this.applyCard = json['applyCard'];
    this.respondCard = json['respondCard'];
    this.applyStatus = json['applyStatus'];
    this.userName = json['userName'];
    this.userCompany = json['userCompany'];
    this.userJob = json['userJob'];
    this.userAvatar = json['userAvatar'];
    this.phoneNumber = json['phoneNumber'];
    this.creatTime = json['creatTime'];
    this.updateTime = json['updateTime'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['applyUser'] = this.applyUser;
    data['respondUser'] = this.respondUser;
    data['applyCard'] = this.applyCard;
    data['respondCard'] = this.respondCard;
    data['applyStatus'] = this.applyStatus;
    data['userName'] = this.userName;
    data['userCompany'] = this.userCompany;
    data['userJob'] = this.userJob;
    data['userAvatar'] = this.userAvatar;
    data['phoneNumber'] = this.phoneNumber;
    data['creatTime'] = this.creatTime;
    data['updateTime'] = this.updateTime;
    return data;
  }

}
