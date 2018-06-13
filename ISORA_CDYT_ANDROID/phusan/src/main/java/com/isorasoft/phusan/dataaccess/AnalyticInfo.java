package com.isorasoft.phusan.dataaccess;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MaiNam on 2/8/2017.
 */

public class AnalyticInfo {

    public static class AnalyticMap {
        Map<String, String> map;

        public Map<String, String> getMap() {
            if (map == null)
                map = new HashMap<>();
            return map;
        }

        public AnalyticMap add(ActionKey key, String value) {
            if (map == null)
                map = new HashMap<>();
            map.put(key.name(), value);
            return this;
        }
    }

    public static Tracker getmTracker() {
        return mTracker;
    }

    public static void setmTracker(Tracker mTracker) {
        AnalyticInfo.mTracker = mTracker;
        mTracker.enableAutoActivityTracking(false);
    }

    static Tracker mTracker;

    public enum Screen {
        ScreenSplash,
        ScreenHome,
        ScreenQuestionNoAnswer,
        ScreenPopularTags,
        ScreenDetailTag,
        ScreenMyPage,
        ScreenMyAccount,
        ScreenNotification,
        ScreenMailBox,
        ScreenChat,
        ScreenSetting,
        ScreenPolicies,
        ScreenTerms,
        ScreenUserPage,
        ScreenUserAccount,
        ScreenChangePassword,
        ScreenLogin,
        ScreenRegister,
        ScreenForgotPassword,
        ScreenDetailPost,
        ScreenDetailComment,
        ScreenCreateQuestion,
        ScreenBooking,
        ScreenDetailHospital,
        ScreenDetailBooking,
        ScreenBookingViewItemResult,
        ScreenListBooking,
        ScreenHistoryBooking,
        ScreenCreateProfile, ScreenProfile, ScreenViewProfile, ScreenSelectProfile, ScreenDetailResultChecking, ScreenDetailItemResultChecking, ScreenSearch
    }

    public static void sendAnanlyticOpenScreen(Screen screen) {
        try {
            if (screen == null)
                return;
            mTracker.setScreenName(screen.name());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void sendAnanlyticAction(CategoryAnalytic categoryAnalytic, ActionAnalytic actionAnalytic, String label, AnalyticMap map) {
        try {
            if (map == null || map.getMap().size() == 0) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryAnalytic.name())
                        .setAction(actionAnalytic.name())
                        .setLabel(label)
                        .build());
            } else
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryAnalytic.name())
                        .setAction(actionAnalytic.name())
                        .setLabel(label)
                        .setCustomDimension(0, map.getMap().toString())
                        .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum ActionKey {
        TagName,
        PostTitle,
        UserId, CommentId, CommentContent, PostId
    }


    public enum CategoryAnalytic {
        Tag, Post,
    }

    public enum ActionAnalytic {
        OpenApp,
        Login,
        Logout,

        LikePost,
        UnLikePost,
        CreatePostWithImage,
        CreatePostWithoutImage,
        CreateCommentLevel1,
        MarkSolution,
        UnMarkSolution,
        FollowPost,
        UnFollowPost,
        ClickTagOnFeed,
        ClickTagInBar,
        ClickTagInPage,
        ClickTagInDetailPost,
        FollowTag,
        UnFollowTag,
    }
}
