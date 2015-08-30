package com.code.lib.bitmap.cache;

import android.graphics.Bitmap;

/**
 * ͼƬ���ݻ���ӿ�
 * 
 */
interface IBitmapCache {

	/**
	 * ��ȡָ��key����Ӧ��ͼƬ��������
	 * 
	 * @param key
	 *            ������������Ӧ��key
	 * @return ���key���ڣ��򷵻ض�Ӧ��Bitmap���ݣ����򷵻�null
	 */
	public Bitmap get(String key);

	/**
	 * �������Bitmap���ݰ���ָ����key���뻺��
	 * 
	 * @param key
	 *            �������Ӧ��key
	 * @param data
	 *            Bitmap����
	 */
	public void put(String key, Bitmap data);

	/**
	 * ɾ��ָ��key����Ӧ�Ļ������ݣ����ͷŻ�����������Ӧ����Դ
	 * 
	 * @param key
	 *            ������������Ӧ��key
	 */
	public void delete(String key);

	/**
	 * �ж��Ƿ��ܹ����л���
	 * 
	 * @param key
	 *            ������������Ӧ��key
	 * @return ������л��棬�򷵻�true�����򷵻�false
	 */
	public boolean exists(String key);

	/**
	 * �������
	 */
	public void clean();
}