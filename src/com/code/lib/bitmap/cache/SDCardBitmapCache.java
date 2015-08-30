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
 * @author ¬�� sa613299@mail.ustc.edu.cn
 * 
 * @since 2015-08-30
 * 
 *        �ڴ洢����ͼƬ����,ÿ�λ��浽�洢����ͼƬ����������ʱ���������ص��ڴ��У�
 * 
 *        ����ͳ�Ƶ����д���Ҳ����Ӧ��ÿ���������������������Ч��
 * 
 *        ͬʱ����Ϊ�Ǵ洢�����棬���Բ�û�а�ͼƬ�浽�ڴ��У���ֻ�����ڴ��л������ļ�����
 * 
 */
class SDCardBitmapCache implements IBitmapCache {
	private static final String TAG = "IBitmapCache";
	private static final String DEFAULT_STORAGE_DIR = "pic_cache_path";
	private static final int DEFAULT_HIT_COUNT_REQUIRED = 10;
	private static final int DEFAULT_MAX_NUMOF_PIXELS = 1080 * 1920;

	/**
	 * �ļ�����ı���·��
	 */
	private String mStorageDir = DEFAULT_STORAGE_DIR;

	/**
	 * �ļ��������г���һ������ʱ��������Ӧ���ڴ滺��
	 */
	private IBitmapCache mMemCache;

	/**
	 * �ļ��������г������ٴκ�����ڴ滺��
	 */
	private int mHitCountRequired = DEFAULT_HIT_COUNT_REQUIRED;

	/**
	 * ���ͼƬ��pixel��С
	 */
	private int mMaxNumOfPixels = DEFAULT_MAX_NUMOF_PIXELS;

	/**
	 * �ļ�����������д���������
	 */
	private Map<String, Integer> mMap = new HashMap<String, Integer>();

	/**
	 * ���캯��
	 * 
	 * @param storageDir
	 *            �ļ�����ı���·��
	 * @param hitCountRequired
	 *            �ļ��������д���������ֵʱ�������ڴ漶����
	 * @param maxNumOfPixels
	 *            �ɶ�ȡͼƬ�����������
	 * @param memCache
	 *            �ڴ漶������
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
	 * ��������ȡ��ͼƬ����ֵ
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
	 * �����ļ���������ж��ٴκ�ſ��Խ����ڴ漶����
	 * 
	 * @param hitCountRequired
	 *            �ļ��������д���������ֵ���ܽ����ڴ漶����
	 * @return ��ǰ����
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
		// ����ڴ滺���д���ͼƬ����ֱ�Ӷ�ȡ��
		if (mMemCache != null) {
			Bitmap pic = mMemCache.get(key);
			if (pic != null) {
				return pic;
			}
		}
		// ���� ���ļ�ϵͳ��ȡ
		String bitmapPath = getFilePath(key);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapPath, opts);
		// ����ͼƬ�Ĵ�С������Ӧ���ͼƬ��С�����ͼƬ�����ű�
		opts.inSampleSize = getInSampleSize(opts, mMaxNumOfPixels);
		opts.inJustDecodeBounds = false;
		Bitmap data = BitmapFactory.decodeFile(bitmapPath, opts);
		if (data != null) {
			Integer value = mMap.get(key);
			if (value == null) {
				value = 0;
			}
			mMap.put(key, value + 1);
			// ���д�������һ����ֵ�������ڴ漶����
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
			// �����ͬ�����ļ����±������£�����ʴ�����Ӧ���޸�
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
		// �ȴ��ڴ��ڻ���ɾ��
		if (mMemCache != null) {
			mMemCache.delete(key);
		}
		// �ٴ��ļ�ϵͳ��ɾ��
		mMap.remove(key);
		File file = new File(getFilePath(key));
		file.delete();
	}

	@Override
	public synchronized boolean exists(String key) {
		// �Ȳ鿴�ڴ滺�����Ƿ����
		if (mMemCache != null) {
			if (mMemCache.exists(key)) {
				return true;
			}
		}
		// �ٲ鿴�ļ�ϵͳ���Ƿ����
		if (mMap.get(key) != null) {
			return true;
		}
		File file = new File(getFilePath(key));
		return file.exists();
	}

	@Override
	public synchronized void clean() {
		// ������ڴ滺��
		if (mMemCache != null) {
			mMemCache.clean();
		}
		// ����ļ�ϵͳ�Ļ���
		for (String key : mMap.keySet()) {
			delete(key);
		}
		mMap.clear();
	}

	/**
	 * �����ļ��������컺�����ļ�ϵͳ�ľ���·��
	 * 
	 * @param key
	 *            ָ����ͼƬ����
	 * @return �ļ�ϵͳ��ָ���ļ���Ϊkey�ľ��Դ洢·��
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
	 *            �������ͼƬ��С����ֵ
	 * @param mMaxNumOfPixels2
	 *            ���ص�ͼƬ�����Ĵ�С�ߴ�
	 * @return ���űȣ��磬����ֵΪ2 ���ʾ�����ص�ͼƬ��С������Ϊԭ����1/2��С
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
