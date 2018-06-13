package com.isorasoft.mllibrary.utils;

import android.util.Log;

/**
 * Created by MaiNam on 11/8/2016.
 */

public class YoutubeUtils {
    private static final String TAG = YoutubeUtils.class.getSimpleName();

    public static String getVideoIdFromUrl(String url) {

        String videoId = getVideoIdFromUrl1(url);
        Log.d(TAG, "getVideoIdFromUrl: " + videoId);
        return videoId;
    }

    public static String getVideoIdFromUrl1(String url) {
        try {
            String[] video_ids = url.split("v=");
            if (video_ids.length == 1)
                return video_ids[0];
            String video_id = video_ids[1];
            int ampersandPosition = video_id.indexOf('&');
            if (ampersandPosition != -1) {
                return video_id.substring(0, ampersandPosition);
            }
            return video_id;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLinkThumbnail(String url) {
        String videoId = getVideoIdFromUrl(url);
        return "http://img.youtube.com/vi/" + videoId + "/0.jpg";

    }

    public static String getLinkFromId(String id) {
        return "https://www.youtube.com/watch?v=" + id;
    }
}
