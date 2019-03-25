
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:flutter_app/widget/LoadingDialog.dart';

void showToast(var info){
  Fluttertoast.showToast(
      msg: info,
      toastLength: Toast.LENGTH_SHORT,
      gravity: ToastGravity.CENTER,
      timeInSecForIos: 1,
      backgroundColor: Colors.black87,
      textColor: Colors.white);
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
 if(pr!=null && pr.isShowing()){
   pr.hide();
 }

}