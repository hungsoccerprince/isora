package com.isorasoft.mllibrary.utils;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.isorasoft.mllibrary.activity.BaseActivity;
import com.isorasoft.mllibrary.constants.Constants;

/**
 * Created by MaiNam on 11/4/2016.
 */

public class GoogleAccountUtils {
    private static final String TAG = GoogleAccountUtils.class.getSimpleName();

    public static void selectAccount(GoogleAccountCredential credential, BaseActivity baseActivity) {
        baseActivity.startActivityForResult(credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER);
    }

    public interface OnCallback {
        void onSuccess(String accountName);

        void onFailed();
    }

    public static void retrieveData(int requestCode, int resultCode, Intent data, OnCallback callback) {
        switch (requestCode) {
            case Constants.REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK)
                    callback.onSuccess("");
                else
                    callback.onFailed();
                break;
            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(
                            AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null && callback != null) {
                        callback.onSuccess(accountName);
                        return;
                    }
                }
                if (callback != null) {
                    callback.onFailed();
                }
        }
    }
}
