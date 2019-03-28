import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/vcard/UserProfileQRCodePage.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
//indicator view of banner
class VcardWidget extends StatefulWidget {

  final int size;
  final VcardEntity vcardEntity;

  VcardWidget({
    Key key,
    this.size,
  @required this.vcardEntity
  } ):super(key: key);

  @override
  _VcardWidgetState createState() => new _VcardWidgetState();
}

class _VcardWidgetState extends State<VcardWidget> {
  @override
  Widget build(BuildContext context) {

    Size deviceSize = MediaQuery.of(context).size;
    return this._getBannerConainerWidget(deviceSize,widget.vcardEntity);
  }

  Card _getBannerConainerWidget(Size deviceSize, VcardEntity data) {
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
                            child: CommonUI.getAvatarWidget(getUserVcardAvatar(data)),
                          ),
                          new Expanded(
                            child: new Padding(
                              padding: const EdgeInsets.only(left: 8),
                              child: new Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: <Widget>[
                                  new Container(
                                      child: new Text(getUserVcardName(data),
                                          style: new TextStyle(color: Colors.black),
                                          maxLines: 1,
                                          overflow: TextOverflow.ellipsis)),
                                  new Text(getUserVcardPhone(data),
                                      style: new TextStyle(color: Colors.grey)),
                                  new Text(getUserVcardCompany(data),
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
                                  Navigator.push(context,
                                      new MaterialPageRoute(builder: (context) => new UserProfileQRCodePage( key:Key('profile'),vcardEntity:data)));

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

}

