package com.code.lib.http.ssl.sslsocketfactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;

import com.code.lib.log.Log;

/**
 * 在密钥库中指定 添加密证书
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

	/**
	 * 
	 * @param context
	 * @param certificateResourceRawId
	 *            证书在res/raw/ 路径下的资源id号，因该路径下文件不会被压缩
	 * @param alias
	 *            证书实体的别名
	 * @return
	 */
	static public SelfSSLSocketFactoryCertificate getInstance(Context context,
			int certificateResourceRawId, String alias) {
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
			keyStore.setCertificateEntry(alias, ca);
			return new SelfSSLSocketFactoryCertificate(keyStore);
		} catch (Throwable e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @param certificateAssetsFile
	 *            证书在assets/ 路径下的文件名称，该路径下文件不会被压缩
	 * @param alias
	 *            证书实体的别名
	 * @return
	 */
	static public SelfSSLSocketFactoryCertificate getInstance(Context context,
			String certificateAssetsFile, String alias) {
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
			keyStore.setCertificateEntry(alias, ca);
			return new SelfSSLSocketFactoryCertificate(keyStore);
		} catch (Throwable e) {
			Log.e(TAG, e);
		}
		return null;
	}
}
