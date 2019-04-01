import 'dart:async';

import 'package:flutter/services.dart';

class HfSdk {
  static const MethodChannel _channel =
      const MethodChannel('hf_sdk');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  //get latest version from bugly
  static Future<String> get getNewVersion async {
    final String result = await _channel.invokeMethod('getLatestVersion');
    return result;
  }
  //bugly初始化
  static Future<String> get initBugly async {
    final String result = await _channel.invokeMethod('buglyInit');
    return result;
  }
}
