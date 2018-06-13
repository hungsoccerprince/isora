package com.isorasoft.mllibrary.utils.upload.IUploadProvider;

import android.content.Context;
import android.net.Uri;

import com.isorasoft.mllibrary.model.DataDictionary;
import com.isorasoft.mllibrary.utils.upload.UploadInfo;

import java.util.ArrayList;

/**
 * Created by MaiNam on 11/24/2016.
 */

public interface IUploadProvider {
    String getServer();

    void upload(Context context, String method, UploadInfo.UploadListener uploadListener, ArrayList<Uri> files, final ArrayList<DataDictionary> data, int timeOut);

    void uploadWithFilePath(Context context, String method, UploadInfo.UploadListener uploadListener, ArrayList<String> filePaths, final ArrayList<DataDictionary> data, int timeOut);
}
