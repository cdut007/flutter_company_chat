import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/home/ReplyPage.dart';
import 'package:flutter_app/entity/Moment.dart';
import 'package:flutter_app/util/ApiManager.dart';
import 'package:flutter_app/widget/VcardBannerView.dart';
import 'package:flutter_app/widget/LoadingWidget.dart';
import 'package:flutter_app/Util/Constants.dart';
import 'package:flutter_app/util/CommonUI.dart';
import 'package:flutter_app/Util/TimeUtils.dart';
import 'package:event_bus/event_bus.dart';
import 'dart:async';
import 'dart:convert';
import 'package:flutter_app/widget/HeaderListView.dart';

class Follow extends StatefulWidget {
  @override
  _FollowState createState() => new _FollowState();
}

class _FollowState extends State<Follow> {
  var currentPage = 1;
  List<Moment> listData = [];
  LoadingType loadingType = LoadingType.Loading;

  Widget wordsCard(Moment article) {
    Widget markWidget;
    if (article.fileContent == null) {
      markWidget = new Text(article.content,
          style: new TextStyle(height: 1.3, color: GlobalConfig.fontColor));
    } else {
      //默认取第一个。
      List   decodedJson = json.decode(article.fileContent);
      var url='';
      if(decodedJson != null && decodedJson.length>0){
       try{
         var fileUrl = decodedJson[0]['publicUrl'];
         if(fileUrl!=null){
           url = fileUrl+"?x-oss-process=image/resize,w_100";
         }
       }catch(e){
         print(e);
       }

      }

      markWidget = new Row(
        children: <Widget>[
          new Expanded(
            flex: 2,
            child: new Container(
              child: new Text(article.content == null?'':article.content,
                  style: new TextStyle(
                      height: 1.3, color: GlobalConfig.fontColor)),
            ),
          ),
          new Expanded(
              flex: 1,
              child: new AspectRatio(
                  aspectRatio: 3.0 / 2.0,
                  child: new Container(
                    foregroundDecoration: new BoxDecoration(
                        image: new DecorationImage(
                          image: new NetworkImage(url),
                          centerSlice:
                              new Rect.fromLTRB(270.0, 180.0, 1360.0, 730.0),
                        ),
                        borderRadius:
                            const BorderRadius.all(const Radius.circular(6.0))),
                  ))),
        ],
      );
    }
    return new Card(
        child: new Container(
            margin: const EdgeInsets.only(top: 0.0, bottom: 0.0),
            child: new FlatButton(
              onPressed: () {
                Navigator.of(context)
                    .push(new MaterialPageRoute(builder: (context) {
                  return new ReplyPage();
                }));
              },
              child: new Column(
                children: <Widget>[
                  new Container(
                    child: new Row(
                      children: <Widget>[
                        new Container(
                          child: CommonUI.getAvatarWidget(article.userAvatar),
                        ),
                        new Padding(
                          padding: const EdgeInsets.only(left: 8),
                          child: new Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: <Widget>[
                              new Container(
                                  child: new Text(article.userName,
                                      style:
                                          new TextStyle(color: Colors.black87),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis)),
                              new Text('666位关注者',
                                  style: new TextStyle(
                                      color: GlobalConfig.fontColor)),
                              new Text(readTimestamp(article.createTime) ,
                                  style: new TextStyle(
                                      color: GlobalConfig.fontColor))
                            ],
                          ),
                        )
                      ],
                    ),
                    padding: const EdgeInsets.only(top: 10.0),
                  ),
//                  new Container(
//                      child: new Text(article.title,
//                          style: new TextStyle(
//                              fontWeight: FontWeight.bold,
//                              fontSize: 16.0,
//                              height: 1.3,
//                              color: Colors.black)),
//                      margin: new EdgeInsets.only(top: 6.0, bottom: 2.0),
//                      alignment: Alignment.topLeft),
                  new Container(
                      child: markWidget,
                      margin: new EdgeInsets.only(top: 6.0),
                      alignment: Alignment.topLeft),
                  new Container(
                    child: new Row(
                      children: <Widget>[
//                        new Expanded(
//                            child: new Text(
//                                article.agreeNum.toString() +
//                                    " 赞同 · " +
//                                    article.commentNum.toString() +
//                                    "评论",
//                                style: new TextStyle(
//                                    color: GlobalConfig.fontColor))),
                        new PopupMenuButton(
                            icon: new Icon(
                              Icons.linear_scale,
                              color: GlobalConfig.fontColor,
                            ),
                            itemBuilder: (BuildContext context) =>
                                <PopupMenuItem<String>>[
                                  new PopupMenuItem<String>(
                                      value: '选项一的值',
                                      child: new Text('屏蔽这个问题')),
                                  new PopupMenuItem<String>(
                                      value: '选项二的值',
                                      child: new Text('取消关注 ')),
                                  new PopupMenuItem<String>(
                                      value: '选项二的值', child: new Text("举报"))
                                ])
                      ],
                    ),
                    padding: const EdgeInsets.only(),
                  )
                ],
              ),
            )));
  }

  EventBus eventBus = GlobalConfig.getEventBus();
  StreamSubscription loginSubscription;
  @override
  void initState() {
    super.initState();
    getDatas(START_REQUEST);
    print('*********【初始化动态列表订阅事件】*********');
    loginSubscription = eventBus.on<Moment>().listen((event) {
      print('*********收到动态列表订阅事件*********');
      print(event);
      pullToRefresh();
    });
  }

  @override
  void dispose(){
    super.dispose();
    loginSubscription.cancel();
    print('*********【取消动态列表订阅事件】*********');

  }

  Future<Null> pullToRefresh() async {
    getDatas(REFRESH_REQIEST);
    return null;
  }

  Future<Null> onFooterRefresh() async {
    getDatas(LOADMORE_REQIEST);
  }

  ///
  /// 请求数据
  /// isLoadMore 是否为加载更多
  ///
  void getDatas(int request_type) async {
    var data = {};
    if (request_type == REFRESH_REQIEST) {
      currentPage = 1;
      data = {'page': '$currentPage', 'pageSize': '10'};
    } else {
      currentPage+=1;
      data = {'page': '$currentPage', 'pageSize': '10'};
    }

    ApiManager.getPostMomentsList(data).then((datas) {
      currentPage++;
      if (datas != null) {
        List<Moment>  momentsData= (datas as List) != null
            ? (datas as List).map((i) => Moment.fromJson(i)).toList()
            : null;
        setState(() {
          if (request_type != LOADMORE_REQIEST) {
            // 不是加载更多，则直接为变量赋值
            if (request_type == START_REQUEST || request_type == REFRESH_REQIEST) {
              listData = new List();
            }
            for (Moment data in momentsData) {
              listData.add(data);
            }
          } else {
            // 是加载更多，则需要将取到的news数据追加到原来的数据后面
            List<Moment> list1 = new List<Moment>();
            list1.addAll(listData);
            for (Moment data in momentsData) {
              list1.add(data);
            }
            listData = list1;
          }

          // 判断是否获取了所有的数据，如果是，则需要显示底部的"我也是有底线的"布局
//              if (has_next_page == false&&"endline"!= listData[listData.length].type) {
//                ListEnity listEnity = new ListEnity("endline", null);
//                listData.add(listEnity);
//              }

          if (request_type == REFRESH_REQIEST) {
            showToast(context, '刷新成功');
          }

          if(listData.length>0){
            loadingType = LoadingType.End;
          }else{
            loadingType = LoadingType.Empty;
          }
        });
      } else {
       setState(() {
         if(listData.length>0){
           loadingType = LoadingType.End;
         }else{
           loadingType = LoadingType.Empty;
         }
       });
        showToast(context, '已经没有更多了');
      }

    }, onError: (errorData) {
      currentPage-=1;
      setState(() {
        if(listData.length>0){
          loadingType = LoadingType.End;
        }else{
          loadingType = LoadingType.Error;
        }
      });
      var error = ApiManager.parseErrorInfo(errorData);
      showErrorInfo(context, '错误码：${error.code}' + ' 错误原因：' + error.msg);
    });
  }

  getBody() {
    if(loadingType == LoadingType.Loading){
      return Column(children: <Widget>[
        new VcardBannerView(),
        new LoadingWidget(loadingType: LoadingType.Loading,)
      ],);
    }

    if(loadingType == LoadingType.Empty){
      return Column(children: <Widget>[
        new VcardBannerView(),
        new LoadingWidget(loadingType: LoadingType.Empty,clickCallback: (){
          setState(() {
            loadingType = LoadingType.Loading;
          });
          pullToRefresh();
        },)
      ],);

    }

    if(loadingType == LoadingType.Error){
      return Column(children: <Widget>[
        new VcardBannerView(),
        new LoadingWidget(loadingType: LoadingType.Error,clickCallback: (){
          setState(() {
            loadingType = LoadingType.Loading;
          });
          pullToRefresh();
        },)
      ],);

    }

      Widget content = new HeaderListView(
        listData,
        headerList: [1],
        headerCreator: (BuildContext context, int position) {
          if (position == 0) {
              return   new VcardBannerView();
          }
        },
        itemWidgetCreator: (BuildContext context, int position) {
          return wordsCard(listData[position]);
        },
        usePullToRefresh:true,
        onFooterRefresh: onFooterRefresh,
        onHeaderRefresh: pullToRefresh,
      );


    return new Container(
          child: content );

  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        body: new Column(
      children: <Widget>[
        new Expanded(
          child: new Container(
            child: getBody(),
          ),
          flex: 1,
        )
      ],
    ));
  }
}
