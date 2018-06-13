package com.isorasoft.phusan.utils;

import android.app.Activity;
import android.content.Intent;

import com.isorasoft.phusan.BaseActivity;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.util.ArrayList;

/**
 * Created by MaiNam on 12/28/2016.
 */

public class MultiFilePickerUtils {
    private static final String TAG = MultiFilePickerUtils.class.getSimpleName();

    public static void selectPicture(BaseActivity activity) {
        Intent intent1 = new Intent(activity, ImagePickActivity.class);
        intent1.putExtra(ImagePickActivity.IS_NEED_CAMERA, true);
        intent1.putExtra(Constant.MAX_NUMBER, 9);
        activity.startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
    }

    public static void getPictureSelected(int requestCode, int resultCode, Intent intent, OnCallbackSelectPicture callBack) {
        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE)
            if (callBack != null) {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<ImageFile> list = intent.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    ArrayList<String> listPaths = new ArrayList<>();
                    for (ImageFile imageFile : list)
                        listPaths.add(imageFile.getPath());
                    callBack.onSuccess(listPaths);
                } else
                    callBack.onFailed();
            }
    }

    public interface OnCallbackSelectPicture {
        void onSuccess(ArrayList<String> filePaths);

        void onFailed();

    }
}
