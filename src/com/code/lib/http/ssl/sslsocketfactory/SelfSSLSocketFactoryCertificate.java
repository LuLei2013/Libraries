package com.code.lib.http.ssl.sslsocketfactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;

import com.code.lib.log.Log;

/**
 * 在密钥库中指定 添加密钥
 * 
 * 
 * @author lulei03
 * 
 */
public class SelfSSLSocketFactoryCertificate extends SSLSocketFactory {
	private static final String TAG = "SelfSSLSocketFactoryCertificate";

	private SelfSSLSocketFactoryCertificate(KeyStore keyStore) throws Exception {
		super(keyStore);
	}

	static public SelfSSLSocketFactoryCertificate getInstance(Context context,
			int certificateResourceRawId) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = context.getResources().openRawResource(
					certificateResourceRawId);
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
			} finally {
				caInput.close();
			}
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
			return new SelfSSLSocketFactoryCertificate(keyStore);
		} catch (Throwable e) {
			Log.e(TAG, e);
		}
		return null;
	}

	static public SelfSSLSocketFactoryCertificate getInstance(Context context,
			String certificateAssetsFile) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = context.getAssets().open(
					certificateAssetsFile);
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
			} finally {
				caInput.close();
			}
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
			return new SelfSSLSocketFactoryCertificate(keyStore);
		} catch (Throwable e) {
			Log.e(TAG, e);
		}
		return null;
	}
}
