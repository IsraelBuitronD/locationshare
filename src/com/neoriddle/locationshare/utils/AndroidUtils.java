package com.neoriddle.locationshare.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * This class contains some utility methods for Android applications.
 *
 * @author Israel Buitron
 */
public class AndroidUtils {

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
}
