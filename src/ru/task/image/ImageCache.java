package ru.task.image;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 30.11.12
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class ImageCache {
    private static final String TAG = "ImageCache";

    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageCache(ImageCacheParams cacheParams) {
        init(cacheParams);
    }

    private void init(ImageCacheParams cacheParams) {
        ImageCacheParams mCacheParams = cacheParams;

        if (mCacheParams.memoryCacheEnabled) {

            mMemoryCache = new LruCache<String, Bitmap>(mCacheParams.memCacheSize) {

                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }

    }

    public void addBitmapToCache(String data, Bitmap bitmap) {
        if (data == null || bitmap == null) {
            return;
        }

        if (mMemoryCache != null && mMemoryCache.get(data) == null) {
            mMemoryCache.put(data, bitmap);
        }

    }


    public Bitmap getBitmapFromMemCache(String data) {
        if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(data);
            if (memBitmap != null) {
                return memBitmap;
            }
        }
        return null;
    }

    public static class ImageCacheParams {
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public final boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;


        public ImageCacheParams(Context context, float percent) {
            setMemCacheSizePercent(context, percent);
        }

        public void setMemCacheSizePercent(Context context, float percent) {
            if (percent < 0.05f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.05 and 0.8 (inclusive)");
            }
            memCacheSize = Math.round(percent * getMemoryClass(context) * 1024 * 1024);
        }

        private static int getMemoryClass(Context context) {
            return ((ActivityManager) context.getSystemService(
                    Context.ACTIVITY_SERVICE)).getMemoryClass();
        }
    }
}
