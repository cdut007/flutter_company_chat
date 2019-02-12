import 'package:flutter/material.dart';
import 'package:flutter_app/schedule/ScheduleView.dart';
import 'package:flutter_app/home/HomePage.dart';
import 'package:flutter_app/ContactView.dart';
import 'package:flutter_app/market/SupplyChainView.dart';
import 'package:flutter_app/searchPage.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:flutter_app/my/MyInfoPage.dart';
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
        color: Theme.of(context).primaryColor,
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.contact_phone),
        icon: const Icon(Icons.contact_phone),
        title: '名片夹',
        color: Theme.of(context).primaryColor,
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.cloud),
        icon: const Icon(Icons.cloud_queue),
        title: '供应链',
        color: new Color.fromARGB(255, 0, 215, 198),
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.radio_button_checked),
        icon: const Icon(Icons.radio_button_unchecked),
        title: '助理',
        color: Theme.of(context).primaryColor,
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.account_circle),
        icon: const Icon(Icons.account_circle),
        title: '我的',
        color:Theme.of(context).primaryColor,
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
    setState(() {
      // Rebuild in order to animate views.
    });
  }

  void initData() {
    body = new IndexedStack(
      children: [
        new HomePage(),
        new ContactView(),
        new SupplyChainView(),
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
        });
      },
    );

    return Scaffold(
        appBar: AppBar(
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

                  }else if(value == '添加名片'){

                  }else if(value == '导入通讯录'){

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
                          value: '导入通讯录',
                          child: Row(children: <Widget>[
                            Padding(
                                padding:
                                    EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                                child: Icon(Icons.person_add)),
                            Text('导入通讯录')
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
