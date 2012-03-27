package com.neoriddle.locationshare.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * This class contains some utility methods for Android applications.
 *
 * @author Israel Buitron
 */
public class AndroidUtils {

    public static String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    /**
     * Get application version code.
     *
     * @param context
     * @return Application version code.
     */
    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (final NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get application version name.
     *
     * @param context
     * @return Application version name.
     */
    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (final NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a string contains a valid SMS number.
     *
     * @param number String to validate
     * @return <code>true</code> if valid, otherwise <code>false</code>.
     */
    public static boolean isValidSMSnumber(String number) {
        return number.matches("\\d{7,10}");
    }

    /**
     * Checks if a string contains a valid email address.
     *
     * @param number String to validate
     * @return <code>true</code> if valid, otherwise <code>false</code>.
     */
    public static boolean isValidEmailAddress(String email) {
        return email.matches(EMAIL_PATTERN);
    }
}
