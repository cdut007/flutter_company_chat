import 'dart:convert';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/util/ApiManager.dart';


enum LoadingType { Loading, Empty,Error ,End}

class LoadingWidget extends StatefulWidget {
  final LoadingType loadingType;
  var clickCallback;
  LoadingWidget({Key key, @required LoadingType this.loadingType,this.clickCallback})  : super(key: key);

  @override
  _LoadingWidgetState createState() => new _LoadingWidgetState();
}

class _LoadingWidgetState extends State<LoadingWidget> {

  @override
  void initState() {
    super.initState();

  }

  @override
  void dispose(){
    super.dispose();


  }

  Widget _renderEmptyWidget(){
    Widget  content = new Center(
      child:  Text('暂无数据',
        style: new TextStyle(color: Colors.grey, fontSize: 22.0),
      ),
    );
    return new GestureDetector(
      onTap: () {

        if(widget.clickCallback!=null){
          widget.clickCallback();
        }

      },
      child: content,
    );
  }

  Widget _renderLoadingWidget(){
    Widget  content = new Center(
      child: new CircularProgressIndicator(),
    );
    return new GestureDetector(
      onTap: () {
        if(widget.clickCallback!=null){
          widget.clickCallback();
        }

      },
      child: content,
    );
  }

  Widget _renderErrorWidget(){
    Widget  content = new Center(
      child: _getActionButtons(),
    );
    return content;
  }


  Widget _getActionButtons() {
    return Padding(
      padding: EdgeInsets.only(left: 25.0, right: 25.0, top: 45.0),
      child: new Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          Expanded(
            child: Padding(
              padding: EdgeInsets.only(right: 10.0),
              child: new Card(
                color: GlobalConfig.themeColor(),
                elevation: 6.0,
                child: new FlatButton(
                    onPressed: () {
                      if(widget.clickCallback!=null){
                        widget.clickCallback();
                      }

                    },
                    child: new Padding(
                      padding: new EdgeInsets.all(10.0),
                      child: new Text(
                        '点击重试',
                        style:
                        new TextStyle(color: Colors.white, fontSize: 16.0),
                      ),
                    )),
              ),
            ),
            flex: 2,
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    LoadingType loadingType = widget.loadingType;
    Widget builderWidget;
    switch(loadingType){
      case LoadingType.Empty:
        builderWidget =_renderEmptyWidget();
        break;
      case LoadingType.Loading:
        builderWidget= _renderLoadingWidget();
        break;
      case LoadingType.Error:
        builderWidget= _renderErrorWidget();
        break;
      case LoadingType.End:
        break;
    }

    return new Expanded(child: builderWidget,flex: 1,);


  }
}
