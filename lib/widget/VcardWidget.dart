import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/vcard/UserProfileQRCodePage.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
//indicator view of banner
class VcardWidget extends StatefulWidget {

  final double size;
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

  Widget createVcardWdiget(VcardEntity data){

    return new Column(
      children: <Widget>[
        new Container(
          margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 16.0),
          decoration: new BoxDecoration(
              color: new Color(0xFFF5F5F5),
              borderRadius: new BorderRadius.all(new Radius.circular(6.0))
          ),
          child: new InkWell(
              child: new Container(
                child: new ListTile(
                  leading: new Container(
                    child: new CircleAvatar(
                        backgroundImage: new NetworkImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1548840758057&di=58135a141205c3f4838fdd6c37601e3e&imgtype=0&src=http%3A%2F%2Fphoto.16pic.com%2F00%2F27%2F18%2F16pic_2718026_b.jpg"),
                        radius: 20.0
                    ),
                  ),
                  title: new Container(
                    margin: const EdgeInsets.only(bottom: 2.0),
                    child: Row(children: <Widget>[
                      Padding(
                          padding:
                          EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                          child: Text(getUserVcardName(data))),
                      Icon(Icons.verified_user,color: Colors.orangeAccent)
                    ]),
                  ),
                  subtitle: new Container(
                    margin: const EdgeInsets.only(top: 2.0),
                    child: new Text(getUserVcardCompany(data)),
                  ),
                ),
              )
          ),
        ),
        new Container(
          child: new Row(
            children: <Widget>[
              new Container(
                child: new Icon(Icons.call, color: GlobalConfig.fontColor, size: 16.0),
                margin: const EdgeInsets.only(right: 12.0),
              ),
              new Expanded(
                child: new Container(
                  child: new Text(getUserVcardPhone(data), style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 13.0),),
                ),
              )
            ],
          ),
          margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 0.0),
          padding: const EdgeInsets.only(bottom: 0.0),
          decoration: new BoxDecoration(
              border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
          ),
        ),
        new Container(
          child: new Row(
            children: <Widget>[
              new Container(
                child: new Icon(Icons.view_stream, color: GlobalConfig.fontColor, size: 16.0),
                margin: const EdgeInsets.only(right: 12.0),
              ),
              new Expanded(
                child: new Container(
                  child: new Text(getUserVcardJobPosition(data), style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 13.0),),
                ),
              )
            ],
          ),
          margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 0.0),
          padding: const EdgeInsets.only(bottom: 0.0),
          decoration: new BoxDecoration(
              border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
          ),
        ),
        new Container(
          child: new Row(
            children: <Widget>[
              new Container(
                child: new Icon(Icons.location_on, color: GlobalConfig.fontColor, size: 16.0),
                margin: const EdgeInsets.only(right: 12.0),
              ),
              new Expanded(
                child: new Container(
                  child: new Text("成都市成华区红星路三段一号IFS一号办公楼23A", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 13.0),),
                ),
              )
            ],
          ),
          margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 0.0),
          padding: const EdgeInsets.only(bottom: 0.0),
          decoration: new BoxDecoration(
              border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
          ),
        )
      ],
    );
  }

  var cardHeight = 200.00;

  Card _getBannerConainerWidget(Size deviceSize, VcardEntity data) {
    if(widget.size!=null){
      cardHeight = widget.size;
    }
    return Card(
        child: Container(
          height: cardHeight,
          padding: EdgeInsets.only(
//              left: 10.0,
//              right: 10.0,
            bottom: 10.0,
          ),
          child: Container(
              width: deviceSize.width,
              child: Container(
                  color: Colors.white,
                  child:
                  Column(children: <Widget>[
                    Container(
                      padding: EdgeInsets.all(18.0),
                      child: createVcardWdiget(data),
//                      Row(
//                        children: <Widget>[
//                          new Container(
//                            child: CommonUI.getAvatarWidget(getUserVcardAvatar(data)),
//                          ),
//                          new Expanded(
//                            child: new Padding(
//                              padding: const EdgeInsets.only(left: 8),
//                              child: new Column(
//                                crossAxisAlignment: CrossAxisAlignment.start,
//                                children: <Widget>[
//                                  new Container(
//                                      child: new Text(getUserVcardName(data),
//                                          style: new TextStyle(color: Colors.black),
//                                          maxLines: 1,
//                                          overflow: TextOverflow.ellipsis)),
//                                  new Text(getUserVcardPhone(data),
//                                      style: new TextStyle(color: Colors.grey)),
//                                  new Text(getUserVcardCompany(data),
//                                      style: new TextStyle(color: Colors.grey)),
//                                ],
//                              ),
//                            ),
//                            flex: 1,
//                          ),
//                          Container(
//                            child: Icon(
//                              Icons.keyboard_arrow_right,
//                              size: 36,
//                              color: Colors.grey,
//                            ),
//                            alignment: Alignment.centerRight,
//                            margin: EdgeInsets.fromLTRB(0.0, 0.0, 10.0, 0.0),
//                          ),
//                        ],
//                      ),
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

