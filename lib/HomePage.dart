import 'package:flutter/material.dart';
import 'package:flutter_app/market/MarketPage.dart';
import 'package:flutter_app/market/StockIndexPage.dart';
import 'package:flutter_app/news/FinanceNewsPage.dart';
import 'package:flutter_app/my/MyInfoPage.dart';
import 'package:flutter_app/widget/CustomIcon.dart';
import 'package:flutter_app/widget/CustomInactiveIcon.dart';
import 'package:flutter_app/my/MyDrawer.dart';
import 'package:flutter_app/widget/NavigationIconView.dart';

/**
 * 主页
 */
class HomePage extends StatefulWidget {
  static const String routeName = '/material/bottom_navigation';

  @override
  HomePageState createState() => HomePageState();
}

class HomePageState extends State<HomePage> with TickerProviderStateMixin {
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
        color: new Color.fromARGB(255, 0, 215, 198),
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.contact_phone),
        icon: const Icon(Icons.contact_phone),
        title: '名片夹',
        color: new Color.fromARGB(255, 0, 215, 198),
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
        color: new Color.fromARGB(255, 0, 215, 198),
        vsync: this,
      ),
      NavigationIconView(
        activeIcon: const Icon(Icons.account_circle),
        icon: const Icon(Icons.account_circle),
        title: '我的',
        color: new Color.fromARGB(255, 0, 215, 198),
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
        new FinanceNewsPage(),
        new StockIndexPage(),
        new MarketPage(),
        new FinanceNewsPage(),
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
//          actions: <Widget>[
//            PopupMenuButton<BottomNavigationBarType>(
//              onSelected: (BottomNavigationBarType value) {
//                setState(() {
//                  type = value;
//                });
//              },
//              itemBuilder: (BuildContext context) =>
//                  <PopupMenuItem<BottomNavigationBarType>>[
//                    const PopupMenuItem<BottomNavigationBarType>(
//                      value: BottomNavigationBarType.fixed,
//                      child: Text('Fixed'),
//                    ),
//                    const PopupMenuItem<BottomNavigationBarType>(
//                      value: BottomNavigationBarType.shifting,
//                      child: Text('Shifting'),
//                    )
//                  ],
//            )
//          ],
        ),
        body: body,
        bottomNavigationBar: botNavBar,
        drawer: new MyDrawer());
  }
}
