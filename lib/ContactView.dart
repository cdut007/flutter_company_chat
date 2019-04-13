import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/widget/BannerView.dart';
import 'package:flutter_app/vcard/ContactDetailsPage.dart';
import 'package:flutter_app/vcard/friend.dart';
import 'package:flutter_app/vcard/CreateEditVcardPage.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter_app/widget/HeaderListView.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
import 'package:flutter_app/widget/LoadingWidget.dart';
import 'package:flutter_app/vcard/friend.dart';
import 'package:flutter_app/widget/VcardBannerView.dart';
import 'dart:async';
import 'package:event_bus/event_bus.dart';
import 'package:flutter_app/vcard/ApplyVcardListPage.dart';

import 'package:http/http.dart' as http;

class ContactView extends StatefulWidget {
  ContactView({Key key}) : super(key: key);

  @override
  _ContactViewState createState() => new _ContactViewState();
}

class _ContactViewState extends State {
  List<VcardEntity> _friends = [];

  LoadingType loadingType = LoadingType.Loading;
  EventBus eventBus = GlobalConfig.getEventBus();
  StreamSubscription loginSubscription;

  @override
  void initState() {
    super.initState();
    _loadFriends();
    print('*********【初始化名片列表订阅事件】*********');
    loginSubscription = eventBus.on<VcardEntity>().listen((event) {
      print('*********收到名片列表订阅事件*********');
      print(event);
      _loadFriends();
    });
  }

  @override
  void dispose() {
    super.dispose();
    loginSubscription.cancel();
    print('*********【取消名片列表订阅事件】*********');
  }

  Future<Null> pullToRefresh() async {
    _loadFriends();
    return null;
  }

  Future<void> _loadFriends() async {
    ApiManager.getVcardList({}).then((datas) {
      List<VcardEntity> vcardList = (datas as List) != null
          ? (datas as List).map((i) => VcardEntity.fromJson(i)).toList()
          : null;
      var applyList = vcardList;
      setState(() {
        _friends = applyList;
        if (_friends.length > 0) {
          loadingType = LoadingType.End;
        } else {
          loadingType = LoadingType.Empty;
        }
      });
    }, onError: (errorData) {
      setState(() {
        if (_friends.length > 0) {
          loadingType = LoadingType.End;
        } else {
          loadingType = LoadingType.Error;
        }
      });
      print('*********getVcardList callback error print*********');
      var error = ApiManager.parseErrorInfo(errorData);
      showErrorInfo(context, '错误码：${error.code}' + ' 错误原因：' + error.msg);
      print('*********getVcardList callback error print end*********');
    });
  }

  @override
  void activate() {}

  @override
  void deactivate() {
    super.deactivate();
  }

  Card getBannerConainerWidget(Size deviceSize, var data) {
    return Card(
        child: Container(
      height: 200.0,
      padding: EdgeInsets.only(
//              left: 10.0,
//              right: 10.0,
        bottom: 10.0,
      ),
      child: Container(
          width: deviceSize.width,
          child: Container(
              color: Colors.white,
              child: Column(children: <Widget>[
                Container(
                  padding: EdgeInsets.all(18.0),
                  child: Row(
                    children: <Widget>[
                      new Container(
                        child: new CircleAvatar(
                            backgroundImage: new NetworkImage(
                                'https://pic3.zhimg.com/50/2b8be8010409012e7cdd764e1befc4d1_s.jpg'),
                            radius: 28.0),
                      ),
                      new Expanded(
                        child: new Padding(
                          padding: const EdgeInsets.only(left: 8),
                          child: new Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: <Widget>[
                              new Container(
                                  child: new Text(data + 'aaa',
                                      style: new TextStyle(color: Colors.black),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis)),
                              new Text('中国max科技',
                                  style: new TextStyle(color: Colors.grey)),
                            ],
                          ),
                        ),
                        flex: 1,
                      ),
                      Container(
                        child: Icon(
                          Icons.keyboard_arrow_right,
                          size: 36,
                          color: Colors.grey,
                        ),
                        alignment: Alignment.centerRight,
                        margin: EdgeInsets.fromLTRB(0.0, 0.0, 10.0, 0.0),
                      ),
                    ],
                  ),
                ),
//                Expanded(
//                  child: Container(
//                    padding: EdgeInsets.only(left: 18.0, right: 18.0),
//                    child: Text(
//                      "科技改变中国.",
//                      style: TextStyle(
//                        fontSize: 14.0,
//                      ),
//                    ),
//                  ),
//                ),
                Expanded(
                  child: Container(),
                ),
                Container(
                  height: 1.0,
                  color: Colors.blueGrey,
                ),
                Container(
                    alignment: Alignment.center,
                    padding: EdgeInsets.all(8.0),
                    child: Row(children: <Widget>[
                      new Card(
                        color: GlobalConfig.themeColor(),
                        elevation: 3.0,
                        child: new FlatButton(
                            onPressed: () {
                              print("send vcard");
                            },
                            child: new Padding(
                              padding: new EdgeInsets.all(0.0),
                              child: new Text(
                                '发送名片',
                                style: new TextStyle(
                                  color: Colors.white,
                                  fontSize: 14.0,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            )),
                      ),
                      Expanded(
                        child: Container(
                          alignment: Alignment.centerRight,
                          child: new QrImage(
                            data: "holdingFuture",
                            size: 56.0,
                          ),
                        ),
                        flex: 1,
                      )
                    ]))
              ]))),
    ));
  }

  Widget renderHead() {
    return new Column(
      children: <Widget>[
       // new VcardBannerView(),
        new Padding(
          padding: EdgeInsets.all(6.0),
          child: Column(
            children: <Widget>[
              InkWell(
                child: new Padding(
                    padding: const EdgeInsets.fromLTRB(10.0, 15.0, 10.0, 15.0),
                    child: new Row(
                      children: <Widget>[
                        Container(
                          child: Icon(
                            Icons.contacts,
                            color: Colors.blueGrey,
                          ),
                          margin: EdgeInsets.fromLTRB(0.0, 0.0, 10.0, 0.0),
                        ),
                        new Expanded(
                          child: new Text(
                            '新的名片申请',
                            style: new TextStyle(fontSize: 16.0),
                          ),
                          flex: 1,
                        ),
//                      Icon(
//                        Icons.keyboard_arrow_right,
//                        size: 36,
//                        color: Colors.grey,
//                      )
                      ],
                    )),
                onTap: () {
                  Navigator.push(
                      context,
                      new MaterialPageRoute(
                          builder: (context) => new ApplyVcardListPage()));
                },
              ),
              Divider(
                height: 1.0,
              )
            ],
          ),
        )
      ],
    );
  }

  Widget getFriendList() {
    Widget content;
    if (loadingType == LoadingType.Loading) {
      return Center(
        child: Column(
          children: <Widget>[
            renderHead(),
            new LoadingWidget(
              loadingType: LoadingType.Loading,
            )
          ],
        ),
      );
    }

    if (loadingType == LoadingType.Empty) {
      return Center(
        child: Column(
          children: <Widget>[
            renderHead(),
            new LoadingWidget(
              loadingType: LoadingType.Empty,
              clickCallback: () {
                print('click empty ui');
                setState(() {
                  loadingType = LoadingType.Loading;
                });
                pullToRefresh();
              },
            )
          ],
        ),
      );
    }

    if (loadingType == LoadingType.Error) {
      return Center(
        child: Column(
          children: <Widget>[
            renderHead(),
            new LoadingWidget(
              loadingType: LoadingType.Error,
              clickCallback: () {
                setState(() {
                  loadingType = LoadingType.Loading;
                });
                pullToRefresh();
              },
            )
          ],
        ),
      );
    }

    Size deviceSize = MediaQuery.of(context).size;
    content = new HeaderListView(
      _friends,
      headerList: [1, 2],
      itemWidgetCreator: _buildFriendListTile,
      headerCreator: (BuildContext context, int position) {
        if (position == 0) {
           return new Container();
          // return   new VcardBannerView();
        } else {
          return renderHead();
        }
      },
      usePullToRefresh: true,
      onHeaderRefresh: pullToRefresh,
    );
//      content = new ListView.builder(
//        itemCount: _friends.length,
//        itemBuilder: _buildFriendListTile,
//      );

    return content;
  }

  Widget _buildFriendListTile(BuildContext context, int index) {
    var friend = _friends[index];

    return new Column(
      children: <Widget>[
        new ListTile(
          onTap: () => _navigateToFriendDetails(friend, index),
          leading: new Hero(
            tag: 'contact_$index',
            child: CommonUI.getAvatarWidget(friend.avatar),
          ),
          title: new Text(getUserVcardName(friend)),
          subtitle: new Text(getUserVcardCompany(friend)),
        ),
        new Divider(
          height: 1,
        )
      ],
    );
  }

  void _navigateToFriendDetails(VcardEntity friend, Object avatarTag) {
    Friend people =  Friend(
        avatar: 'http://res',
        name: 'test',
        email: 'ww@ww',
        location: 'china');
    people.id = friend.userId;
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (c) {
          return new ContactDetailsPage(
              people,
              avatarTag: avatarTag);
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build

    Size deviceSize = MediaQuery.of(context).size;
    return new Scaffold(body: getFriendList());
  }
}
