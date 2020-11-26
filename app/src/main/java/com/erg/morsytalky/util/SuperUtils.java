package com.erg.morsytalky.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import static com.erg.morsytalky.util.Constants.MIN_VIBRATE_TIME;
import static com.erg.morsytalky.util.Constants.SPECIAL_MIN_VIBRATE_TIME;
import static com.erg.morsytalky.util.Constants.SPECIAL_VIBRATE_TIME;
import static com.erg.morsytalky.util.Constants.VIBRATE_TIME;

public class SuperUtils {
    private static final String TAG = "SuperUtils";

    public static void vibrate(Context context) {
        long VIBRATION_TIME = getRightVibrationTime(context, true);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE));
            Log.d(TAG, "regular vibrate: VIBRATE_TIME VibrationEffect :" + VIBRATION_TIME);
        } else {
            v.vibrate(VIBRATION_TIME);
            Log.d(TAG, "regular vibrate: VIBRATE_TIME :" + VIBRATION_TIME);
        }
    }

    public static void vibrateMin(Context context) {

        long VIBRATION_TIME = getRightVibrationTime(context, false);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE));
            Log.d(TAG, "vibrateMin: MIN_VIBRATE_TIME VibrationEffect " + VIBRATION_TIME);
        } else {
            v.vibrate(VIBRATION_TIME);
            Log.d(TAG, "vibrateMin: MIN_VIBRATE_TIME " + VIBRATION_TIME);
        }
    }

    private static long getRightVibrationTime(Context context, boolean isRegular) {
        if (isRegular) {
            for (String brand : Constants.getBrands(context)) {
                if (Build.MANUFACTURER.toLowerCase().contains(brand.toLowerCase())) {
                    return SPECIAL_VIBRATE_TIME;
                }
                Log.d(TAG, "regular vibrate: flagSwitchVibrationTime: false");
            }
            return VIBRATE_TIME;
        } else {
            for (String brand : Constants.getBrands(context)) {
                if (Build.MANUFACTURER.toLowerCase().contains(brand.toLowerCase())) {
                    return SPECIAL_MIN_VIBRATE_TIME;
                }
                Log.d(TAG, "regular vibrate: flagSwitchVibrationTime: false");
            }
            return MIN_VIBRATE_TIME;
        }
    }
}
