package com.isorasoft.phusan.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.main.MainActivity;
import com.isorasoft.phusan.activity.main.UpdateActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.AppInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.DialogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    private static final long SPLASH_TIME_OUT = 2000;
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Integer newVersion = 0;
    private boolean existedPermission = false;
    private boolean next;

    @BindView(R.id.imgSplash)
    ImageView imgSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if(Constants.DEMO){
            imgSplash.setImageResource(R.drawable.ic_demo);

        }


        readVersion();
    }

    /**
     * Check to newest version of app
     */
    private void checkAppVersion() {
        Log.d(TAG, "checkAppVersion: ");
        readVersion();
//        new GetNewestVersionCodeAsyncTask().execute();
    }


    private void nextStep() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent, TypeSlideIn.in_bottom_to_up);

        BaseActivity.setSlideIn(SplashActivity.this, BaseActivity.TypeSlideIn.in_bottom_to_up);
        finish();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashActivity.this, UpdateActivity.class);
//                startActivity(intent, TypeSlideIn.in_bottom_to_up);
//
//                BaseActivity.setSlideIn(SplashActivity.this, BaseActivity.TypeSlideIn.in_bottom_to_up);
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return AnalyticInfo.Screen.ScreenSplash;
    }

    private void readVersion() {
        Log.d(TAG, "readVersion: ");
        try {
            new AsyncTask<Void, JsonObject, Void>() {
                @Override
                protected void onPreExecute() {
                    showProgress();
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    hideProgress();
                    super.onPostExecute(aVoid);
                }

                @Override
                protected void onProgressUpdate(JsonObject... values) {
                    if (values != null && values[0] != null) {
                        if (ConvertUtils.toInt(values[0].get("Version")) > Constants.APP_VERSION) {
                            if (ConvertUtils.toBoolean(values[0].get("ForceUpdate")))
                                showConfirmDialog(true);
                            else
                                showConfirmDialog(false);
                        } else
                            nextStep();
                    } else {
                        nextStep();
                    }
                    super.onProgressUpdate(values);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        publishProgress(AppInfo.getAppVersion());
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        finish();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        finish();
                    }
                    return null;
                }
            }.execute();
        } catch (Exception e) {
            nextStep();
            e.printStackTrace();
        }

//        final String versionUrl = Constants.LINK_APP_VERSION;
//        try {
//            // Create a URL for the desired page
//            URL url = new URL(versionUrl);
//            // Read all the text returned by the server
//            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//            String str;
//            while ((str = in.readLine()) != null) {
//                Log.d(TAG, "readVersion: " + str);
//                newVersion = Integer.parseInt(str);
//                return newVersion;
//            }
//            in.close();
//        } catch (Exception e) {
//            nextStep();
//        }
//        return -1;
    }

    private void showConfirmDialog(boolean update) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm_base, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvOK = (TextView) view.findViewById(R.id.tvOK);
        TextView tvNo = (TextView) view.findViewById(R.id.tvNo);

        tvTitle.setText(getString(R.string.update_new_version));
        tvNo.setText("Bỏ qua");
        tvOK.setText("Cập nhật");

        if (update) {
            tvNo.setVisibility(View.GONE);
        } else
            tvNo.setVisibility(View.VISIBLE);

        DialogUtils.Builder builder = new DialogUtils.Builder(getActivity(), view);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                nextStep();
            }
        });

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo.setCurrentUser(getActivity(), null);
                goToGooglePlay();
            }
        });



//        DialogUtils.Builder builder = new DialogUtils.Builder(getActivity(), "");
//        builder.setCancelable(false);
//        builder.setTitle(getString(R.string.update_new_version));
//
//        if(update){
//            builder.setPositiveButton("Cập nhật", ContextCompat.getColor(getActivity(), R.color.xanhgreen),new DialogUtils.Builder.OnClickListener() {
//                @Override
//                public void onClick(Dialog dialog, View v) {
//                    Log.d(TAG, "onClick: download_now click ");
//                    UserInfo.setCurrentUser(getActivity(), null);
//                    goToGooglePlay();
////                new DownloadAsync().execute();
//                }
//            });
//
//            builder.setNegativeButton("Bỏ qua", new DialogUtils.Builder.OnClickListener() {
//                @Override
//                public void onClick(Dialog dialog, View v) {
//                    Log.d(TAG, "onClick: cancel click ");
//                    nextStep();
//                }
//            });
//        }else {
//            builder.setNegativeButton("Cập nhật", ContextCompat.getColor(getActivity(), R.color.xanhgreen),new DialogUtils.Builder.OnClickListener() {
//                @Override
//                public void onClick(Dialog dialog, View v) {
//                    Log.d(TAG, "onClick: download_now click ");
//                    UserInfo.setCurrentUser(getActivity(), null);
//                    goToGooglePlay();
////                new DownloadAsync().execute();
//                }
//            });
//
//        }

//        builder.create().show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.update_new_version)
//                .setPositiveButton(R.string.download_now, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.d(TAG, "onClick: download_now click ");
//                        new DownloadAsync().execute();
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.d(TAG, "onClick: cancel click ");
//                        finish();
//                    }
//                });
//        // Create the AlertDialog object and return it
//        builder.create().show();
    }

    private void goToGooglePlay() {
        Uri uri = null;
        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            uri = Uri.parse("market://details?id=" + appPackageName);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException anfe) {
            uri = Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private void downloadApk() {
        Log.d(TAG, "downloadApk: ");

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "APPS");
        folder.mkdir();
        File file = new File(folder, Constants.APK_NAME + newVersion + ".apk");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            DownloadFile(Constants.APP_LINK_DROP_BOX, file);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        /**
         * APKURL is your apk file url(server url)
         */

    }

    public void DownloadFile(String fileURL, File directory) {
        try {
            FileOutputStream f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            //c.setDoOutput(true);
            c.connect();
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            System.out.println("exception in DownloadFile: --------" + e.toString());
            e.printStackTrace();
        }
    }


    private void startApp() {
        Log.d(TAG, "startApp: ");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/APPS/" + Constants.APK_NAME + newVersion + ".apk")), "application/vnd.android.package-archive");
//
//
//        startActivity(intent);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        String mimeType = "application/vnd.android.package-archive";
        File file = new File(Environment.getExternalStorageDirectory() + "/APPS/" + Constants.APK_NAME + newVersion + ".apk");
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }


    class DownloadAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            downloadApk();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
            startApp();
        }
    }

//    class GetNewestVersionCodeAsyncTask extends AsyncTask<Void, Integer, Void> {
//        @Override
//        protected void onPreExecute() {
//            showProgress();
//        }
//
//        @Override
//        protected void onPostExecute(Void integer) {
//            hideProgress();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            try{
//                if (values != null && values[0] != null) {
//                    int serverVersion = values[0];
//                    if (serverVersion != -1 && serverVersion > Constants.APP_VERSION) {
//                        showConfirmDialog();
//                    } else {
//                        nextStep();
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            publishProgress(readVersion());
//            return null;
//        }
//    }
}
