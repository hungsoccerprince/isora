package com.isorasoft.phusan.model;

import java.io.Serializable;

/**
 * Created by xuanhung on 6/2/18.
 */

public class FirstNumber implements Serializable {

    private String oldValue;
    private String newValue;
    public FirstNumber(String oldValue, String newValue){
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
