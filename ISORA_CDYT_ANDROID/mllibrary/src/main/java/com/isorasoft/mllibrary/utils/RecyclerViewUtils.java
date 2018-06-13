package com.isorasoft.mllibrary.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.isorasoft.mllibrary.layoutmanager.MyLinearLayoutManager;

/**
 * Created by MaiNam on 9/14/2016.
 */

public class RecyclerViewUtils {
    private boolean userScrolled;

    public interface RecyclerViewUtilsListener {
        void onScrollToEnd();
    }

    RecyclerViewUtilsListener recyclerViewUtils;
    RecyclerView.OnScrollListener onScrollListener;

    public RecyclerViewUtils(RecyclerViewUtilsListener listener, RecyclerView.OnScrollListener onScrollListener) {
        this.recyclerViewUtils = listener;
        this.onScrollListener = onScrollListener;
    }

    public void setUp(RecyclerView recyclerView) {
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager))
            recyclerView.setLayoutManager(new MyLinearLayoutManager(recyclerView.getContext()));

        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
                if (onScrollListener != null)
                    onScrollListener.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (userScrolled
                        && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    userScrolled = false;
                    recyclerViewUtils.onScrollToEnd();
                }

                if (onScrollListener != null)
                    onScrollListener.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}
