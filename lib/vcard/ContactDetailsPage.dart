import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/vcard/footer/friend_detail_footer.dart';
import 'package:flutter_app/vcard/friend_detail_body.dart';
import 'package:flutter_app/vcard/header/friend_detail_header.dart';
import 'package:flutter_app/vcard/friend.dart';
import 'package:meta/meta.dart';

class ContactDetailsPage extends StatefulWidget {
  ContactDetailsPage(
      this.friend, {
        @required this.avatarTag,
      });

  final Friend friend;
  final Object avatarTag;



  @override
  _ContactDetailsPageState createState() => new _ContactDetailsPageState();
}

class _ContactDetailsPageState extends State<ContactDetailsPage> {
  Color themeColor = GlobalConfig.themeColor();
  @override
  Widget build(BuildContext context) {


    var linearGradient = const BoxDecoration(
      gradient: const LinearGradient(
        begin: FractionalOffset.centerRight,
        end: FractionalOffset.bottomLeft,
        colors: <Color>[
          const Color.fromARGB(255, 0, 215, 198) ,
          const Color.fromARGB(255, 0, 215, 198),
        ],
      ),
    );

    return new Scaffold(
      body: new SingleChildScrollView(
        child: new Container(
          decoration: linearGradient,
          child: new Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              new FriendDetailHeader(
                widget.friend,
                avatarTag: widget.avatarTag,
              ),
              new Padding(
                padding: const EdgeInsets.all(24.0),
                child: new FriendDetailBody(widget.friend),
              ),
              new FriendShowcase(widget.friend),
            ],
          ),
        ),
      ),
    );
  }
}