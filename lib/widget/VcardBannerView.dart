import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/widget/BannerView.dart';
import 'package:flutter_app/vcard/CreateEditVcardPage.dart';
import 'package:qr_flutter/qr_flutter.dart';

class VcardBannerView extends StatefulWidget {
  VcardBannerView({Key key}) : super(key: key);

  @override
  _VcardBannerState createState() => new _VcardBannerState();
}

class _VcardBannerState extends State {


  @override
  void initState() {
    super.initState();
    _loadVcardLists();
  }

  Future<void> _loadVcardLists() async {
    // response = ApiManager.getVcardListInfo();


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

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    Size deviceSize = MediaQuery.of(context).size;
    return new Column(
      children: <Widget>[
        new BannerView(
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
        )
      ],
    );
  }
}
