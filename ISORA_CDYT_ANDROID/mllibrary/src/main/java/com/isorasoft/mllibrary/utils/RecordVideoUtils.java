package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.isorasoft.mllibrary.constants.Constants;


/**
 * Created by MaiNam on 11/4/2016.
 */

public class RecordVideoUtils {
    private static final String TAG = RecordVideoUtils.class.getSimpleName();

    private static Uri mFileUri;

    public static void selectVideo(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, ""), Constants.REQ_CODE_PICK_VIDEO);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            activity.startActivityForResult(intent, Constants.REQ_CODE_PICK_VIDEO);
        }
    }

    public interface OnCallback {
        void onSuccess(Uri uriFile);

        void onFailed();

    }

    public static void recordVideo(Activity activity) {
        Log.d(TAG, "takeImage");

        if (!AndroidPermissionUtils.mayRequestPermission(activity, AndroidPermissionUtils.TypePermission.PERMISSION_WRIRE_EXTERNAL_STORAGE))
            return;

        // Create intent
        Intent recodeVideo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        recodeVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        activity.startActivityForResult(recodeVideo, Constants.REQ_CODE_RECORD_VIDEO);
    }

    public static void getRecordVideo(int requestCode, int resultCode, RecordVideoUtils.OnCallback callBack) {
        if (requestCode == Constants.REQ_CODE_RECORD_VIDEO)
            if (callBack != null) {
                if (resultCode == Activity.RESULT_OK)
                    callBack.onSuccess(mFileUri);
                else
                    callBack.onFailed();
            }
    }

    public static void getVideoSelected(int requestCode, int resultCode, Intent intent, RecordVideoUtils.OnCallback callBack) {
        if (requestCode == Constants.REQ_CODE_PICK_VIDEO)
            if (callBack != null) {
                if (resultCode == Activity.RESULT_OK) {
                    callBack.onSuccess(intent.getData());
                } else
                    callBack.onFailed();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void getVideoSelected(Activity activity, int requestCode, int resultCode, Intent intent, RecordVideoUtils.OnCallback callBack) {
        if (requestCode == Constants.REQ_CODE_PICK_VIDEO)
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
