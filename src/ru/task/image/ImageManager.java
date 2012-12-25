package ru.task.image;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import ru.task.ui.TwittArrayAdapter;
import ru.task.utils.HttpSocket;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 30.11.12
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class ImageManager extends HttpSocket<Bitmap> {
    private static final String TAG = "ImageManager";
    private static final int FADE_IN_TIME = 200;

    private ImageCache mImageCache;
    private Bitmap mLoadingBitmap;
    private boolean mFadeInBitmap = true;

    private final Resources mResources;


    public ImageManager(Context context) {
        super((Activity) context);
        mResources = context.getResources();
    }


    public void loadImage(Object data, TwittArrayAdapter.ViewHolder holder) {
        if (data == null) {
            return;
        }
        Bitmap bitmap = null;

        if (mImageCache != null) {
            bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }

        if (bitmap != null) {
            holder.addHttpResult(bitmap, 0);
        } else if (cancelPotentialWork(data, holder.getIconView())) {
            final HttpSocketAsyncTask task = new HttpSocketAsyncTask<String, Void, Bitmap>(holder);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
            holder.getIconView().setImageDrawable(asyncDrawable);
            task.execute(data);
        }
    }

    public void addImageCache(ImageCache.ImageCacheParams cacheParams) {
        ImageCache imageCache = new ImageCache(cacheParams);
        setImageCache(imageCache);
    }

    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    private static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final HttpSocketAsyncTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.getData();
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static HttpSocketAsyncTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    @Override
    protected Bitmap readerInputStream(InputStream is, HttpSocketAsyncTask httpSocketAsyncTask) throws IOException {
        Object imageObject = httpSocketAsyncTask.getAttachedObjectForResult();
        if (imageObject != null) {
            Bitmap bitmap = null;
            ImageView imageView = ((TwittArrayAdapter.ViewHolder) imageObject).getIconView();
            final HttpSocketAsyncTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (bitmapWorkerTask == httpSocketAsyncTask) {
                bitmap = BitmapFactory.decodeStream(is);
            }

            if (bitmap != null && mImageCache != null) {
                mImageCache.addBitmapToCache((String) httpSocketAsyncTask.getData(), bitmap);
            }
            return bitmap;
        } else {
            httpSocketAsyncTask.cancel(true);
        }
        return null;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<HttpSocketAsyncTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, HttpSocketAsyncTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<HttpSocketAsyncTask>(bitmapWorkerTask);
        }

        public HttpSocketAsyncTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}

