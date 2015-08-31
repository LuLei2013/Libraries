package com.code.lib.http.ssl.sslsocketfactory;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;

import com.code.lib.log.Log;

/**
 * 
 * 
 * @author lulei03
 * 
 */
public class SelfSSLSocketFactoryKeyStore extends SSLSocketFactory {
	private static final String TAG = "SelfSSLSocketFactoryKeyStore";

	private SelfSSLSocketFactoryKeyStore(KeyStore keyStore) throws Exception {
		super(keyStore);
	}

	static public SelfSSLSocketFactoryKeyStore getInstance(Context context,
			int keystoreRawResouceId, String keyStorePassWord) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream ins = context.getResources().openRawResource(
					keystoreRawResouceId);
			keyStore.load(ins, keyStorePassWord.toCharArray());
			ins.close();
			return new SelfSSLSocketFactoryKeyStore(keyStore);
		} catch (Throwable e) {
			Log.e(TAG, e);
		}
		return null;
	}

	static public SelfSSLSocketFactoryKeyStore getInstance(Context context,
			String keystoreAssetsFile, String keyStorePassWord) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream ins = context.getAssets().open(keystoreAssetsFile);
			keyStore.load(ins, keyStorePassWord.toCharArray());
			ins.close();
			return new SelfSSLSocketFactoryKeyStore(keyStore);
		} catch (Throwable e) {
			Log.e(TAG, e);
		}
		return null;
	}

}
