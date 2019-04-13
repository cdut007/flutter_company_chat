package com.ultralinked.voip.api.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.ultralinked.voip.api.Log;

/**
 * Created by yongjun on 8/10/16.
 */
public class CommonUtils {


    private  static  final String TAG = "CommonUtils";

    public static  boolean isMainPid(Context context){
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        String packageName = context.getPackageName();
        if(!TextUtils.isEmpty(packageName) && !packageName.contains(":") && !packageName.contains("push")) {
            if (packageName.equals(processName)){
                Log.i(TAG,"is UI process:"+packageName);
                return  true;
            }else{
                Log.i(TAG,"running process name is :"+processName);
                return  false;
            }


        } else {
            Log.i(TAG,"is not UI process:"+packageName);
            return  false;
        }

    }
}
