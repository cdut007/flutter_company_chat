import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_refresh/flutter_refresh.dart';

typedef HeaderWidgetBuild = Widget Function(BuildContext context, int position);

typedef ItemWidgetBuild = Widget Function(BuildContext context, int position);


class HeaderListView extends StatefulWidget {
  List headerList;
  List listData;
  ItemWidgetBuild itemWidgetCreator;
  HeaderWidgetBuild headerCreator;
  bool usePullToRefresh=false;
  var  onFooterRefresh;
  var  onHeaderRefresh;

  HeaderListView(List this.listData,
      {Key key,
        List this.headerList,
        ItemWidgetBuild this.itemWidgetCreator,
        HeaderWidgetBuild this.headerCreator, bool this.usePullToRefresh, var  this.onFooterRefresh,
       var  this.onHeaderRefresh,})
      : super(key: key);

  @override
  HeaderListViewState createState() {
    return new HeaderListViewState();
  }
}

class HeaderListViewState extends State<HeaderListView> {
  @override
  Widget build(BuildContext context) {

    if(widget.usePullToRefresh == true){
      return  new Refresh(
          onFooterRefresh: widget.onFooterRefresh,
          onHeaderRefresh:  widget.onHeaderRefresh,
          child: new ListView.builder(
        itemBuilder: (BuildContext context, int position) {
          return buildItemWidget(context, position);
        },
        itemCount: _getListCount(),
        physics: new AlwaysScrollableScrollPhysics(),
        shrinkWrap: true,
      ));
    }else{
      return new ListView.builder(
        itemBuilder: (BuildContext context, int position) {
          return buildItemWidget(context, position);
        },
        itemCount: _getListCount(),
        physics: new AlwaysScrollableScrollPhysics(),
        shrinkWrap: true,
      );
    }


  }

  int _getListCount() {
    int itemCount = widget.listData.length;
    return getHeaderCount() + itemCount;
  }

  int getHeaderCount() {
    int headerCount = widget.headerList != null ? widget.headerList.length : 0;
    return headerCount;
  }

  Widget _headerItemWidget(BuildContext context, int index) {
    if (widget.headerCreator != null) {
      return widget.headerCreator(context, index);
    } else {
      return new GestureDetector(
        child: new Padding(
            padding: new EdgeInsets.all(10.0),
            child: new Text("Header Row $index")),
        onTap: () {
          print('header click $index --------------------');
        },
      );
    }
  }

  Widget buildItemWidget(BuildContext context, int index) {
    if (index < getHeaderCount()) {
      return _headerItemWidget(context, index);
    } else {
      int pos = index - getHeaderCount();
      return _itemBuildWidget(context, pos);
    }
  }

  Widget _itemBuildWidget(BuildContext context, int index) {
    if (widget.itemWidgetCreator != null) {
      return widget.itemWidgetCreator(context, index);
    } else {
      return new GestureDetector(
        child: new Padding(
            padding: new EdgeInsets.all(10.0), child: new Text("Row $index")),
        onTap: () {
          print('click $index --------------------');
        },
      );
    }
  }
}
