import 'dart:async';
import 'package:flutter_web/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_web/cupertino.dart';

typedef void PressOperationCallback(Number number);

typedef void PressDialerOperationCallback(String tag);

abstract class Number {
  String display;

  String apply(String original);
}

class NormalNumber extends Number {
  NormalNumber(String display) {
    this.display = display;
  }

  apply(original) {
    if (original == '0') {
      return display;
    } else {
      return original + display;
    }
  }
}

class SymbolNumber extends Number {
  @override
  String get display => '*';

  @override
  String apply(String original) {
    return '*' + original;
  }
}

class DecimalNumber extends Number {
  @override
  String get display => ('#');

  @override
  String apply(String original) {
    return original + '#';
  }
}

class DialerButtonLine extends StatelessWidget {
  DialerButtonLine({this.onPress}) : assert(onPress != null);
  final PressDialerOperationCallback onPress;

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Row(children: <Widget>[
        DialerButton(
            tag: 'close',
            container: Container(
                alignment: Alignment.center,
                child: new CircleAvatar(
                  backgroundColor: Colors.white,
                  radius: 50.0,
                  child: new Icon(
                    Icons.dialpad,
                    color: Colors.lightBlueAccent,
                    size: 40,
                  ),
                )),
            pad: EdgeInsets.only(bottom: 4.0),
            onPress: onPress),
        DialerButton(
            tag: 'call',
            container: Container(
                alignment: Alignment.center,
                child: new CircleAvatar(
                  radius: 50.0,
                  child: new Icon(
                    Icons.call,
                    color: Colors.green,
                    size: 40,
                  ),
                )),
            pad: EdgeInsets.only(left: 4.0, right: 4.0, bottom: 4.0),
            onPress: onPress),
        DialerButton(
            tag: 'delete',
            container: Container(
                alignment: Alignment.center,
                child: new CircleAvatar(
                  backgroundColor: Colors.white,
                  radius: 50.0,
                  child: new Icon(
                    Icons.backspace,
                    color: Colors.redAccent,
                    size: 40,
                  ),
                )),
            pad: EdgeInsets.only(bottom: 4.0),
            onPress: onPress)
      ]),
    );
  }
}

class DialerButton extends StatefulWidget {
  const DialerButton(
      {@required this.tag,
      @required this.container,
      @required this.pad,
      this.onPress})
      : assert(tag != null),
        assert(container != null),
        assert(pad != null);
  final String tag;
  final Container container;
  final EdgeInsetsGeometry pad;
  final PressDialerOperationCallback onPress;

  @override
  State<StatefulWidget> createState() =>
      new DialerButtonState(tag: tag, container: container);
}

class DialerButtonState extends State<DialerButton> {
  DialerButtonState({@required this.tag, @required this.container})
      : assert(tag != null),
        assert(container != null);
  final String tag;
  final Container container;
  bool pressed = false;

  @override
  Widget build(BuildContext context) {
    return Expanded(
        flex: 1,
        child: Padding(
          padding: widget.pad,
          child: GestureDetector(
            onTap: () {
              if (widget.onPress != null) {
                widget.onPress(widget.tag);
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
            child: widget.container,
          ),
        ));
  }
}

class NumberButtonLine extends StatelessWidget {
  NumberButtonLine({@required this.array, this.onPress})
      : assert(array != null);
  final List<Number> array;
  final PressOperationCallback onPress;

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Row(children: <Widget>[
        NumberButton(
            number: array[0],
            pad: EdgeInsets.only(bottom: 4.0),
            onPress: onPress),
        NumberButton(
            number: array[1],
            pad: EdgeInsets.only(left: 4.0, right: 4.0, bottom: 4.0),
            onPress: onPress),
        NumberButton(
            number: array[2],
            pad: EdgeInsets.only(bottom: 4.0),
            onPress: onPress)
      ]),
    );
  }
}

class NumberButton extends StatefulWidget {
  const NumberButton({@required this.number, @required this.pad, this.onPress})
      : assert(number != null),
        assert(pad != null);
  final Number number;
  final EdgeInsetsGeometry pad;
  final PressOperationCallback onPress;

  @override
  State<StatefulWidget> createState() => new NumberButtonState();
}

class NumberButtonState extends State<NumberButton> {
  bool pressed = false;

  @override
  Widget build(BuildContext context) {
    return Expanded(
        flex: 1,
        child: Padding(
          padding: widget.pad,
          child: GestureDetector(
            onTap: () {
              if (widget.onPress != null) {
                widget.onPress(widget.number);
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
                child: new CircleAvatar(
                  backgroundColor: pressed
                      ? Colors.grey[200]
                      : GlobalConfig.themeColor(),
                  radius: 50.0,
                  child: Text(
                    '${widget.number.display}',
                    style: TextStyle(fontSize: 40.0, color: Colors.white),
                  ),
                )),
          ),
        ));
  }
}
