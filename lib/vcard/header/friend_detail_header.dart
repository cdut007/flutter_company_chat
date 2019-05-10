import 'package:flutter_web/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/vcard/header/diagonally_cut_colored_image.dart';
import 'package:flutter_app/entity/Friend.dart';
import 'package:meta/meta.dart';
import 'package:flutter_app/chat/ChatModule.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/chat/ChatPage.dart';
import 'package:flutter_app/chat/ConversationType.dart';
import 'package:flutter_app/vcard/UserProfileQRCodePage.dart';

class FriendDetailHeader extends StatelessWidget {
  static const BACKGROUND_IMAGE = 'images/profile_header_background.png';

  FriendDetailHeader(
      this.friend, {
        @required this.avatarTag,
      });

  final Friend friend;
  final Object avatarTag;

  Widget _buildDiagonalImageBackground(BuildContext context) {
    var screenWidth = MediaQuery.of(context).size.width;

    return new DiagonallyCutColoredImage(
      new Image.asset(
        BACKGROUND_IMAGE,
        width: screenWidth,
        height: 280.0,
        fit: BoxFit.cover,
      ),
      color:   const Color.fromARGB(255, 0, 131, 198),
    );
  }

  Widget _buildAvatar() {
    return new Hero(
      tag: avatarTag,
      child: CommonUI.getAvatarWidget(friend.avatar,size: 50),
    );
  }

  Widget _buildFollowerInfo(TextTheme textTheme) {
    var followerStyle =
    textTheme.subhead.copyWith(color: const Color(0xBBFFFFFF));

    return new Padding(
      padding: const EdgeInsets.only(top: 16.0),
      child: new Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          new Text('90 关注', style: followerStyle),
          new Text(
            ' | ',
            style: followerStyle.copyWith(
                fontSize: 24.0, fontWeight: FontWeight.normal),
          ),
          new Text('100 被关注', style: followerStyle),
        ],
      ),
    );
  }

  Widget _buildActionButtons(BuildContext context, ThemeData theme) {
    return new Padding(
      padding: const EdgeInsets.only(
        top: 16.0,
        left: 16.0,
        right: 16.0,
      ),
      child: new Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          _createPillButton(context,
            '聊天',
            backgroundColor: theme.accentColor,
          ),
          _createPillButton(context,
            '关注',
            textColor: Colors.white70,
            backgroundColor: Colors.teal,
          )
        ],
      ),
    );
  }

  Widget _createPillButton(BuildContext context,
      String text, {
        Color backgroundColor = Colors.transparent,
        Color textColor = Colors.white70,
      }) {
    return new ClipRRect(
      borderRadius: new BorderRadius.circular(10.0),
      child: new MaterialButton(
        minWidth: 140.0,
        color: backgroundColor,
        textColor: textColor,
        onPressed: () {
          Navigator.of(context).push(
            new MaterialPageRoute(
              builder: (c) {
                return new ChatPage(key:Key('chat'),peerId:ChatModule.getSendTo(friend.id, ConversationType.Single.toString()),peerName:friend.username,peerAvatar:friend.avatar);
              },
            ),
          );
        },
        child: new Text(text),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    var theme = Theme.of(context);
    var textTheme = theme.textTheme;

    return new Stack(
      children: <Widget>[
        _buildDiagonalImageBackground(context),
        new Align(
          alignment: FractionalOffset.bottomCenter,
          heightFactor: 1.4,
          child: new Column(
            children: <Widget>[
              _buildAvatar(),
              _buildFollowerInfo(textTheme),
              _buildActionButtons(context,theme),
            ],
          ),
        ),
        new Positioned(
          top: 26.0,
          left: 4.0,
          child: new BackButton(color: Colors.white),
        ),
      ],
    );
  }
}