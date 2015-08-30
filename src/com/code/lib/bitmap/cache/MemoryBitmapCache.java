package com.code.lib.bitmap.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * @author ¬�� sa613299@mail.ustc.edu.cn
 * 
 * @since 2015-08-30
 * 
 *        ���ڴ��ͼƬ���л���
 * 
 */
class MemoryBitmapCache implements IBitmapCache {
	private static final int DEFAULT_MAX_SIZE = 100;
	private static final IEvictPolicy DEFAULT_EVENT_POLICY = new LRUPolicy();

	private static class CacheEntry {
		/**
		 * ͼƬ����
		 */
		public SoftReference<Bitmap> mImage;

		/**
		 * cache�����д���
		 */
		public int mHits;

		/**
		 * ���һ�η���ʱ��
		 */
		public long mTimeStamp;
	}

	/*
	 * ��������Ŀ����
	 */
	private int mMaxSize;

	/*
	 * ������̭����
	 */
	private IEvictPolicy mEvictPolicy;

	/*
	 * ����ռ�
	 */
	private Map<String, CacheEntry> mMap = new HashMap<String, CacheEntry>();

	/**
	 * ���캯��
	 * 
	 * @param maxSize
	 *            �����������Ŀ
	 */
	public MemoryBitmapCache(int maxSize) {
		this(maxSize, null);
	}

	/**
	 * ���캯��
	 * 
	 * @param maxSize
	 *            �����������Ŀ
	 * @param evictPolicy
	 *            ������̭����
	 */
	public MemoryBitmapCache(int maxSize, IEvictPolicy evictPolicy) {
		mMaxSize = maxSize;
		mEvictPolicy = evictPolicy;
		setMaxSize(maxSize);
		setEvictPolicy(evictPolicy);
	}

	/**
	 * ���û�������Ŀ����
	 * 
	 * @param maxSize
	 *            ��������Ŀ����
	 * @return ��ǰ����
	 */
	public MemoryBitmapCache setMaxSize(int maxSize) {
		mMaxSize = maxSize;
		if (maxSize <= 0) {
			maxSize = DEFAULT_MAX_SIZE;
		}
		return this;
	}

	/**
	 * ���û�����̭����
	 * 
	 * @param policy
	 *            ������̭����
	 * @return ��ǰ����
	 */
	public MemoryBitmapCache setEvictPolicy(IEvictPolicy policy) {
		mEvictPolicy = policy;
		if (mEvictPolicy == null) {
			mEvictPolicy = DEFAULT_EVENT_POLICY;
		}
		return this;
	}

	@Override
	public synchronized Bitmap get(String key) {
		CacheEntry entry = mMap.get(key);
		if (entry != null && entry.mImage != null) {
			Bitmap bitmap = entry.mImage.get();
			if (bitmap != null) {
				mEvictPolicy.updateCacheItem(entry);
			} else {
				// �����ͼƬ�����Ѿ��������ռ������գ���Ӧ�ͷ�
				delete(key);
			}
			return entry.mImage.get();
		}
		return null;
	}

	@Override
	public synchronized void put(String key, Bitmap data) {
		// ����һ�������ü��
		compress();
		// ����������Ԥ�����ޣ������Ԥ�����̭������̭��һ��������
		if (mMap.size() >= mMaxSize) {
			String outkey = mEvictPolicy.findItemToDelete(mMap);
			delete(outkey);
		}
		CacheEntry entry = mMap.get(key);
		if (entry != null && entry.mImage != null) {
			Bitmap bitmap = entry.mImage.get();
			if (bitmap != null) {
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		} else {
			entry = new CacheEntry();
			entry.mHits = 1;
		}
		entry.mTimeStamp = System.currentTimeMillis();
		entry.mImage = new SoftReference<Bitmap>(data);
		mMap.put(key, entry);
	}

	@Override
	public synchronized void delete(String key) {
		CacheEntry entry = mMap.remove(key);
		if (entry != null && entry.mImage != null) {
			Bitmap image = entry.mImage.get();
			if (image != null && !image.isRecycled()) {
				image.recycle();
			}
		}
		mMap.remove(key);
	}

	@Override
	public synchronized boolean exists(String key) {
		CacheEntry entry = mMap.get(key);
		if (entry == null || entry.mImage == null) {
			return false;
		}
		Bitmap bitmap = entry.mImage.get();
		return bitmap != null;
	}

	@Override
	public synchronized void clean() {
		Iterator<String> iterator = mMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			CacheEntry entry = mMap.get(key);
			if (entry != null && entry.mImage != null) {
				Bitmap image = entry.mImage.get();
				if (image != null && !image.isRecycled()) {
					image.recycle();
				}
			}
			iterator.remove();
		}
	}

	/**
	 * �� mMap��һ����������Ч��飬������õ�Bitmap�ѱ������գ���Ӧ��������mMap�е�Entry
	 */
	private synchronized void compress() {
		Iterator<String> iterator = mMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			CacheEntry entry = mMap.get(key);
			if (entry != null && entry.mImage != null) {
				Bitmap image = entry.mImage.get();
				if (image == null) {
					iterator.remove();
				}
			}

		}
	}

	public static interface IEvictPolicy {
		public static final int LRU = 0;
		public static final int LFU = 1;

		String findItemToDelete(Map<String, CacheEntry> map);

		void updateCacheItem(CacheEntry entry);
	}

	/*
	 * ��̭�ʱ��δ�����ʵ�Entry
	 */
	public static class LRUPolicy implements IEvictPolicy {

		@Override
		public String findItemToDelete(Map<String, CacheEntry> map) {
			CacheEntry oldest = null;
			String evictKey = null;
			for (String key : map.keySet()) {
				CacheEntry entry = map.get(key);
				if (oldest == null || entry.mTimeStamp < oldest.mTimeStamp) {
					oldest = entry;
					evictKey = key;
				}
			}
			return evictKey;
		}

		@Override
		public void updateCacheItem(CacheEntry entry) {
			entry.mHits++;
			entry.mTimeStamp = System.currentTimeMillis();
		}
	}

	/*
 * 
 */
	public static class LFUPolicy implements IEvictPolicy {
		/*
		 * Ĭ��ʱ�䴰��Ϊ 1��
		 */
		private long mRecentTimeRange = 24 * 60 * 60 * 1000 * 1000;

		public LFUPolicy(long recentTimeRange) {
			mRecentTimeRange = recentTimeRange;
		}

		@Override
		public String findItemToDelete(Map<String, CacheEntry> map) {
			CacheEntry evictEntry = null;
			String evictKey = null;
			// ��¼�Ƿ�û���ҵ�һ����ʱ�䴰����Ĵ���̭��Entry
			boolean isInDuration = true;
			for (String key : map.keySet()) {
				CacheEntry entry = map.get(key);
				long now = System.currentTimeMillis();
				if (now - entry.mTimeStamp < mRecentTimeRange) {
					// �����ʱ�䷶Χ�ڣ�������̭ʹ�ô������ٵ�
					if (isInDuration
							&& (evictEntry == null || entry.mHits < evictEntry.mHits)) {
						evictEntry = entry;
						evictKey = key;
					}
				} else {
					// �������ʱ�䷶Χ��Ļ�����ʱ��������̭ʱ�������
					if (evictEntry == null
							|| entry.mTimeStamp < evictEntry.mTimeStamp) {
						evictEntry = entry;
						evictKey = key;
						isInDuration = false;
					}
				}
			}
			return evictKey;
		}

		@Override
		public void updateCacheItem(CacheEntry entry) {
			long now = System.currentTimeMillis();
			if (entry.mTimeStamp + mRecentTimeRange < now) {
				// ���ûʹ�ù�
				entry.mHits = 0;
			} else {
				entry.mHits++;
			}
			entry.mTimeStamp = System.currentTimeMillis();
		}
	}
}
