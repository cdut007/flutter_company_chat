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
  bool usePullToRefresh = false;
  var onFooterRefresh;
  var onHeaderRefresh;
  double scrollOffset = 0.0;

  ScrollController scrollController;
  var setScrollOffset;

  HeaderListView(List this.listData,
      {Key key,
      List this.headerList,
      ItemWidgetBuild this.itemWidgetCreator,
      HeaderWidgetBuild this.headerCreator,
      bool this.usePullToRefresh,
      var this.onFooterRefresh,
      var this.onHeaderRefresh,
      double this.scrollOffset,
      var this.setScrollOffset})
      : super(key: key);

  @override
  HeaderListViewState createState() {
    return new HeaderListViewState();
  }
}

class HeaderListViewState extends State<HeaderListView> {
  @override
  void initState() {
    super.initState();
    if (widget.setScrollOffset == null) {
      widget.setScrollOffset = (offset) {};
      widget.scrollOffset = 0.0;
    }
    widget.scrollController = new ScrollController(
        initialScrollOffset: widget.scrollOffset, keepScrollOffset: true);
  }

  @override
  Widget build(BuildContext context) {
    if (widget.usePullToRefresh == true) {
      if (widget.scrollController == null && widget.scrollOffset!=null) {
        widget.scrollController = new ScrollController(
            initialScrollOffset: widget.scrollOffset, keepScrollOffset: true);
      }
      return new NotificationListener(
        child: Refresh(
            scrollController: widget.scrollController,
            onFooterRefresh: widget.onFooterRefresh,
            onHeaderRefresh: widget.onHeaderRefresh,
            child: new ListView.builder(
              itemBuilder: (BuildContext context, int position) {
                return buildItemWidget(context, position);
              },
              itemCount: _getListCount(),
              physics: new AlwaysScrollableScrollPhysics(),
              shrinkWrap: true,
            )),
        onNotification: (notification) {
          if (notification is ScrollStartNotification) {
            if (notification.dragDetails != null) {
              widget.setScrollOffset(
                  notification.dragDetails.globalPosition.distance);
            }

            print(
                '###################notification ScrollStartNotification########################');
            // print(notification.dragDetails.globalPosition);
          } else if (notification is ScrollEndNotification) {

              print(
                  '###################notification ScrollEndNotification########################');


          }
        },
      );
    } else {
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
