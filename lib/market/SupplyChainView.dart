import 'package:flutter_web/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/widget/number.dart';
import 'package:flutter_app/widget/result.dart';

class SupplyChainView extends StatefulWidget {
  SupplyChainView({Key key}) : super(key: key);

  @override
  _SupplyChainViewState createState() => new _SupplyChainViewState();
}

class _SupplyChainViewState extends State {
  List<Result> results = [];
  String currentDisplay = '';

  onNumberButtonPressed(Number number) {
    var result = results.length > 0 ? results[results.length - 1] : Result();
    if (result.firstNum == null) {
      result.firstNum = number.apply(currentDisplay);
    } else if (result.result == null) {
      if (result.secondNum == null) {
        currentDisplay = '';
      }
      result.secondNum = number.apply(currentDisplay);
    } else {
      var newRes = Result();
      currentDisplay = '';
      newRes.firstNum = number.apply(currentDisplay);
      results.add(newRes);
    }
    if (results.length == 0) {
      results.add(result);
    }
    pickCurrentDisplay();
  }


  onDialerButtonPressed(String tag) {

  }

  pickCurrentDisplay() {
    this.setState(() {
      var display = '0';
      results.removeWhere(
          (item) => item.firstNum == null && item.secondNum == null);
      if (results.length > 0) {
        var result = results[results.length - 1];
        if (result.result != null) {
          display = result.result;
        } else if (result.secondNum != null) {
          display = result.secondNum;
        } else if (result.firstNum != null) {
          display = result.firstNum;
        }
      }
      currentDisplay = display;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        body: new Container(
      child: new Container(
        color: Colors.white,
        child: new Column(
          children: <Widget>[
            Expanded(
              key: Key('Current_Display'),
              flex: 1,
              child: FractionallySizedBox(
                widthFactor: 1.0,
                heightFactor: 1.0,
                child: Container(
                  color: Colors.white,
                  alignment: Alignment.center,
                  padding: const EdgeInsets.all(16.0),
                  child: ResultDisplay(result: currentDisplay),
                ),
              ),
            ),
            new Expanded(
                key: Key('Number_Button_Line_1'),
                child: NumberButtonLine(
                  array: [
                    NormalNumber('1'),
                    NormalNumber('2'),
                    NormalNumber('3')
                  ],
                  onPress: onNumberButtonPressed,
                ),
                flex: 1),
            new Expanded(
                key: Key('Number_Button_Line_2'),
                child: NumberButtonLine(
                  array: [
                    NormalNumber('4'),
                    NormalNumber('5'),
                    NormalNumber('6')
                  ],
                  onPress: onNumberButtonPressed,
                ),
                flex: 1),
            new Expanded(
                key: Key('Number_Button_Line_3'),
                child: NumberButtonLine(
                  array: [
                    NormalNumber('7'),
                    NormalNumber('8'),
                    NormalNumber('9')
                  ],
                  onPress: onNumberButtonPressed,
                ),
                flex: 1),
            new Expanded(
                key: Key('Number_Button_Line_4'),
                child: NumberButtonLine(
                  array: [SymbolNumber(), NormalNumber('0'), DecimalNumber()],
                  onPress: onNumberButtonPressed,
                ),
                flex: 1),
            Expanded(
              key: Key('Current_dialer'),
              child: DialerButtonLine(
                onPress: onDialerButtonPressed,
              ),
            ),
          ],
        ),
      ),
    ));
  }
}
