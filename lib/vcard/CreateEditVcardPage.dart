import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter/cupertino.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'dart:async';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:path_provider/path_provider.dart';

class CreateEditVcardPage extends StatefulWidget {
  @override
  CreateEditVcardPageState createState() => CreateEditVcardPageState();
}

class CreateEditVcardPageState extends State<CreateEditVcardPage>
    with SingleTickerProviderStateMixin {
  bool _status = true;
  final FocusNode myFocusNode = FocusNode();


  var _nameController = new TextEditingController();
  var _mailController = new TextEditingController();
  var _phoneController = new TextEditingController();
  var _telController = new TextEditingController();
  var _addressController = new TextEditingController();

  File _image;

  Future getImage() async {
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);

    var fileName = GlobalConfig.getFileName(image);
    ApiManager.uploadFile(fileName, image.path);
    setState(() {
      _image = image;

    });
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
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
          child: new ListView(
            children: <Widget>[
              Column(
                children: <Widget>[
                  new Container(
                    height: 220.0,
                    color: Colors.white,
                    child: new Column(
                      children: <Widget>[
                        Padding(
                          padding: EdgeInsets.only(top: 20.0),
                          child:
                              new Stack(fit: StackFit.loose, children: <Widget>[
                            new Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: <Widget>[
                                new Container(
                                    width: 140.0,
                                    height: 140.0,
                                    decoration: new BoxDecoration(
                                      shape: BoxShape.circle,
                                      image:  new DecorationImage(
                                        image: new ExactAssetImage(
                                            'images/a001.jpg'),
                                        fit: BoxFit.cover,
                                      ),
                                    )),
                              ],
                            ),
                            Padding(
                                padding:
                                    EdgeInsets.only(top: 90.0, right: 100.0),
                                child: new GestureDetector(
                                    onTapUp: (details) {
                                      getImage();
                                    },
                                    child: new Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: <Widget>[
                                        new CircleAvatar(
                                          backgroundColor:
                                              GlobalConfig.themeColor(),
                                          radius: 25.0,
                                          child: new Icon(
                                            Icons.camera_alt,
                                            color: Colors.white,
                                          ),
                                        )
                                      ],
                                    ))),
                          ]),
                        )
                      ],
                    ),
                  ),
                  new Container(
                    color: Color(0xffFFFFFF),
                    child: Padding(
                      padding: EdgeInsets.only(bottom: 25.0),
                      child: new Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: <Widget>[
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 0.0),
                              child: new Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    mainAxisSize: MainAxisSize.min,
                                    children: <Widget>[
                                      new Text(
                                        '个人信息',
                                        style: TextStyle(
                                            fontSize: 18.0,
                                            fontWeight: FontWeight.bold),
                                      ),
                                    ],
                                  ),
                                  new Column(
                                    mainAxisAlignment: MainAxisAlignment.end,
                                    mainAxisSize: MainAxisSize.min,
                                    children: <Widget>[
                                      _status
                                          ? _getEditIcon()
                                          : new Container(),
                                    ],
                                  )
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 25.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    mainAxisSize: MainAxisSize.min,
                                    children: <Widget>[
                                      new Text(
                                        '全名',
                                        style: TextStyle(
                                            fontSize: 16.0,
                                            fontWeight: FontWeight.bold),
                                      ),
                                    ],
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 2.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Flexible(
                                    child: new TextField(
                                      decoration: const InputDecoration(
                                        hintText: "输入全名",
                                      ),
                                      enabled: !_status,
                                      autofocus: !_status,
                                      controller: _nameController,
                                    ),
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 25.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    mainAxisSize: MainAxisSize.min,
                                    children: <Widget>[
                                      new Text(
                                        '邮箱',
                                        style: TextStyle(
                                            fontSize: 16.0,
                                            fontWeight: FontWeight.bold),
                                      ),
                                    ],
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 2.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Flexible(
                                    child: new TextField(
                                      decoration: const InputDecoration(
                                          hintText: "输入邮箱"),
                                      enabled: !_status,
                                      controller: _mailController,
                                    ),
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 25.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: <Widget>[
                                  Expanded(
                                    child: Container(
                                      child: new Text(
                                        '移动电话',
                                        style: TextStyle(
                                            fontSize: 16.0,
                                            fontWeight: FontWeight.bold),
                                      ),
                                    ),
                                    flex: 2,
                                  ),
                                  Expanded(
                                    child: Container(
                                      child: new Text(
                                        '办公电话',
                                        style: TextStyle(
                                            fontSize: 16.0,
                                            fontWeight: FontWeight.bold),
                                      ),
                                    ),
                                    flex: 2,
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 2.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: <Widget>[
                                  Flexible(
                                    child: Padding(
                                      padding: EdgeInsets.only(right: 10.0),
                                      child: new TextField(
                                        decoration: const InputDecoration(
                                            hintText: "输入移动电话"),
                                        enabled: !_status,
                                        controller: _phoneController,
                                      ),
                                    ),
                                    flex: 2,
                                  ),
                                  Flexible(
                                    child: new TextField(
                                      decoration: const InputDecoration(
                                          hintText: "输入办公电话"),
                                      enabled: !_status,
                                      controller: _telController,
                                    ),
                                    flex: 2,
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 25.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    mainAxisSize: MainAxisSize.min,
                                    children: <Widget>[
                                      new Text(
                                        '地址',
                                        style: TextStyle(
                                            fontSize: 16.0,
                                            fontWeight: FontWeight.bold),
                                      ),
                                    ],
                                  ),
                                ],
                              )),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 25.0, right: 25.0, top: 2.0),
                              child: new Row(
                                mainAxisSize: MainAxisSize.max,
                                children: <Widget>[
                                  new Flexible(
                                    child: new TextField(
                                      decoration: const InputDecoration(
                                          hintText: "输入地址"),
                                      enabled: !_status,
                                      controller: _addressController,
                                    ),
                                  ),
                                ],
                              )),
                          !_status ? _getActionButtons() : new Container(),
                        ],
                      ),
                    ),
                  )
                ],
              ),
            ],
          ),
        ));
  }

  @override
  void dispose() {
    // Clean up the controller when the Widget is disposed
    myFocusNode.dispose();
    super.dispose();
  }
  createOrEditCard(){
    showLoadingDialog(context);
    var hfCardDetails=[];
    hfCardDetails.add({"cardSide": "FRONT",
      "companyName": "公司名称",
      "jobPosition": "职位信息",
      "language": "语言",
      "name": _nameController.text,
      "phoneNumber": _phoneController.text});
    Map<String, dynamic> data ={'hfCardDetails':hfCardDetails};
    final future = ApiManager.createCard(data);
    future.then((data){
      print('*********createCard callback*********');
      closeLoadingDialog();
      print(data);
      showToast(context, '添加名片成功');
      var vcardData = {};
      final vcardInfoFuture = ApiManager.getUserVcardList(vcardData);
      setState(() {
        _status = true;
        FocusScope.of(context).requestFocus(new FocusNode());
      });

    },onError: (errorData){
      print('********* createCard error print*********');
      var error =  ApiManager.parseErrorInfo(errorData);
      closeLoadingDialog();
      showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
      print('*********createCard callback error print end*********');
      //
    });
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
                      if( _nameController.text.isEmpty){
                        showToast(context,'请输入全名');
                        return;
                      }
                      if( _mailController.text.isEmpty){
                        showToast(context,'请输入邮箱');
                        return;
                      }
                      if( _phoneController.text.isEmpty){
                        showToast(context,'请输入手机号');
                        return;
                      }

                      createOrEditCard();


                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '保存',
                        style:
                            new TextStyle(color: Colors.white, fontSize: 16.0),
                      ),
                    )),
              ),
            ),
            flex: 2,
          ),
          Expanded(
            child: Padding(
              padding: EdgeInsets.only(left: 10.0),
              child: new Card(
                color: Colors.red,
                elevation: 6.0,
                child: new FlatButton(
                    onPressed: () {
                      setState(() {
                        _status = true;
                        FocusScope.of(context).requestFocus(new FocusNode());
                      });
                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '取消',
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

  Widget _getEditIcon() {
    return new GestureDetector(
      child: new CircleAvatar(
        backgroundColor: GlobalConfig.themeColor(),
        radius: 14.0,
        child: new Icon(
          Icons.edit,
          color: Colors.white,
          size: 16.0,
        ),
      ),
      onTap: () {
        setState(() {
          _status = false;
        });
      },
    );
  }
}
