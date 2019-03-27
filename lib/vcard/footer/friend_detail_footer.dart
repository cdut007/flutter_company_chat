import 'package:flutter/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/vcard/footer/articles_showcase.dart';
import 'package:flutter_app/vcard/footer/portfolio_showcase.dart';
import 'package:flutter_app/vcard/footer/skills_showcase.dart';
import 'package:flutter_app/vcard/friend.dart';

class FriendShowcase extends StatefulWidget {
  FriendShowcase(this.friend);

  final Friend friend;

  @override
  _FriendShowcaseState createState() => new _FriendShowcaseState();
}

class _FriendShowcaseState extends State<FriendShowcase>
    with TickerProviderStateMixin {
  List<Tab> _tabs;
  List<Widget> _pages;
  TabController _controller;

  @override
  void initState() {
    super.initState();
    _tabs = [
      new Tab(child: Container(child: Text('个人简介',style: TextStyle(color: Colors.white),),),),
      new Tab(child: Container(child: Text('生活',style: TextStyle(color: Colors.white),),),),
//      new Tab(text: 'Articles'),
    ];
    _pages = [
      new PortfolioShowcase(),
      new SkillsShowcase(),
//      new ArticlesShowcase(),
    ];
    _controller = new TabController(
      length: _tabs.length,
      vsync: this,
    );
  }

  @override
  Widget build(BuildContext context) {
    return new Padding(
      padding: const EdgeInsets.all(16.0),
      child: new Column(
        children: <Widget>[
          new TabBar(
            controller: _controller,
            tabs: _tabs,
            indicatorColor: Colors.white,
          ),
          new SizedBox.fromSize(
            size: const Size.fromHeight(300.0),
            child: new TabBarView(
              controller: _controller,
              children: _pages,
            ),
          ),
        ],
      ),
    );
  }
}