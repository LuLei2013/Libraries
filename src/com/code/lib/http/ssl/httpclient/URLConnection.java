package com.code.lib.http.ssl.httpclient;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.code.lib.http.ssl.sslsocketfactory.AllowAllSSLSocketFactory;
import com.code.lib.log.Log;

public class URLConnection {
	private static final String TAG = "URLConnection";

	public static HttpURLConnection getURLConnection(String url) {

		try {
			URL imgUrl = new URL(url);
			HttpURLConnection urlConn = null;
			// 使用HttpURLConnection打开连接
			if (imgUrl.getProtocol().endsWith("http")) {
				urlConn = (HttpURLConnection) imgUrl.openConnection();
			} else if (imgUrl.getProtocol().endsWith("https")) {
				HttpsURLConnection urlConnTttps = (HttpsURLConnection) imgUrl
						.openConnection();
				urlConnTttps.setSSLSocketFactory(AllowAllSSLSocketFactory
						.getInstance().getSSLContext().getSocketFactory());
				urlConn = urlConnTttps;
			}
			if (urlConn == null) {
				return null;
			}
			urlConn.setDoInput(true);
			urlConn.connect();
			return urlConn;
		} catch (Exception e) {
			Log.e(TAG, e);
		}
		return null;
	}
}
