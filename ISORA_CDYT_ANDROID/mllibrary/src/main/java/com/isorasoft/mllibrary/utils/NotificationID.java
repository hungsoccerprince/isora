package com.isorasoft.mllibrary.utils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by MaiNam on 9/1/2016.
 */

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    static HashMap<String, Integer> dataNotification = new HashMap<>();

    public static int getID() {
        return c.incrementAndGet();
    }

    public static int getID(String key) {
        try {
            if (dataNotification.containsKey(key))
                return dataNotification.get(key);
            int value = c.incrementAndGet();
            dataNotification.put(key, value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.incrementAndGet();
    }

}
