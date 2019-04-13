package com.ultralinked.voip.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.ultralinked.voip.rtcapi.rtcapij;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class NetRtcFactory {

	public static String TAG	= "netrtcFactory";


	private static final String CONFIG_FILE="configall.cfg";

	private NetRtcFactory() {

	};

	protected static synchronized void InitNetRtc(Context context) {


		String configAuthString="";

		AssetManager am =  context.getAssets();

		try {

			 InputStream inputStream = am.open(CONFIG_FILE);

			 configAuthString = readStream(inputStream);

		} catch (IOException e1) {

			e1.printStackTrace();
		}

        Log.i(TAG, "configAuth : " + configAuthString);

		try {

            rtcapij.netrtc_set_context("android_context", (Object) context);

			rtcapij.netrtc_init(configAuthString, "test");

		 	if (!CallApi.disableLogToFile){
				rtcapij.netrtc_set_config("logpath", CallApi.logfilePath);
			}


		} catch (Exception e) {

		}
	}

   private static String readStream(InputStream iStream) throws IOException {
        //build a Stream Reader, it can read char by char
        InputStreamReader iStreamReader = new InputStreamReader(iStream);
        //build a buffered Reader, so that i can read whole line at once
        BufferedReader bReader = new BufferedReader(iStreamReader);
        String line = null;
        StringBuilder builder = new StringBuilder();
        while((line = bReader.readLine()) != null) {  //Read till end
            builder.append(line);
        }
        bReader.close();         //close all opened stuff
        iStreamReader.close();
        iStream.close();
        return builder.toString();
    }
}
