import 'package:flutter/material.dart';
import '../global_config.dart';

class HomePage extends StatefulWidget {

  HomePage({Key key}) : super(key: key);

  @override
  HomePageState createState() => new HomePageState();

}

class HomePageState extends State<HomePage> {


  Widget myInfoCard() {
    return new Card(child:new Container(
      margin: const EdgeInsets.only(top: 0.0, bottom: 6.0),
      padding: const EdgeInsets.only(top: 12.0, bottom: 8.0),
      child: new Column(
        children: <Widget>[
          new Container(
            margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 16.0),
            decoration: new BoxDecoration(
                color: new Color(0xFFF5F5F5),
                borderRadius: new BorderRadius.all(new Radius.circular(6.0))
            ),
            child: new FlatButton(
                onPressed: (){},
                child: new Container(
                  child: new ListTile(
                    leading: new Container(
                      child: new CircleAvatar(
                          backgroundImage: new NetworkImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1548840758057&di=58135a141205c3f4838fdd6c37601e3e&imgtype=0&src=http%3A%2F%2Fphoto.16pic.com%2F00%2F27%2F18%2F16pic_2718026_b.jpg"),
                          radius: 20.0
                      ),
                    ),
                    title: new Container(
                      margin: const EdgeInsets.only(bottom: 2.0),
                      child: Row(children: <Widget>[
                        Padding(
                            padding:
                            EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                            child: Text('马化腾 CEO')),
                        Icon(Icons.verified_user,color: Colors.orangeAccent)
                      ]),
                    ),
                    subtitle: new Container(
                      margin: const EdgeInsets.only(top: 2.0),
                      child: new Text("腾讯科技股份有限公司"),
                    ),
                  ),
                )
            ),
          ),
          new Container(
            child: new Row(
              children: <Widget>[
                new Container(
                  child: new Icon(Icons.call, color: GlobalConfig.fontColor, size: 16.0),
                  margin: const EdgeInsets.only(right: 12.0),
                ),
                new Expanded(
                  child: new Container(
                    child: new Text("18688173429", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 13.0),),
                  ),
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
                  child: new Icon(Icons.mail, color: GlobalConfig.fontColor, size: 16.0),
                  margin: const EdgeInsets.only(right: 12.0),
                ),
                new Expanded(
                  child: new Container(
                    child: new Text("316458704@qq.com", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 13.0),),
                  ),
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
                  child: new Icon(Icons.location_on, color: GlobalConfig.fontColor, size: 16.0),
                  margin: const EdgeInsets.only(right: 12.0),
                ),
                new Expanded(
                  child: new Container(
                    child: new Text("成都市成华区红星路三段一号IFS一号办公楼23A", style: new TextStyle( color: GlobalConfig.fontColor, fontSize: 13.0),),
                  ),
                )
              ],
            ),
            margin: const EdgeInsets.only(left: 16.0, right: 16.0, bottom: 10.0),
            padding: const EdgeInsets.only(bottom: 10.0),
            decoration: new BoxDecoration(
                border: new BorderDirectional(bottom: new BorderSide(color: GlobalConfig.dark == true ?  Colors.white12 : Colors.black12))
            ),
          )
        ],
      ),
    ));
  }

  Widget toolsCard() {
    return new Container(
      color: GlobalConfig.cardBackgroundColor,
      margin: const EdgeInsets.only(top: 6.0, bottom: 6.0),
      padding: const EdgeInsets.only(top: 12.0, bottom: 8.0),
      child: new Row(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          new Container(
            width: MediaQuery.of(context).size.width / 4,
            child: new FlatButton(
                onPressed: (){},
                child: new Container(
                  child: new Column(
                    children: <Widget>[
                      new Container(
                        margin: const EdgeInsets.only(bottom: 6.0),
                        child: new CircleAvatar(
                          radius: 20.0,
                          child: new Icon(Icons.account_box, color: Colors.white),
                          backgroundColor: new Color(0xFFB88800),
                        ),
                      ),
                      new Container(
                        child: new Text("发名片", style: new TextStyle(color: GlobalConfig.fontColor, fontSize: 14.0)),
                      )
                    ],
                  ),
                )
            ),
          ),
          new Container(
            width: MediaQuery.of(context).size.width / 4,
            child: new FlatButton(
                onPressed: (){},
                child: new Container(
                  child: new Column(
                    children: <Widget>[
                      new Container(
                        margin: const EdgeInsets.only(bottom: 6.0),
                        child: new CircleAvatar(
                          radius: 20.0,
                          child: new Icon(Icons.photo_size_select_actual, color: Colors.white),
                          backgroundColor: new Color(0xFF63616D),
                        ),
                      ),
                      new Container(
                        child: new Text("发画册", style: new TextStyle(color: GlobalConfig.fontColor, fontSize: 14.0)),
                      )
                    ],
                  ),
                )
            ),
          ),
          new Container(
            width: MediaQuery.of(context).size.width / 4,
            child: new FlatButton(
                onPressed: (){
                  setState((){


                  });
                },
                child: new Container(
                  child: new Column(
                    children: <Widget>[
                      new Container(
                        margin: const EdgeInsets.only(bottom: 6.0),
                        child: new CircleAvatar(
                          radius: 20.0,
                          child: new Icon( Icons.business, color: Colors.white),
                          backgroundColor: new Color(0xFFB86A0D),
                        ),
                      ),
                      new Container(
                        child: new Text("公司信息", style: new TextStyle(color: GlobalConfig.fontColor, fontSize: 14.0)),
                      )
                    ],
                  ),
                )
            ),
          ),
          new Container(
            width: MediaQuery.of(context).size.width / 4,
            child: new FlatButton(
                onPressed: (){},
                child: new Container(
                  child: new Column(
                    children: <Widget>[
                      new Container(
                        margin: const EdgeInsets.only(bottom: 6.0),
                        child: new CircleAvatar(
                          radius: 20.0,
                          child: new Icon(Icons.security, color: Colors.white),
                          backgroundColor: new Color(0xFF636269),
                        ),
                      ),
                      new Container(
                        child: new Text("安全空间", style: new TextStyle(color: GlobalConfig.fontColor, fontSize: 14.0)),
                      )
                    ],
                  ),
                )
            ),
          ),
        ],
      ),
    );
  }


  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body:  new SingleChildScrollView(
        child: new Container(
          child: new Column(
            children: <Widget>[
              myInfoCard(),
              toolsCard(),
            ],
          ),
        ),
      )
    );

  }

}