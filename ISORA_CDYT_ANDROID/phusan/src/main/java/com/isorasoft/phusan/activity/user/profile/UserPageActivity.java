package com.isorasoft.phusan.activity.user.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;

import butterknife.BindView;
import butterknife.OnClick;

public class UserPageActivity extends BaseActivity {


    private String TAG = UserPageActivity.class.getName();
    private int role;


    public static void open(Context context, String userId, String name, String imageUrl, int role) {
        Intent intent = new Intent(context, UserPageActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        context.startActivity(intent);
        BaseActivity.setSlideIn(context, TypeSlideIn.in_right_to_left);

    }

    String name, imageUrl, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;


    @OnClick(R.id.btnClose)
    void close() {
        finish();
    }



    @Override
    public AnalyticInfo.Screen getScreen() {
        return AnalyticInfo.Screen.ScreenUserPage;
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            UserInfo.showAvatar(ivAvatar, UserInfo.getCurrentUserImageUrl());
//            tvName.setText(ConvertUtils.toString(UserInfo.getCurrentUser().get("FullName")));
//        }
//    }
}
