package com.code.lib.bitmap.cache;

import android.graphics.Bitmap;
import android.text.TextUtils;

public class ImageManager {

	private SDCardBitmapCache mCacheBitmap;

	private ImageManager() {
		mCacheBitmap = new SDCardBitmapCache(null, -1, -1,
				new MemoryBitmapCache(-1));
	}

	public void put(String name, Bitmap bitmap) {
		if (TextUtils.isEmpty(name)) {
			return;
		}
		mCacheBitmap.put(name, bitmap);
	}

	public Bitmap get(String name) {
		if (TextUtils.isEmpty(name)) {
			return null;
		}
		return mCacheBitmap.get(name);
	}

	public boolean exists(String name) {
		if (TextUtils.isEmpty(name)) {
			return false;
		}
		return mCacheBitmap.exists(name);
	}

	public static ImageManager getInstance() {
		return InnerImageManager.mImageManager;
	}

	private static class InnerImageManager {
		static private ImageManager mImageManager = new ImageManager();
	}
}
