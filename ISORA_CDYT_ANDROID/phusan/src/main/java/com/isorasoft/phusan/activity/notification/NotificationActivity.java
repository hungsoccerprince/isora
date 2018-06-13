package com.isorasoft.phusan.activity.notification;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.IntentServicePost;
import com.isorasoft.phusan.IntentServiceSync;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.main.UpdateActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.DataCacheInfo;
import com.isorasoft.phusan.dataaccess.NotificationInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.mllibrary.Connectivity;
import com.isorasoft.mllibrary.adapter.BaseRecyclerAdapter;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.ImageUtils;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationActivity extends BaseActivity {

    private String TAG = NotificationActivity.class.getName();
    private BaseRecyclerAdapter<ViewHolder, JsonObject> adapter;
    ArrayList<JsonObject> listObjects = new ArrayList<>();
    private boolean userScrolled = false;
    private LinearLayoutManager mLayoutManager;
    private boolean hasShowAll;
    private boolean isLoading;
    private int page = 1;
    private int pageSize = 10;

    public static void open(Context context) {
        if (!UserInfo.isLogin())
            return;
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
        BaseActivity.setSlideIn(context, TypeSlideIn.in_right_to_left);

    }

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.loadmore)
    View loadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(getActivity());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getData(page, pageSize);
            }
        });
        setSlideOut(TypeSlideOut.out_left_to_right);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new BaseRecyclerAdapter<ViewHolder, JsonObject>(R.layout.item_notification, new BaseRecyclerAdapter.BaseViewHolder<ViewHolder, JsonObject>() {
            @Override
            public ViewHolder getViewHolder(View v) {
                return new ViewHolder(v);
            }

            @Override
            public void bindData(ViewHolder viewHolder, final JsonObject data, int position) {
//                final JsonObject user = ConvertUtils.toJsonObject(data.get("LinkedUser"));
                ImageUtils.loadImageByGlide(viewHolder.ivAvatar, false, 0, 0, ConvertUtils.toString(data.get("UserAvatar")), R.drawable.ic_default_avatar, R.drawable.ic_default_avatar, true,getResources().getDimensionPixelSize(R.dimen.avatar_radius));

                int type = ConvertUtils.toInt(data.get("DisplayType"));
                String title = "";
                StringBuilder stringBuilder = new StringBuilder();

                switch (type){
                    case 0:
                        stringBuilder.append("<font color='#333333'><b>").append(ConvertUtils.toString(data.get("UserDisplayName"))).append("</b></font>").append(" ").append(ConvertUtils.toString(data.get("Content")));

                        Link link = new Link(ConvertUtils.toString(data.get("UserDisplayName")))
                                .setTextColor(ContextCompat.getColor(getActivity(), R.color.xanhden))                  // optional, defaults to holo blue
                                .setUnderlined(false)                                       // optional, defaults to true
                                .setBold(true)                                              // optional, defaults to false
                                .setOnClickListener(new Link.OnClickListener() {
                                    @Override
                                    public void onClick(String clickedText) {
                                        UserInfo.showUserProfile(getActivity(), ConvertUtils.toString(data.get("UserId")), ConvertUtils.toString(data.get("UserDisplayName")),
                                                ConvertUtils.toString(data.get("UserAvatar")), ConvertUtils.toInt(data.get("UserRole")));
                                    }
                                });

                        LinkBuilder.on(viewHolder.tvTitle)
                                .addLink(link)
                                .build(); // create the clickable links

                        break;
                    case 1:
                        stringBuilder.append(ConvertUtils.toString(data.get("Content")));
                        break;
                }
                viewHolder.tvTitle.setText(Html.fromHtml(stringBuilder.toString()), TextView.BufferType.NORMAL);
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(), !ConvertUtils.toBoolean(data.get("IsRead")) ? R.color.xanhgreennhat2 : R.color.white));


            }
        }, listObjects, new BaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(View v, final int position, final Object o) {
                viewNotification((JsonObject) o, position);
            }
        });
        adapter.bindData(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // If scroll state is touch scroll then set userScrolled
                // true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisiblesItems = mLayoutManager
                        .findFirstVisibleItemPosition();

                if (!hasShowAll && !isLoading && userScrolled
                        && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    userScrolled = false;
                    page = page + 1;
                    getData(page, pageSize);
                    isLoading = true;
                }
            }
        });

        Intent intent = new Intent(getActivity(), IntentServiceSync.class);
        intent.setAction(IntentServiceSync.SYNC_NOTIFICATION_COUNT);
        startService(intent);

        getData(page, pageSize);
    }

    private void viewNotification(JsonObject o, int position) {
        if (!Connectivity.isConnected()) {
            showCheckConnection();
            return;
        }
        if (!UserInfo.isLogin())
            UserInfo.goToLogin(getActivity());
//        o = ConvertUtils.toJsonObject(o.get("Notification"));
        String id = ConvertUtils.toString(o.get("Id"));
        IntentServicePost.startActionSetReadNotification(getActivity(), id, UserInfo.getCurrentUserId());
        int count = ConvertUtils.toInt(DataCacheInfo.getData(getActivity(), DataCacheInfo.EnumCacheType.NotificationCount,UserInfo.getCurrentUserId()),0);
        if(count>0)
            count--;
        DataCacheInfo.setData(getActivity(), DataCacheInfo.EnumCacheType.NotificationCount,UserInfo.getCurrentUserId(),0);

        Intent intent=new Intent(Constants.ACTION_NOTIFICATION);
        intent.putExtra(Constants.USER_ID,UserInfo.getCurrentUserId());
        intent.putExtra(Constants.NOTIFICATION_COUNT,count);
        sendBroadcast(intent);

        o.addProperty("IsRead", true);
        adapter.notifyItemChanged(position);

        final JsonObject finalJsonObject = o;
        int type = ConvertUtils.toInt(finalJsonObject.get("Type"));
        NotificationInfo.EnumNotification enumNotification = NotificationInfo.EnumNotification.Case1.valueOf(type);
        String detailId;
        String parentId;
        switch (enumNotification) {

            case Case1:
                intent = new Intent(Constants.ACTION_BOOKING);
                sendBroadcast(intent);
                intent = new Intent(getActivity(), UpdateActivity.class);
                intent.putExtra(Constants.FROM_NOTIFICATION, true);
                startActivity(intent);
                finish();
                break;
        }

    }

    private void getData(final int page, final int pageSize) {
        new AsyncTask<Void, JsonObject, Void>() {
            @Override
            protected void onPreExecute() {
                try {
                    if (page != 1) {
                        loadMore.setVisibility(View.VISIBLE);
                    } else {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                loadMore.setVisibility(View.GONE);
                super.onPostExecute(aVoid);
            }

            @Override
            protected void onProgressUpdate(JsonObject... values) {
                if (values != null && values.length != 0 && values[0] != null) {
                    JsonArray jsonArray = ConvertUtils.toJsonArray(values[0].get("Data"));

                    if(page == 1)
                        listObjects.clear();
                    listObjects.addAll(ConvertUtils.toArrayList(jsonArray));
                    if (listObjects.size() == 0) {
                        hasShowAll = true;
                    }
                    adapter.notifyDataSetChanged();
                }
                super.onProgressUpdate(values);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    publishProgress(NotificationInfo.getListWithPaging(getActivity(),page, pageSize));

                } catch (UnknownHostException e) {
                    showTimeOutError();

                } catch (SocketTimeoutException e) {
                    showTimeOutError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return AnalyticInfo.Screen.ScreenNotification;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAvatar)
        ImageView ivAvatar;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvTime)
        TextView tvTime;

        @OnClick(R.id.tvTitle)
        void readNotification() {
            JsonObject jsonObject = listObjects.get(getAdapterPosition());
            viewNotification(jsonObject, getAdapterPosition());

        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @OnClick(R.id.btnClose)
    void close() {
        finish();
    }

}
