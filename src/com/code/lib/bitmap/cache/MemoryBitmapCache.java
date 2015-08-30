package com.code.lib.bitmap.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * @author 卢磊 sa613299@mail.ustc.edu.cn
 * 
 * @since 2015-08-30
 * 
 *        在内存对图片进行缓存
 * 
 */
class MemoryBitmapCache implements IBitmapCache {
	private static final int DEFAULT_MAX_SIZE = 100;
	private static final IEvictPolicy DEFAULT_EVENT_POLICY = new LRUPolicy();

	private static class CacheEntry {
		/**
		 * 图片数据
		 */
		public SoftReference<Bitmap> mImage;

		/**
		 * cache项命中次数
		 */
		public int mHits;

		/**
		 * 最后一次访问时间
		 */
		public long mTimeStamp;
	}

	/*
	 * 缓存项数目上限
	 */
	private int mMaxSize;

	/*
	 * 缓存淘汰策略
	 */
	private IEvictPolicy mEvictPolicy;

	/*
	 * 缓存空间
	 */
	private Map<String, CacheEntry> mMap = new HashMap<String, CacheEntry>();

	/**
	 * 构造函数
	 * 
	 * @param maxSize
	 *            缓存项最大数目
	 */
	public MemoryBitmapCache(int maxSize) {
		this(maxSize, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param maxSize
	 *            缓存项最大数目
	 * @param evictPolicy
	 *            缓存淘汰策略
	 */
	public MemoryBitmapCache(int maxSize, IEvictPolicy evictPolicy) {
		mMaxSize = maxSize;
		mEvictPolicy = evictPolicy;
		setMaxSize(maxSize);
		setEvictPolicy(evictPolicy);
	}

	/**
	 * 设置缓存项数目上限
	 * 
	 * @param maxSize
	 *            缓存项数目上限
	 * @return 当前对象
	 */
	public MemoryBitmapCache setMaxSize(int maxSize) {
		mMaxSize = maxSize;
		if (maxSize <= 0) {
			maxSize = DEFAULT_MAX_SIZE;
		}
		return this;
	}

	/**
	 * 设置缓存淘汰策略
	 * 
	 * @param policy
	 *            缓存淘汰策略
	 * @return 当前对象
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
				// 缓存的图片对象已经被垃圾收集器回收，则应释放
				delete(key);
			}
			return entry.mImage.get();
		}
		return null;
	}

	@Override
	public synchronized void put(String key, Bitmap data) {
		// 先做一次弱引用检查
		compress();
		// 如果缓存项超过预设上限，则根据预设的淘汰策略淘汰掉一个缓存项
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
	 * 对 mMap做一次弱引用有效检查，如果引用的Bitmap已被被回收，则应回收器在mMap中的Entry
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
	 * 淘汰最长时间未被访问的Entry
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
		 * 默认时间窗口为 1天
		 */
		private long mRecentTimeRange = 24 * 60 * 60 * 1000 * 1000;

		public LFUPolicy(long recentTimeRange) {
			mRecentTimeRange = recentTimeRange;
		}

		@Override
		public String findItemToDelete(Map<String, CacheEntry> map) {
			CacheEntry evictEntry = null;
			String evictKey = null;
			// 记录是否还没有找到一个在时间窗口外的待淘汰的Entry
			boolean isInDuration = true;
			for (String key : map.keySet()) {
				CacheEntry entry = map.get(key);
				long now = System.currentTimeMillis();
				if (now - entry.mTimeStamp < mRecentTimeRange) {
					// 在最近时间范围内，优先淘汰使用次数最少的
					if (isInDuration
							&& (evictEntry == null || entry.mHits < evictEntry.mHits)) {
						evictEntry = entry;
						evictKey = key;
					}
				} else {
					// 有在最近时间范围外的缓存项时，优先淘汰时间最早的
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
				// 最近没使用过
				entry.mHits = 0;
			} else {
				entry.mHits++;
			}
			entry.mTimeStamp = System.currentTimeMillis();
		}
	}
}
