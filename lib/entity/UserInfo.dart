class UserInfo {
  String id;
  String username;
  String passwd;
  String email;
  String phoneNumber;
  String nickname;
  String realname;
  String createTime;
  String gender;
  String organizationId;
  String fromName;
  String fromId;
  String randomCode;
  String introduce;
  String avatar;
  String userType;
  String blockStatus;
  num lastLoginTime;

  UserInfo({this.id, this.username, this.passwd, this.email, this.phoneNumber, this.nickname, this.realname, this.createTime, this.gender, this.organizationId, this.fromName, this.fromId, this.randomCode, this.introduce, this.avatar, this.userType, this.blockStatus, this.lastLoginTime});

  UserInfo.fromJson(Map<String, dynamic> json) {    
    this.id = json['id'];
    this.username = json['username'];
    this.passwd = json['passwd'];
    this.email = json['email'];
    this.phoneNumber = json['phoneNumber'];
    this.nickname = json['nickname'];
    this.realname = json['realname'];
    this.createTime = json['createTime'];
    this.gender = json['gender'];
    this.organizationId = json['organizationId'];
    this.fromName = json['fromName'];
    this.fromId = json['fromId'];
    this.randomCode = json['randomCode'];
    this.introduce = json['introduce'];
    this.avatar = json['avatar'];
    this.userType = json['userType'];
    this.blockStatus = json['blockStatus'];
    this.lastLoginTime = json['lastLoginTime'];
  }


  @override
  String toString() {
    return 'UserInfo{id: $id, username: $username, passwd: $passwd, email: $email, phoneNumber: $phoneNumber, nickname: $nickname, realname: $realname, createTime: $createTime, gender: $gender, organizationId: $organizationId, fromName: $fromName, fromId: $fromId, randomCode: $randomCode, introduce: $introduce, avatar: $avatar, userType: $userType, blockStatus: $blockStatus, lastLoginTime: $lastLoginTime}';
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['username'] = this.username;
    data['passwd'] = this.passwd;
    data['email'] = this.email;
    data['phoneNumber'] = this.phoneNumber;
    data['nickname'] = this.nickname;
    data['realname'] = this.realname;
    data['createTime'] = this.createTime;
    data['gender'] = this.gender;
    data['organizationId'] = this.organizationId;
    data['fromName'] = this.fromName;
    data['fromId'] = this.fromId;
    data['randomCode'] = this.randomCode;
    data['introduce'] = this.introduce;
    data['avatar'] = this.avatar;
    data['userType'] = this.userType;
    data['blockStatus'] = this.blockStatus;
    data['lastLoginTime'] = this.lastLoginTime;
    return data;
  }

}
