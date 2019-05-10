import 'package:flutter_web/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_web/cupertino.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'dart:async';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/StringUtil.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
import 'package:path_provider/path_provider.dart';

class CreateEditVcardPage extends StatefulWidget {

   VcardEntity vcardEntity = new VcardEntity();

  CreateEditVcardPage({Key key,  this.vcardEntity}) : super(key: key);

  @override
  CreateEditVcardPageState createState() => CreateEditVcardPageState();
}

class CreateEditVcardPageState extends State<CreateEditVcardPage>
    with SingleTickerProviderStateMixin {
  bool _status = true;
  final FocusNode myFocusNode = FocusNode();

  VcardEntity _vcardEntity = VcardEntity();

  var _nameController = new TextEditingController();
  var _mailController = new TextEditingController();
  var _phoneController = new TextEditingController();
  var _jobPostionController = new TextEditingController();
  var _companyController = new TextEditingController();
  var avatarUrl;

  File _image;

  Future getImage() async {
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);

    var fileName = GlobalConfig.getFileName(image);
    showLoadingDialog(context,'正在上传头像...');
    ApiManager.uploadFile(fileName, image.path).then((data){
      closeLoadingDialog();
      setState(() {
        avatarUrl = data['publicUrl'];
      });
    },onError: (errorData){
      print('*********uploadFile userPhoto callback error print*********');
      var error =  ApiManager.parseErrorInfo(errorData);
      closeLoadingDialog();
      showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
    });
    setState(() {
      _image = image;

    });
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _vcardEntity = widget.vcardEntity;
    if(_vcardEntity == null){
      _vcardEntity = new VcardEntity();
    }
    avatarUrl = _vcardEntity.avatar;

    if(_vcardEntity.hfCardDetails!=null && _vcardEntity.hfCardDetails.length>0){
      _nameController.text = getUserVcardName(_vcardEntity);
      _phoneController.text = getUserVcardPhone(_vcardEntity);
      _jobPostionController.text = getUserVcardJobPosition(_vcardEntity);
      _companyController.text = getUserVcardCompany(_vcardEntity);
      setState(() {

      });
    }

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
                                    child:  CommonUI.getAvatarWidget(avatarUrl),),
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
                                        '职位',
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
                                        keyboardType:TextInputType.phone,
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
                                          hintText: "输入职位"),
                                      enabled: !_status,
                                      controller: _jobPostionController,
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
                                        '公司',
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
                                          hintText: "输入公司"),
                                      enabled: !_status,
                                      controller: _companyController,
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
                                          hintText: "输入公司地址"),
                                      enabled: !_status,
                                      controller: _companyController,
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
    var hfCardDetailsId;
    if(_vcardEntity.id!=null){
      hfCardDetailsId=_vcardEntity.hfCardDetails[0].id;
    }
    hfCardDetails.add({"cardSide": "FRONT",
      "companyName": _companyController.text,
      "jobPosition": _jobPostionController.text,
      "language": "中文",
      "id":hfCardDetailsId,
      "name": _nameController.text,
      "phoneNumber": _phoneController.text});
    Map<String, dynamic> data ={'hfCardDetails':hfCardDetails};

    if(avatarUrl!=null){
      data['avatar']= avatarUrl;
    }

    if(_vcardEntity.id!=null){
      data['id']=_vcardEntity.id;

      final future = ApiManager.updateCard(data);
      future.then((data){
        print('*********updateCard callback*********');
        closeLoadingDialog();
        print(data);
        showToast(context, '修改名片成功');
        Navigator.pop(context,ApiManager.vcard_list_refresh_tag);

      },onError: (errorData){
        print('********* updateCard error print*********');
        var error =  ApiManager.parseErrorInfo(errorData);
        closeLoadingDialog();
        showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
        print('*********updateCard callback error print end*********');
        //
      });
    }else{
      final future = ApiManager.createCard(data);
      future.then((data){
        print('*********createCard callback*********');
        closeLoadingDialog();
        print(data);
        showToast(context, '添加名片成功');
        Navigator.pop(context,ApiManager.vcard_list_refresh_tag);

      },onError: (errorData){
        print('********* createCard error print*********');
        var error =  ApiManager.parseErrorInfo(errorData);
        closeLoadingDialog();
        showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
        print('*********createCard callback error print end*********');
        //
      });
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
                      if( _nameController.text.isEmpty){
                        showToast(context,'请输入全名');
                        return;
                      }

                      if( _phoneController.text.isEmpty){
                        showToast(context,'请输入手机号');
                        return;
                      }

                      if( _jobPostionController.text.isEmpty){
                        showToast(context,'请输入职位');
                        return;
                      }

                      if( _companyController.text.isEmpty){
                        showToast(context,'请输入公司');
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
