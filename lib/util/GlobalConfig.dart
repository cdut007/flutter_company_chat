
import 'package:flutter/material.dart';

import 'dart:io';
import 'dart:async';

class GlobalConfig{
  static bool dark = true;
  static ThemeData themeData = new ThemeData.dark();
  static Color searchBackgroundColor = Colors.white10;
  static Color cardBackgroundColor = new Color(0xFFEBEBEB);
  static Color fontColor = Colors.blueGrey;
  static var POST='post';
  static var PATCH='patch';
  static var GET='get';
  static var PUT='put';
  static var file_domain;

   static  Color themeColor(){
    return new Color.fromARGB(255, 0, 35, 198);//(255, 0, 215, 198
  }

  static String getFileName(File image){
    var fileName = image.path.substring(image.parent.path.length+1,image.path.length);
    return fileName;
  }
}