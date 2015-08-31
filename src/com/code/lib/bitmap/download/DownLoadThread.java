package com.code.lib.bitmap.download;

import java.io.InputStream;
import java.net.HttpURLConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.code.lib.bitmap.ImageUtils;
import com.code.lib.http.ssl.httpclient.URLConnection;

/**
 * 用以去下载指定uri的图片
 * 
 * @author 卢磊 sa613299@mail.ustc.edu.cn
 * 
 */
public class DownLoadThread extends Thread {
	static final int DEFAULT_MAX_NUMOF_PIXELS = 1080 * 1920;
	static final String TAG = "DownLoadThread";
	private LoaderListener mListener;
	// 待下载的图片的uri
	private Uri imgsUrls[];
	// 最大返回 * 大小的图片
	private int mMaxNumOfPixels;

	public DownLoadThread(int mMaxNumOfPixels, LoaderListener listener,
			Uri... uris) {
		if (listener == null) {
			Log.e("DownLoadThread", "listener is null");
			return;
		}
		this.mMaxNumOfPixels = mMaxNumOfPixels;
		if (mMaxNumOfPixels <= 0) {
			mMaxNumOfPixels = DEFAULT_MAX_NUMOF_PIXELS;
		}
		this.mListener = listener;
		this.imgsUrls = uris;
	}

	@Override
	public void run() {
		onPostExecute(doInBackground(imgsUrls));
	}

	private Bitmap[] doInBackground(Uri[] uris) {
		if (uris == null || uris.length < 1) {
			return null;
		}
		Bitmap[] maps = new Bitmap[uris.length];
		for (int i = 0; i < uris.length; i++) {
			InputStream in = getInputStreamFromUri(uris[i]);
			if (in != null) {
				try {
					// 避免bitmap outofmemory exception
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(in, null, opts);
					opts.inSampleSize = ImageUtils.getInSampleSize(opts,
							mMaxNumOfPixels);
					opts.inJustDecodeBounds = false;
					Bitmap bitmap = BitmapFactory.decodeStream(in, null, opts);
					maps[i] = bitmap;
				} catch (Error err) {
					Log.e(TAG, "out of memory err no bitmap found");
					maps[i] = null;
				} finally {
					try {
						in.close();
					} catch (Exception ex) {
						Log.e(TAG, "IO exception " + ex.getMessage());
					}
				}
			} else {
				maps[i] = null;
			}
		}
		return maps;
	}

	private InputStream getInputStreamFromUri(Uri uri) {
		if (uri == null || uri.toString().length() == 0
				|| !ImageUtils.isUrl(uri)) {
			Log.e(TAG, "getInputStreamFromUri function's uri param is error");
			return null;
		}
		InputStream in = null;
		try {
			HttpURLConnection urlConn = URLConnection.getURLConnection(uri
					.toString());
			if (urlConn == null) {
				return null;
			}
			urlConn.setDoInput(true);
			urlConn.connect();
			// 将得到的数据转化成InputStream
			in = urlConn.getInputStream();
		} catch (Exception e) {
			in = null;
			Log.e(TAG, "Uri can't open a inputstream");
		}
		return in;
	}

	protected void onPostExecute(final Bitmap[] result) {

		if (mListener != null) {
			mListener.onComplete(result);
		}
	}

	/**
	 * 当图片下载完成时回调的接口,构造DownLoadThread时应该实例化该接口的子类，并传作为构成函数的参数传入
	 * 
	 * @author 卢磊 sa613299@mail.ustc.edu.cn
	 * 
	 */
	public static interface LoaderListener {
		public abstract void onComplete(Bitmap... bitmap);
	}

}