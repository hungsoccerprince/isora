package com.isorasoft.phusan.dataaccess;

import android.util.Log;

import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.StringUtils;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by MaiNam on 12/27/2016.
 */

public class RequestFormInfo {
    private static String KEY = "123i@123sora";

    public static JSONObject getRequestForm() {
        JSONObject jsonObject = new JSONObject();
        try {
            String time = String.valueOf(new Date().getTime()/1000);
            String key = "";
            if(UserInfo.isLogin()){
                key = UserInfo.getCurrentUserId();
            }else {
                key = time;
            }
            JSONObject jsonObject1 = new JSONObject().put("Key", key).put("Token", StringUtils.getMD5(key + KEY));
            return jsonObject.accumulate("Auth", jsonObject1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getRequestFormNew() {
        JSONObject jsonObject = new JSONObject();
        try {
            String time = String.valueOf(new Date().getTime()/1000);
            String key = "";
            String loginToken = "";
            if(UserInfo.isLogin()){
                key = UserInfo.getCurrentUserId();
                loginToken = ConvertUtils.toString(UserInfo.getCurrentUser().get("LoginToken"));
            }else {
                key = time;
            }

            JSONObject jsonObject1 = new JSONObject().put("Key", key).put("Token", StringUtils.getMD5(key + KEY))
                    .put("LoginToken", loginToken);

            Log.d("RequestFormInfo", "getRequestFormNew: " + jsonObject1.toString());
            return jsonObject1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
