package com.code.lib.http.ssl.httpclient;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.code.lib.http.ssl.sslsocketfactory.AllowAllSSLSocketFactory;
import com.code.lib.http.ssl.sslsocketfactory.SelfSSLSocketFactoryCertificate;
import com.code.lib.http.ssl.sslsocketfactory.SelfSSLSocketFactoryKeyStore;
import com.code.lib.log.Log;

import android.content.Context;

public class HttpClientFactory {
	private static final String TAG = "HttpClient";

	public static HttpClient getSelfKeyStoreHttpClient(Context context,
			int keystoreRawResouceId, String keyStorePassWord) {
		try {
			SSLSocketFactory sf = SelfSSLSocketFactoryKeyStore.getInstance(
					context, keystoreRawResouceId, keyStorePassWord);
			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			return getClient(sf);
		} catch (Exception e) {
			Log.e(TAG, e);
		}
		return null;
	}

	public static HttpClient getSelfKeyStoreHttpClient(Context context,
			String keystoreAssetsFile, String keyStorePassWord) {
		try {
			SSLSocketFactory sf = SelfSSLSocketFactoryKeyStore.getInstance(
					context, keystoreAssetsFile, keyStorePassWord);
			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			return getClient(sf);
		} catch (Exception e) {
			Log.e(TAG, e);
		}
		return null;
	}

	public static HttpClient getAllowAllHttpClient(Context context) {
		try {
			SSLSocketFactory sf = AllowAllSSLSocketFactory.getInstance();
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return getClient(sf);
		} catch (Exception e) {
			Log.e(TAG, e);
		}
		return null;
	}

	public static HttpClient getSelfCertificateHttpClient(Context context,
			int certificateResourceRawId, String alias) {
		try {
			SSLSocketFactory sf = SelfSSLSocketFactoryCertificate.getInstance(
					context, certificateResourceRawId, alias);
			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			return getClient(sf);
		} catch (Exception e) {
			Log.e(TAG, e);
		}
		return null;
	}

	public static HttpClient getSelfCertificateHttpClient(Context context,
			String certificateAssetsFile, String alias) {
		try {
			SSLSocketFactory sf = SelfSSLSocketFactoryCertificate.getInstance(
					context, certificateAssetsFile, alias);
			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			return getClient(sf);
		} catch (Exception e) {
			Log.e(TAG, e);
		}
		return null;
	}

	private static HttpClient getClient(SSLSocketFactory sf) {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		registry.register(new Scheme("https", sf, 443));
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
				registry);
		return new DefaultHttpClient(ccm, params);
	}
}
