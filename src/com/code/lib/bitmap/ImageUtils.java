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
 * @author 卢磊 sa613299@mail.ustc.edu.cn
 * 
 * @since 2015-08-31
 * 
 */
final public class ImageUtils {

	private static final String NOT_NULL_FMT = "Argument '%s' cannot be null";

	/**
	 * 判断一个Uri是否是一个有效的URL，即scheme是否是http或https
	 * 
	 * @param uri
	 *            待检测的Uri
	 * @return 如果是一个有效的URL，则返回true，否则返回false
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
	 * 计算传入的字符串的md串
	 * 
	 * @param str
	 *            待计算的字符串
	 * @return md串
	 */
	public static String md(String str) {
		notNull(str, "str");
		return md(str.getBytes());
	}

	/**
	 * 计算传入的数据流的md串
	 * 
	 * @param data
	 *            待计算的数据流
	 * @return md串
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
	 * 有无网络连接判断
	 * 
	 * @param context
	 * @return true 为网络连接正常， false为无网络连接
	 */
	public static boolean isNetWorkAvaliable(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 检查网络连接，如果无网络可用，就不需要进行连网操作等
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
	 *            待计算的图片大小参数值
	 * @param mMaxNumOfPixels2
	 *            返回的图片的最大的大小尺寸
	 * @return 缩放比，如，返回值为2 这表示，返回的图片大小被缩放为原来的1/2大小
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