import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
//import 'package:fluwx/fluwx.dart';
import 'package:flutter_app/vcard/UserProfileQRCodePage.dart';
import 'package:flutter_app/Login/LoginPage.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/news/NewsWebPage.dart';
import 'package:flutter_app/my/WechatPage.dart';
import 'package:flutter_app/entity/UserInfo.dart';
import 'package:flutter_app/entity/VcardEntity.dart';
import 'package:flutter_app/util/ApiManager.dart';
//import 'package:fluwx/fluwx.dart' as fluwx;
import 'package:share/share.dart';
import 'package:event_bus/event_bus.dart';
//import 'package:hf_sdk/hf_sdk.dart';
/**
 * @Description  文我的界面
 * @Author  james
 * @Date 2019/03/25  10:26
 * @Version  1.0
 */

class MyInfoPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => new MyInfoPageState();
}

class MyInfoPageState extends State<MyInfoPage> {
  static const double IMAGE_ICON_WIDTH = 30.0;
  static const double ARROW_ICON_WIDTH = 16.0;

  var titles = ["", "退出登录", "检查新版本", "公司认证", "联系我们", "分享"];
  List icons = [
    Icons.all_out,
    Icons.payment,
    Icons.verified_user,
    Icons.phone_forwarded,
    Icons.share
  ];
  var userAvatar;
  var userName;
  var rightArrowIcon = new Image.asset(
    'images/ic_arrow_right.png',
    width: ARROW_ICON_WIDTH,
    height: ARROW_ICON_WIDTH,
  );

  bool _loggedIn=false;
  UserInfo _userInfo;
  @override
  void initState() {
    super.initState();
   // fluwx.register(appId: "wx423d7d8752fd810c");

   checkLoginStatus();
   _initBuly();
  }
  _initBuly(){
    print("bugly初始化");
    //HfSdk.initBugly;
  }

  reloadMenuInfo(){
    if(_loggedIn){
      titles = ["", "退出登录", "检查新版本", "公司认证", "联系我们", "分享"];
      icons = [
        Icons.all_out,
        Icons.payment,
        Icons.verified_user,
        Icons.phone_forwarded,
        Icons.share
      ];
    }else{
      titles = ["",  "检查新版本", "公司认证", "联系我们", "分享"];
      icons = [
        Icons.payment,
        Icons.verified_user,
        Icons.phone_forwarded,
        Icons.share
      ];
    }
  }

  checkLoginStatus(){
    print("checkLoginStatus... ");
    ApiManager.getUserInfo().then((userInfo){
      setState(() {

        _userInfo = userInfo;
        print('******** load user info *******');
        print(_userInfo);
        if(_userInfo!=null){
          userName = _userInfo.username;
          userAvatar = _userInfo.avatar;
        }else{
          userName = null;
          userAvatar = null;
        }
      });

      ApiManager.isLoggedIn().then((loggedIn){
        setState(() {
          _loggedIn = loggedIn;
          reloadMenuInfo();
        });
      });

    },onError: (errorInfo){
      print("checkLoginStatus has error... ");
      setState(() {
        _userInfo=null;
        userName = null;
        userAvatar = null;
      });
      ApiManager.isLoggedIn().then((loggedIn){
        setState(() {
          _loggedIn = loggedIn;
          reloadMenuInfo();
        });
      });
    });

  }

  Widget getIconImage(path) {
    return new Padding(
      padding: const EdgeInsets.fromLTRB(0.0, 0.0, 10.0, 0.0),
      child: new Image.asset(path,
          width: IMAGE_ICON_WIDTH, height: IMAGE_ICON_WIDTH),
    );
  }

  @override
  Widget build(BuildContext context) {
    var listView = new ListView.builder(
      itemCount: titles.length,
      itemBuilder: (context, i) => renderRow(i),
    );
    return listView;
  }

  renderRow(i) {
    if (i == 0) {
      var avatarContainer = new Container(
        color: GlobalConfig.themeColor(),
        height: 200.0,
        child: new Center(
          child: new Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              CommonUI.getAvatarWidget(userAvatar,size:30,color: Colors.white),
              Container(
                margin: EdgeInsets.fromLTRB(0.0, 5.0, 0.0, 0.0),
                child: Text(
                  userName == null ? "点击头像登录" : userName,
                  style: new TextStyle(color: Colors.white, fontSize: 16.0),
                ),
              ),
            ],
          ),
        ),
      );
      return new GestureDetector(
        onTap: () {
          if(!_loggedIn){
            Navigator.push(context,
                new MaterialPageRoute<String>(builder: (context) => new LoginPage())).then((String result){

              //处理代码
              print('********获取上一个页面返回的参数*******');
              print(result);
              if(result == ApiManager.refresh_tag){
                checkLoginStatus();
                EventBus eventBus = GlobalConfig.getEventBus();
                eventBus.fire(VcardEntity());
              }

            });
          }else{
            //my profile
            Navigator.push(context,
                new MaterialPageRoute(builder: (context) => new UserProfileQRCodePage()));
          }
        },
        child: avatarContainer,
      );
    }

    return new InkWell(
      child: Column(
        children: <Widget>[
          new Padding(
            padding: const EdgeInsets.fromLTRB(10.0, 15.0, 10.0, 15.0),
            child: new Row(
              children: <Widget>[
                Container(
                  child: Icon(icons[i - 1]),
                  margin: EdgeInsets.fromLTRB(0.0, 0.0, 10.0, 0.0),
                ),
                new Expanded(
                    child: new Text(
                  titles[i],
                  style: new TextStyle(fontSize: 16.0),
                )),
                rightArrowIcon
              ],
            ),
          ),
          Divider(
            height: 1.0,
          )
        ],
      ),
      onTap: () {
        _handleListItemClick(i);
      },
    );
  }

  _handleListItemClick(int index) {
    if(!_loggedIn){
      index=index+1;
    }
    switch (index) {
      case 1:
         if(_loggedIn){
           showOkCancelDialog(context, (){
             ApiManager.logout().then((logout){
               print("clear logout info... ");
              // checkLoginStatus();
               try {
                 Navigator.of(context).pushAndRemoveUntil(new MaterialPageRoute(
                     builder: (BuildContext context) => new LoginPage(fromSplashPage: true,)), (//跳转到主页
                     Route route) => route == null);
               } catch (e) {
                 print(e);
               }
             });
           }, '登出', '确认登出吗？');
         }
        break;
      case 2:

        String h5_url = "https://github.com/zhibuyu";
//        Navigator.push(
//            context,
//            new MaterialPageRoute(
//                builder: (context) => new NewsWebPage(h5_url, '我的开源')));
        //HfSdk.getNewVersion;
        break;
      case 3:
//        Navigator.push(context,
//            new MaterialPageRoute(builder: (context) => new WechatPage()));
        break;
      case 4:
        String h5_url = "https://github.com/zhibuyu/Flutter_Stocks/issues";
//        Navigator.push(
//            context,
//            new MaterialPageRoute(
//                builder: (context) => new NewsWebPage(h5_url, '意见反馈')));
        break;
      case 5:
        showDemoDialog<String>(
          context: context,
          child: const CupertinoDessertDialog(),
        );

        break;
    }
  }

  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  void showDemoDialog<T>({BuildContext context, Widget child}) {
    showDialog<T>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) => child,
    ).then<void>((T value) {
      // The value passed to Navigator.pop() or null.

    });
  }
}

class CupertinoDessertDialog extends StatelessWidget {
  const CupertinoDessertDialog({Key key, this.title, this.content})
      : super(key: key);

  final Widget title;
  final Widget content;

  @override
  Widget build(BuildContext context) {
    return CupertinoAlertDialog(
      title: title,
      content: content,
      actions: <Widget>[
        CupertinoDialogAction(
          child: const Text('全平台分享'),
          onPressed: () {
            final RenderBox box = context.findRenderObject();
            Share.share("https://github.com/zhibuyu/Flutter_Stocks/releases/tag/v0.01",
                sharePositionOrigin:
                box.localToGlobal(Offset.zero) &
                box.size);
            Navigator.pop(context, 'Cancel');
          },
        ),
        CupertinoDialogAction(
          child: const Text('微信好友'),
          onPressed: () {
//            fluwx.share(WeChatShareImageModel(
//                image: "assets://images/down_qrcode.png",
//                thumbnail: "",
//                transaction: "",
//                scene: WeChatScene.SESSION,
//                description: "image"));
            Navigator.pop(context, 'Cancel');
          },
        ),
        CupertinoDialogAction(
          child: const Text('微信朋友圈'),
          onPressed: () {
//            fluwx.share(WeChatShareImageModel(
//                image: "assets://images/down_qrcode.png",
//                thumbnail: "",
//                transaction: "",
//                scene: WeChatScene.TIMELINE,
//                description: "image"));
            Navigator.pop(context, 'Cancel');
          },
        ),
        CupertinoDialogAction(
          child: const Text("微信收藏"),
          onPressed: () {
//            fluwx.share(WeChatShareImageModel(
//                image: "assets://images/down_qrcode.png",
//                thumbnail: "",
//                transaction: "",
//                scene: WeChatScene.FAVORITE,
//                description: "image"));
            Navigator.pop(context, 'Cancel');
          },
        ),
        CupertinoDialogAction(
          child: const Text('取消'),
          isDestructiveAction: true,
          onPressed: () {
            Navigator.pop(context, 'Cancel');
          },
        ),
      ],
    );
  }
}
