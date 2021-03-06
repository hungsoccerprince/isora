package com.isorasoft.phusan.model;

import java.io.Serializable;

/**
 * Created by xuanhung on 6/2/18.
 */

public class Contact implements Serializable {

    private String phoneNumber;
    private String name;
    private String id;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
