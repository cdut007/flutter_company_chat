class Friend {
  String id;
  String username;
  String phoneNumber;
  String introduce;
  String organizationName;
  String avatar;
  String organizationLogo;
  String organizationWebsite;

  Friend({this.id, this.username, this.phoneNumber, this.introduce, this.organizationName, this.avatar, this.organizationLogo, this.organizationWebsite});


  @override
  String toString() {
    return 'Friend{id: $id, username: $username, phoneNumber: $phoneNumber, introduce: $introduce, organizationName: $organizationName, avatar: $avatar, organizationLogo: $organizationLogo, organizationWebsite: $organizationWebsite}';
  }

  Friend.fromJson(Map<String, dynamic> json) {
    this.id = json['id'];
    this.username = json['username'];
    this.phoneNumber = json['phoneNumber'];
    this.introduce = json['introduce'];
    this.organizationName = json['organizationName'];
    this.avatar = json['avatar'];
    this.organizationLogo = json['organizationLogo'];
    this.organizationWebsite = json['organizationWebsite'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['username'] = this.username;
    data['phoneNumber'] = this.phoneNumber;
    data['introduce'] = this.introduce;
    data['organizationName'] = this.organizationName;
    data['avatar'] = this.avatar;
    data['organizationLogo'] = this.organizationLogo;
    data['organizationWebsite'] = this.organizationWebsite;
    return data;
  }

}
