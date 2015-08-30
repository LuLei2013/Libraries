package com.code.lib.bitmap.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.code.lib.log.Log;

/**
 * 
 * @author 卢磊 sa613299@mail.ustc.edu.cn
 * 
 * @since 2015-08-30
 * 
 *        在存储进行图片缓存,每次缓存到存储卡的图片不会在启动时被立即加载到内存中，
 * 
 *        这里统计的命中次数也是在应用每次启动后的生命周期内有效。
 * 
 *        同时，因为是存储卡缓存，所以并没有把图片存到内存中，而只是在内存中缓存了文件名。
 * 
 */
class SDCardBitmapCache implements IBitmapCache {
	private static final String TAG = "IBitmapCache";
	private static final String DEFAULT_STORAGE_DIR = "pic_cache_path";
	private static final int DEFAULT_HIT_COUNT_REQUIRED = 10;
	private static final int DEFAULT_MAX_NUMOF_PIXELS = 1080 * 1920;

	/**
	 * 文件缓存的保存路径
	 */
	private String mStorageDir = DEFAULT_STORAGE_DIR;

	/**
	 * 文件缓存命中超过一定次数时，进入相应的内存缓存
	 */
	private IBitmapCache mMemCache;

	/**
	 * 文件缓存命中超过多少次后进入内存缓存
	 */
	private int mHitCountRequired = DEFAULT_HIT_COUNT_REQUIRED;

	/**
	 * 最大图片的pixel大小
	 */
	private int mMaxNumOfPixels = DEFAULT_MAX_NUMOF_PIXELS;

	/**
	 * 文件缓存访问命中次数管理器
	 */
	private Map<String, Integer> mMap = new HashMap<String, Integer>();

	/**
	 * 构造函数
	 * 
	 * @param storageDir
	 *            文件缓存的保存路径
	 * @param hitCountRequired
	 *            文件缓存命中次数超过该值时将进入内存级缓存
	 * @param maxNumOfPixels
	 *            可读取图片的最大像素数
	 * @param memCache
	 *            内存级缓存句柄
	 */
	public SDCardBitmapCache(String storageDir, int hitCountRequired,
			int maxNumOfPixels, IBitmapCache memCache) {
		mStorageDir = storageDir;
		mMemCache = memCache;
		setHitCountRequired(hitCountRequired);
		setMaxNumOfPixels(maxNumOfPixels);
		if (mStorageDir == null) {
			mStorageDir = DEFAULT_STORAGE_DIR;
		}
	}

	/**
	 * 设置最大读取的图片像素值
	 * 
	 * @param maxPixels
	 * @return
	 */
	public SDCardBitmapCache setMaxNumOfPixels(int maxPixels) {
		mMaxNumOfPixels = maxPixels;
		if (mMaxNumOfPixels <= 0) {
			mMaxNumOfPixels = DEFAULT_MAX_NUMOF_PIXELS;
		}
		return this;
	}

	/**
	 * 设置文件缓存项被命中多少次后才可以进入内存级缓存
	 * 
	 * @param hitCountRequired
	 *            文件缓存命中次数超过该值才能进入内存级缓存
	 * @return 当前对象
	 */
	public SDCardBitmapCache setHitCountRequired(int hitCountRequired) {
		mHitCountRequired = hitCountRequired;
		if (mHitCountRequired <= 0) {
			mHitCountRequired = DEFAULT_HIT_COUNT_REQUIRED;
		}
		return this;
	}

	@Override
	public synchronized Bitmap get(String key) {
		// 如果内存缓存中存在图片，则直接读取出
		if (mMemCache != null) {
			Bitmap pic = mMemCache.get(key);
			if (pic != null) {
				return pic;
			}
		}
		// 否则， 从文件系统读取
		String bitmapPath = getFilePath(key);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapPath, opts);
		// 根据图片的大小和最大的应许的图片大小计算出图片的缩放比
		opts.inSampleSize = getInSampleSize(opts, mMaxNumOfPixels);
		opts.inJustDecodeBounds = false;
		Bitmap data = BitmapFactory.decodeFile(bitmapPath, opts);
		if (data != null) {
			Integer value = mMap.get(key);
			if (value == null) {
				value = 0;
			}
			mMap.put(key, value + 1);
			// 命中次数超过一定阀值，进入内存级缓存
			if (value + 1 >= mHitCountRequired) {
				if (mMemCache != null) {
					mMemCache.put(key, data);
				}
			}
			return data;
		} else {
			return null;
		}
	}

	@Override
	public synchronized void put(String key, Bitmap data) {
		File file = new File(getFilePath(key));
		File parentFile = file.getParentFile();
		if (parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			data.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			// 如果是同名的文件重新保存了下，则访问次数不应该修改
			Integer count = mMap.get(key);
			if (count == null) {
				mMap.put(key, 1);
			} else {
				mMap.put(key, count);
			}
		} catch (Exception e) {
			Log.e(TAG, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					Log.e(TAG, e);
				}
			}
		}
	}

	@Override
	public synchronized void delete(String key) {
		// 先从内存内缓中删除
		if (mMemCache != null) {
			mMemCache.delete(key);
		}
		// 再从文件系统中删除
		mMap.remove(key);
		File file = new File(getFilePath(key));
		file.delete();
	}

	@Override
	public synchronized boolean exists(String key) {
		// 先查看内存缓存中是否存在
		if (mMemCache != null) {
			if (mMemCache.exists(key)) {
				return true;
			}
		}
		// 再查看文件系统中是否存在
		if (mMap.get(key) != null) {
			return true;
		}
		File file = new File(getFilePath(key));
		return file.exists();
	}

	@Override
	public synchronized void clean() {
		// 先清空内存缓存
		if (mMemCache != null) {
			mMemCache.clean();
		}
		// 情况文件系统的缓存
		for (String key : mMap.keySet()) {
			delete(key);
		}
		mMap.clear();
	}

	/**
	 * 根据文件名，构造缓存中文件系统的绝对路径
	 * 
	 * @param key
	 *            指定的图片名称
	 * @return 文件系统中指定文件名为key的绝对存储路劲
	 */
	private String getFilePath(String key) {
		String sdCardPath = android.os.Environment
				.getExternalStorageDirectory().getAbsolutePath();
		return new StringBuilder(sdCardPath).append(File.separator)
				.append(mStorageDir).append(File.separator).append(key)
				.append(".png").toString();
	}

	/**
	 * 
	 * @param opts
	 *            待计算的图片大小参数值
	 * @param mMaxNumOfPixels2
	 *            返回的图片的最大的大小尺寸
	 * @return 缩放比，如，返回值为2 这表示，返回的图片大小被缩放为原来的1/2大小
	 */
	private int getInSampleSize(Options options, int maxNumOfPixels) {
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
