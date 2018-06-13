package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;

/**
 * Created by MaiNam on 6/2/2016.
 */
public class RippleUtils {
    public static void showRipple(View v) {
        try {
            MaterialRippleLayout.on(v)
                    .rippleColor(Color.parseColor("#ff269dff"))
                    .rippleAlpha(0.2f)
                    .rippleOverlay(true)
                    .rippleHover(true)
                    .create();

        } catch (Exception e) {
        }
    }

    public static void showRippleColor(View v, int color) {
        try {
            MaterialRippleLayout.on(v)
                    .rippleColor(color)
                    .rippleAlpha(0.2f)
                    .rippleOverlay(true)
                    .rippleHover(true)
                    .create();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showRipple(View parent, int resourceId) {
        try {
            showRipple(parent.findViewById(resourceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showRipple(Activity context, int resourceId) {
        try {
            showRipple(context.findViewById(resourceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
