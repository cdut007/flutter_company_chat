import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/entity/ApplyVcardEntity.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/widget/LoadingWidget.dart';
import 'package:flutter_app/util/CommonUI.dart';
class ApplyVcardListPage extends StatefulWidget{
  @override
  _ApplyVcardListPageState createState() => new _ApplyVcardListPageState();
}
class _ApplyVcardListPageState extends State{

  List<ApplyVcardEntity> _applyList = [];

  LoadingType loadingType = LoadingType.Loading;

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("申请名片列表", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
        ),
      body: _buildListView(),
    );
  }

  @override
  void initState() {
    super.initState();
    _loadApplyVcardLists();
  }

  _loadApplyVcardLists()  {
    var requestData={};
    ApiManager.getApplyVcardList(requestData).then((datas){
      List<ApplyVcardEntity> vcardList = (datas as List) != null
          ? (datas as List).map((i) => ApplyVcardEntity.fromJson(i)).toList()
          : null;
      var applyList = vcardList;
      print('applyList  load length：${applyList.length}');
      setState(() {
        _applyList = applyList;
        if(_applyList.length>0){
          loadingType = LoadingType.End;
        }else{
          loadingType = LoadingType.Empty;
        }
      });
    },onError: (errorData){
      //重试
      if(_applyList.length>0){
        loadingType = LoadingType.End;
      }else{
        loadingType = LoadingType.Error;
      }
      print('*********getApplyVcardList callback error print*********');
      var error =  ApiManager.parseErrorInfo(errorData);
      showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
      print('*********getApplyVcardList callback error print end*********');
    });


  }

  void applyVcard(ApplyVcardEntity data ,int index){
    var requestData = {'approve':true,'applyId':data.id};
    ApiManager.acceptedCard(requestData).then((info){
      showToast(context, '名片添加成功');
      //event bus.
      Navigator.pop(context);
    },onError: (errorData){
      print('*********acceptedCard callback error print*********');
      var error =  ApiManager.parseErrorInfo(errorData);
      showErrorInfo(context,'错误码：${error.code}'+' 错误原因：'+error.msg);
      print('*********acceptedCard callback error print end*********');
    });
  }

  void _navigateToConversationDetails(ApplyVcardEntity vcardEntity, Object avatarTag) {
//    Navigator.of(context).push(
//      new MaterialPageRoute(
//        builder: (c) {
//          conversation.peerId='11';
//          conversation.peerAvatar='https://pic3.zhimg.com/50/2b8be8010409012e7cdd764e1befc4d1_s.jpg';
//          return new ChatPage(key:Key('chat'),peerId:conversation.peerId,peerAvatar:conversation.peerAvatar);
//        },
//      ),
//    );
  }

  Widget _buildConversationListTile(BuildContext context, int index) {
    var apply = _applyList[index];

    return new Column(children: <Widget>[
      new ListTile(
        onTap: () => _navigateToConversationDetails(apply, index),
        leading: new Hero(
          tag: 'vcard apply ${index}',
          child:  CommonUI.getAvatarWidget(apply.userAvatar),
        ),
        title: new Text(apply.userName),
        subtitle: new Text(apply.userCompany),
        trailing: new Container(child: new Card(
          color: GlobalConfig.themeColor(),
          elevation: 2.0,
          child: new FlatButton(
              onPressed: () {
                print("apply vcard call");
                applyVcard(apply,index);
              },
              child: new Padding(
                padding: new EdgeInsets.all(0.0),
                child: new Text(
                  '同 意',
                  style: new TextStyle(
                    color: Colors.white,
                    fontSize: 14.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              )),
        ),height: 44,),
      ),
      new Divider(height: 1,)
    ],);
  }

  Widget getApplyListList() {
    Widget content;

    print('applyList length：${_applyList.length}');

    if(loadingType == LoadingType.Loading){
      return Column(children: <Widget>[
        new LoadingWidget(loadingType: LoadingType.Loading,)
      ],);
    }

    if(loadingType == LoadingType.Empty){
    return Column(children: <Widget>[
      new LoadingWidget(loadingType: LoadingType.Empty,clickCallback: (){
        _loadApplyVcardLists();
      },)
    ],);

    }

    if(loadingType == LoadingType.Error){
    return Column(children: <Widget>[
    new LoadingWidget(loadingType: LoadingType.Error,clickCallback: (){
    _loadApplyVcardLists();
    },)
    ],);

    }

      content = new ListView.builder(
        itemCount: _applyList.length,
        itemBuilder: _buildConversationListTile,
      );


    return content;
  }

  Container _buildListView(){
    return Container(child: getApplyListList(),);
  }
}