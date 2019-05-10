import 'dart:convert';
import 'dart:async';
import 'package:flutter_web/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/widget/BannerView.dart';
import 'package:flutter_app/widget/VcardWidget.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
import 'package:flutter_app/vcard/CreateEditVcardPage.dart';
import 'package:event_bus/event_bus.dart';

class VcardScanBannerView extends StatefulWidget {

  VcardScanBannerView({Key key }) : super(key: key);

  @override
  _VcardScanBannerState createState() => new _VcardScanBannerState();
}

class _VcardScanBannerState extends State {
  List<VcardEntity> vcardList = [];
  var currentIndex=-1;
  EventBus eventBus = GlobalConfig.getEventBus();
  StreamSubscription loginSubscription;
  @override
  void initState() {
    super.initState();
    print('*********【初始化名片订阅事件】*********');
     loginSubscription = eventBus.on<VcardEntity>().listen((event) {
       print('*********收到名片订阅事件*********');
      print(event);
      _loadVcardLists();
    });

    _loadVcardLists();
  }

  @override
  void dispose(){
    super.dispose();
    loginSubscription.cancel();
    print('*********【取消名片订阅事件】*********');

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




  var cardHeight = 286.00;

  Widget createAddCard(){
    return   Card(
        child:  new InkWell(child:Container(
          child: Row(mainAxisAlignment: MainAxisAlignment.center,children: <Widget>[
            Padding(
                padding:
                EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                child: Icon(Icons.add,size: 56,)),
            Text('添加名片',style: TextStyle(fontSize: 16),)
          ]),
          height: cardHeight,
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
    );
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build

    Size deviceSize = MediaQuery.of(context).size;
    if (vcardList.isEmpty) {
      return new Column(
        children: <Widget>[
        createAddCard()
        ],
      );
    } else {
      List<VcardEntity> vcardItemList = new List();
      vcardItemList.addAll(vcardList);
      vcardItemList.add(new VcardEntity());
      return new Column(
        children: <Widget>[
          new BannerView(
            data: vcardItemList,
            buildShowView: (index, data) {
              if((data as VcardEntity).id == null){
                return createAddCard();
              }
              return VcardWidget(vcardEntity: data,size: cardHeight,);
            },
            onBannerClickListener: (index, data) {
              print('onBannerClickListener=$index');
              currentIndex = index;
              Navigator.of(context)
                  .push(new MaterialPageRoute<String>(builder: (context) {
                return new CreateEditVcardPage(key:Key('vcard'),vcardEntity:data);
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
            height: cardHeight,
          )
        ],
      );
    }
  }
}
