package com.isorasoft.mllibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by MaiNam on 4/26/2016.
 */

public class Connectivity extends BroadcastReceiver {

    private static final String TAG = Connectivity.class.getSimpleName();
    static boolean isConnected = false;

    public static boolean isConnected() {
        return isConnected;
    }

    public synchronized static boolean setIsConnected(boolean connected) {
        return isConnected = connected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        activeNetInfo != null && activeNetInfo.isConnectedOrConnecting()
        try {
            setIsConnected(checkConn(context));
        } catch (Exception e) {
            setIsConnected(false);
            e.printStackTrace();
        }
    }

    public static boolean checkConn(Context context) {
//        try {
//            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
//            int returnVal = p1.waitFor();
////            return (returnVal == 0);
//            return true;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
//            try {
//                String url = "http://www.google.com";
//
//                Request request = new Request.Builder().url(url)
//                        .build();
//                Response response = getClient().newCall(request).execute();
//
//                Log.d(TAG,"response"+ response.body() );
//
//                return true;
//
//            } catch (MalformedURLException e) {
//                Log.d(TAG,"MalformedURLException");
//                e.printStackTrace();
//            } catch (IOException e) {
//                Log.d(TAG,"IOException");
//                e.printStackTrace();
//            }catch (Exception e){
//                Log.d(TAG,"Exception");
//            }
        }
        return false;

//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(
//                ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo.State mobile = NetworkInfo.State.DISCONNECTED;
//        if (mobileInfo != null) {
//            mobile = mobileInfo.getState();
//        }
//        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(
//                ConnectivityManager.TYPE_WIFI);
//        NetworkInfo.State wifi = NetworkInfo.State.DISCONNECTED;
//        if (wifiInfo != null) {
//            wifi = wifiInfo.getState();
//        }
//        boolean dataOnWifiOnly = (Boolean) PreferenceManager
//                .getDefaultSharedPreferences(context).getBoolean(
//                        "data_wifi_only", true);
//        if ((!dataOnWifiOnly && (mobile.equals(NetworkInfo.State.CONNECTED) || wifi
//                .equals(NetworkInfo.State.CONNECTED)))
//                || (dataOnWifiOnly && wifi.equals(NetworkInfo.State.CONNECTED))) {
//            return true;
//        } else {
//            return false;
//        }
    }

}