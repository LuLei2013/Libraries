package com.code.lib.http.ssl.sslsocketfactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

import com.code.lib.log.Log;

/**
 * 
 * @author 卢磊 sa613299@mail.ustc.edu.cn
 * 
 *         自动忽略对所有主机的校验
 * 
 */
public class AllowAllSSLSocketFactory extends SSLSocketFactory {
	private static final String TAG = "AllowAllSSLSocketFactory";

	private SSLContext mSSLContext = SSLContext.getInstance("TLS");

	private AllowAllSSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);

		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		mSSLContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return mSSLContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return mSSLContext.getSocketFactory().createSocket();
	}

	public SSLContext getSSLContext() {
		return mSSLContext;
	}

	public static AllowAllSSLSocketFactory getInstance() {
		return Inner.mInstance;
	}

	private static class Inner {
		static KeyStore keyStore;
		private static AllowAllSSLSocketFactory mInstance;
		static {
			try {
				keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				mInstance = new AllowAllSSLSocketFactory(keyStore);
			} catch (Exception e) {
				Log.e(TAG, e);
			}

		}

	}
}
