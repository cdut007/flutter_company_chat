import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/widget/BannerView.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
import 'package:flutter_app/vcard/CreateEditVcardPage.dart';
import 'package:qr_flutter/qr_flutter.dart';

class VcardBannerView extends StatefulWidget {
  VcardBannerView({Key key}) : super(key: key);

  @override
  _VcardBannerState createState() => new _VcardBannerState();
}

class _VcardBannerState extends State {
  List<VcardEntity> vcardList = [];
  var currentIndex=-1;
  @override
  void initState() {
    super.initState();

    ApiManager.getVcardListInfo().then((vcard_list_result_Str) {
      var   decodedJson = json.decode(vcard_list_result_Str);
      vcardList = _parseVcardList(decodedJson);
      _renderInfo();
      _loadVcardLists();
    }, onError: (empty) {
      _renderInfo();
      _loadVcardLists();
    });
  }

  _renderInfo() {
    setState(() {});
  }

  _parseVcardList(List<dynamic> datas) {
    List<VcardEntity> vcardList = (datas as List) != null
        ? (datas as List).map((i) => VcardEntity.fromJson(i)).toList()
        : null;
    return vcardList;
  }

  _loadVcardLists() {
    ApiManager.getUserVcardList({}).then((responseData) {
      vcardList = _parseVcardList(responseData);
      _renderInfo();
      ApiManager.saveVcardListInfo(json.encode(vcardList));
    }, onError: (errorData) {
      print('*********getUserVcardList callback error print*********');
      var error = ApiManager.parseErrorInfo(errorData);
    });
  }

  
  _getUserVcardName(VcardEntity vcardEntiy){
    return vcardEntiy.hfCardDetails[0].name;
  }
  _getUserVcardPhone(VcardEntity vcardEntiy){
    return vcardEntiy.hfCardDetails[0].phoneNumber;
  }
  _getUserVcardCompany(VcardEntity vcardEntiy){
    return vcardEntiy.hfCardDetails[0].companyName;
  }

  _getUserVcardAvatar(VcardEntity vcardEntiy){
    return vcardEntiy.avatar;
  }

  Widget _getAvatarWidget(VcardEntity data){
    var avatar = _getUserVcardAvatar(data);
    if(avatar!=null){
      return new CircleAvatar(
          backgroundImage: new NetworkImage(GlobalConfig.getHttpFilePath(avatar)),
          radius: 28.0);
    }else{
      return new Image.asset(
        "images/ic_avatar_default.png",
        width: 56.0,
        color: Colors.blueAccent,
      );
    }
  }
  
  Card getBannerConainerWidget(Size deviceSize, VcardEntity data) {
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
                        child: _getAvatarWidget(data),
                      ),
                      new Expanded(
                        child: new Padding(
                          padding: const EdgeInsets.only(left: 8),
                          child: new Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: <Widget>[
                              new Container(
                                  child: new Text(_getUserVcardName(data),
                                      style: new TextStyle(color: Colors.black),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis)),
                              new Text(_getUserVcardPhone(data),
                                  style: new TextStyle(color: Colors.grey)),
                              new Text(_getUserVcardCompany(data),
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

  @override
  Widget build(BuildContext context) {
    // TODO: implement build

    Size deviceSize = MediaQuery.of(context).size;
    if (vcardList.isEmpty) {
      return new Column(
        children: <Widget>[
          Card(
              child:  new InkWell(child:Container(
                  child: Row(mainAxisAlignment: MainAxisAlignment.center,children: <Widget>[
                    Padding(
                        padding:
                        EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                        child: Icon(Icons.add,size: 56,)),
                    Text('添加名片',style: TextStyle(fontSize: 16),)
                  ]),
                  height: 200.0,
                  padding: EdgeInsets.only(
                  ), alignment: Alignment.center,),onTap: () {
                Navigator.of(context)
                    .push(new MaterialPageRoute<String>(builder: (context) {
                  return new CreateEditVcardPage();
                })).then((String result){

                  //处理代码
                  print('********vcard banner获取上一个页面返回的参数*******');
                  print(result);


                  if(result == ApiManager.vcard_list_refresh_tag){
                    _loadVcardLists();
                  }

                });
              })
          )
        ],
      );
    } else {
      return new Column(
        children: <Widget>[
          new BannerView(
            data: vcardList,
            buildShowView: (index, data) {
              return getBannerConainerWidget(deviceSize, data);
            },
            onBannerClickListener: (index, data) {
              print(index);
              currentIndex = index;
              Navigator.of(context)
                  .push(new MaterialPageRoute<String>(builder: (context) {
                return new CreateEditVcardPage();
              })).then((String result){

                //处理代码
                print('********vcard item banner获取上一个页面返回的参数*******');
                print(result);
                if(result == ApiManager.vcard_list_refresh_tag){
                  _loadVcardLists();
                }

              });
            },
            index: currentIndex,
          )
        ],
      );
    }
  }
}
