package com.isorasoft.phusan.realmmodel;

/**
 * Created by MaiNam on 11/24/2016.
 */

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class FirstDataEntity extends RealmObject {

    @PrimaryKey
    @Required
    String Type;
    String Data;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}