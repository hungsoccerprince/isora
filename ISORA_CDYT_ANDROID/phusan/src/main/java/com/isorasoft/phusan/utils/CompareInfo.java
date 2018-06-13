package com.isorasoft.phusan.utils;

import android.util.Log;

import com.isorasoft.mllibrary.utils.ConvertUtils;

/**
 * Created by MaiNam on 2/28/2017.
 */

public class CompareInfo {
    private static final String TAG = CompareInfo.class.getSimpleName();

    public static int compare(long date1, long date2) {
        Log.d(TAG, "compare date1: " + ConvertUtils.toDateString(date1, "yyyyMMdd"));
        Log.d(TAG, "compare date2: " + ConvertUtils.toDateString(date2, "yyyyMMdd"));
        return ConvertUtils.toDateString(date1, "yyyyMMdd").compareTo(ConvertUtils.toDateString(date2, "yyyyMMdd"));
    }
}
