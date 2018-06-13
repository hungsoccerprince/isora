package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.UiThread;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity;
import com.etiennelawlor.imagegallery.library.adapters.FullScreenImageGalleryAdapter;
import com.google.gson.JsonElement;
import com.isorasoft.mllibrary.constants.ServerConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by MaiNam on 5/12/2016.
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    static int scaleSize = 512;

    public static Bitmap resizeImageForImageView(Bitmap bitmap) {
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if (originalHeight > originalWidth) {
            newHeight = scaleSize;
            multFactor = (float) originalWidth / (float) originalHeight;
            newWidth = (int) (newHeight * multFactor);
        } else if (originalWidth > originalHeight) {
            newWidth = scaleSize;
            multFactor = (float) originalHeight / (float) originalWidth;
            newHeight = (int) (newWidth * multFactor);
        } else if (originalHeight == originalWidth) {
            newHeight = scaleSize;
            newWidth = scaleSize;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }

    public static String toBase64(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            bitmap = resizeImageForImageView(bitmap);
            return toBase64(context, bitmap);
        } catch (IOException e) {
            return "";
        } finally {
            if (bitmap != null)
                bitmap.recycle();
        }
    }

    public static String toBase64(Context context, Bitmap bitmap) {
        try {
            bitmap = resizeImageForImageView(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object

            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT | Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static class     Size {
        private int height;
        private int width;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @UiThread
    public static void loadImageNotUseGlide(ImageView imageView, @DrawableRes int resId) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageResource(resId);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public static void loadImage(ImageView imageView, @DrawableRes int resId) {
        try {
            Log.d(TAG, "loadImage: ");
            DrawableRequestBuilder builder = Glide.with(imageView.getContext()).load(resId).dontAnimate();
            builder.fitCenter().listener(new RequestListener<Integer, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Integer model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Integer model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public static void loadImage(Activity context, @IdRes int imageViewId, @DrawableRes int resId) {
        try {
            loadImage((ImageView) context.findViewById(imageViewId), resId);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    @UiThread
    public static void loadImageGif(Activity context, @IdRes int imageViewId, @RawRes int resId) {
        try {
            Glide.with(context).load(resId).asGif().dontAnimate().into((ImageView) context.findViewById(imageViewId));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    @UiThread
    public static void loadImage(View view, @IdRes int imageViewId, @DrawableRes int resId) {
        try {
            DrawableRequestBuilder builder = Glide.with(view.getContext()).load(resId).dontAnimate();
            builder.into((ImageView) view.findViewById(imageViewId));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    @UiThread
    public static void loadImageByGlide(ImageView imageView, boolean isResize, int with, int height, @NonNull String url, @DrawableRes int errorResource, @DrawableRes int placeHolder, boolean isCache) {
        try {
            if (url == null || url.isEmpty()) {
                imageView.setImageResource(placeHolder);
                return;
            }
            Log.d(TAG, "loadImageByGlide: " + imageView.getContext());

            glideCreateRequest(url, imageView.getContext(), placeHolder, errorResource, isResize, with, height, isCache).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    static DrawableTypeRequest glideCreateRequestUri(DrawableTypeRequest builder, @DrawableRes int placeHolder, @DrawableRes int errorResource, boolean isResize, int width, int height, boolean isCache) {
        builder.placeholder(placeHolder).crossFade(2000).dontAnimate().error(errorResource);
        if (isResize) {
            builder.override(width, height).centerCrop();
        }
        if (isCache) {
            builder.diskCacheStrategy(DiskCacheStrategy.ALL);
        } else {
            builder.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        builder.listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        });
        return builder;
    }
    static DrawableTypeRequest glideCreateRequest(DrawableTypeRequest builder, @DrawableRes int placeHolder, @DrawableRes int errorResource, boolean isResize, int width, int height, boolean isCache) {
        builder.placeholder(placeHolder).crossFade(2000).dontAnimate().error(errorResource);
        if (isResize) {
            builder.override(width, height).centerCrop();
        }
        if (isCache) {
            builder.diskCacheStrategy(DiskCacheStrategy.ALL);
        } else {
            builder.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        builder.listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.d(TAG, "onResourceReady");
                return false;
            }
        });
        return builder;
    }

    static DrawableRequestBuilder glideCreateRequest(Uri uri, Context context, @DrawableRes int placeHolder, @DrawableRes int errorResource, boolean isResize, int width, int height, boolean isCache) {
        return glideCreateRequestUri(Glide.with(context).load(uri), placeHolder, errorResource, isResize, width, height, isCache);
    }

    static DrawableTypeRequest glideCreateRequest(String url, Context context, @DrawableRes int placeHolder, @DrawableRes int errorResource, boolean isResize, int width, int height, boolean isCache) {
        url = url == null ? "" : url;
        url = ClientUtils.getLink(url);
        Log.d(TAG, "loadImageByGlide: " + url);

        if (!url.contains(ServerConstants.HTTP) && !url.contains(ServerConstants.HTTPS)) {
            url = ServerConstants.getServerLink() + url;
        }
        return glideCreateRequest(Glide.with(context).load(url), placeHolder, errorResource, isResize, width, height, isCache);
    }


    @UiThread
    public static void loadImageByGlide(final ImageView imageView, boolean isResize, int with, int height, @NonNull String url, @DrawableRes int errorResource, @DrawableRes int placeHolder, boolean isCache, final float radius) {
        try {
            if (url == null || url.isEmpty()) {
                imageView.setImageResource(placeHolder);
                return;
            }
            glideCreateRequest(url, imageView.getContext(), placeHolder, errorResource, isResize, with, height, isCache).asBitmap()
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(radius);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


    @UiThread
    public static void loadImageByGlide(ImageView imageView, boolean isResize, int with, int height, Uri uri, @DrawableRes int errorResource, @DrawableRes int placeHolder, boolean isCache) {
        try {
            glideCreateRequest(uri, imageView.getContext(), placeHolder, errorResource, isResize, with, height, isCache).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    @UiThread
    public static void loadImageByGlide(int imageResource, final ImageView imageView) {
        try {
            loadImageByGlide(imageView, imageResource, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public static void loadImageByGlide(final ImageView imageView, int imageResource, boolean asGif) {
        try {
            DrawableTypeRequest<Integer> builder = Glide.with(imageView.getContext()).load(imageResource);
            if (asGif) {
                builder.asGif().into(imageView);
            } else {
                builder.into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public enum LoaderEnum {
        Glide,
        Picasso
    }

    @UiThread
    public static void loadImage(ImageView imageView, @DrawableRes int imageResource, boolean asGif, LoaderEnum loaderEnum) {
        try {
            if (asGif)
                loadImageByGlide(imageView, imageResource, true);
            else {
                switch (loaderEnum) {
                    case Glide:
                        loadImageByGlide(imageView, imageResource, asGif);
                        break;
                    case Picasso:
                        loadImageByPicasso(imageView, imageResource);
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


    public static Bitmap rotateImage(int orientation, Context context, Uri uri) {

        Log.d(TAG, "roate Bitmap ");
        Bitmap bitmap = null;
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            Bitmap src = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
            Log.d(TAG, "end rotate Bitmap ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }


    /**
     * Get Image orientation with Uri created from File
     *
     * @param uri: uri created from file
     * @return 0, 90, 180, 270
     */
    public static int getExifOrientation(Uri uri) {

        ExifInterface exif;
        int orientation = 0;
        try {
            exif = new ExifInterface(uri.getPath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "got orientation " + orientation);

        int rotation = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }

        return rotation;
    }


    /**
     * Get Image orientation of Uri which get from Gallery, when open gallery
     *
     * @param uri: Uri from gallery
     * @return 0 , 90, 180, 270
     */
    public static int getImageRotated(Uri uri, Context context) {
        Log.d(TAG, "uri " + uri.getPath());
        int rotate = 0;
        try {
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = context.getContentResolver().query(uri, orientationColumn, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                rotate = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                Log.d(TAG, "rotate " + rotate);
            } else Log.d(TAG, "cur " + cur);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rotate;
    }


    public static Uri getImageContentUri(Context context, File file) {
        String imagePath = file.getAbsolutePath();
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectArgs = new String[]{imagePath};

        Cursor cImage = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectArgs, null);

        if (cImage != null && cImage.moveToFirst()) {
            int columnIdIndex = cImage.getColumnIndex(MediaStore.Images.Media._ID);
            int id = cImage.getInt(columnIdIndex);
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            Log.i(TAG, "Insert Image to Content Image");
            if (file.exists()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DATA, imagePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else {
                Log.e(TAG, "File is not existed");
                return null;
            }
        }
    }

    public static void setRatingBarColor(RatingBar ratingBar, @ColorRes int colorSelected, @ColorRes int colorNotSelected) {
        try {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ratingBar.getResources().getColor(colorSelected), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(ratingBar.getResources().getColor(colorSelected), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(ratingBar.getResources().getColor(colorNotSelected), PorterDuff.Mode.SRC_ATOP);
//
//            Drawable stars = ratingBar.getProgressDrawable();
//            DrawableCompat.setTint(stars, ratingBar.getResources().getColor(colorSelected));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadImageByPicasso(ImageView imageView, JsonElement url, int placeHolderResource, int errorResource, boolean clearCache, Size size) {
        try {
            loadImageByPicasso(imageView, url.getAsString(), placeHolderResource, errorResource, clearCache, size);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }


    public static void loadImageByPicasso(ImageView imageView, @DrawableRes int resId) {
        try {
            RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(resId);
            requestCreator.into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public static void loadImageByPicasso(ImageView imageView, String url, boolean clearCache, Size size) {
        try {

            imageView.setImageBitmap(null);
            if (url == null || url.equals("")) {
                Log.d(TAG, "url");
                url = ServerConstants.getServerLink();
            }
            url = url.trim();
            url = ClientUtils.getLink(url);


            RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(url);
            if (size != null) {
                requestCreator = requestCreator.resize(size.getWidth(), size.getHeight()).centerCrop();
            }
            if (clearCache)
                requestCreator = requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE);
            requestCreator.into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


    public static void loadImageByPicasso(ImageView imageView, String url, int placeHolderResource, int errorResource, boolean clearCache, Size size) {
        try {

            imageView.setImageBitmap(null);
            if (url == null || url.equals("")) {
                Log.d(TAG, "url");
                url = ServerConstants.getServerLink();
            }
            url = url.trim();
            url = ClientUtils.getLink(url).trim();
            Log.d(TAG, "loadImageByPicasso: " + url);

            RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(url);
            if (size != null) {
                requestCreator = requestCreator.resize(size.getWidth(), size.getHeight()).centerCrop();
            }
            if (clearCache)
                requestCreator = requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE);

            requestCreator = requestCreator.placeholder(placeHolderResource).error(errorResource);
            requestCreator.into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public static void loadImageByPicasso(Context context, String url, int placeHolderResource, int errorResource, boolean clearCache, Size size, Target target) {
        try {
            if (url == null || url.equals("")) {
                Log.d(TAG, "url");
                url = ServerConstants.getServerLink();
            }

            if (!(url.startsWith("http://") || url.startsWith("https://")))
                url = ServerConstants.getServerLink() + url;

            RequestCreator requestCreator = Picasso.with(context).load(url);
            if (size != null) {
                requestCreator = requestCreator.resize(size.getWidth(), size.getHeight()).centerCrop();
            }
            if (clearCache)
                requestCreator = requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE);

            requestCreator = requestCreator.placeholder(placeHolderResource).error(errorResource);
            requestCreator.into(target);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public static void showImage(Context context, FullScreenImageGalleryAdapter.FullScreenImageLoader loader, ArrayList<String> listUrl, int position) {
        try {
            FullScreenImageGalleryActivity.setFullScreenImageLoader(loader);
            Intent intent = new Intent(context, FullScreenImageGalleryActivity.class);
            intent.putStringArrayListExtra("KEY_IMAGES", listUrl);
            intent.putExtra("KEY_POSITION", position);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void showImage(Context context, FullScreenImageGalleryAdapter.FullScreenImageLoader loader, String imageUrl, int position) {
        try {
            FullScreenImageGalleryActivity.setFullScreenImageLoader(loader);
            Intent intent = new Intent(context, FullScreenImageGalleryActivity.class);
            ArrayList<String> listUrl = new ArrayList<>();
            listUrl.add(imageUrl);
            intent.putStringArrayListExtra("KEY_IMAGES", listUrl);
            intent.putExtra("KEY_POSITION", position);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showImage(Context context, FullScreenImageGalleryAdapter.FullScreenImageLoader loader, String imageUrl, String caption, int position) {
        try {
            FullScreenImageGalleryActivity.setFullScreenImageLoader(loader);
            Intent intent = new Intent(context, FullScreenImageGalleryActivity.class);
            ArrayList<String> listUrl = new ArrayList<>();
            ArrayList<String> listCaption = new ArrayList<>();
            listUrl.add(imageUrl);
            listCaption.add(caption);
            intent.putStringArrayListExtra("images", listUrl);
            intent.putStringArrayListExtra("captions", listCaption);
            intent.putExtra("position", position);
            context.startActivity(intent);
        } catch (Exception
                e) {
            e.printStackTrace();
        }
    }


}
