package com.isorasoft.mllibrary.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

/**
 * Created by MaiNam on 6/7/2016.
 */
public class ViewGroupCleanerUtils {
    private static Handler sHandler;

    static class CleanTask implements Runnable {
        WeakReference<ViewGroup> mViewRef;

        public CleanTask(ViewGroup view) {
            this.mViewRef = new WeakReference(view);
        }

        public void run() {
            try {
                ViewGroup view = (ViewGroup) this.mViewRef.get();
                if (view != null) {
                    if (view instanceof RecyclerView) {
                        ((RecyclerView) view).setAdapter(null);
                    }
                    int count = view.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View child = view.getChildAt(i);
                        if (child instanceof WebView) {
                            ((WebView) child).destroy();
                        }
                    }
                    view.removeAllViews();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static {
        sHandler = new Handler(Looper.getMainLooper());
    }

    public static void clean(ViewGroup view) {
        sHandler.postDelayed(new CleanTask(view), 1000);
    }

    public static void destroy() {
        sHandler.removeCallbacksAndMessages(null);
    }
}
