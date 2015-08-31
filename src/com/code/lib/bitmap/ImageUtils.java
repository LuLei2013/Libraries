package com.code.lib.bitmap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.Context;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * 
 * @author ¬�� sa613299@mail.ustc.edu.cn
 * 
 * @since 2015-08-31
 * 
 */
final public class ImageUtils {

	private static final String NOT_NULL_FMT = "Argument '%s' cannot be null";

	/**
	 * �ж�һ��Uri�Ƿ���һ����Ч��URL����scheme�Ƿ���http��https
	 * 
	 * @param uri
	 *            ������Uri
	 * @return �����һ����Ч��URL���򷵻�true�����򷵻�false
	 */
	public static boolean isUrl(Uri uri) {
		if (uri != null) {
			String scheme = uri.getScheme();
			if (scheme != null
					&& (scheme.equalsIgnoreCase("http") || scheme
							.equalsIgnoreCase("https"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ���㴫����ַ�����md��
	 * 
	 * @param str
	 *            ��������ַ���
	 * @return md��
	 */
	public static String md(String str) {
		notNull(str, "str");
		return md(str.getBytes());
	}

	/**
	 * ���㴫�����������md��
	 * 
	 * @param data
	 *            �������������
	 * @return md��
	 */
	public static String md(byte[] data) {
		notNull(data, "data");
		StringBuilder builder = new StringBuilder();
		try {
			MessageDigest digest = MessageDigest.getInstance("MD");
			digest.update(data);
			byte[] md5 = digest.digest();
			for (int i = 0; i < md5.length; i++) {
				String hex = Integer.toHexString(md5[i] & 0xFF);
				if (hex.length() == 1) {
					builder.append("0");
				}
				builder.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {

		}

		return builder.toString();
	}

	/**
	 * �������������ж�
	 * 
	 * @param context
	 * @return true Ϊ�������������� falseΪ����������
	 */
	public static boolean isNetWorkAvaliable(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// ����������ӣ������������ã��Ͳ���Ҫ��������������
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

	public static void notNull(Object arg, String name) {
		if (arg == null) {
			throw new NullPointerException(String.format(NOT_NULL_FMT, name));
		}
	}

	/**
	 * 
	 * @param opts
	 *            �������ͼƬ��С����ֵ
	 * @param mMaxNumOfPixels2
	 *            ���ص�ͼƬ�����Ĵ�С�ߴ�
	 * @return ���űȣ��磬����ֵΪ2 ���ʾ�����ص�ͼƬ��С������Ϊԭ����1/2��С
	 */
	public static int getInSampleSize(Options options, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int initialSize = (maxNumOfPixels <= 0) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int roundedSize = 1;
		while (roundedSize < initialSize) {
			roundedSize <<= 1;
		}
		return roundedSize;
	}
}