import 'package:flutter_web/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';


class SearchPage extends StatefulWidget{
  @override
  SearchPageState createState() => new SearchPageState();
}

class SearchPageState extends State<SearchPage> {

  Widget searchInput() {
    return new Container(
      child: new Row(
        children: <Widget>[
          new Container(
            child: new FlatButton.icon(
              onPressed: (){
                Navigator.of(context).pop();
              },
              icon: new Icon(Icons.arrow_back, color: Colors.white),
              label: new Text(""),
            ),
            width: 60.0,
          ),
          new Expanded(
            child: new TextField(
              autofocus: true,
              decoration: new InputDecoration.collapsed(
                  hintText: "搜索 姓名/公司/职位",
                  hintStyle: new TextStyle(color: Colors.white)
              ),
            ),
          )
        ],
      ),
      decoration: new BoxDecoration(
        borderRadius: const BorderRadius.all(const Radius.circular(4.0)),
        color: GlobalConfig.searchBackgroundColor
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
        theme: new ThemeData(
          primaryIconTheme: const IconThemeData(color: Colors.white),
          brightness: Brightness.light,
          primaryColor: GlobalConfig.themeColor(),
          accentColor: Colors.cyan[300],
        ),
        home: new Scaffold(
          appBar: new AppBar(
            title: searchInput(),
          ),
          body: new SingleChildScrollView(
            child: new Column(
              children: <Widget>[
                new Container(
                  child: new Text("当前热搜", style: new TextStyle(fontWeight: FontWeight.bold, fontSize: 16.0)),
                  margin: const EdgeInsets.only(top: 16.0, left: 16.0, bottom: 16.0),
                  alignment: Alignment.topLeft,
                ),
                new Row(
                  children: <Widget>[
                    new Container(
                      child: new Chip(
                        label: new FlatButton(onPressed: (){}, child: new Text("中国移动",style: new TextStyle(color: GlobalConfig.fontColor),)),
                        backgroundColor: GlobalConfig.dark == true ? Colors.black12 : Colors.black12,
                      ),
                      margin: const EdgeInsets.only(left: 16.0, bottom: 16.0),
                      alignment: Alignment.topLeft,
                    ),
                    new Container(
                      child: new Chip(
                        label: new FlatButton(onPressed: (){}, child: new Text("中国航天",style: new TextStyle(color: GlobalConfig.fontColor))),
                        backgroundColor: GlobalConfig.dark == true ? Colors.black12 : Colors.black12,
                      ),
                      margin: const EdgeInsets.only(left: 16.0, bottom: 16.0),
                      alignment: Alignment.topLeft,
                    ),
                  ],
                ),
                new Row(
                  children: <Widget>[
                    new Container(
                      child: new Chip(
                        label: new FlatButton(onPressed: (){}, child: new Text("特斯拉中国",style: new TextStyle(color: GlobalConfig.fontColor))),
                        backgroundColor: GlobalConfig.dark == true ? Colors.black12 : Colors.black12,
                      ),
                      margin: const EdgeInsets.only(left: 16.0, bottom: 16.0),
                      alignment: Alignment.topLeft,
                    ),
                    new Container(
                      child: new Chip(
                        label: new FlatButton(onPressed: (){}, child: new Text("供应链",style: new TextStyle(color: GlobalConfig.fontColor))),
                        backgroundColor: GlobalConfig.dark == true ? Colors.black12 : Colors.black12,
                      ),
                      margin: const EdgeInsets.only(left: 16.0, bottom: 16.0),
                      alignment: Alignment.topLeft,
                    ),
                  ],
                ),
                new Container(
                  child: new Text("搜索历史", style: new TextStyle(fontWeight: FontWeight.bold, fontSize: 16.0)),
                  margin: const EdgeInsets.only(left: 16.0, bottom: 16.0),
                  alignment: Alignment.topLeft,
                ),
                new Container(
                  child: new Row(
                    children: <Widget>[
                      new Container(
                        child: new Icon(Icons.access_time, color: GlobalConfig.fontColor, size: 16.0),
                        margin: const EdgeInsets.only(right: 12.0),
                      ),
                      new Expanded(
                        child: new Container(
                          child: new Text("成都城投", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 14.0),),
                        ),
                      ),
                      new Container(
                        child: new Icon(Icons.clear, color: GlobalConfig.fontColor, size: 16.0),
                      )
                    ],
                  ),
                  margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 10.0),
                  padding: const EdgeInsets.only(bottom: 10.0),
                  decoration: new BoxDecoration(
                    border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
                  ),
                ),
                new Container(
                  child: new Row(
                    children: <Widget>[
                      new Container(
                        child: new Icon(Icons.access_time, color: GlobalConfig.fontColor, size: 16.0),
                        margin: const EdgeInsets.only(right: 12.0),
                      ),
                      new Expanded(
                        child: new Container(
                          child: new Text("中粮集团", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 14.0),),
                        ),
                      ),
                      new Container(
                        child: new Icon(Icons.clear, color: GlobalConfig.fontColor, size: 16.0),
                      )
                    ],
                  ),
                  margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 10.0),
                  padding: const EdgeInsets.only(bottom: 10.0),
                  decoration: new BoxDecoration(
                      border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
                  ),
                ),
                new Container(
                  child: new Row(
                    children: <Widget>[
                      new Container(
                        child: new Icon(Icons.access_time, color: GlobalConfig.fontColor, size: 16.0),
                        margin: const EdgeInsets.only(right: 12.0),
                      ),
                      new Expanded(
                        child: new Container(
                          child: new Text("金龙鱼", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 14.0),),
                        ),
                      ),
                      new Container(
                        child: new Icon(Icons.clear, color: GlobalConfig.fontColor, size: 16.0),
                      )
                    ],
                  ),
                  margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 10.0),
                  padding: const EdgeInsets.only(bottom: 10.0),
                  decoration: new BoxDecoration(
                      border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
                  ),
                ),
              ],
            ),
          )
        )
    );
  }
}