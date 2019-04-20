import 'dart:async';
import 'package:flutter_app/entity/Friend.dart';
import 'package:flutter_app/util/ApiManager.dart';

class ContactManager {
  static List<Friend> _currentFriends = List();

  static saveFriends() {}

  static Future<Friend> searchPeopleInfo(senderId) async {
    for(Friend friend in _currentFriends){
      if(friend.id == senderId){
        return friend;
      }
    }
    var data = {};
//    var userIds = [];
//    userIds.add(senderId);
//    data['users'] = userIds;
    data['userId'] = senderId;
    var respData = await ApiManager.searchPeoplesInfo(data);
    return Friend.fromJson(respData);
  }

  static Future<List<Friend>> getFriendList(Map data, {bool localData}) async {
    if (localData && localData == true) {
      return _currentFriends;
    }
    List datas = await ApiManager.getFriendList(data);
    List<Friend> vcardList = (datas as List) != null
        ? (datas as List).map((i) => Friend.fromJson(i)).toList()
        : null;
    _currentFriends = vcardList;
    return _currentFriends;
  }
}
