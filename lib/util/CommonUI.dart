
import 'package:flutter/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:flutter_app/widget/LoadingDialog.dart';

void showErrorInfo(BuildContext context,var info){
    showToast(context,info);
}

void showToast(BuildContext context,var info){
 // Scaffold.of(context).showSnackBar(new SnackBar(content:new Text(info)));
  Fluttertoast.showToast(
      msg: info,
      toastLength: Toast.LENGTH_SHORT,
      gravity: ToastGravity.CENTER,
      timeInSecForIos: 1,
      backgroundColor: Colors.black87,
      textColor: Colors.white);
}

void showOkCancelDialog(BuildContext context,var onPress,var title,var content) {
  NavigatorState navigator= context.rootAncestorStateOfType(const TypeMatcher<NavigatorState>());
  debugPrint("navigator is null?"+(navigator==null).toString());


  showDialog(
      context: context,
      builder: (_) => new AlertDialog(
          title: new Text(title),
          content: new Text(content),
          actions:<Widget>[
            new FlatButton(child:new Text("取消"), onPressed: (){
              Navigator.of(context).pop();

            },),
            new FlatButton(child:new Text("确认"), onPressed:(){
              onPress();
              Navigator.of(context).pop();
            } ,)
          ]
      ));
}


LoadingDialog pr;
void showLoadingDialog(BuildContext context,[info]){
  closeLoadingDialog();
   pr =new LoadingDialog(context,ProgressDialogType.Normal);
  if(info == null){
    pr.setMessage('请稍候...');
  }else{
    pr.setMessage(info);
  }
  pr.show();
}


void closeLoadingDialog(){
  print('closeLoadingDialog');
 if(pr!=null){
    try{
      pr.hide();
      print('closeLoadingDialog finish');
    }catch (e){
      print(e);
    }

 }
}


class CommonUI {
  static Widget getAvatarWidget(String avatarUrl,{double size,Color color,String defaultImgIcon}){
    double _size = 28;
    if(size!=null){
      _size =size;
    }
    Color _color= Colors.blueAccent;
    if(color!=null){
      _color = color;
    }
    String _defaultImgIcon=
        "images/ic_avatar_default.png";
    if(defaultImgIcon!=null){
      _defaultImgIcon = defaultImgIcon;
    }

    if(avatarUrl!=null){
      return new CircleAvatar(
          backgroundImage: new NetworkImage(GlobalConfig.getHttpFilePath(avatarUrl)),
          radius: _size);
    }else{
      return new Image.asset(_defaultImgIcon,
        width: _size*2,
        color: _color,
      );
    }
  }
}