package com.code.lib.http.ssl.sslsocketfactory;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;

import com.code.lib.log.Log;

/**
 * 
 * 引入自定义的密钥库，需要自己制作密钥库，具体制作过程见readme.txt文件知道说明
 * 
 * @author lulei03
 * 
 */
public class SelfSSLSocketFactoryKeyStore extends SSLSocketFactory {
	private static final String TAG = "SelfSSLSocketFactoryKeyStore";

	private SelfSSLSocketFactoryKeyStore(KeyStore keyStore) throws Exception {
		super(keyStore);
	}

	/**
	 * 
	 * @param context
	 * @param keystoreRawResouceId
	 *            res/raw/ 文件夹下的keystore的资源文件id
	 * @param keyStorePassWord
	 *            与keyStore对应的 password
	 * @return
	 */
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

	/**
	 * 
	 * @param context
	 * @param keystoreAssetsFile
	 *            assets/ 文件夹下的keyStore文件名称
	 * @param keyStorePassWord
	 *            与keyStore对应的 password
	 * @return
	 */
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
