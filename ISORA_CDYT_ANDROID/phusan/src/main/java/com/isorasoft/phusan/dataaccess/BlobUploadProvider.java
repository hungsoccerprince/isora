package com.isorasoft.phusan.dataaccess;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.isorasoft.mllibrary.constants.ServerConstants;
import com.isorasoft.mllibrary.model.DataDictionary;
import com.isorasoft.mllibrary.utils.ClientUtils;
import com.isorasoft.mllibrary.utils.FileInfo;
import com.isorasoft.mllibrary.utils.upload.IUploadProvider.IUploadProvider;
import com.isorasoft.mllibrary.utils.upload.UploadInfo;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by MaiNam on 11/24/2016.
 */

public class BlobUploadProvider implements IUploadProvider {

    private static final String TAG = BlobUploadProvider.class.getSimpleName();

    @Override
    public String getServer() {
        return ServerConstants.getServerLink();
    }

    @Override
    public void upload(final Context context, final String method, final UploadInfo.UploadListener uploadListener, final ArrayList<Uri> files, final ArrayList<DataDictionary> data, final int timeOut) {
        Log.d(TAG, "upload: "+method);
        new AsyncTask<Void, Void, ClientUtils.DataResponse>() {
            @Override
            protected void onPreExecute() {
                try {
                    if (uploadListener != null)
                        uploadListener.beginUpload();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPreExecute();
            }

            @Override
            protected ClientUtils.DataResponse doInBackground(Void... voids) {
                ClientUtils.DataResponse response = null;
                try {
                    MultipartBody.Builder formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    File _file;
                    for (Uri uri : files) {
                        _file = FileInfo.getFromUri(context, uri);
                        formBody.addFormDataPart("file", _file.getName(), RequestBody.create(MediaType.parse("image/png"), _file));
                    }
                    for (DataDictionary dataDictionary : data)
                        formBody.addFormDataPart(dataDictionary.getKey(), dataDictionary.getValue());

                    Request request = new Request.Builder().addHeader("Auth", RequestFormInfo.getRequestFormNew().toString()).url(getServer() + method).post(formBody.build()).build();

                    response = ClientUtils.postData(request, timeOut);

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    response = new ClientUtils.DataResponse(404, "{\"ErrorCode\":0, \"Descirption\":\"" + e.getMessage() + "\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    response = new ClientUtils.DataResponse(404, "{\"ErrorCode\":0, \"Descirption\":\"" + e.getMessage() + "\"}");
                }
                return response;
            }

            @Override
            protected void onPostExecute(ClientUtils.DataResponse aVoid) {
                try {
                    if (uploadListener != null)
                        uploadListener.endUpload();

                    if (aVoid != null && aVoid.isOK())
                        if (uploadListener != null)
                            uploadListener.onSuccess(aVoid);
                        else
                            uploadListener.onError(aVoid);
                    else uploadListener.onError(aVoid);
                } catch (OutOfMemoryError e) {
                    e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }

        }.execute();

    }

    @Override
    public void uploadWithFilePath(final Context context, final String method, final UploadInfo.UploadListener uploadListener, final ArrayList<String> filePaths, final ArrayList<DataDictionary> data, final int timeOut) {
        new AsyncTask<Void, Void, ClientUtils.DataResponse>() {
            @Override
            protected void onPreExecute() {
                try {
                    if (uploadListener != null)
                        uploadListener.beginUpload();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPreExecute();
            }

            @Override
            protected ClientUtils.DataResponse doInBackground(Void... voids) {
                ClientUtils.DataResponse response = null;
                try {
                    MultipartBody.Builder formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    File _file;
                    for (String path : filePaths) {
                        _file = new File(path);
                        formBody.addFormDataPart("file", _file.getName(),
                                RequestBody.create(MediaType.parse("image/png"), _file));
                    }
                    for (DataDictionary dataDictionary : data)
                        formBody.addFormDataPart(dataDictionary.getKey(), dataDictionary.getValue());

                    Request request = new Request.Builder().addHeader("Auth", RequestFormInfo.getRequestFormNew().toString()).url(getServer() + method).post(formBody.build()).build();

                    response = ClientUtils.postData(request, timeOut);

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    response = new ClientUtils.DataResponse(404, "{\"ErrorCode\":0, \"Descirption\":\"" + e.getMessage() + "\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    response = new ClientUtils.DataResponse(404, "{\"ErrorCode\":0, \"Descirption\":\"" + e.getMessage() + "\"}");
                }
                return response;
            }

            @Override
            protected void onPostExecute(ClientUtils.DataResponse aVoid) {
                try {
                    if (uploadListener != null)
                        uploadListener.endUpload();

                    if (aVoid != null && aVoid.isOK())
                        if (uploadListener != null)
                            uploadListener.onSuccess(aVoid);
                        else
                            uploadListener.onError(aVoid);
                    else uploadListener.onError(aVoid);
                } catch (OutOfMemoryError e) {
                    e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }

        }.execute();
    }
}