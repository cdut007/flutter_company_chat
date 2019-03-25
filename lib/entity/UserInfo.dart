class UserInfo {
  String accessToken;
  String age;
  String developerId;
  String domain;
  String domainId;
  String email;
  String iconUrl;
  String id;
  String mobile;
  String name;
  String nickname;
  String privateContactPassword;
  String remarkname;
  String roles;
  String settingJson;
  String url;
  bool enable;
  bool hasCompany;
  bool hasPassword;
  int gender;
  SettingsBean settings;

  UserInfo({this.accessToken, this.age, this.developerId, this.domain, this.domainId, this.email, this.iconUrl, this.id, this.mobile, this.name, this.nickname, this.privateContactPassword, this.remarkname, this.roles, this.settingJson, this.url, this.enable, this.hasCompany, this.hasPassword, this.gender, this.settings});

  UserInfo.fromJson(Map<String, dynamic> json) {    
    this.accessToken = json['access_token'];
    this.age = json['age'];
    this.developerId = json['developer_id'];
    this.domain = json['domain'];
    this.domainId = json['domain_id'];
    this.email = json['email'];
    this.iconUrl = json['icon_url'];
    this.id = json['id'];
    this.mobile = json['mobile'];
    this.name = json['name'];
    this.nickname = json['nickname'];
    this.privateContactPassword = json['private_contact_password'];
    this.remarkname = json['remarkname'];
    this.roles = json['roles'];
    this.settingJson = json['setting_json'];
    this.url = json['url'];
    this.enable = json['enable'];
    this.hasCompany = json['has_company'];
    this.hasPassword = json['has_password'];
    this.gender = json['gender'];
    this.settings = json['settings'] != null ? SettingsBean.fromJson(json['settings']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['access_token'] = this.accessToken;
    data['age'] = this.age;
    data['developer_id'] = this.developerId;
    data['domain'] = this.domain;
    data['domain_id'] = this.domainId;
    data['email'] = this.email;
    data['icon_url'] = this.iconUrl;
    data['id'] = this.id;
    data['mobile'] = this.mobile;
    data['name'] = this.name;
    data['nickname'] = this.nickname;
    data['private_contact_password'] = this.privateContactPassword;
    data['remarkname'] = this.remarkname;
    data['roles'] = this.roles;
    data['setting_json'] = this.settingJson;
    data['url'] = this.url;
    data['enable'] = this.enable;
    data['has_company'] = this.hasCompany;
    data['has_password'] = this.hasPassword;
    data['gender'] = this.gender;
    if (this.settings != null) {
      data['settings'] = this.settings.toJson();
    }
    return data;
  }

}

class SettingsBean {
  String apiWebsocketAddr;
  String audioCodecs;
  String audioIceEnable;
  String blockPayment;
  String callbackAllowed;
  String connectionMode;
  String currentOutgoingCallDisplayNumber;
  String ddd1;
  String domain;
  String httpProxyIp;
  String httpProxyPort;
  String huaweiProductionPushAppkey;
  String huaweiProductionPushAppsecret;
  String imDomain;
  String imServerIp;
  String imServerPort;
  String imWebsocketIp;
  String imWebsocketPort;
  String key5;
  String paypalClientid;
  String paypalSandbox;
  String qrcodeUrl;
  String sendsmsAllowed;
  String sipProxyIp;
  String sipProxyPort;
  String sipWebsocketIp;
  String sipWebsocketPort;
  String stageTestAccounts;
  String sysCallcliLega;
  String task1;
  String teskkey3;
  String tunnelIp;
  String tunnelPort;
  String turnIp;
  String turnPassword;
  String turnPort;
  String turnUsername;
  String ulwebManagerUrl;
  String vosIp;
  String vosPrefix;
  List<AssistantListBean> assistant;

  SettingsBean({this.apiWebsocketAddr, this.audioCodecs, this.audioIceEnable, this.blockPayment, this.callbackAllowed, this.connectionMode, this.currentOutgoingCallDisplayNumber, this.ddd1, this.domain, this.httpProxyIp, this.httpProxyPort, this.huaweiProductionPushAppkey, this.huaweiProductionPushAppsecret, this.imDomain, this.imServerIp, this.imServerPort, this.imWebsocketIp, this.imWebsocketPort, this.key5, this.paypalClientid, this.paypalSandbox, this.qrcodeUrl, this.sendsmsAllowed, this.sipProxyIp, this.sipProxyPort, this.sipWebsocketIp, this.sipWebsocketPort, this.stageTestAccounts, this.sysCallcliLega, this.task1, this.teskkey3, this.tunnelIp, this.tunnelPort, this.turnIp, this.turnPassword, this.turnPort, this.turnUsername, this.ulwebManagerUrl, this.vosIp, this.vosPrefix, this.assistant});

  SettingsBean.fromJson(Map<String, dynamic> json) {    
    this.apiWebsocketAddr = json['api_websocket_addr'];
    this.audioCodecs = json['audio_codecs'];
    this.audioIceEnable = json['audio_ice_enable'];
    this.blockPayment = json['block_payment'];
    this.callbackAllowed = json['callback_allowed'];
    this.connectionMode = json['connection_mode'];
    this.currentOutgoingCallDisplayNumber = json['currentOutgoingCallDisplayNumber'];
    this.ddd1 = json['ddd1'];
    this.domain = json['domain'];
    this.httpProxyIp = json['http_proxy_ip'];
    this.httpProxyPort = json['http_proxy_port'];
    this.huaweiProductionPushAppkey = json['huawei_production_push_appkey'];
    this.huaweiProductionPushAppsecret = json['huawei_production_push_appsecret'];
    this.imDomain = json['im_domain'];
    this.imServerIp = json['im_server_ip'];
    this.imServerPort = json['im_server_port'];
    this.imWebsocketIp = json['im_websocket_ip'];
    this.imWebsocketPort = json['im_websocket_port'];
    this.key5 = json['key5'];
    this.paypalClientid = json['paypal_clientid'];
    this.paypalSandbox = json['paypal_sandbox'];
    this.qrcodeUrl = json['qrcode_url'];
    this.sendsmsAllowed = json['sendsms_allowed'];
    this.sipProxyIp = json['sip_proxy_ip'];
    this.sipProxyPort = json['sip_proxy_port'];
    this.sipWebsocketIp = json['sip_websocket_ip'];
    this.sipWebsocketPort = json['sip_websocket_port'];
    this.stageTestAccounts = json['stage_test_accounts'];
    this.sysCallcliLega = json['sys_callcli_lega'];
    this.task1 = json['task1'];
    this.teskkey3 = json['teskkey3'];
    this.tunnelIp = json['tunnel_ip'];
    this.tunnelPort = json['tunnel_port'];
    this.turnIp = json['turn_ip'];
    this.turnPassword = json['turn_password'];
    this.turnPort = json['turn_port'];
    this.turnUsername = json['turn_username'];
    this.ulwebManagerUrl = json['ulweb_manager_url'];
    this.vosIp = json['vos_ip'];
    this.vosPrefix = json['vos_prefix'];
    this.assistant = (json['assistant'] as List)!=null?(json['assistant'] as List).map((i) => AssistantListBean.fromJson(i)).toList():null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['api_websocket_addr'] = this.apiWebsocketAddr;
    data['audio_codecs'] = this.audioCodecs;
    data['audio_ice_enable'] = this.audioIceEnable;
    data['block_payment'] = this.blockPayment;
    data['callback_allowed'] = this.callbackAllowed;
    data['connection_mode'] = this.connectionMode;
    data['currentOutgoingCallDisplayNumber'] = this.currentOutgoingCallDisplayNumber;
    data['ddd1'] = this.ddd1;
    data['domain'] = this.domain;
    data['http_proxy_ip'] = this.httpProxyIp;
    data['http_proxy_port'] = this.httpProxyPort;
    data['huawei_production_push_appkey'] = this.huaweiProductionPushAppkey;
    data['huawei_production_push_appsecret'] = this.huaweiProductionPushAppsecret;
    data['im_domain'] = this.imDomain;
    data['im_server_ip'] = this.imServerIp;
    data['im_server_port'] = this.imServerPort;
    data['im_websocket_ip'] = this.imWebsocketIp;
    data['im_websocket_port'] = this.imWebsocketPort;
    data['key5'] = this.key5;
    data['paypal_clientid'] = this.paypalClientid;
    data['paypal_sandbox'] = this.paypalSandbox;
    data['qrcode_url'] = this.qrcodeUrl;
    data['sendsms_allowed'] = this.sendsmsAllowed;
    data['sip_proxy_ip'] = this.sipProxyIp;
    data['sip_proxy_port'] = this.sipProxyPort;
    data['sip_websocket_ip'] = this.sipWebsocketIp;
    data['sip_websocket_port'] = this.sipWebsocketPort;
    data['stage_test_accounts'] = this.stageTestAccounts;
    data['sys_callcli_lega'] = this.sysCallcliLega;
    data['task1'] = this.task1;
    data['teskkey3'] = this.teskkey3;
    data['tunnel_ip'] = this.tunnelIp;
    data['tunnel_port'] = this.tunnelPort;
    data['turn_ip'] = this.turnIp;
    data['turn_password'] = this.turnPassword;
    data['turn_port'] = this.turnPort;
    data['turn_username'] = this.turnUsername;
    data['ulweb_manager_url'] = this.ulwebManagerUrl;
    data['vos_ip'] = this.vosIp;
    data['vos_prefix'] = this.vosPrefix;
    data['assistant'] = this.assistant != null?this.assistant.map((i) => i.toJson()).toList():null;
    return data;
  }
}

class AssistantListBean {
  String iconUrl;
  String id;
  String name;
  String nameEn;
  String nameZh;
  String nickname;

  AssistantListBean({this.iconUrl, this.id, this.name, this.nameEn, this.nameZh, this.nickname});

  AssistantListBean.fromJson(Map<String, dynamic> json) {    
    this.iconUrl = json['icon_url'];
    this.id = json['id'];
    this.name = json['name'];
    this.nameEn = json['name_en'];
    this.nameZh = json['name_zh'];
    this.nickname = json['nickname'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['icon_url'] = this.iconUrl;
    data['id'] = this.id;
    data['name'] = this.name;
    data['name_en'] = this.nameEn;
    data['name_zh'] = this.nameZh;
    data['nickname'] = this.nickname;
    return data;
  }
}
