package com.ultralinked.voip.api;


import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.ultralinked.voip.rtcapi.rtcapij;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Copyright (C) 2010 Lytsing Huang http://lytsing.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Wrapper API for sending log output.
 */
/**/


public class Log {

	protected String TAG	= "Log";

	private static final int VERBOSE = android.util.Log.VERBOSE;
	private static final int DEBUG = android.util.Log.DEBUG;
	private static final int INFO = android.util.Log.INFO;
	private static final int WARN = android.util.Log.WARN;
	private static final int ERROR = android.util.Log.ERROR;
	private static final int ASSERT = android.util.Log.ASSERT;
	private static final long MAX_LOG_FILE = 1024 * 1024 * 8; //8MB

	private static boolean sDebug = false;
	private static boolean sFileLog = false;
	private static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final SimpleDateFormat sFormat1 = new SimpleDateFormat("yyyyMMdd");

	private Log() {
	}

	private static final File sDir = new File(Environment.getExternalStorageDirectory(), "360Log/Plugin/");

	static {
		sFileLog = sDir.exists() && sDir.isDirectory();
		sDebug = sFileLog;
	}

	public static boolean isDebug() {
		return sDebug;
	}

	private static boolean isFileLog() {
		return sFileLog;
	}

	public static boolean isLoggable(int i) {
		return isDebug();
	}

	public static boolean isLoggable() {
		return isDebug();
	}

	private static String levelToStr(int level) {
		switch (level) {
			case VERBOSE:
				return "V";
			case DEBUG:
				return "D";
			case INFO:
				return "I";
			case WARN:
				return "W";
			case ERROR:
				return "E";
			case ASSERT:
				return "A";
			default:
				return "UNKNOWN";
		}
	}

	private  static String logPath = null;
	public static void setLogPath(String logfilePath) {
		logPath = logfilePath;
	}

	private static File getLogFile() {
		File file =null;

		if (logPath!=null){
			file = new File(Environment.getExternalStorageDirectory(), String.format(logPath+ File.separator+"Log_%s_%s.log", sFormat1.format(new Date()), android.os.Process.myPid()));

		}else{
			file = new File(Environment.getExternalStorageDirectory(), String.format("uluc"+ File.separator+"Log_%s_%s.log", sFormat1.format(new Date()), android.os.Process.myPid()));

		}
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return file;
	}

	private static HandlerThread sHandlerThread;
	private static Handler sHandler;

	static {
		sHandlerThread = new HandlerThread("UL@FileLogThread");
		sHandlerThread.start();
		sHandler = new Handler(sHandlerThread.getLooper());
	}

	private static void logToFile(final int level, final String tag, final String format, final Object[] args, final Throwable tr) {
		sHandler.post(new Runnable() {
			@Override
			public void run() {
				logToFileInner(level, tag, format, args, tr);
			}
		});
	}

	private static void logToFileInner(int level, String tag, String format, Object[] args, Throwable tr) {
		PrintWriter writer = null;
		try {
			if (!isFileLog()) {
				return;
			}

			File logFile = getLogFile();
			if (logFile.length() > MAX_LOG_FILE) {
				logFile.delete();
			}
			writer = new PrintWriter(new FileWriter(logFile, true));
			String msg = String.format(format, args);
			String log = String.format("%s %s-%s/%s %s/%s %s", sFormat.format(new Date()), android.os.Process.myPid(),  android.os.Process.myUid(), getProcessName(), levelToStr(level), tag, msg);
			writer.println(log);
			if (tr != null) {
				tr.printStackTrace(writer);
				writer.println();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable e) {
				}
			}
		}
	}

	private static String getProcessName() {
		return "?";
	}

	private static void println(final int level, final String tag, final String format, final Object[] args, final Throwable tr) {
		logToFile(level, tag, format, args, tr);
		String message;
		if (args != null && args.length > 0) {
			message = String.format(format, args);
		} else {
			message = format;
		}

		if (tr != null) {
			message += android.util.Log.getStackTraceString(tr);
		}
		android.util.Log.println(level, tag, message);
	}

	public static void v(String tag, String format, Object... args) {
		v(tag, format, null, args);
	}

	public static void v(String tag, String format, Throwable tr, Object... args) {
		if (!isLoggable(VERBOSE)) {
			return;
		}

		println(VERBOSE, tag, format, args, tr);
	}


	public static void d(String tag, String format, Object... args) {
		d(tag, format, null, args);
	}

	public static void d(String tag, String format, Throwable tr, Object... args) {
		if (!isLoggable(DEBUG)) {
			return;
		}
		println(DEBUG, tag, format, args, tr);
	}

	public static void i(String tag, String format, Object... args) {
		i(tag, format, null, args);
	}

	public static void i(String tag, String format, Throwable tr, Object... args) {
		if (!isLoggable(INFO)) {
			return;
		}
		println(INFO, tag, format, args, tr);
	}

	public static void w(String tag, String format, Object... args) {
		w(tag, format, null, args);
	}

	public static void w(String tag, String format, Throwable tr, Object... args) {
		if (!isLoggable(WARN)) {
			return;
		}
		println(WARN, tag, format, args, tr);
	}

	public static void w(String tag, Throwable tr) {
		w(tag, "Log.warn", tr);
	}

	public static void e(String tag, String format, Object... args) {
		e(tag, format, null, args);
	}

	public static void e(String tag, String format, Throwable tr, Object... args) {
		if (!isLoggable(ERROR)) {
			return;
		}
		println(ERROR, tag, format, args, tr);
	}

	public static void wtf(String tag, String format, Object... args) {
		wtf(tag, format, null, args);
	}

	public static void wtf(String tag, Throwable tr) {
		wtf(tag, "wtf", tr);
	}

	public static void wtf(String tag, String format, Throwable tr, Object... args) {
		if (!isLoggable()) {
			return;
		}
		println(ASSERT, tag, format, args, tr);
	}

	/**
	 * Send a VERBOSE log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void v(String tag, String msg) {
		android.util.Log.v(tag, buildMessage(msg));
		rtcapij.netrtc_log(7, tag + "-- > " + buildMessage(msg));

	}


	/**
	 * Send a DEBUG log message.
	 *
	 * @param msg
	 * @param tag
	 */
	public static void d(String tag, String msg) {
		android.util.Log.d(tag, buildMessage(msg));
		rtcapij.netrtc_log(7, tag+"-- > "+buildMessage(msg));

	}


	/**
	 * Send an INFO log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void i(String tag, String msg) {
		android.util.Log.i(tag, buildMessage(msg));
		rtcapij.netrtc_log(6, tag+"-- > "+buildMessage(msg));

	}
	/**
	 * Send a WARN log message
	 *
	 * @param msg The message you would like logged.
	 */
	public static void w(String tag, String msg) {
		android.util.Log.w(tag, buildMessage(msg));
		rtcapij.netrtc_log(4, tag+"-- > "+buildMessage(msg));

	}


	/**
	 * Send an ERROR log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void e(String tag, String msg) {
		android.util.Log.e(tag, buildMessage(msg));
		rtcapij.netrtc_log(3, tag+"-- > "+buildMessage(msg));

	}
	public static void e(String tag, String msg, Exception e) {
		android.util.Log.e(tag, buildMessage(msg));
		rtcapij.netrtc_log(3, tag+"-- > "+buildMessage(msg));

	}



	/**
	 * Building Message
	 *
	 * @param msg The message you would like logged.
	 * @return Message String
	 */
	protected static String buildMessage(String msg) {
		if (TextUtils.isEmpty(msg)) {
			return "msg is null";
		}
		return msg;

	}


}
