package com.ultralinked.voip.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class NetworkManager {

	/**
	 * judge the current network is available or not
	 * @param context
	 * @return
	 */

	public static boolean isNetworkAvailable(Context context) {
		boolean isAvailable = true;

		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !info.isConnectedOrConnecting()) {

			isAvailable = false;

		}
		return isAvailable;

	}
	public static String getWIFILocalIpAdress(Context mContext) {

		//获取wifi服务
		WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		//判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = formatIpAddress(ipAddress);
		return ip;
	}
	private static String formatIpAddress(int ipAdress) {

		return (ipAdress & 0xFF ) + "." +
				((ipAdress >> 8 ) & 0xFF) + "." +
				((ipAdress >> 16 ) & 0xFF) + "." +
				( ipAdress >> 24 & 0xFF) ;
	}
	public static String getGPRSLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
