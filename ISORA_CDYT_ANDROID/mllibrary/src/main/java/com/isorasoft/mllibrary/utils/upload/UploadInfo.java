package com.isorasoft.mllibrary.utils.upload;

import android.content.Context;
import android.net.Uri;

import com.isorasoft.mllibrary.model.DataDictionary;
import com.isorasoft.mllibrary.utils.ClientUtils;
import com.isorasoft.mllibrary.utils.upload.IUploadProvider.IUploadProvider;

import java.util.ArrayList;

/**
 * Created by MaiNam on 11/24/2016.
 */

public class UploadInfo {


    public interface UploadListener {
        void beginUpload();

        void endUpload();

        void onSuccess(ClientUtils.DataResponse response);

        void onError(ClientUtils.DataResponse response);
    }

    IUploadProvider iUploadProvider;
    UploadListener uploadListener;

    public UploadInfo(IUploadProvider iUploadProvider, UploadListener uploadListener) {
        this.iUploadProvider = iUploadProvider;
        this.uploadListener = uploadListener;
    }

    public void upload(Context context, String method, ArrayList<Uri> files, int timeOut) {
        upload(context, method, files, new ArrayList<DataDictionary>(), timeOut);
    }

    public void upload(Context context, String method, ArrayList<Uri> files, ArrayList<DataDictionary> data, int timeOut) {
        if (iUploadProvider != null) {
            iUploadProvider.upload(context, method, uploadListener, files, data, timeOut);
        }
    }
    public void uploadWithFilePath(Context context, String method, ArrayList<String> filePaths, ArrayList<DataDictionary> data, int timeOut) {
        if (iUploadProvider != null) {
            iUploadProvider.uploadWithFilePath(context, method, uploadListener, filePaths, data, timeOut);
        }
    }
    public void uploadWithFilePath(Context context, String method, ArrayList<String> filePaths, int timeOut) {
        if (iUploadProvider != null) {
            iUploadProvider.uploadWithFilePath(context, method, uploadListener, filePaths, new ArrayList<DataDictionary>(), timeOut);
        }
    }
}
