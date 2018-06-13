package com.isorasoft.mllibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.isorasoft.mllibrary.constants.Constants;

import java.util.ArrayList;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by MaiNam on 6/23/2016.
 */
public class AndroidPermissionUtils {
    private static final int REQUEST_READ_CONTACTS = 100;
    private static ArrayList<String> permissions;

    public enum TypePermission {
        PERMISSION_READ_CONNTACT(Manifest.permission.READ_CONTACTS),
        PERMISSION_WRITE_CONNTACT(Manifest.permission.WRITE_CONTACTS),
        PERMISSION_WRIRE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        PERMISSION_ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
        PERMISSION_GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS),
        CALL_PHONE(Manifest.permission.CALL_PHONE),
        PERMISSION_CAMERA(Manifest.permission.CAMERA);

        String permission = "";

        TypePermission(String permission) {
            this.permission = permission;
        }

        public String getValue() {
            return permission;
        }
    }

    public static boolean mayRequestPermission(final Activity activity, final TypePermission... typePermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (activity == null)
            return false;

        permissions = new ArrayList<>();
        for (TypePermission typePermission : typePermissions) {
            if (activity.checkSelfPermission(typePermission.getValue()) != PackageManager.PERMISSION_GRANTED) {
                if (activity instanceof Activity && ((Activity) activity).shouldShowRequestPermissionRationale(typePermission.getValue())) {
                    permissions.add(typePermission.getValue());
                } else {
                    permissions.add(typePermission.getValue());
                }
            }
        }
        if (permissions.size() > 0) {
            String[] stockArr = new String[permissions.size()];
            stockArr = permissions.toArray(stockArr);
            if (activity instanceof Activity)
                ((Activity) activity).requestPermissions(stockArr, Constants.REQ_REQUEST_PERMISSION);
        } else
            return true;

        return false;
    }

    public static boolean hasPermission(final Activity activity, final TypePermission... typePermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (activity == null)
            return false;

        ArrayList<String> needRequests = new ArrayList<>();
        for (TypePermission typePermission : typePermissions) {
            if (activity.checkSelfPermission(typePermission.getValue()) != PackageManager.PERMISSION_GRANTED) {
                if (activity.shouldShowRequestPermissionRationale(typePermission.getValue())) {
                    needRequests.add(typePermission.getValue());
                } else {
                    needRequests.add(typePermission.getValue());
                }
            }
        }
        return needRequests.size() == 0;
    }

    public interface OnCallbackRequestPermission {
        void onSuccess();
        void onFailed();
    }

    public static void getPermission(int requestCode, int[] grantResults, OnCallbackRequestPermission callBack, Activity activity) {
        if (requestCode == Constants.REQ_REQUEST_PERMISSION)
            if (callBack != null) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callBack.onSuccess();
                else
                {
                    if (EasyPermissions.somePermissionPermanentlyDenied(activity, permissions)) {
                        new AppSettingsDialog.Builder(activity).build().show();
                    }
                    callBack.onFailed();
                }
            }
    }

}
