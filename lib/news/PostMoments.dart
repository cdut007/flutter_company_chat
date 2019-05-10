import 'package:flutter_web/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'package:flutter_app/entity/Moment.dart';
import 'package:event_bus/event_bus.dart';


List<File> photoFileList = [];

class PostMoments extends StatefulWidget{

  @override
  _PostMomentsState createState() => new _PostMomentsState();
}


class _PostMomentsState extends State<PostMoments>{
  var textController = new TextEditingController();
  var fsNode = new FocusNode();
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    photoFileList = [];
  }

  postMomentsToServer() async{
    var files = [];
    for(var i=0;i<photoFileList.length;i++){
      var filePath = photoFileList[i].path;
      print('上传文件路径：'+filePath);
      var fileName = GlobalConfig.getFileName(photoFileList[i]);
      var fileResult =  await  ApiManager.uploadFile(fileName, filePath);
      var data = fileResult['file'];
      data['publicUrl'] =  fileResult['publicUrl'];
      files.add(data);
      // GlobalConfig.getFileName(widget.photoFileList[i]);
    }
    //fileContent

    //photoList
    print('发表文字:'+textController.text);
    var data = {'content':textController.text,'publishType':'PERSONAL','fileContent':files};
    ApiManager.postMoments(data);
  }

  @override
  Widget build(BuildContext context){
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
          actions: <Widget>[
            FlatButton(
              child: new Text('发表', style: new TextStyle(
                  color: Colors.white,
              )),
              onPressed: () {
                var text = textController.text;
                if(text.isEmpty){
                  showToast(context, '请输入内容');
                  return;
                }
                showLoadingDialog(context);
                postMomentsToServer().then((result){
                  closeLoadingDialog();
                  showToast(context, '发表成功');
                  EventBus eventBus = GlobalConfig.getEventBus();
                  eventBus.fire(Moment());
                  Navigator.pop(context,ApiManager.refresh_tag);
                },onError: (errorData){
                  closeLoadingDialog();
                  print('*********postMoments callback error print*********');
                  var error =  ApiManager.parseErrorInfo(errorData);
                  showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
                });
              },
            )
          ],
        ),
        body: new Container(
          child: ListView(
            children: <Widget>[
              new Container(
                padding: new EdgeInsets.all(10.0),
                width: MediaQuery.of(context).size.width,
                child: new TextField(
                  focusNode: fsNode,
                  controller: textController,
                  decoration: new InputDecoration(
                      hintText: '这一刻的想法...',
                      border: InputBorder.none
                  ),
                  keyboardType: TextInputType.text,
                  maxLines: 6,
                  onSubmitted: (value){
                    fsNode.unfocus();
                  },
                ),
              ),
              new SelectPhoto(),
              new ListTile(
                  leading: Icon(Icons.person_pin_circle),
                  title: new Text('所在位置')
              ),
              new Divider(height: 2.0, color: Color(0xFFededed)),
              new ListTile(
                leading: Icon(Icons.people),
                title: new Text('谁可以看'),

              ),
              new Divider(height: 2.0, color: Color(0xFFededed)),
              new ListTile(
                  leading: Icon(Icons.insert_link),
                  title: new Text('提醒谁看')
              ),
            ],
          ),
        )
    );
  }
}


class SelectPhoto extends StatefulWidget{

  @override
  SelectPhotoState createState() => new SelectPhotoState();
}

class SelectPhotoState extends State<SelectPhoto>{

  List photoList = [];
  Widget selectPhotoWidget;

  getWrapList() {
    var width = (MediaQuery.of(context).size.width - 40) / 3;

    List warpList = <Widget>[];
    for(var i = 0; i < photoList.length; i++) {
      if (photoList.length <= 9 ) {
        warpList.add(
            new GestureDetector(
              child: new Container(
                  width: width,
                  height: width,
                  decoration: new BoxDecoration(
                    color: Color(0xFFededed),
                  ),
                  child: new Center(
                    child:  photoList[i],
                  )
              ),
              onTap: (){
//                print('remove size= ${widget._photoFileList.length}');
              if( photoFileList.length>0){
                photoFileList.removeAt(i);
              }

                photoList.removeAt(i);
                getWrapList();
              },
            )
        );
      } else {
        return;
      }
    }
    if (photoList.length != 9 || photoList.length == 0) {
      warpList.add(
          new GestureDetector(
            child: new Container(
                width: width,
                height: width,
                decoration: new BoxDecoration(
                  color: Color(0xFFcccccc),
                ),
                child: new Center(
                  child:  new Icon(Icons.add,size: 50.0,color: Colors.grey,),
                )
            ),
            onTap: (){
              openImage();
            },
          )
      );
    }

    selectPhotoWidget = Builder(

      builder: (context) {
        return Wrap(
            alignment: WrapAlignment.start,
            spacing: 10.0,
            runSpacing: 10.0,
            children: warpList
        );
      },
    );

    setState(() {
      selectPhotoWidget = selectPhotoWidget;
    });
  }

  Future openImage() async{
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);

    if (image != null) {
      photoFileList.add(image);
      var width = (MediaQuery.of(context).size.width - 40) / 3;
      photoList.insert(photoList.length, new Image.file(image,fit:BoxFit.cover,width: width,height: width,));
      getWrapList();
    }
  }

  Widget build(BuildContext context){
    getWrapList();
    return new Container(
        padding: EdgeInsets.all(10.0),
        width: MediaQuery.of(context).size.width,
        child: selectPhotoWidget
    );
  }

}