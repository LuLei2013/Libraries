package com.code.lib.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorksUtils {
	/**
	 * 判断当前网络是否可用
	 * 
	 * 需要再AndroidManifest.xml文件中添加如下权限 <uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
	 * 
	 * @param context
	 * @return true 如果当前处于联网状态，否则为 false
	 */
	public static boolean isNetWorkAvaliable(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE) {
			return info.isConnected();
		} else {
			return false;
		}
	}
}
