package com.isorasoft.phusan.dataaccess;

import android.content.Context;
import android.util.Log;

import com.isorasoft.phusan.realmmodel.FirstDataEntity;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.SharedPreferencesUtil;

import io.realm.Realm;
import io.realm.exceptions.RealmError;

/**
 * Created by MaiNam on 8/6/2016.
 */

public class DataCacheInfo {

    private static final String TAG = DataCacheInfo.class.getSimpleName();

    public enum EnumCacheType {
        NotificationCount, CurrentUser, ListAdmin
    }

    public static String getData(Realm realm, EnumCacheType enumCacheType) {
        try {
            FirstDataEntity firstDataEntity = realm.where(FirstDataEntity.class).equalTo("Type", enumCacheType.toString()).findFirst();
            if (firstDataEntity != null)
                return firstDataEntity.getData();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (RealmError e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getData(Realm realm, EnumCacheType enumCacheType, String key) {
        try {
            FirstDataEntity firstDataEntity = realm.where(FirstDataEntity.class).equalTo("Type", enumCacheType.toString() + "_" + key).findFirst();
            if (firstDataEntity != null)
                return firstDataEntity.getData();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (RealmError e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getData(Context context, EnumCacheType enumCacheType, String key) {
        try {
            Log.d(TAG, "getData: " + enumCacheType.toString() + "_" + key);
            String data = SharedPreferencesUtil.getString(context, enumCacheType.toString() + "_" + key, "");
            Log.d(TAG, "getData: " + data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setData(Context context, EnumCacheType enumCacheType, String key, Object data) {
        try {
            SharedPreferencesUtil.setSharedPreferences(context, SharedPreferencesUtil.EnumType.String, enumCacheType.toString() + "_" + key, data instanceof String ? data.toString() : ConvertUtils.toJson(data));
            Log.d(TAG, "setData: " + enumCacheType.toString() + "_" + key);
            Log.d(TAG, "setData: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setData(Realm realm, EnumCacheType enumCacheType, String data, boolean needTransation) {
        try {
            if (needTransation) {
                realm.beginTransaction();
                setData(realm, enumCacheType, data);
                realm.commitTransaction();
            } else
                setData(realm, enumCacheType, data);
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setData(Realm realm, EnumCacheType enumCacheType, String key, String data, boolean needTransation) {
        try {
            if (needTransation) {
                realm.beginTransaction();
                setData(realm, enumCacheType, key, data);
                realm.commitTransaction();
            } else
                setData(realm, enumCacheType, data);
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setData(Realm realm, EnumCacheType enumCacheType, String key, String data) {
        try {
            FirstDataEntity firstDataEntity =
                    realm.where(FirstDataEntity.class).equalTo("Type", enumCacheType.toString() + "_" + key).findFirst();
            if (firstDataEntity == null) {
                firstDataEntity = new FirstDataEntity();
                firstDataEntity.setData(data == null ? "" : data);
                firstDataEntity.setType(enumCacheType.toString() + "_" + key);
                realm.copyToRealm(firstDataEntity);
            } else {
                firstDataEntity.setData(data);
            }
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setData(Realm realm, EnumCacheType enumCacheType, String data) {
        try {
            FirstDataEntity firstDataEntity =
                    realm.where(FirstDataEntity.class).equalTo("Type", enumCacheType.toString()).findFirst();
            if (firstDataEntity == null) {
                firstDataEntity = new FirstDataEntity();
                firstDataEntity.setData(data == null ? "" : data);
                firstDataEntity.setType(enumCacheType.toString());
                realm.copyToRealm(firstDataEntity);
            } else {
                firstDataEntity.setData(data);
            }
        } catch (RealmError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}