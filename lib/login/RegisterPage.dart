import 'package:flutter/material.dart';

/**
 * @Description  注册页面
 * @Author  james
 * @Date 2019/03/22  19:00
 * @Version  1.0
 */
class RegisterPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return new _RegisterPageState();
  }
}

class _RegisterPageState extends State<RegisterPage> {
  var leftRightPadding = 30.0;
  var topBottomPadding = 4.0;
  var textTips = new TextStyle(fontSize: 16.0, color: Colors.black);
  var hintTips = new TextStyle(fontSize: 15.0, color: Colors.black26);

//  static const LOGO = "images/oschina.png";

  var _userPassController = new TextEditingController();
  var _userNameController = new TextEditingController();

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("注册", style: new TextStyle(color: Colors.white)),
          iconTheme: new IconThemeData(color: Colors.white),
        ),
        body: new Column(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[

            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 50.0, leftRightPadding, topBottomPadding),
              child: new TextField(
                style: hintTips,
                controller: _userNameController,
                decoration: new InputDecoration(
                    hintText: "全名", prefixIcon: Icon(Icons.account_box)),
                autofocus: true,
              ),
            ),

            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 50.0, leftRightPadding, topBottomPadding),
              child: new TextField(
                style: hintTips,
                controller: _userNameController,
                decoration: new InputDecoration(
                    hintText: "手机号（中国）", prefixIcon: Icon(Icons.phone_iphone)),
                autofocus: true,
              ),
            ),
            new Padding(
              padding: new EdgeInsets.fromLTRB(
                  leftRightPadding, 30.0, leftRightPadding, topBottomPadding),
              child: new TextField(
                style: hintTips,
                controller: _userPassController,
                maxLength: 32,
                decoration: new InputDecoration(
                    hintText: "密码", prefixIcon: Icon(Icons.lock)),
                obscureText: true, //是否隐藏正在编辑的文本
              ),
            ),
            new Container(
              width: 360.0,
              margin: new EdgeInsets.fromLTRB(10.0, 40.0, 10.0, 0.0),
              padding: new EdgeInsets.fromLTRB(10,
                  topBottomPadding, 10, topBottomPadding),
              child: new Card(
                color: new Color.fromARGB(255, 0, 215, 198),
                elevation: 6.0,
                child: new FlatButton(
                    onPressed: () {
                      print("the pass is" + _userNameController.text);
                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '同意并加入',
                        style:
                        new TextStyle(color: Colors.white, fontSize: 16.0),
                      ),
                    )),
              ),
            ),
            Row(
              children: <Widget>[
                Expanded(
                  child: buildLoginTipsText(context),
                  flex: 1,
                ),
              ],
            )


          ],
        ));
  }

  Padding buildLoginTipsText(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 8.0, right: 10),
      child: Align(
        alignment: Alignment.center,
        child: FlatButton(
          child: Text(
            '已经有账号？登录',
            style: TextStyle(fontSize: 14.0, color: new Color.fromARGB(255, 0, 215, 198)),
          ),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
    );
  }

}