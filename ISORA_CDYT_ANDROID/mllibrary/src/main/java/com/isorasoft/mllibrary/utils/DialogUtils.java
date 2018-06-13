package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isorasoft.mllibrary.R;

/**
 * Created by MaiNam on 7/5/2016.
 */
public class DialogUtils {

    public static class Builder {
        private static final String TAG = DialogUtils.class.getSimpleName();
        private View closeButton;
        private TextView positiveButton;
        private TextView negativeButton;
        private View splitButton;
        private View layoutTitle;
        private TextView titleDialog;
        private LinearLayout main_dialog;
        private View action_dialog;
        Dialog dialog = null;

        public Builder(Activity activity, String text) {
            TextView textView = new TextView(activity);
            textView.setText(text);
            init(activity, textView);

        }

        public static Builder createBuilder(Activity activity, View view) {
            return new Builder(activity, view);
        }

        public static Builder createBuilder(Activity activity, @LayoutRes int layout) {
            return new Builder(activity, layout);
        }

        public Builder setOnDismissListener(Dialog.OnDismissListener dismissListener) {
            dialog.setOnDismissListener(dismissListener);
            return this;
        }


        public interface OnClickListener {
            void onClick(Dialog dialog, View v);
        }

        public Builder(Activity activity, View view) {

            init(activity, view);
        }

        public Builder(Activity activity, @LayoutRes int view) {
            init(activity, activity.getLayoutInflater().inflate(view, null, false));
        }

        public Builder(Activity activity, @LayoutRes int formDialog, @LayoutRes int view) {
            init(activity, formDialog, activity.getLayoutInflater().inflate(view, null, false));
        }
        public Builder(Activity activity, @LayoutRes int formDialog, View view) {
            init(activity, formDialog, view);
        }

        void init(final Activity activity, View view) {
            init(activity, R.layout.dialog_base, view);
        }

        void init(final Activity activity, int formDialog, View view) {
            View layout = activity.getLayoutInflater().inflate(formDialog, null, false);
            dialog = new AlertDialog.Builder(activity).setView(layout).create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            closeButton = layout.findViewById(R.id.close_dialog);
            splitButton = layout.findViewById(R.id.split_action_dialog);
            positiveButton = (TextView) layout.findViewById(R.id.btnOK);
            negativeButton = (TextView) layout.findViewById(R.id.btnCancel);
            layoutTitle = layout.findViewById(R.id.layout_title_dialog);
            titleDialog = (TextView) layout.findViewById(R.id.title_dialog);
            main_dialog = (LinearLayout) layout.findViewById(R.id.main_dialog);
            main_dialog.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    Log.d(TAG, "onLayoutChange i: " + i);
                    Log.d(TAG, "onLayoutChange i1: " + i1);
                    Log.d(TAG, "onLayoutChange i2: " + i2);
                    Log.d(TAG, "onLayoutChange i3: " + i3);
                    Log.d(TAG, "onLayoutChange y: " + CommonUtils.getDeviceSize(activity).y);
                    Log.d(TAG, "onLayoutChange: action " + activity.getResources().getDimension(R.dimen.dialog_action_height));
                    if (i3 + activity.getResources().getDimension(R.dimen.dialog_action_height) > CommonUtils.getDeviceSize(activity).y) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) main_dialog.getLayoutParams();
                        params.height = i3 - (int) activity.getResources().getDimension(R.dimen.dialog_action_height);
                        main_dialog.setLayoutParams(params);
                    }
                }
            });
            action_dialog = layout.findViewById(R.id.action_dialog);

            if (view != null)
                main_dialog.addView(view);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(dialog, view);
                }
            });
            splitButton.setVisibility(View.GONE);
            positiveButton.setVisibility(View.GONE);
            negativeButton.setVisibility(View.GONE);
            action_dialog.setVisibility(View.GONE);
            showCloseButton(false);
        }


        public Builder setCancelable(boolean cancelable) {
            dialog.setCancelable(cancelable);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancelable) {
            dialog.setCanceledOnTouchOutside(cancelable);
            return this;
        }

        public Builder showCloseButton(boolean showCloseButton) {
            closeButton.setVisibility(showCloseButton ? View.VISIBLE : View.GONE);
            return this;
        }

        public Builder setTitle(String title) {
            titleDialog.setText(title);
            layoutTitle.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setShowNegativeButton(boolean show) {
            negativeButton.setVisibility(show ? View.VISIBLE : View.GONE);
            if (positiveButton.getVisibility() == View.VISIBLE || negativeButton.getVisibility() == View.VISIBLE)
                action_dialog.setVisibility(View.VISIBLE);
            else
                action_dialog.setVisibility(View.GONE);


            if (positiveButton.getVisibility() == View.VISIBLE && negativeButton.getVisibility() == View.VISIBLE)
                splitButton.setVisibility(View.VISIBLE);
            else
                splitButton.setVisibility(View.GONE);
            return this;
        }

        public Builder setShowPositiveButton(boolean show) {
            positiveButton.setVisibility(show ? View.VISIBLE : View.GONE);
            if (positiveButton.getVisibility() == View.VISIBLE || negativeButton.getVisibility() == View.VISIBLE)
                action_dialog.setVisibility(View.VISIBLE);
            else
                action_dialog.setVisibility(View.GONE);


            if (positiveButton.getVisibility() == View.VISIBLE && negativeButton.getVisibility() == View.VISIBLE)
                splitButton.setVisibility(View.VISIBLE);
            else
                splitButton.setVisibility(View.GONE);
            return this;
        }

        public Builder setPositiveButton(String text, final OnClickListener listener) {
            positiveButton.setText(text);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(dialog, view);
                    else
                        mListener.onClick(dialog, view);
                }
            });

            setShowPositiveButton(true);
            return this;
        }

        public Builder setPositiveButton(String text, int color, OnClickListener listener) {
            setPositiveButton(text, listener);
            positiveButton.setTextColor(color);

            return this;
        }

        public Builder setNegativeButton(String text, final OnClickListener listener) {
            negativeButton.setText(text);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(dialog, view);
                    else
                        mListener.onClick(dialog, view);
                }
            });
            setShowNegativeButton(true);
            return this;
        }

        public Builder setNegativeButton(String text, int color, OnClickListener listener) {
            setNegativeButton(text, listener);
            negativeButton.setTextColor(color);

            return this;
        }

        public Builder setActionButtonBackgroung(Drawable drawable) {
            action_dialog.setBackgroundDrawable(drawable);
            return this;
        }

        public Builder setSplitButtonColor(Drawable drawable) {
            splitButton.setBackgroundDrawable(drawable);
            return this;
        }


        public Dialog create() {
            return dialog;
        }

        OnClickListener mListener = new OnClickListener() {
            @Override
            public void onClick(Dialog dialog, View view) {
                try {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


//        public Builder setCanceledOnTouchOutside(boolean cancelable) {
//            dialog.setCanceledOnTouchOutside(cancelable);
//            return this;
//        }
    }

}
