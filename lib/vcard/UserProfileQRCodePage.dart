import 'package:flutter/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:qr_flutter/qr_flutter.dart';


class UserProfileQRCodePage extends StatefulWidget {
  @override
  _UserProfileQRCodePageState createState() => _UserProfileQRCodePageState();
}

class _UserProfileQRCodePageState extends State<UserProfileQRCodePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: ListView(
          children: <Widget>[
            Stack(
              children: <Widget>[
                Container(
                  height: 550.0,
                  width: double.infinity,
                ),
                Container(
                  height: 200.0,
                  width: double.infinity,
                  color: GlobalConfig.themeColor(),
                ),
                Align(
                  alignment: Alignment.topLeft,
                  child: IconButton(
                    icon: Icon(Icons.arrow_back_ios),
                    onPressed: () {

                      Navigator.pop(context);
                    },
                    color: Colors.white,
                  ),
                ),
                Positioned(
                  top: 125.0,
                  left: 15.0,
                  right: 15.0,
                  child: Material(
                    elevation: 3.0,
                    borderRadius: BorderRadius.circular(7.0),
                    child: Container(
                      height: 400.0,
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(7.0),
                          color: Colors.white),
                    ),
                  ),
                ),
                Positioned(
                  top: 75.0,
                  left: (MediaQuery.of(context).size.width / 2 - 50.0),
                  child: Container(
                    height: 100.0,
                    width: 100.0,
                    decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(50.0),
                        image: DecorationImage(
                            image: AssetImage('images/a001.jpg'),
                            fit: BoxFit.cover)),
                  ),
                ),
                Container(
                  margin: const EdgeInsets.only(top: 190.0),
                  padding: const EdgeInsets.only(top: 0.0, bottom: 8.0),
                  alignment: Alignment.center,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      Text(
                        'James chen',
                        style: TextStyle(
                            fontFamily: 'Comfortaa',
                            fontWeight: FontWeight.bold,
                            fontSize: 17.0),
                      ),
                      SizedBox(height: 7.0),
                      Text(
                        '中国',
                        style: TextStyle(
                            fontFamily: 'Comfortaa',
                            fontWeight: FontWeight.bold,
                            fontSize: 17.0,
                            color: Colors.grey),
                      ),
                      SizedBox(height: 10.0),
                      Container(  margin: const EdgeInsets.only(top: 10.0),height: 240, child:
                      new QrImage(
                        data: "1234567890",
                        size: 240.0,
                      ),)

                    ],
                  ),
                )
              ],
            ),
          ],
        ));
  }



}