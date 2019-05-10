
import 'package:flutter_web/material.dart';

import 'package:event_bus/event_bus.dart';
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
  static var DELETE = "delete";
  static var Token_expireDay='15';
  static var file_domain;

  static int _tabIndex = 0;

  static setCurrentHomeTabIndex(int index){
    _tabIndex = index;
  }

  static int getCurrentHomeTabIndex(){
    return _tabIndex;
  }
   static  Color themeColor(){
    return new Color.fromARGB(255, 1, 110, 197);//(255, 0, 215, 198
  }

  static String getFileName(File image){
    var fileName = image.path.substring(image.parent.path.length+1,image.path.length);
    return fileName;
  }

  static String getThumbImgUrl(var avatarUrl, {int size}){
    int _size = 200;
    if(size!=null ){
      _size = size;
    }
    var url = avatarUrl+'?x-oss-process=image/resize,w_${_size}';
    return url;
  }


  static String getHttpFilePath(String filePath){
     if(filePath.startsWith('http')){
       return filePath;
     }
    var fileName = 'http://39.96.161.237:9090/api/'+filePath;
    return fileName;
  }

  static EventBus _eventBus;

  static getEventBus(){
     if(_eventBus == null){
       _eventBus = new EventBus();
     }
     return _eventBus;
  }



}