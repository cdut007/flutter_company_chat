import 'package:flutter_web/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:date_utils/date_utils.dart';

class CalendarTile extends StatelessWidget {
  final VoidCallback onDateSelected;
  final DateTime date;
  final String dayOfWeek;
  final bool isDayOfWeek;
  final bool isSelected;
  final TextStyle dayOfWeekStyles;
  final TextStyle dateStyles;
  final Widget child;

  CalendarTile({
    this.onDateSelected,
    this.date,
    this.child,
    this.dateStyles,
    this.dayOfWeek,
    this.dayOfWeekStyles,
    this.isDayOfWeek: false,
    this.isSelected: false,
  });

  Widget renderDateOrDayOfWeek(BuildContext context) {
    if (isDayOfWeek) {
       String dayOfWeekStr = dayOfWeek;
       if(dayOfWeek == 'Sun'){
         dayOfWeekStr = '日';
       }else  if(dayOfWeek == 'Mon'){
         dayOfWeekStr = '一';
       }else  if(dayOfWeek == 'Tue'){
         dayOfWeekStr = '二';
       }else  if(dayOfWeek == 'Wed'){
         dayOfWeekStr = '三';
       }else  if(dayOfWeek == 'Thu'){
         dayOfWeekStr = '四';
       }else  if(dayOfWeek == 'Fri'){
         dayOfWeekStr = '五';
       }else  if(dayOfWeek == 'Sat'){
         dayOfWeekStr = '六';
       }

      return new InkWell(
        child: new Container(
          alignment: Alignment.center,
          child: new Text(
            dayOfWeekStr,
            style: dayOfWeekStyles,
          ),
        ),
      );
    } else {
      TextStyle textStyle = TextStyle(color: Colors.white);
      return new InkWell(
        onTap: onDateSelected,
        child: new Container(
          decoration: isSelected
              ? new BoxDecoration(
            shape: BoxShape.circle,
            color: GlobalConfig.themeColor(),
          )
              : new BoxDecoration(),
          alignment: Alignment.center,
          child: new Text(
            Utils.formatDay(date).toString(),
            style: isSelected ? textStyle : dateStyles,
            textAlign: TextAlign.center,
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (child != null) {
      return new InkWell(
        child: child,
        onTap: onDateSelected,
      );
    }
    return new Container(
      child: renderDateOrDayOfWeek(context),
    );
  }
}