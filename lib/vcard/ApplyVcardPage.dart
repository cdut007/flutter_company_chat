import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter/cupertino.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'dart:convert';
import 'dart:async';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
import 'package:flutter_app/widget/VcardWidget.dart';
import 'package:path_provider/path_provider.dart';

class ApplyVcardPage extends StatefulWidget {

   String applyUrl;

   ApplyVcardPage({Key key,  this.applyUrl}) : super(key: key);

  @override
  ApplyVcardPageState createState() => ApplyVcardPageState();
}

class ApplyVcardPageState extends State<ApplyVcardPage>
    with SingleTickerProviderStateMixin {
  bool _status = true;

  VcardEntity _vcardEntity = VcardEntity();

  VcardEntity _appliedVcardEntity = VcardEntity();

  var _nameController = new TextEditingController();
  var _mailController = new TextEditingController();
  var _phoneController = new TextEditingController();
  var _jobPostionController = new TextEditingController();
  var _companyController = new TextEditingController();



  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    if(_vcardEntity.hfCardDetails!=null && _vcardEntity.hfCardDetails.length>0){
      _nameController.text = getUserVcardName(_vcardEntity);
      _phoneController.text = getUserVcardPhone(_vcardEntity);
      _jobPostionController.text = getUserVcardJobPosition(_vcardEntity);
      _companyController.text = getUserVcardCompany(_vcardEntity);
      setState(() {

      });
    }

    ApiManager.getVcardListInfo().then((vcard_list_result_Str) {
      var   decodedJson = json.decode(vcard_list_result_Str);
      List<VcardEntity> vcardList = _parseVcardList(decodedJson);
      if(vcardList.length>0){
        _vcardEntity = vcardList[0];
      }
    }, onError: (empty) {

    });

    var data={'url':widget.applyUrl};
    ApiManager.parseQRCodeLink(data).then((result){

      setState(() {
        _appliedVcardEntity = VcardEntity.fromJson(result);
      });
    },onError: (errorData){
      print('*********parseQRCodeLink callback error print*********');
      var error =  ApiManager.parseErrorInfo(errorData);
      showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
      print('*********parseQRCodeLink callback error print end*********');
    });

  }
  _parseVcardList(List<dynamic> datas) {
    List<VcardEntity> vcardList = (datas as List) != null
        ? (datas as List).map((i) => VcardEntity.fromJson(i)).toList()
        : null;
    return vcardList;
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("名片详情", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
        ),
        body: new Container(
          color: Colors.white,
          child:
              Column(
                children: <Widget>[
               VcardWidget(vcardEntity: _appliedVcardEntity,),
                  _getActionButtons()
                ],
              ),
        ));
  }

  @override
  void dispose() {
    // Clean up the controller when the Widget is disposed
    super.dispose();
  }
  applyCard(){
    showLoadingDialog(context);

    Map<String, dynamic> data ={'applyCard':_vcardEntity.id,'respondCard':_appliedVcardEntity.id};
    if(_vcardEntity.id!=null){
      final future = ApiManager.applyCard(data);
      future.then((data){
        print('*********applyCard callback*********');
        closeLoadingDialog();
        print(data);
        showToast(context, '成功发送名片交换');
        Navigator.pop(context,ApiManager.vcard_list_refresh_tag);

      },onError: (errorData){
        print('********* applyCard error print*********');
        var error =  ApiManager.parseErrorInfo(errorData);
        closeLoadingDialog();
        showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
        print('*********applyCard callback error print end*********');
        //
      });
    }else{
      //先创建名片。。。
    }

  }

  Widget _getActionButtons() {
    return Padding(
      padding: EdgeInsets.only(left: 25.0, right: 25.0, top: 45.0),
      child: new Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          Expanded(
            child: Padding(
              padding: EdgeInsets.only(right: 10.0),
              child: new Card(
                color: GlobalConfig.themeColor(),
                elevation: 6.0,
                child: new FlatButton(
                    onPressed: () {

                      applyCard();


                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '添加名片',
                        style:
                            new TextStyle(color: Colors.white, fontSize: 16.0),
                      ),
                    )),
              ),
            ),
            flex: 2,
          ),
        ],
      ),
    );
  }

}
