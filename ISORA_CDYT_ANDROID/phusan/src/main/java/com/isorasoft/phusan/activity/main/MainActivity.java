package com.isorasoft.phusan.activity.main;


import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.isorasoft.mllibrary.utils.AndroidPermissionUtils;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.model.Contact;
import com.isorasoft.phusan.model.FirstNumber;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xuanhung on 6/2/18.
 */

public class MainActivity extends BaseActivity {

    public static String log = MainActivity.class.getSimpleName();


    @OnClick(R.id.btnUpdateByFirstNumber)
    void updateByFirstNumber(View view){
        Intent intent = new Intent(getActivity(), UpdateActivity.class);
        startActivity(intent, TypeSlideIn.in_right_to_left);
        setSlideOut(TypeSlideOut.out_left_to_right);
    }

    @OnClick(R.id.btnUpdateSimple)
    void updateSimple(View view){
        Intent intent = new Intent(getActivity(), ListContactActivity.class);
        startActivity(intent, TypeSlideIn.in_right_to_left);
    }

    @OnClick(R.id.btnUpdateAll)
    void update(View view){
        Intent intent = new Intent(getActivity(), ListContactActivity.class);
        startActivity(intent, TypeSlideIn.in_right_to_left);
    }

    @OnClick(R.id.btnFinish)
    void back(View v){
        getActivity().finish();
    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(getActivity());
    }

}
