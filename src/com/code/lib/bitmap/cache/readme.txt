1、 需要声明读取存储卡的权限
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
2、用户默认使用ImageManager类提供的接口即可
3、当使用SDCardBitmapCache 和MemoryBitmapCache时传入的参数如果为null或者-1，则会使用默认的参数
4、MemoryBitmapCache 提供了两种LRU和LFU策略
5、默认命中率低于最大不超过Integet.MAX_VALUE,如果大于则算法失败