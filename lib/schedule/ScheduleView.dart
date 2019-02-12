import 'package:flutter/material.dart';
import 'package:flutter_app/widget/flutter_calendar.dart';
import 'package:flutter_app/market/enity/Stock.dart';
import 'package:http/http.dart' as http;

class ScheduleView extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => new ScheduleViewState();
}

class ScheduleViewState extends State<ScheduleView> {
  List<Stock> stocks = [];

  @override
  Widget build(BuildContext context) {
    return new Center(child: getBody());
  }

  getBody() {
    return Container(
      margin: EdgeInsets.symmetric(
        horizontal: 2.0,
        vertical: 10.0,
      ),
      child: ListView(
        children: <Widget>[
          Calendar(
            onSelectedRangeChange: (range) =>
                print("Range is ${range.item1}, ${range.item2}"),
            onDateSelected: (date) => handleNewDate(date),
          ),
          Divider(
            height: 50.0,
          ),
          _buildTimeLine('踢足球'),
          _buildTimeLine('玩游戏'),
          _buildTimeLine('看比赛'),
          _buildTimeLine('打篮球'),
          _buildTimeLine('休息'),
        ],
      ),
    );
  }

  /// handle new date selected event
  void handleNewDate(date) {}

  Widget _buildTimeLine(String message) {
    return Stack(
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.only(left: 50.0),
          child: Card(
            margin: EdgeInsets.all(20.0),
            child: Container(
              padding: EdgeInsets.symmetric(horizontal: 16.0, vertical: 10.0),
              width: double.infinity,
              child: Text(message),
            ),
          ),
        ),
        Positioned(
          top: 0.0,
          bottom: 0.0,
          left: 35.0,
          child: Container(
            height: double.infinity,
            width: 2.0,
            color: Colors.blue,
          ),
        ),
        Positioned(
          top: 13.0,
          left: 22.5,
          child: Container(
            height: 26.0,
            width: 26.0,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: new Color.fromARGB(255, 0, 215, 198),
            ),
            child: Container(
              margin: EdgeInsets.all(3.0),
              height: 26.0,
              width: 26.0,
              decoration:
                  BoxDecoration(shape: BoxShape.circle, color: Colors.lightBlueAccent),
            ),
          ),
        )
      ],
    );
  }
}
