package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import com.isorasoft.mllibrary.R;

/**
 * Created by MaiNam on 5/17/2016.
 */
public class CommonUtils {
    public static int getActionbarSize(Activity activity) {
        TypedValue typedvalue = new TypedValue();
        int i = 0;
        if (activity.getTheme().resolveAttribute(R.dimen.actionBarSize, typedvalue, true)) {
            i = TypedValue.complexToDimensionPixelSize(typedvalue.data, activity.getResources().getDisplayMetrics());
        }
        return i;
    }

    static Display display = null;

    public static Point getDeviceSize(Activity activity) {
        try {
            if (display == null) {
                display = activity.getWindowManager().getDefaultDisplay();
            }
            Point point = new Point();
            display.getSize(point);
            return point;
        } catch (Exception e) {
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static int getScreenSizeWidth(Context context) {
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getScreenSizeHeight(Context context) {
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static float dpFromPx(Context context, float f) {
        return f / context.getResources().getDisplayMetrics().density;
    }
}
