package com.iamservice.kernel.utils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class DataUtils {
    private DataUtils() {}

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\+?\\d{9,11}$");

    public static boolean isNull(Object o) { return o == null; }

    public static boolean nonNull(Object o) { return o != null; }

    public static boolean isEmpty(CharSequence s) { return s == null || s.length() == 0; }

    public static boolean isBlank(CharSequence s) {
        if (s == null) return true;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isNullOrEmpty(Collection<?> c) { return c == null || c.isEmpty(); }

    public static boolean isNullOrEmpty(Map<?, ?> m) { return m == null || m.isEmpty(); }

    public static boolean anyBlank(CharSequence... arr) {
        if (arr == null) return true;
        for (CharSequence s : arr) if (isBlank(s)) return true;
        return false;
    }

    public static boolean allBlank(CharSequence... arr) {
        if (arr == null) return true;
        for (CharSequence s : arr) if (!isBlank(s)) return false;
        return true;
    }

    public static boolean lengthBetween(String s, int min, int max) {
        if (s == null) return false;
        int len = s.length();
        return len >= min && len <= max;
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        return EMAIL_REGEX.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) return false;
        return PHONE_REGEX.matcher(phone).matches();
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) return false;
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (c < 'a' || c > 'z') return false;
        }
        return true;
    }

}
