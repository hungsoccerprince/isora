package com.isorasoft.mllibrary.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by MaiNam on 8/19/2016.
 */

public class FileInfo {
    private static final String TAG = FileInfo.class.getSimpleName();
    public static final int TYPE_IMAGE = MEDIA_TYPE_IMAGE;
    public static final int TYPE_VIDEO = MEDIA_TYPE_VIDEO;

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri)
    {

        //check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

            else if(isGoogleDriver(uri)){
                return uri.getLastPathSegment();
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriver(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }



    public static void writeFile(String fileName, byte[] data1) {
        OutputStream fo = null;
        try {
            String filename = Environment.getExternalStorageDirectory() + "/" + fileName;
            Log.d(TAG, "writeFile: " + filename);
            File file = new File(filename);
            file.createNewFile();
            if (file.exists()) {
                fo = new FileOutputStream(file);
                fo.write(data1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeStream(fo);
        }
    }


    public static String getRealPathFromUri(Context context, Uri contentUri) throws Exception {
        File file = new File(contentUri.getPath());
        if (file.exists())
            return file.getAbsolutePath();
        String path = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            path = getPath(context,contentUri);//getRealPathFromURI_API19(context, contentUri);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                path = getRealPathFromURI_API11to18(context, contentUri);
            } else
                path = getRealPathFromURI_BelowAPI11(context, contentUri);
        }
        return path;
    }

    public static File getFromUri(Context context, Uri uri) throws Exception {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // TODO: 9/16/2017 can phai lam tiep
            return saveToInternalStorage(context, uri);
        }else {
            String path = getRealPathFromUri(context, uri);
            return new File(path);
        }
    }

    public static File saveToInternalStorage(Context context, Uri tempUri)
    {
        Log.d(TAG, "saveToInternalStorage: ");
        Log.d(TAG, "saveToInternalStorage: " + tempUri.getPath());
        InputStream in = null;
        OutputStream out = null;

        File sourceExternalImageFile = new File(tempUri.getPath());
        File destinationInternalImageFile = null;
        try
        {
            destinationInternalImageFile = getTempImageFile(context);
            destinationInternalImageFile.createNewFile();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                in = context.getContentResolver().openInputStream(tempUri);
            }else {
                in = new FileInputStream(sourceExternalImageFile);
            }
            out = new FileOutputStream(destinationInternalImageFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            //Handle error
        }
        finally
        {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    in.close();
                }
            } catch (IOException e) {
                // Eh
            }
        }
        return destinationInternalImageFile;
    }

    private static File getOutputInternalMediaFile(Context context, int type)
    {
        File mediaStorageDir = new File(context.getFilesDir(), "myInternalPicturesDir");

        createMediaStorageDir(mediaStorageDir);

        return createFile(type, mediaStorageDir);
    }

    private static File createFile(int type, File mediaStorageDir ) // Used to be 'private File ...'
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        }
        else if(type == TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        }
        return mediaFile;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(Context context, Uri uri) throws Exception {
        String filePath = "";
        String wholeID = null;
        wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String[] strs = wholeID.split(":");
        String id = strs.length > 1 ? strs[1] : strs[0];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = null;
        try {
            context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        if (filePath.isEmpty())
            throw new Exception("file not found");
        return filePath;
    }

    private static void createMediaStorageDir(File mediaStorageDir) // Used to be 'private void ...'
    {
        if (!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdirs(); // Used to be 'mediaStorage.mkdirs();'
        }
    } // Was flipped the other way

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static  File getTempImageFile(Context context) throws IOException {

        File path = new File(Environment.getExternalStorageDirectory().getPath() + "/tempFile/" + context.getPackageName());
        if(!path.exists()) path.mkdirs();
        File file = new File(path, UUID.randomUUID().toString() + ".jpg");
        if(!file.exists()){
            file.createNewFile();
        }
        return file;
    }
}
