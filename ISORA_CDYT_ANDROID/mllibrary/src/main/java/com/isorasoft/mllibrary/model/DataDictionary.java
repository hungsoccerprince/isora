package com.isorasoft.mllibrary.model;

/**
 * Created by MaiNam on 11/24/2016.
 */

public class DataDictionary {
    String key;
    String value;
    Object obj;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataDictionary(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Object getObj() {
        return obj;
    }

    public DataDictionary(String key, String value, Object obj) {
        this.key = key;
        this.value = value;
        this.obj = obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}