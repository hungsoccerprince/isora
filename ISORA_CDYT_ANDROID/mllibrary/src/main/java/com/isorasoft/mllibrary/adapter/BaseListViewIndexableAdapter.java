package com.isorasoft.mllibrary.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;


import com.isorasoft.mllibrary.utils.StringUtils;

import java.util.List;

/**
 * Created by MaiNam on 5/31/2016.
 */
public class BaseListViewIndexableAdapter<T, VH> extends ArrayAdapter<T> implements SectionIndexer {

    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private Activity mContext;
    private List<T> objects;
    private int layoutItem;
    private CompareListener<T> compareListener;
    private CreateViewHolderListener<VH, T> createViewHolderListener;

    public interface CompareListener<T> {
        boolean compare(T itemName, String section);
    }

    public interface CreateViewHolderListener<VH, T> {
        VH createViewHolder(View v);

        void bindData(VH viewHolder, T data, int position);
    }


    public BaseListViewIndexableAdapter(Activity context, int layoutItem
            , List<T> objects
            , CompareListener<T> compareListener
            , CreateViewHolderListener<VH, T> createViewHolderListener) {
        super(context, layoutItem, objects);
        this.mContext = context;
        this.objects = objects;
        this.layoutItem = layoutItem;
        this.createViewHolderListener = createViewHolderListener;
        this.compareListener = compareListener;
    }

    public static char getSection(String name) {
        name = StringUtils.removeAccent(name.toUpperCase());

        if (name.isEmpty())
            return '#';
        char first = name.charAt(0);
        if (first >= 'A' && first <= 'Z')
            return first;
        return '#';
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(layoutItem, parent, false);
            if (createViewHolderListener != null) {
                viewHolder = createViewHolderListener.createViewHolder(convertView);
//
//                viewHolder = new ViewHolder();
//                viewHolder.label = (TextView) convertView.findViewById(R.id.tvLable);
//                viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (VH) convertView.getTag();
        }

        if (createViewHolderListener != null && viewHolder != null)
            createViewHolderListener.bindData(viewHolder, objects.get(position), position);
        return convertView;
    }

    @Override
    public int getPositionForSection(int section) {
        // If there is no item for current section, previous section will be selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        if (compareListener != null && compareListener.compare(getItem(j), String.valueOf(k)))
                            return j;
//                            if (StringMatcher.match(String.valueOf(StringUtils.removeAccent(getItem(j)).trim().toUpperCase().charAt(0)), String.valueOf(k).toUpperCase()))
//                                return j;
                    }
                } else {
                    if (compareListener != null && compareListener.compare(getItem(j), String.valueOf(mSections.charAt(i))))
                        return j;
//                        if (StringMatcher.match(String.valueOf(StringUtils.removeAccent(getItem(j)).trim().toUpperCase().charAt(0)), String.valueOf(mSections.charAt(i)).toUpperCase()))
//                            return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }
}