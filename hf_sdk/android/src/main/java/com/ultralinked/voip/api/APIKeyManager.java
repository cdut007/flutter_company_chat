package com.ultralinked.voip.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2015/12/18.
 */
public class APIKeyManager {
    /**
     * Get the developer api key
     * @param context
     * @return
     */
    protected static String getApiKey(Context context) throws PackageManager.NameNotFoundException {

        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

        return appInfo.metaData.getString("com.globalroam.rtcapi.API_KEY");
    }

    /**
     * Get the developer secret key
     * @param context
     * @return
     */
    protected static String getSecretKey(Context context) throws PackageManager.NameNotFoundException{

        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

        return appInfo.metaData.getString("com.globalroam.rtcapi.SECRET_KEY");
    }
}
