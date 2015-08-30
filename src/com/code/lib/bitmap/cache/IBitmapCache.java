package com.code.lib.bitmap.cache;

import android.graphics.Bitmap;

/**
 * 图片数据缓存接口
 * 
 */
interface IBitmapCache {

	/**
	 * 获取指定key所对应的图片缓存数据
	 * 
	 * @param key
	 *            缓存数据所对应的key
	 * @return 如果key存在，则返回对应的Bitmap数据，否则返回null
	 */
	public Bitmap get(String key);

	/**
	 * 将传入的Bitmap数据按照指定的key存入缓存
	 * 
	 * @param key
	 *            缓存项对应的key
	 * @param data
	 *            Bitmap数据
	 */
	public void put(String key, Bitmap data);

	/**
	 * 删除指定key所对应的缓存数据，并释放缓存数据所对应的资源
	 * 
	 * @param key
	 *            缓存数据所对应的key
	 */
	public void delete(String key);

	/**
	 * 判断是否能够命中缓存
	 * 
	 * @param key
	 *            缓存数据所对应的key
	 * @return 如果命中缓存，则返回true，否则返回false
	 */
	public boolean exists(String key);

	/**
	 * 清除缓存
	 */
	public void clean();
}