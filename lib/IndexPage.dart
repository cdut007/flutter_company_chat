import 'package:flutter/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/schedule/ScheduleView.dart';
import 'package:flutter_app/home/Follow.dart';
import 'package:flutter_app/ContactView.dart';
import 'package:flutter_app/chat/ConversationsPage.dart';
import 'package:flutter_app/QRCodeScanPage.dart';
import 'package:flutter_app/news/PostMoments.dart';
import 'package:flutter_app/chat/ChatPage.dart';
import 'package:flutter_app/my/MyInfoPage.dart';
import 'package:flutter_app/vcard/CreateEditVcardPage.dart';
import 'package:flutter_app/searchPage.dart';
import 'package:flutter_app/widget/CustomIcon.dart';
import 'package:flutter_app/widget/CustomInactiveIcon.dart';
import 'package:flutter_app/widget/NavigationIconView.dart';

/**
 * 主页
 */
class IndexPage extends StatefulWidget {
  static const String routeName = '/material/bottom_navigation';

  @override
  IndexPageState createState() => IndexPageState();
}

class IndexPageState extends State<IndexPage> with TickerProviderStateMixin {
  int currentIndex = 0;
  BottomNavigationBarType type = BottomNavigationBarType.fixed;
  List<NavigationIconView> navigationViews;
  var body;
  List<Widget> Pages = [];

  @override
  void initState() {
    super.initState();
    navigationViews = <NavigationIconView>[
      NavigationIconView(
        activeIcon: const Icon(Icons.home),
        icon: const Icon(Icons.home),
        title: '首页',
        color: GlobalConfig.themeColor(),
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.chat),
        icon: const Icon(Icons.chat),
        title: '消息',
        color: GlobalConfig.themeColor(),
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.contact_phone),
        icon: const Icon(Icons.contact_phone),
        title: '名片夹',
        color: GlobalConfig.themeColor(),
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.cloud),
        icon: const Icon(Icons.cloud_queue),
        title: '商业',
        color: GlobalConfig.themeColor(),
        vsync: this,
      ),

      NavigationIconView(
        activeIcon: const Icon(Icons.account_circle),
        icon: const Icon(Icons.account_circle),
        title: '我的',
        color:GlobalConfig.themeColor(),
        vsync: this,
      )
    ];

    for (NavigationIconView view in navigationViews)
      view.controller.addListener(rebuild);

    navigationViews[currentIndex].controller.value = 1.0;
  }

  @override
  void dispose() {
    for (NavigationIconView view in navigationViews) view.controller.dispose();
    super.dispose();
  }

  void rebuild() {
//    setState(() {
//      // Rebuild in order to animate views.
//    });
  }

  void initData() {
    body = new IndexedStack(
      children: [
        new Follow(),
        new ConversationsPage(),
        new ContactView(),
        new ScheduleView(),
        new MyInfoPage()
      ],
      index: currentIndex,
    );
  }

  @override
  Widget build(BuildContext context) {
    initData();
    final BottomNavigationBar botNavBar = BottomNavigationBar(
      items: navigationViews
          .map((NavigationIconView navigationView) => navigationView.item)
          .toList(),
      currentIndex: currentIndex,
      type: type,
      onTap: (int index) {
        setState(() {
          navigationViews[currentIndex].controller.reverse();
          currentIndex = index;
          navigationViews[currentIndex].controller.forward();
          GlobalConfig.setCurrentHomeTabIndex(currentIndex);
        });
      },
    );

    return Scaffold(

        appBar: AppBar(
          centerTitle: true,
          leading:  new IconButton(
              icon: new Icon(Icons.camera_alt),
              tooltip: '发动态',
              onPressed: () {
                Navigator.push(context, new MaterialPageRoute(builder: (context)=> new PostMoments()));
                // do nothing
              }),
          title: new Text(
            navigationViews[currentIndex].title,
            style: new TextStyle(color: Colors.white),
          ),
          iconTheme: new IconThemeData(color: Colors.white),
          actions: <Widget>[
            new IconButton(
                icon: new Icon(Icons.search),
                tooltip: '搜索',
                onPressed: () {
                  Navigator.of(context)
                      .push(new MaterialPageRoute(builder: (context) {
                    return new SearchPage();
                  }));
                  // do nothing
                }),
            new Padding(
              padding: const EdgeInsets.symmetric(horizontal: 0.0),
            ),
            new PopupMenuButton(
                offset: new Offset(0, 100),
                icon: new Icon(
                  Icons.add,
                  color: Colors.white,
                ),
                onSelected: (String value) {
                  if(value == '扫一扫'){
                    Navigator.push(context, new MaterialPageRoute(builder: (context)=> new QrCodeScanPage()));
                  }else if(value == '添加名片'){
                    Navigator.push(context, new MaterialPageRoute(builder: (context)=> new CreateEditVcardPage()));
                  }else if(value == '发动态'){
                    Navigator.push(context, new MaterialPageRoute(builder: (context)=> new PostMoments()));
                  }

                },
                itemBuilder: (BuildContext context) => <PopupMenuEntry<String>>[
                      new PopupMenuItem<String>(
                          value: '扫一扫',
                          child: Row(children: <Widget>[
                            Padding(
                                padding:
                                    EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                                child: Icon(Icons.camera_alt)),
                            Text('扫一扫')
                          ])),
                      new PopupMenuDivider(height: 1.0),
                      new PopupMenuItem<String>(
                          value: '添加名片',
                          child: Row(children: <Widget>[
                            Padding(
                                padding:
                                    EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                                child: Icon(Icons.add)),
                            Text('添加名片')
                          ])),
                      new PopupMenuDivider(height: 1.0),
                      new PopupMenuItem<String>(
                          value: '发动态',
                          child: Row(children: <Widget>[
                            Padding(
                                padding:
                                    EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                                child: Icon(Icons.person_add)),
                            Text('发动态')
                          ]))
                    ]),
            new Padding(
              padding: const EdgeInsets.symmetric(horizontal: 5.0),
            )
          ],
        ),
        body: body,
        bottomNavigationBar: botNavBar,
//        drawer: new MyDrawer()
    );
  }
}
