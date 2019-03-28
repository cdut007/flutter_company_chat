class VcardEntity {
  String id;
  String avatar;
  String cardBgImg;
  String cardStyle;
  String userId;
  String organizationId;
  String authStatus;
  String qrCode;
  String cardType;
  num createTime;
  num updateTime;
  List<HfCardDetailsListBean> hfCardDetails;


  @override
  String toString() {
    return 'VcardEntity{id: $id, avatar: $avatar, cardBgImg: $cardBgImg, cardStyle: $cardStyle, userId: $userId, organizationId: $organizationId, authStatus: $authStatus, qrCode: $qrCode, cardType: $cardType, createTime: $createTime, updateTime: $updateTime, hfCardDetails: $hfCardDetails}';
  }

  VcardEntity({this.id, this.avatar, this.cardBgImg, this.cardStyle, this.userId, this.organizationId, this.authStatus, this.qrCode, this.cardType, this.createTime, this.updateTime, this.hfCardDetails});

  VcardEntity.fromJson(Map<String, dynamic> json) {    
    this.id = json['id'];
    this.avatar = json['avatar'];
    this.cardBgImg = json['cardBgImg'];
    this.cardStyle = json['cardStyle'];
    this.userId = json['userId'];
    this.organizationId = json['organizationId'];
    this.authStatus = json['authStatus'];
    this.qrCode = json['qrCode'];
    this.cardType = json['cardType'];
    this.createTime = json['createTime'];
    this.updateTime = json['updateTime'];
    this.hfCardDetails = (json['hfCardDetails'] as List)!=null?(json['hfCardDetails'] as List).map((i) => HfCardDetailsListBean.fromJson(i)).toList():null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['avatar'] = this.avatar;
    data['cardBgImg'] = this.cardBgImg;
    data['cardStyle'] = this.cardStyle;
    data['userId'] = this.userId;
    data['organizationId'] = this.organizationId;
    data['authStatus'] = this.authStatus;
    data['qrCode'] = this.qrCode;
    data['cardType'] = this.cardType;
    data['createTime'] = this.createTime;
    data['updateTime'] = this.updateTime;
    data['hfCardDetails'] = this.hfCardDetails != null?this.hfCardDetails.map((i) => i.toJson()).toList():null;
    return data;
  }

}

class HfCardDetailsListBean {
  String id;
  String cardId;
  String cardSide;
  String name;
  String jobPosition;
  String companyName;
  String phoneNumber;
  String language;


  @override
  String toString() {
    return 'HfCardDetailsListBean{id: $id, cardId: $cardId, cardSide: $cardSide, name: $name, jobPosition: $jobPosition, companyName: $companyName, phoneNumber: $phoneNumber, language: $language}';
  }

  HfCardDetailsListBean({this.id, this.cardId, this.cardSide, this.name, this.jobPosition, this.companyName, this.phoneNumber, this.language});

  HfCardDetailsListBean.fromJson(Map<String, dynamic> json) {    
    this.id = json['id'];
    this.cardId = json['cardId'];
    this.cardSide = json['cardSide'];
    this.name = json['name'];
    this.jobPosition = json['jobPosition'];
    this.companyName = json['companyName'];
    this.phoneNumber = json['phoneNumber'];
    this.language = json['language'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['cardId'] = this.cardId;
    data['cardSide'] = this.cardSide;
    data['name'] = this.name;
    data['jobPosition'] = this.jobPosition;
    data['companyName'] = this.companyName;
    data['phoneNumber'] = this.phoneNumber;
    data['language'] = this.language;
    return data;
  }
}
