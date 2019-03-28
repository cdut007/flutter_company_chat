/**
 * @Description  字符串工具类
 * @Author  james
 * @Date 2019/03/24  9:25
 * @Version  1.0
 */

import 'package:flutter_app/Util/MD5Utils.dart';
import 'package:flutter_app/entity/VcardEntity.dart';

class StringUtil{
  ///大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
  /// 此方法中前三位格式有：
  /// 13+任意数 * 15+除4的任意数 * 18+除1和4的任意数 * 17+除9的任意数 * 147
  static bool isChinaPhoneLegal(String str) {
    return new RegExp('^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}\$').hasMatch(str);
  }

}

 getUserVcardName(VcardEntity vcardEntiy){
  if(vcardEntiy.hfCardDetails == null){
    return '';
  }
  return vcardEntiy.hfCardDetails[0].name;
}
 getUserVcardPhone(VcardEntity vcardEntiy){
   if(vcardEntiy.hfCardDetails == null){
     return '';
   }
  return vcardEntiy.hfCardDetails[0].phoneNumber;
}
 getUserVcardCompany(VcardEntity vcardEntiy){
   if(vcardEntiy.hfCardDetails == null){
     return '';
   }
  return vcardEntiy.hfCardDetails[0].companyName;
}

getUserVcardJobPosition(VcardEntity vcardEntiy){
  if(vcardEntiy.hfCardDetails == null){
    return '';
  }
  return vcardEntiy.hfCardDetails[0].jobPosition;
}

 getUserVcardAvatar(VcardEntity vcardEntiy){
  return vcardEntiy.avatar;
}

/**
 * 获取财经新闻请求url
 */
String GetFinanceNewsUrl(String query){
  String user_key = "e66f2652b0-NDlmNDhmOT";
  var now = new DateTime.now();
  String time = now.millisecondsSinceEpoch.toString();
  String secret_key = "llNjZmMjY1MmIwNT-58ba0f5e5a49f48";
  String md5_str = StringToMd5(user_key + time + secret_key);
  String source_id = "2358538";
  String news_url = "https://graphql.shenjian.io/?user_key=" +user_key +"&timestamp=" + time +"&sign=" +md5_str +"&source_id=" +source_id +"&query=" +query;
  return news_url;
}
