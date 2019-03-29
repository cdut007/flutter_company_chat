import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/entity/VcardEntity.dart';


class UserProfileQRCodePage extends StatefulWidget {
  VcardEntity vcardEntity = new VcardEntity();

  UserProfileQRCodePage({Key key,  this.vcardEntity}) : super(key: key);


  @override
  _UserProfileQRCodePageState createState() => _UserProfileQRCodePageState();
}

class _UserProfileQRCodePageState extends State<UserProfileQRCodePage> {

  VcardEntity _vcardEntity = new VcardEntity();
  var qrUrl=ApiManager.getDomain();
  String name='',company='',avatar;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _vcardEntity = widget.vcardEntity;
    if(_vcardEntity!=null){
     // qrUrl = _vcardEntity.id;
      if(_vcardEntity.hfCardDetails!=null && _vcardEntity.hfCardDetails.length>0){
        name = getUserVcardName(_vcardEntity);
        company = getUserVcardCompany(_vcardEntity);
        avatar = getUserVcardAvatar(_vcardEntity);
        setState(() {

        });
    }

    ApiManager.getVcardQRCodeLink({'id':_vcardEntity.id}).then((result){
      setState(() {
        qrUrl = result;
      });
    },onError: (errorData){
      print('*********getVcardQRCodeLink callback error print*********');
      var error =  ApiManager.parseErrorInfo(errorData);
      showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
    });

    }

  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: ListView(
          children: <Widget>[
            Stack(
              children: <Widget>[
                Container(
                  height: 550.0,
                  width: double.infinity,
                ),
                Container(
                  height: 200.0,
                  width: double.infinity,
                  color: GlobalConfig.themeColor(),
                ),
                Align(
                  alignment: Alignment.topLeft,
                  child: IconButton(
                    icon: Icon(Icons.arrow_back_ios),
                    onPressed: () {

                      Navigator.pop(context);
                    },
                    color: Colors.white,
                  ),
                ),
                Positioned(
                  top: 125.0,
                  left: 15.0,
                  right: 15.0,
                  child: Material(
                    elevation: 3.0,
                    borderRadius: BorderRadius.circular(7.0),
                    child: Container(
                      height: 400.0,
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(7.0),
                          color: Colors.white),
                    ),
                  ),
                ),
                Positioned(
                  top: 75.0,
                  left: (MediaQuery.of(context).size.width / 2 - 50.0),
                  child: Container(
                    height: 100.0,
                    width: 100.0,
                    child: CommonUI.getAvatarWidget(avatar,size: 100,color: Colors.orange),
                  ),
                ),
                Container(
                  margin: const EdgeInsets.only(top: 190.0),
                  padding: const EdgeInsets.only(top: 0.0, bottom: 8.0),
                  alignment: Alignment.center,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      Text(name,
                        style: TextStyle(
                            fontFamily: 'Comfortaa',
                            fontWeight: FontWeight.bold,
                            fontSize: 17.0),
                      ),
                      SizedBox(height: 7.0),
                      Text(company,
                        style: TextStyle(
                            fontFamily: 'Comfortaa',
                            fontWeight: FontWeight.bold,
                            fontSize: 17.0,
                            color: Colors.grey),
                      ),
                      SizedBox(height: 10.0),
                      Container(  margin: const EdgeInsets.only(top: 10.0),height: 240,width: 240,  child:
                      new QrImage(
                        data: qrUrl,
                        size: 240.0,
                        version: 7,
                      ),)

                    ],
                  ),
                )
              ],
            ),
          ],
        ));
  }



}