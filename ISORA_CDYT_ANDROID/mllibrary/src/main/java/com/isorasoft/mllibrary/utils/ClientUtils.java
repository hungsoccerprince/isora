package com.isorasoft.mllibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isorasoft.mllibrary.constants.Constants;
import com.isorasoft.mllibrary.constants.ServerConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by MaiNam on 4/19/2016.
 */


public class ClientUtils {
    private static final String TAG = ClientUtils.class.getSimpleName();


    public static String getFromStream(InputStream stream) {
        return getFromStream(stream, "UTF-8");
    }

    public static String getFromStream(InputStream stream, String charset) {
        InputStreamReader in = null;
        BufferedReader buff = null;
        StringBuilder content = new StringBuilder();
        try {
            StringBuffer text = new StringBuffer();
            in = new InputStreamReader(stream, charset);
            buff = new BufferedReader(in);
            String line = "";
            while (true) {
                line = buff.readLine();
                if (line == null)
                    break;
                content.append(line);
            }
//            if (BuildConfig.DEBUG) {
//                Log.d(TAG, content.toString());
//            }
            return content.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            CloseUtils.closeStream(buff);
            CloseUtils.closeStream(in);
        }

        return "";
    }

    public static String getHtml(String url) {
        StringBuilder content = new StringBuilder();
        HttpURLConnection connection = null;

        URL _url = null;
        try {
            _url = new URL(url);
            connection = (HttpURLConnection) (_url.openConnection());
            connection.connect();
            return getFromStream((InputStream) connection.getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return "";
    }

    public static boolean downloadFile(String url, File file, int timeOut) throws SocketTimeoutException {
        try {
            url = getLink(url);
//            Log.d(TAG, url);
            Request request = new Request.Builder().url(url)
                    .build();
            Response response = getClient(timeOut).newCall(request).execute();

            InputStream in = response.body().byteStream();
//            Log.d(TAG, String.valueOf(response.code()));
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
            response.body().close();
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (Exception e) {
        }
        return false;
    }

    private static OkHttpClient client;
    private static int timeOut = 100;

    static OkHttpClient getClient() {
        if (client == null)
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut, TimeUnit.SECONDS)
                    .writeTimeout(timeOut, TimeUnit.SECONDS)
                    .build();
        return client;
    }

    static OkHttpClient getClient(int timeOut) {
        if (timeOut == 0)
            return getClient();
        return new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .build();
    }


//    public static void savedFile(final Activity activity, final ArrayList<String> urls) {
//
//        String folderName = String.valueOf(Calendar.getInstance().getTimeInMillis());
//        String temp = Environment.getExternalStorageDirectory().getPath() + "/cdyt_temp/";
//        File fileTemp = new File(temp);
//        if (fileTemp.exists()) {
//            fileTemp.delete();
//        }
//        File folderPath = new File(temp + folderName);
//        fileTemp.mkdirs();
//
//        for (int i = 0; i < urls.size(); i++) {
//
//            try {
//
//                URL url = new URL(urls.get(i));
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }


    public static String getLink(String imageUrl) {
        if (imageUrl == null)
            return ServerConstants.getServerLink();
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))
            return imageUrl;
        return ServerConstants.getServerLink() + imageUrl.trim();
    }

    public static void hideCamera(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            view.setVisibility(View.GONE);
        }
    }


    public static class DataResponse {
        private int status;
        private String body;

        public JsonElement jsonBody() {
            try {
                return new JsonParser().parse(getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JsonParser().parse("{\"ErrorCode\":0, \"Descirption\":\"\"}").getAsJsonObject();
        }

        public boolean isOK() {
            return status == ServerConstants.RESPONSE_OK;
        }

        public boolean isTimeOut() {
            return status == ServerConstants.REQUEST_TIMEOUT_ERROR;
        }

        public DataResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public DataResponse(Response response) {
            this.status = response.code();
            try {
                this.body = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static DataResponse postData(JSONObject author, String method, JSONObject json, int timeOut) throws SocketTimeoutException, UnknownHostException {
        return postDataHeader(author, method, json.toString(), timeOut);
    }

    public static DataResponse putData(JSONObject author, String method, JSONObject json, int timeOut) throws SocketTimeoutException, UnknownHostException {
        return putData(author, method, json.toString(), timeOut);
    }

    public static DataResponse deleteData(JSONObject author, String method, JSONObject json, int timeOut) throws SocketTimeoutException, UnknownHostException {
        return deleteData(author, method, json.toString(), timeOut);
    }

    public static DataResponse getDataHeader(JSONObject author, String method, int timeOut) throws SocketTimeoutException, UnknownHostException {
        if (!(method.startsWith("https://") || method.startsWith("http://")))
            method = ServerConstants.getServerLink() + method;
//        Log.d(TAG, "url " +
//                method);
        Request request = new Request.Builder()
                .addHeader("Auth", author.toString())
                .url(method)
                .get()
                .build();
        Response response = null;
        try {
            OkHttpClient client = null;
            if (timeOut != 0) {
                client = new OkHttpClient.Builder()
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(timeOut, TimeUnit.SECONDS)
                        .writeTimeout(timeOut, TimeUnit.SECONDS)
                        .build();
            } else {
                client = getClient();
            }

            response = client.newCall(request).execute();
            String bodyString = response.body().string();
            Log.d(TAG, method + ": " + bodyString);
            return new DataResponse(response.code(), bodyString);
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
//            Log.e(TAG, "postData: UnknownHostException ");
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return new DataResponse(404, "{\"ErrorCode\":0, \"Descirption\":\"\"}");
    }

    public static DataResponse postDataHeader(JSONObject author, String method, String json, int timeOut) throws SocketTimeoutException, UnknownHostException {
        RequestBody body = RequestBody.create(ServerConstants.JSON, json);
        String url = ServerConstants.getServerLink() + method;
        Log.d(TAG, "url " +
                url);
        Log.d(TAG, "data " +
                json);
        Request request = new Request.Builder()
                .addHeader("Auth", author.toString())
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            OkHttpClient client = null;
            if (timeOut != 0) {
                client = new OkHttpClient.Builder()
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(timeOut, TimeUnit.SECONDS)
                        .writeTimeout(timeOut, TimeUnit.SECONDS)
                        .build();
            } else {
                client = getClient();
            }

            response = client.newCall(request).execute();
//            Log.d(TAG, String.valueOf(response.code()));
            String bodyString = response.body().string();//getFromStream(response.body().byteStream(),"UTF-16");

            Log.d(TAG, method + ": " + bodyString);
            return new DataResponse(response.code(), bodyString);
        } catch (SocketTimeoutException e) {
//            Log.e(TAG, "postData: SocketTimeoutException ");
            throw e;
        } catch (UnknownHostException e) {
//            Log.e(TAG, "postData: UnknownHostException ");
            throw e;
        } catch (Exception e) {
//            Log.e(TAG, "postData: Exception ");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return new DataResponse(ServerConstants.RESPONSE_ERROR, "{\"ErrorCode\":0, \"Descirption\":\"\"}");
    }

    public static DataResponse putData(JSONObject author, String method, String json, int timeOut) throws SocketTimeoutException, UnknownHostException {
        RequestBody body = RequestBody.create(ServerConstants.JSON, json);
        String url = ServerConstants.getServerLink() + method;
        Log.d(TAG, "url " +
                url);
        Log.d(TAG, "data " +
                json);
        Request request = new Request.Builder()
                .addHeader("Auth", author.toString())
                .url(url)
                .put(body)
                .build();
        Response response = null;
        try {
            OkHttpClient client = null;
            if (timeOut != 0) {
                client = new OkHttpClient.Builder()
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(timeOut, TimeUnit.SECONDS)
                        .writeTimeout(timeOut, TimeUnit.SECONDS)
                        .build();
            } else {
                client = getClient();
            }

            response = client.newCall(request).execute();
//            Log.d(TAG, String.valueOf(response.code()));
            String bodyString = response.body().string();//getFromStream(response.body().byteStream(),"UTF-16");
            return new DataResponse(response.code(), bodyString);
        } catch (SocketTimeoutException e) {
//            Log.e(TAG, "postData: SocketTimeoutException ");
            throw e;
        } catch (UnknownHostException e) {
//            Log.e(TAG, "postData: UnknownHostException ");
            throw e;
        } catch (Exception e) {
//            Log.e(TAG, "postData: Exception ");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return new DataResponse(ServerConstants.RESPONSE_ERROR, "{\"ErrorCode\":0, \"Descirption\":\"\"}");
    }

    public static DataResponse deleteData(JSONObject author, String method, String json, int timeOut) throws SocketTimeoutException, UnknownHostException {
        RequestBody body = RequestBody.create(ServerConstants.JSON, json);
        String url = ServerConstants.getServerLink() + method;
        Log.d(TAG, "url " +
                url);
        Log.d(TAG, "data " +
                json);
        Request request = new Request.Builder()
                .addHeader("Auth", author.toString())
                .url(url)
                .delete(body)
                .build();
        Response response = null;
        try {
            OkHttpClient client = null;
            if (timeOut != 0) {
                client = new OkHttpClient.Builder()
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(timeOut, TimeUnit.SECONDS)
                        .writeTimeout(timeOut, TimeUnit.SECONDS)
                        .build();
            } else {
                client = getClient();
            }

            response = client.newCall(request).execute();
//            Log.d(TAG, String.valueOf(response.code()));
            String bodyString = response.body().string();//getFromStream(response.body().byteStream(),"UTF-16");
            return new DataResponse(response.code(), bodyString);
        } catch (SocketTimeoutException e) {
//            Log.e(TAG, "postData: SocketTimeoutException ");
            throw e;
        } catch (UnknownHostException e) {
//            Log.e(TAG, "postData: UnknownHostException ");
            throw e;
        } catch (Exception e) {
//            Log.e(TAG, "postData: Exception ");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return new DataResponse(ServerConstants.RESPONSE_ERROR, "{\"ErrorCode\":0, \"Descirption\":\"\"}");
    }

    public static DataResponse postData(Request request) throws SocketTimeoutException, UnknownHostException {
        try {
            OkHttpClient client = new OkHttpClient.Builder().build();

            Response response = client.newCall(request).execute();
//            Log.d(TAG, String.valueOf(response.code()));
            String bodyString = response.body().string();//getFromStream(response.body().byteStream(),"UTF-16");
            Log.d(TAG, "postData: "+bodyString);
            return new DataResponse(response.code(), bodyString);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "postData: SocketTimeoutException ");
            throw e;
        } catch (UnknownHostException e) {
            Log.e(TAG, "postData: UnknownHostException ");
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "postData: Exception ");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return new DataResponse(ServerConstants.RESPONSE_ERROR, "{\"ErrorCode\":0, \"Descirption\":\"\"}");
    }

    public static DataResponse postData(Request request, int timeOut) throws SocketTimeoutException, UnknownHostException {
        try {
            OkHttpClient client = getClient(timeOut);

            Response response = client.newCall(request).execute();
//            Log.d(TAG, String.valueOf(response.code()));
            String bodyString = response.body().string();//getFromStream(response.body().byteStream(),"UTF-16");
            return new DataResponse(response.code(), bodyString);
        } catch (SocketTimeoutException e) {
//            Log.e(TAG, "postData: SocketTimeoutException ");
            throw e;
        } catch (UnknownHostException e) {
//            Log.e(TAG, "postData: UnknownHostException ");
            throw e;
        } catch (Exception e) {
//            Log.e(TAG, "postData: Exception ");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return new DataResponse(ServerConstants.RESPONSE_ERROR, "{\"ErrorCode\":0, \"Descirption\":\"\"}");
    }


    public static <T> T cloneObject(Object object) {
        try {
            String json = getGson().toJson(object);
            return (T) getGson().fromJson(json, object.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static Gson gson;

    public static Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    static JsonParser jsonParser;

    public static JsonParser getJsonParser() {
        if (jsonParser == null)
            jsonParser = new JsonParser();
        return jsonParser;
    }


    /**
     * Create Content Uri from URL
     *
     * @param context : Must be getApplicationContext
     * @param urls    List URL
     * @return List Uri
     */
    public static ArrayList<Uri> savedFile(Context context, ArrayList<String> urls) throws IOException {

        String strTempDir = Environment.getExternalStorageDirectory().getPath() + "/" + ServerConstants.getFolderRoot();
        File folderTime = new File(strTempDir);
        folderTime.mkdirs();

        ArrayList<File> files = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
//            Log.d(TAG, "TAG  " + i);
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                URL url = new URL(urls.get(i));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                File fileOut = new File(folderTime, "image" + i + ".png");
                if (!fileOut.exists()) {
                    boolean createResult = fileOut.createNewFile();
//                    Log.d(TAG, "create Result " + createResult);
                }
                outputStream = new FileOutputStream(fileOut);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                files.add(fileOut);
            } catch (Exception e) {
                throw e;
            } finally {
                if (httpURLConnection != null)
                    try {
                        httpURLConnection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                CloseUtils.closeStream(inputStream);
                CloseUtils.closeStream(outputStream);
            }
        }

        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Uri uri = ImageUtils.getImageContentUri(context, files.get(i));
            if (uri != null) {
                uris.add(uri);
            }
        }
        return uris;

    }


    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Remove Folder and it's children
     *
     * @param file File or folder
     */
    public static void deleteRecursive(File file) {

        try {
            if (file.isDirectory()) {
                for (File chiFile1 : file.listFiles()) {
                    deleteRecursive(chiFile1);
                }
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteTempFolder() {
        try {
            String strTempDir = Environment.getExternalStorageDirectory().getPath() + "/" + ServerConstants.getFolderRoot();
//            Log.d(TAG, "strTempDir " + strTempDir);
            File file = new File(strTempDir);
            if (file.exists()) {
                deleteRecursive(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String generateBroadcastCode(String data) {
        if (data == null) data = "";
        data += ConvertUtils.toDateString(new Date().getTime(), "dd/MM/yyyhh:mm:ss");
        return StringUtils.getMD5(data);
    }

    public static String generateId() {
        return StringUtils.getMD5(ConvertUtils.toDateString(new Date().getTime(), "dd/MM/yyyhh:mm:ss"));
    }

    public static String generateId(String code) {
        return StringUtils.getMD5(ConvertUtils.toDateString(new Date().getTime(), "dd/MM/yyyhh:mm:ss" + code));
    }

    public static String getBase64FromFile(File file) throws FileNotFoundException {

        if (file == null) {
            throw new FileNotFoundException();
        } else {
            try {
//                Log.d(TAG, "getBase64FromFile: " + file.toString());
                InputStream inputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024 * 8];
                byte[] bytes;
                int byteRead;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    while ((byteRead = inputStream.read(buffer)) != -1) {
//                        Log.d(TAG, "getBase64FromFile: byteRead: " + byteRead);
                        byteArrayOutputStream.write(buffer, 0, byteRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = byteArrayOutputStream.toByteArray();
//                Log.d(TAG, "getBase64FromFile: byte length " + bytes.length);

                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();

                String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
//                Log.d(TAG, "getBase64FromFile: base64 " + base64);
                return base64;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
