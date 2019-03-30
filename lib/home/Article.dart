
class Article {
  String headUrl;
  String user;
  String action;
  String time;
  String title;
  String mark;
  String imgUrl;
  int agreeNum;
  int commentNum;


  Article(this.headUrl, this.user, this.action, this.time, this.title, this.mark, this.agreeNum, this.commentNum, {this.imgUrl});
}

List<Article> articleList = [
  new Article(
      "https://pic3.zhimg.com/50/2b8be8010409012e7cdd764e1befc4d1_s.jpg",
      "Amazon Web Services(AWS)",
      "666 位关注者",
      "2小时前",
      "在三体中，罗辑为什么会把控制权交给程心，难道他没有推测过后果吗？",
      "因为罗辑遵守了人类伦理。这个伦理大概就叫民主。 大刘其实是个典型的精英主义者，在他笔下...",
      32, 10,
      imgUrl: "https://pic2.zhimg.com/50/v2-710b7a6fea12a7203945b666790b7181_hd.jpg"
  ),
  new Article(
    "https://pic4.zhimg.com/50/v2-9a3cb5d5ee4339b8cf4470ece18d404f_s.jpg",
    "于丹",
    "666 位关注者",
    "5小时前",
    "我的手机系统是安卓。无意间发现自己的屏幕被人监控，请问怎样才能彻底摆脱被监控的处境呢？",
    "检查一下你手机里是不是被人装了什么软件，把你不认识的非系统软件删掉。不会删就去找手机店里的小哥，为什么这么多人...",
    38, 13,
  ),
  new Article(
      "https://pic3.zhimg.com/50/v2-8943c20cecab028e19644cccf0f3a38b_s.jpg",
      "HoldingFuture",
      "666 位关注者",
      "7小时前",
      "如何评价Google Flutter程序设计竞赛？",
      "跨平台统一方案，覆盖android，ios，web，desktop，真正的前端大统一。dart语言带来颠覆式的编程...",
      38, 13,
      imgUrl: "https://pic4.zhimg.com/v2-a7493d69f0d8f849c6345f8f693454f3_200x112.jpg"
  ),
  new Article(
      "https://pic3.zhimg.com/50/v2-8943c20cecab028e19644cccf0f3a38b_s.jpg",
      "嗑药的小毛妮",
      "555 位关注者",
      "7小时前",
      "老子明天不上班",
      "不上班爽翻，巴适得板...",
      38, 13,
      imgUrl: "https://pic3.zhimg.com/v2-b67be50be51e2e6d6112a64528683b09_b.jpg"
  )
];