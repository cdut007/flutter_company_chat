
import 'package:flutter/material.dart';
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