package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.isorasoft.mllibrary.R;
import com.michael.easydialog.EasyDialog;

/**
 * Created by MaiNam on 7/5/2016.
 */
public class HelperUtils {

    private static final String TAG = HelperUtils.class.getSimpleName();
    EasyDialog dialog = null;
    View view;


    public HelperUtils(Activity activity, String mes, @DrawableRes int icon) {
        view = activity.getLayoutInflater().inflate(R.layout.item_sos_tip_with_image, null, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivImage);
        imageView.setImageResource(icon);
        TextView textView = (TextView) view.findViewById(R.id.tvTip);
        textView.setText(mes);
        init(activity);
    }

    public HelperUtils(Activity activity, String mes) {
        view = activity.getLayoutInflater().inflate(R.layout.item_sos_tip_with_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivImage);
        imageView.setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.tvTip);
        textView.setText(mes);
        init(activity);
    }

    void init(final Activity activity) {
        dialog = new EasyDialog(activity)
                .setLayout(view)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 300, 400, 0)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 300, 400, 0)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 300, 0, 400)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 300, 0, 400)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .setMarginLeftAndRight(24, 24);
    }

    public HelperUtils setBackgroundColor(@ColorInt int color) {
        dialog.setBackgroundColor(color);
        return this;
    }

    public HelperUtils setOutsideColor(@ColorInt int color) {
        dialog.setOutsideColor(color);
        return this;
    }

    public void show(View locationByAttachedView, HelpGravity helpGravity) {
        dialog.setLocationByAttachedView(locationByAttachedView).setGravity(helpGravity.getValue()).show();
    }

    public HelperUtils setOnDismissListener(EasyDialog.OnEasyDialogDismissed listener) {
        if (listener != null)
            dialog.setOnEasyDialogDismissed(listener);
        return this;
    }

    public enum HelpGravity {
        TOP(0),
        BOTTOM(1),
        LEFT(2),
        RIGHT(3);
        int currentValue;

        HelpGravity(int value) {
            currentValue = value;
        }

        public int getValue() {
            return currentValue;
        }
    }

}
