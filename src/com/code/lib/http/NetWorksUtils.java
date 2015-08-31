package com.code.lib.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorksUtils {
	/**
	 * �жϵ�ǰ�����Ƿ����
	 * 
	 * ��Ҫ��AndroidManifest.xml�ļ����������Ȩ�� <uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
	 * 
	 * @param context
	 * @return true �����ǰ��������״̬������Ϊ false
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
