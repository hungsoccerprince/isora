package com.isorasoft.mllibrary.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MaiNam on 4/27/2016.
 */
public class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, _Type extends Object> {

    RecyclerView.Adapter adapter = null;
    private int mLayoutId;
    private List<_Type> mListData;
    private _Type[] mArrayData;
    private JsonArray mJsonArrayData;
    private OnClickListener<_Type> mListener;


    public interface BaseViewHolder<VH, _Type> {
        VH getViewHolder(View v);

        void bindData(VH viewHolder, _Type data, int position);
    }

    public interface BaseMultipleViewHolder<VH, _Type> {
        int getViewType(int position);

        View getView(int viewType);

        VH getViewHolder(View v, int viewType);

        void bindData(VH viewHolder, _Type data, int position, int viewType);
    }

    private BaseViewHolder<VH, _Type> mBaseViewHolder;
    private BaseMultipleViewHolder<VH, _Type> mBaseMultipleViewHolder;

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public BaseRecyclerAdapter(int resouceLayout, BaseViewHolder<VH, _Type> mBaseViewHolder, JsonArray mListData, OnClickListener mListener) {
        this.mLayoutId = resouceLayout;
        this.mBaseViewHolder = mBaseViewHolder;
        this.mListData = null;
        this.mArrayData = null;
        this.mJsonArrayData = mListData;
        this.mListener = mListener;
    }

    public BaseRecyclerAdapter(BaseMultipleViewHolder<VH, _Type> mBaseViewHolder, JsonArray mListData, OnClickListener mListener) {
        this.mBaseMultipleViewHolder = mBaseViewHolder;
        this.mListData = null;
        this.mArrayData = null;
        this.mJsonArrayData = mListData;
        this.mListener = mListener;
    }

    public BaseRecyclerAdapter(int resouceLayout, BaseViewHolder<VH, _Type> mBaseViewHolder, List<_Type> mListData, OnClickListener mListener) {
        this.mLayoutId = resouceLayout;
        this.mBaseViewHolder = mBaseViewHolder;
        this.mListData = mListData;
        this.mJsonArrayData = null;
        this.mArrayData = null;
        this.mListener = mListener;
    }

    public BaseRecyclerAdapter(BaseMultipleViewHolder<VH, _Type> mBaseViewHolder, List<_Type> mListData, OnClickListener mListener) {
        this.mBaseMultipleViewHolder = mBaseViewHolder;
        this.mListData = mListData;
        this.mJsonArrayData = null;
        this.mArrayData = null;
        this.mListener = mListener;
    }

    public void setData(List<_Type> mListData) {
        this.mListData = mListData;
        this.mJsonArrayData = null;
        this.mArrayData = null;
    }

    public void setData(JsonArray mListData) {
        this.mJsonArrayData = mListData;
        this.mListData = null;
        this.mArrayData = null;
    }

    public void setData(_Type[] mListData) {
        this.mArrayData = mListData;
        this.mListData = null;
        this.mJsonArrayData = null;
    }


    public BaseRecyclerAdapter(int resouceLayout, BaseViewHolder<VH, _Type> mBaseViewHolder, _Type[] mListData, OnClickListener<_Type> mListener) {
        this.mLayoutId = resouceLayout;
        this.mBaseViewHolder = mBaseViewHolder;
        this.mListData = null;
        this.mArrayData = mListData;
        this.mJsonArrayData = null;
        this.mListener = mListener;
    }

    public BaseRecyclerAdapter(BaseMultipleViewHolder<VH, _Type> mBaseViewHolder, _Type[] mListData, OnClickListener<_Type> mListener) {
        this.mBaseMultipleViewHolder = mBaseViewHolder;
        this.mListData = null;
        this.mArrayData = mListData;
        this.mJsonArrayData = null;
        this.mListener = mListener;
    }

    int currentPosition = 0;

    public interface OnClickListener<_Type> {
        void onClick(View v, int position, _Type type);
    }

    HashMap<Integer, Integer> mapViewType = new HashMap<>();

    public void bindData(RecyclerView recyclerView) {
        adapter = new RecyclerView.Adapter<VH>() {
            @Override
            public int getItemViewType(int position) {
                if (mBaseMultipleViewHolder != null) {
                    int type = mBaseMultipleViewHolder.getViewType(position);
                    mapViewType.put(position, type);
                    return type;
                }
                return super.getItemViewType(position);
            }

            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                try {
                    Context context = parent.getContext();
                    LayoutInflater inflater = LayoutInflater.from(context);
                    // Inflate the custom layout
                    View view = null;
                    if (mBaseMultipleViewHolder != null)
                        view = mBaseMultipleViewHolder.getView(viewType);
                    else
                        view = inflater.inflate(mLayoutId, parent, false);
                    VH vh1 = null;
                    if (mBaseMultipleViewHolder != null)
                        vh1 = mBaseMultipleViewHolder.getViewHolder(view, viewType);
                    else
                        vh1 = mBaseViewHolder.getViewHolder(view);

                    final VH vh = vh1;

                    if (mListener != null)
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    currentPosition = vh.getAdapterPosition();
                                    _Type type = null;
                                    if (mListData != null)
                                        type = mListData.get(currentPosition);
                                    else if (mJsonArrayData != null)
                                        type = (_Type) mJsonArrayData.get(currentPosition).getAsJsonObject();
                                    else if (mArrayData != null)
                                        type = mArrayData[currentPosition];

                                    if (mListener != null)
                                        mListener.onClick(v, currentPosition, type);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    return vh;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    return onCreateViewHolder(parent, viewType);
                }
//                (VH) mBaseViewHolder.getViewHolder(view);
            }

            @Override
            public void onBindViewHolder(VH holder, int position) {
                try {
                    Log.d("onBindViewHolder", "onBindViewHolder: " + position);
                    _Type data;
                    if (mListData != null)
                        data = mListData.get(position);
                    else if (mJsonArrayData != null)
                        if (mJsonArrayData.get(position) instanceof JsonObject)
                            data = (_Type) mJsonArrayData.get(position).getAsJsonObject();
                        else
                            data = (_Type) mJsonArrayData.get(position);
                    else
                        data = (_Type) mArrayData[position];
                    if (mBaseMultipleViewHolder != null)
                        mBaseMultipleViewHolder.bindData(holder, data, position, mapViewType.get(position));
                    else
                        mBaseViewHolder.bindData(holder, data, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public int getItemCount() {
                return mListData != null ? mListData.size() : mJsonArrayData != null ? mJsonArrayData.size() : mArrayData != null ? mArrayData.length : 0;
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public void notifyDataSetChanged() {
        try {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            else
                Log.d("notifyDataSetChanged", "notifyDataSetChanged: null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyItemInserted(int position) {
        try {
            if (adapter != null) {
                if (adapter.getItemCount() > position)
                    adapter.notifyItemInserted(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyItemRemoved(int position) {
        try {
            if (adapter != null)
                adapter.notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyItemChanged(int position) {
        try {
            if (position >= 0 && position < adapter.getItemCount())
                adapter.notifyItemChanged(position);
            adapter.notifyItemChanged(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
