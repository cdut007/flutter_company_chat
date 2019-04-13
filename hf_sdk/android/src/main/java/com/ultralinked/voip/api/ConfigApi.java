package com.ultralinked.voip.api;


/**
 * Created by yongjun on 8/17/16.
 */
public class ConfigApi {

    public  static  final int IM_CONFIG = 1;

    public  static  final int APP_NAME = 2;

    public  static String appName = "SealedChat";

    public static  void  setConfig(int majorValue,int minorType,String value){
        switch (majorValue){
            case IM_CONFIG:
                setIMCongig(minorType,value);
                break;
            case APP_NAME:
                appName = value;
                break;

        }

    }

    public  static  final class IMConfig{

        public  static  final int IM_ENCRYPT_TYPE = 1;

        public  static  final int IM_THUMB_UPLOAD_URL = 2;

        public  static  final String IM_ENCRYPT_TYPE_VALUE_ENABLE = "true";

        public  static  final String IM_ENCRYPT_TYPE_VALUE_DISABLE = "false";

        public  static  final int IM_MEDIA_AUTODOWNLOAD = 3;

        public static  boolean mediaAutoDownload;


    }

    private static void setIMCongig(int minorType, String value) {
        switch (minorType){
            case IMConfig.IM_ENCRYPT_TYPE:
                MessagingApi.setConfig("IM_ENCRYPT_FLAG",value);
                break;
            case IMConfig.IM_THUMB_UPLOAD_URL:
                MessagingApi.THUMB_UPLOAD_URL = value;
                break;

            case IMConfig.IM_MEDIA_AUTODOWNLOAD:
                if ("true".equals(value)){
                    IMConfig.mediaAutoDownload = true;
                }else{
                    IMConfig.mediaAutoDownload = false;
                }

                break;


        }
    }
}
