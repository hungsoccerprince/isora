package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.isorasoft.mllibrary.constants.Constants;
import com.isorasoft.mllibrary.constants.ServerConstants;

import java.io.File;
import java.util.UUID;


/**
 * Created by MaiNam on 6/22/2016.
 */
public class TakePictureUtils {
    private static final String TAG = TakePictureUtils.class.getSimpleName();

    private static Uri mFileUri;

    public static void selectPicture(AppCompatActivity activity) {
//        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        getIntent.setType("image/*");
//
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setType("image/*");
//
//        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//
//        activity.startActivityForResult(chooserIntent, Constants.REQ_CODE_PICK_IMAGE);

        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, ""), Constants.REQ_CODE_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            activity.startActivityForResult(intent, Constants.REQ_CODE_PICK_IMAGE);
        }


//        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        activity.startActivityForResult(i, Constants.REQ_CODE_PICK_IMAGE);
////
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        activity.startActivityForResult(intent, Constants.REQ_CODE_PICK_IMAGE);
    }

    public interface OnCallbackTakePicture {
        void onSuccess(Uri uriFile);

        void onFailed();

    }

    /**
     * Used when Fragment and Activity which not BaseActivity
     *
     * @param activity
     */
    public static void takeImage(Activity activity) {
        Log.d(TAG, "takeImage");

        if (!AndroidPermissionUtils.mayRequestPermission(activity, AndroidPermissionUtils.TypePermission.PERMISSION_WRIRE_EXTERNAL_STORAGE))
            return;

        // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
        File path = new File(Environment.getExternalStorageDirectory().getPath() + "/" + ServerConstants.getFolderRoot());

        path.mkdirs();

        // Choose file storage location
        File file = new File(path, UUID.randomUUID().toString() + ".jpg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "takeImage:"+activity.getPackageName());
            mFileUri = FileProvider.getUriForFile(activity,  activity.getPackageName() + ".provider", file);
        } else {
            mFileUri = Uri.fromFile(file);
        }

//        mFileUri =  Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        // Launch intent
        activity.startActivityForResult(takePictureIntent, Constants.REQ_CODE_TAKE_PICTURE);
    }

    public static void getPictureTaken(int requestCode, int resultCode, OnCallbackTakePicture callBack) {
        if (requestCode == Constants.REQ_CODE_TAKE_PICTURE)
            if (callBack != null) {
                if (resultCode == Activity.RESULT_OK)
                    callBack.onSuccess(mFileUri);
                else
                    callBack.onFailed();
            }
    }

    public static void getPictureSelected(int requestCode, int resultCode, Intent intent, OnCallbackTakePicture callBack) {
        if (requestCode == Constants.REQ_CODE_PICK_IMAGE)
            if (callBack != null) {
                if (resultCode == Activity.RESULT_OK) {
                    callBack.onSuccess(intent.getData());
                } else
                    callBack.onFailed();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void getPictureSelected(Activity activity, int requestCode, int resultCode, Intent intent, OnCallbackTakePicture callBack) {
        if (requestCode == Constants.REQ_CODE_PICK_IMAGE)
            if (callBack != null) {
                if (resultCode == Activity.RESULT_OK) {
                    final int takeFlags = intent.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // Check for the freshest data.

                    activity.getContentResolver().takePersistableUriPermission(intent.getData(), takeFlags);
                    callBack.onSuccess(intent.getData());
                } else
                    callBack.onFailed();
            }
    }
}
