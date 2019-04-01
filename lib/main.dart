import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/SplashPage.dart';


void main() {
  runApp(new MaterialApp(
      title: "掌起",
      theme: new ThemeData(
        primaryIconTheme: const IconThemeData(color: Colors.white),
        brightness: Brightness.light,
        primaryColor: GlobalConfig.themeColor(),
        accentColor: Colors.cyan[300],
      ),
      home: new SplashPage()));
}