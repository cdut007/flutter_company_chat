package com.ultralinked.voip.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ultralinked.voip.rtcapi.rtcapij;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class LogUtils {

	private final static String TAG = "LogUtils";

	public  static String emailSubject= ConfigApi.appName+" Android Log Report";

	public  static  String[] emailAddress=new String[]{"android@sealedchat.com"};

	public static Handler mHandler=new Handler(Looper.getMainLooper());
	// you can change to you own path

	/**
	 * report a bug with log file to server by email
	 * @param context
	 */
	public static void reportBug(final Context context, final String title){

		MessagingApi.flushLog();

		rtcapij.netrtc_set_config("fflush_log", "");

		final String srcFilePath= CallApi.logfilePath;

		final String imSrcFilePath= CallApi.imLogfilePath;

		SimpleDateFormat time=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

        String timePrefix=time.format(new Date(System.currentTimeMillis()));

		final String distFilePath= CallApi.logfilePath.substring(0, srcFilePath.lastIndexOf("/"))+timePrefix+".zip";

		final String distFilePath2= CallApi.imLogfilePath.substring(0, imSrcFilePath.lastIndexOf("/"))+"IMLog"+timePrefix+".zip";

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {


					File calllogFile = new File(distFilePath);
					File imlogFile = new File(distFilePath2);

					String appName= ConfigApi.appName;

					deleteLastZipFiles(calllogFile.getParent(),appName,calllogFile.getName());
					deleteLastZipFiles(imlogFile.getParent(),appName,imlogFile.getName());

					java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(distFilePath));


					File file = new File(srcFilePath);


					ZipUitls.zipFiles(file.getParent(), file.getName(), outZip);


					outZip.finish();
					outZip.close();


					 outZip = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(distFilePath2));


					 file = new File(imSrcFilePath);

					ZipUitls.zipFiles(file.getParent(), file.getName(), outZip);


					outZip.finish();
					outZip.close();


					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LogUtils.sendLogFile(context, title, new String[]{distFilePath,distFilePath2});
						}
					});



					//deleteFiles(srcFilePath);
					//deleteFiles(imSrcFilePath);

				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		}).start();



	}
	protected static  void  deleteDumpFiles(String filePath){

		File dir = new File(filePath);

		if(dir.isDirectory()){

			String[] subFiles=dir.list();

			for (int i = 0; i <subFiles.length ; i++) {

				String fileName=subFiles[i];
				if(fileName.contains("dmp")){
					Log.i(TAG,"FileName : "+fileName);
					String absfilePath=filePath+ File.separator+fileName;
                   File file=new File(absfilePath);
					file.delete();
				}
			}
		}
	}



	protected static  void  deleteLastZipFiles(String ParentDir, String ignoreFilePerfex, String ignorefileName){

		File dir = new File(ParentDir);

		if(dir.isDirectory()){

			String[] subFiles=dir.list();
			if(subFiles==null){
				return;

			}

			for (int i = 0; i <subFiles.length ; i++) {

				String fileName=subFiles[i];

				if(fileName.startsWith(ignoreFilePerfex)&&fileName.endsWith("zip") && !fileName.equals(ignorefileName)){
					Log.i(TAG,"delete log FileName : "+fileName);
					String absfilePath=ParentDir+ File.separator+fileName;
					File file=new File(absfilePath);
					file.delete();
				}
			}
		}
	}


	protected static  void  deleteFiles(String filePath){
		File dir = new File(filePath);

		if(dir.isDirectory()){

			String[] subFiles=dir.list();
			if(subFiles==null){
				return;

			}

			for (int i = 0; i <subFiles.length ; i++) {

				String fileName=subFiles[i];
				Log.i(TAG,"delete logdir FileName : "+fileName);
				String absfilePath=filePath+ File.separator+fileName;
				File file=new File(absfilePath);
				if (!file.isDirectory()){
					file.delete();
				}

			}
		}
	}

	protected static  void  deleteZipFiles(String filePath){

	}
	public static String getEmailContent(Context context){

		String versionName="";

		try {

			versionName=context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;


		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

		StringBuilder sb=new StringBuilder();

		sb.append("Please describe problem :").append("\r\n").append("\r\n").append("\r\n").append("\r\n")
				.append("Attach Screenshots (if any) : ").append("\r\n").append("\r\n").append("\r\n").append("\r\n")
				.append("Running on ").append(android.os.Build.MODEL).append("\r\n")
				.append("OS Version : ").append(android.os.Build.VERSION.RELEASE).append("\r\n")
				.append("Client Version : ").append(versionName);

		return  sb.toString();

	}
	public static String getLogFilePath(){

		rtcapij.netrtc_set_config("fflush_log", "");

		final String srcFilePath= CallApi.logfilePath;

		final String imSrcFilePath= CallApi.imLogfilePath;

		SimpleDateFormat time=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		String timePrefix=time.format(new Date(System.currentTimeMillis()));

		final String distFilePath= CallApi.logfilePath.substring(0, srcFilePath.lastIndexOf("/"))+timePrefix+".zip";


		try {
			ZipUitls.zipFolder(srcFilePath, distFilePath, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return distFilePath;


	}


	private static void sendLogFile(Context context, String title , String[] filePaths){

		String versionName="";

		try {

			versionName=context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;


		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

		StringBuffer sb=new StringBuffer();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timePrefix=time.format(new Date(System.currentTimeMillis()));
		emailSubject = ConfigApi.appName+" Android Log Report--"+timePrefix;

		sb.append("Please describe problem :").append("\r\n").append("\r\n").append("\r\n").append("\r\n")
	    .append("Attach Screenshots (if any) : ").append("\r\n").append("\r\n").append("\r\n").append("\r\n")
		.append("Running on ").append(android.os.Build.MODEL).append("\r\n")
		.append("OS Version : ").append(android.os.Build.VERSION.RELEASE).append("\r\n")
		.append("Client Version : ").append(versionName).append("\r\n")
		.append("occur time:").append(timePrefix);

		    List<Intent> targetShareIntents=new ArrayList<Intent>();
		    Intent shareIntent=new Intent();

        String accounts = "";
        String separator = ";";

        if (emailAddress.length == 1) {
            accounts = emailAddress[0];
        } else {
            for (int i = 0; i < emailAddress.length - 1; i++) {

                accounts += emailAddress[i] + separator;
            }
            accounts += emailAddress[emailAddress.length - 1];
        }

        Log.i("inviteToULUC", "accounts==" + accounts);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // Android 4.4
        // and up
        {
            shareIntent.setAction(Intent.ACTION_SENDTO);
            shareIntent.setData(Uri.parse("mailto:"+accounts));

        }else{

		    shareIntent.setAction(Intent.ACTION_SEND);
		    shareIntent.setType("message/rfc822");
        }


        List<ResolveInfo> resInfos=context.getPackageManager().queryIntentActivities(shareIntent, 0);

		    removeDuplicateWithOrder(resInfos);

		    if(!resInfos.isEmpty()){

		        for(ResolveInfo resInfo : resInfos){

		            String packageName=resInfo.activityInfo.packageName;

		            Log.i(TAG,"packageName = "+ packageName);

		            if(packageName.contains("mail")||"com.google.android.gm".equals(packageName)){

						ArrayList<Uri> uris = new ArrayList< Uri>();
						//convert from paths to Android friendly Parcelable Uri's
						for (String file : filePaths)
						{
							File fileIn = new File(file);
							Uri u = Uri.fromFile(fileIn);
							uris.add(u);
						}

		                Intent intent=new Intent();

		                intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
		                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
		                intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
		                intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
		                intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		               // intent.setType("application/zip");
						intent.setType("*/*");
		                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
		                intent.setPackage(packageName);
		                targetShareIntents.add(intent);
		            }
		        }
		        if(!targetShareIntents.isEmpty()){

		        	Log.i(TAG, "Do not Have Intent size is "+targetShareIntents.size());

					 if (TextUtils.isEmpty(title)){
						 title = "Choose email client";
					 }

		            Intent chooserIntent= Intent.createChooser(targetShareIntents.remove(0), title);

		            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));

					chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		            context.startActivity(chooserIntent);

		        }else{

		        	Log.i(TAG, "Do not Have Intent");
		        }
		    }
	}



	public static void removeDuplicateWithOrder(List<ResolveInfo> list) {

		if (list == null) {

			return;

		}

		Set<String> set = new HashSet<String>();

		List<ResolveInfo> newList = new ArrayList<ResolveInfo>();

		for (Iterator<ResolveInfo> iter = list.iterator(); iter.hasNext();) {

			ResolveInfo info = (ResolveInfo) iter.next();

			if (set.add(info.activityInfo.packageName)) {

				newList.add(info);

			}
		}

		list.clear();

		list.addAll(newList);

	}

	/**
	 * make native so library crash
	 */
	public  static  void makeSDKCrash(){

		rtcapij.netrtc_call_audio(CallApi.configName, "cRaSh_loG_tEst", 0);
	}

	/**
	 * disable catch native crash bug
	 * @param disable
	 */
	public  static void disableCatchSDKCrashLog(boolean disable){

		{
			SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(CallApi.getContext());

			if(disable){

				Log.i(TAG, "~ diable catch native crash log ~");

				rtcapij.netrtc_set_config("crash_log", "close");

			}else{

				Log.i(TAG, "~ enable catch catch crash log ~");

				rtcapij.netrtc_set_config("crash_log", "open");

			}
			preferences.edit().putBoolean("DISABLE_CRASH_LOG", disable).commit();
			return;
		}


	}

}
