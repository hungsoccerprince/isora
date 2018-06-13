package com.isorasoft.phusan.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by hungs on 5/20/2017.
 */

public class CameraUtils {

    public static final int TYPE_IMAGE = MEDIA_TYPE_IMAGE;
    private static final String TAG = CameraUtils.class.getSimpleName();

    public static Uri getOutputMediaFileUri(int type, Context context) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, com.isorasoft.phusan.BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
        } else {
            uri = Uri.fromFile(getOutputMediaFile(type));
        }

//        Uri uri = Uri.fromFile(getOutputMediaFile(type));
//         return Uri.fromFile(saveToInternalStorage(context, uri));
        return uri;
    }

    public static File getOutputMediaFile(int type)
    {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "camera_hung");
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath());
        createMediaStorageDir(mediaStorageDir);
        Log.d(TAG, "getOutputMediaFile: " + mediaStorageDir);
        return createFile(type, mediaStorageDir);
    }

    private static File getOutputInternalMediaFile(Context context, int type)
    {
        File mediaStorageDir = new File(context.getFilesDir(), "myInternalPicturesDir");

        createMediaStorageDir(mediaStorageDir);

        return createFile(type, mediaStorageDir);
    }

    private static void createMediaStorageDir(File mediaStorageDir) // Used to be 'private void ...'
    {
        if (!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdirs(); // Used to be 'mediaStorage.mkdirs();'
        }
    } // Was flipped the other way

    private static File createFile(int type, File mediaStorageDir ) // Used to be 'private File ...'
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        }
        else if(type == TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        }
        return mediaFile;
    }

    public static File saveToInternalStorage(Context context, Uri tempUri)
    {
        Log.d(TAG, "saveToInternalStorage: ");
        Log.d(TAG, "saveToInternalStorage: " + tempUri.getPath());
        InputStream in = null;
        OutputStream out = null;

        File sourceExternalImageFile = new File(tempUri.getPath());
        File destinationInternalImageFile = new File(getOutputInternalMediaFile(context, TYPE_IMAGE).getPath());

        try
        {
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
}
