package com.isorasoft.mllibrary.utils;

import android.content.Context;

import java.util.Set;

/**
 * Created by MaiNam on 4/19/2016.
 */
public class SharedPreferencesUtil {
    static String MY_PREFS_NAME = "congdongnhanai";

    public enum EnumType {
        String,
        Int,
        Bool,
        Float,
        Long, StringSet

    }

    public static boolean setSharedPreferences(Context context, EnumType type, String key, Object value) {
        try {
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
            switch (type) {
                case Bool:
                    editor.putBoolean(key, (boolean) value);
                    break;
                case Int:
                    editor.putInt(key, (int) value);
                    break;
                case String:
                    editor.putString(key, value.toString());
                    break;
                case Float:
                    editor.putFloat(key, (float) value);
                    break;
                case Long:
                    editor.putLong(key, (long) value);
                    break;
                case StringSet:
                    editor.putStringSet(key, (Set<String>) value);
                    break;
                default:
                    return false;
            }
            editor.commit();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static Integer getInt(Context context, String key, int defaultValue) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
        android.content.SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE);
        Integer value = prefs.getInt(key, defaultValue);
        return value;
    }
    public static String getString(Context context, String key, String defaultValue) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
        android.content.SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE);
        String value = prefs.getString(key, defaultValue);
        return value;
    }
    public static Float getFloat(Context context, String key, Float defaultValue) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
        android.content.SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE);
        Float value = prefs.getFloat(key, defaultValue);
        return value;
    }
    public static Long getLong(Context context, String key, Long defaultValue) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
        android.content.SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE);
        Long value = prefs.getLong(key, defaultValue);
        return value;
    }
    public static Boolean getBoolean(Context context, String key, Boolean defaultValue) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
        android.content.SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE);
        Boolean value = prefs.getBoolean(key, defaultValue);
        return value;
    }
    public static Set<String> getSetString(Context context, String key, Set<String> defaultValue) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE).edit();
        android.content.SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,  Context.MODE_PRIVATE);
        Set<String> value = prefs.getStringSet(key, defaultValue);
        return value;
    }

}
