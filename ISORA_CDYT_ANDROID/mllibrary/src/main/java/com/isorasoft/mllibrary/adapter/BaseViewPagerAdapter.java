package com.isorasoft.mllibrary.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by MaiNam on 4/19/2016.
 */
public class BaseViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = BaseViewPagerAdapter.class.getSimpleName();
    public int pos = 0;
    private Context mContext=null;

    private List<Fragment> myFragments;
    private List<String> TITLES;

    public BaseViewPagerAdapter(Context mContext, FragmentManager fm, List<Fragment> fragments, List<String> listTitle) {
        super(fm);
        this.mContext=mContext;
        myFragments = fragments;
        this.TITLES = listTitle;
    }

    @Override
    public int getCount() {

        return myFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        this.pos = position;
        if(position>TITLES.size())
            return "";
        return TITLES.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "Fragment getItem");
        return myFragments.get(position);
    }

}
