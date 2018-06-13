package com.isorasoft.mllibrary.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MaiNam on 4/23/2016.
 */
public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();
    private static final String USERNAME_PATTERN = "[A-Za-z0-9_.@]";
    private static final String PHONE_NUMBER = "[0-9]";

    private static char[] SPECIAL_CHARACTERS = {' ', '!', '"', '#', '$', '%',
            '*', '+', ',', ':', '<', '=', '>', '?', '@', '[', '\\', ']', '^',
            '`', '|', '~', 'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò',
            'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â', 'ã', 'è', 'é', 'ê',
            'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý', 'Ă', 'ă', 'Đ', 'đ',
            'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ',
            'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ', 'Ắ', 'ắ', 'Ằ', 'ằ',
            'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế',
            'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị',
            'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ', 'Ộ',
            'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ',
            'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ', 'ữ', 'Ự', 'ự',};

    private static char[] REPLACEMENTS = {' ', '\0', '\0', '\0', '\0', '%',
            '\0', '_', '\0', '_', '\0', '\0', '\0', '\0', '\0', '\0', '_',
            '\0', '\0', '\0', '\0', '\0', 'A', 'A', 'A', 'A', 'E', 'E', 'E',
            'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a', 'a', 'a',
            'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A',
            'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I',
            'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u',};

    public static String toUrlFriendly(String s) {
        int maxLength = Math.min(s.length(), 236);
        char[] buffer = new char[maxLength];
        int n = 0;
        for (int i = 0; i < maxLength; i++) {
            char ch = s.charAt(i);
            buffer[n] = removeAccent(ch);
            // skip not printable characters
            if (buffer[n] > 31) {
                n++;
            }
        }
        // skip trailing slashes
        while (n > 0 && buffer[n - 1] == '/') {
            n--;
        }
        return String.valueOf(buffer, 0, n);
    }

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SPECIAL_CHARACTERS, ch);
        if (index >= 0) {
            ch = REPLACEMENTS[index];
        }
        return ch;
    }

    public static boolean validateUserName(String s){
        for(char s1: s.toCharArray()){
            String s2 = String.valueOf(s1);
            Pattern p = Pattern.compile(USERNAME_PATTERN);
            Matcher m = p.matcher(s2);
            if(!m.matches()){
                return false;
            }
        }
        return true;
    }

    public static boolean validatePhoneNumber(String s){
        for(char s1: s.toCharArray()){
            String s2 = String.valueOf(s1);
            Pattern p = Pattern.compile(PHONE_NUMBER);
            Matcher m = p.matcher(s2);
            if(!m.matches()){
                return false;
            }
        }
        return true;
    }

    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    public static String removeAccent(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static String removeAccentLowerCase(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString().toLowerCase();
    }

    public static class Validate {
        public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]{4,}@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        public static final Pattern VALID_YEAR_REGEX =
                Pattern.compile("^\\d{4}$", Pattern.CASE_INSENSITIVE);

        public static final Pattern VALID_PERSONAL_NAME_REGEX =
                Pattern.compile("[A-Z][a-z]+( [A-Z][a-z]+)?", Pattern.CASE_INSENSITIVE);

        public static final Pattern VALID_PHONE_NUMBER_REGEX =
                Pattern.compile("^(0|\\+\\d{1,3})\\d{6,10}$", Pattern.CASE_INSENSITIVE);
        private static final Pattern VALID_DATE_REGEX = Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", Pattern.CASE_INSENSITIVE);


        public static boolean isEmail(CharSequence emailStr) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
            return matcher.find();
        }

        public static boolean isPhone(CharSequence emailStr) {
            Matcher matcher = VALID_PHONE_NUMBER_REGEX.matcher(emailStr);
            return matcher.find();
        }

        public static boolean isDate(CharSequence dateStr) {
            Matcher matcher = VALID_DATE_REGEX.matcher(dateStr);
            return matcher.find();
        }


        public static boolean isYear(CharSequence yearStr) {
            Matcher matcher = VALID_YEAR_REGEX.matcher(yearStr);
            return matcher.find();
        }


        public static boolean isPersonalName(CharSequence emailStr) {
            Matcher matcher = VALID_PERSONAL_NAME_REGEX.matcher(emailStr);
            return matcher.find() && emailStr.length() >= 6 && emailStr.length() <= 255;
        }


        public static boolean isPassword(String s) {
            return s.length() >= 6 && s.length() <= 30;
        }
        public static boolean isPassword(Editable s) {
            return isPassword(s.toString());
        }
    }

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String join(String split, String[] values) {
        return TextUtils.join(split, values);
    }

    public static String join(String split, List<String> values) {
        return TextUtils.join(split, values);
    }

    public static String join(String split, JsonArray values) {
        try {
            return TextUtils.join(split, values);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getText(String text, int limitChar, String readMore) {
        if (text.length() < limitChar)
            return text;
        return text.substring(0, limitChar) + " " + readMore;
    }

    public static Spanned fromHtml(String html) {
        Log.d(TAG, "fromHtml: "+html);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static Spanned fromHtml(Context context, @StringRes int resString) {
        return fromHtml(context.getString(resString));
    }

    /**
     * Kiem tra string null or empty
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str){
        return (str==null || str.trim().equals(""));
    }

}
