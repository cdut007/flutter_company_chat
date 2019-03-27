import 'dart:async';
import 'package:flutter/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_app/widget/number.dart';

typedef void PressOperationCallback(display);

class Result {
  Result();

  String firstNum;
  String secondNum;
  String result;
}

class ResultButton extends StatefulWidget {
  ResultButton({@required this.display, @required this.color, this.onPress});

  final String display;
  final Color color;
  final PressOperationCallback onPress;

  @override
  State<StatefulWidget> createState() => ResultButtonState();
}

class ResultButtonState extends State<ResultButton> {
  bool pressed = false;

  @override
  Widget build(BuildContext context) {
    return Expanded(
        flex: 1,
        child: Padding(
            padding: EdgeInsets.only(
                left: 10.0, right: 10.0, top: 10.0, bottom: 24.0),
            child: GestureDetector(
              onTap: () {
                if (widget.onPress != null) {
                  widget.onPress(widget.display);
                  setState(() {
                    pressed = true;
                  });
                  Future.delayed(
                      const Duration(milliseconds: 200),
                      () => setState(() {
                            pressed = false;
                          }));
                }
              },
              child: Container(
                alignment: Alignment.center,
                decoration: BoxDecoration(
                    color: pressed ? Colors.grey[200] : null,
                    border: Border.all(color: widget.color, width: 2.0),
                    borderRadius: BorderRadius.all(Radius.circular(16.0))),
                child: Text(
                  '${widget.display}',
                  style: TextStyle(
                      fontSize: 36.0,
                      color: widget.color,
                      fontWeight: FontWeight.w300),
                ),
              ),
            )));
  }
}

class ResultDisplay extends StatelessWidget {
  ResultDisplay({this.result});

  final String result;

  @override
  Widget build(BuildContext context) {
    return Text(
      '$result',
      softWrap: false,
      overflow: TextOverflow.fade,
      textScaleFactor: 7.5 / result.length > 1.0 ? 1.0 : 7.5 / result.length,
      style: TextStyle(
          fontSize: 60.0, fontWeight: FontWeight.w500, color: Colors.black),
    );
  }
}

