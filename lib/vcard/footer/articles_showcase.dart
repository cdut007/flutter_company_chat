import 'package:flutter_web/material.dart';import 'package:flutter_app/util/GlobalConfig.dart';

class ArticlesShowcase extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    var textTheme = Theme.of(context).textTheme;

    return new Center(
      child: new Text(
        'Articles: TODO',
        style: textTheme.title.copyWith(color: Colors.white),
      ),
    );
  }
}