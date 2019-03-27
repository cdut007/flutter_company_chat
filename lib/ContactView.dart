import 'package:flutter/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/widget/BannerView.dart';
import 'package:flutter_app/vcard/ContactDetailsPage.dart';
import 'package:flutter_app/vcard/friend.dart';
import 'package:flutter_app/vcard/CreateEditVcardPage.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter_app/widget/HeaderListView.dart';

import 'package:http/http.dart' as http;

class ContactView extends StatefulWidget {
  ContactView({Key key}) : super(key: key);

  @override
  _ContactViewState createState() => new _ContactViewState();
}

class _ContactViewState extends State {
  List<Friend> _friends = [];

  @override
  void initState() {
    super.initState();
    _loadFriends();
  }

  Future<void> _loadFriends() async {
    http.Response response =
        await http.get('https://randomuser.me/api/?results=25');

    setState(() {
      _friends = Friend.allFromResponse(response.body);
    });
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
                          child:   new QrImage(
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

  Widget getFriendList() {
    Widget content;

    if (_friends.isEmpty) {
      content = new Center(
        child: new CircularProgressIndicator(),
      );
    } else {

      Size deviceSize = MediaQuery.of(context).size;
      content = new HeaderListView(
        _friends,
        headerList: [1],
        itemWidgetCreator: _buildFriendListTile,
        headerCreator: (BuildContext context, int position) {
          if(position == 0) {
            return    new BannerView(
              data: ['a', 'b'],
              buildShowView: (index, data) {
                return getBannerConainerWidget(deviceSize, data);
              },
              onBannerClickListener: (index, data) {
                print(index);
                Navigator.of(context)
                    .push(new MaterialPageRoute(builder: (context) {
                  return new CreateEditVcardPage();
                }));
              },
            );
          }else {
            return new Padding(padding: EdgeInsets.all(10.0), child:
            Text('$position -----header------- '),);
          }
        },
      );
//      content = new ListView.builder(
//        itemCount: _friends.length,
//        itemBuilder: _buildFriendListTile,
//      );
    }

    return content;
  }

  Widget _buildFriendListTile(BuildContext context, int index) {
    var friend = _friends[index];

    return new Column(children: <Widget>[
      new ListTile(
        onTap: () => _navigateToFriendDetails(friend, index),
        leading: new Hero(
          tag: 'contact_$index',
          child: new CircleAvatar(
            backgroundImage: new NetworkImage(friend.avatar),
          ),
        ),
        title: new Text(friend.name),
        subtitle: new Text(friend.email),
      ),
      new Divider(height: 1,)
    ],);
  }

  void _navigateToFriendDetails(Friend friend, Object avatarTag) {
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (c) {
          return new ContactDetailsPage(friend, avatarTag: avatarTag);
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    Size deviceSize = MediaQuery.of(context).size;
    return new Scaffold(
        body: new Column(
      children: <Widget>[
//        new BannerView(
//          data: ['a', 'b'],
//          buildShowView: (index, data) {
//            return getBannerConainerWidget(deviceSize, data);
//          },
//          onBannerClickListener: (index, data) {
//            print(index);
//            Navigator.of(context)
//                .push(new MaterialPageRoute(builder: (context) {
//              return new CreateEditVcardPage();
//            }));
//          },
//        ),
        new Expanded(child: new Container(child:  getFriendList(),),flex: 1,)
      ],
    ));
  }
}
