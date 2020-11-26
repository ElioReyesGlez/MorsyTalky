package com.erg.morsytalky.util;

import android.content.Context;

import com.erg.morsytalky.R;

import java.util.ArrayList;
import java.util.Collections;

public class Constants {
    /*VIBRATION*/
    public static final long VIBRATE_TIME = 12;
    public static final long SPECIAL_VIBRATE_TIME = 70;
    public static final long SPECIAL_MIN_VIBRATE_TIME = 50;
    public static final long MIN_VIBRATE_TIME = 10;

    public static ArrayList<String> getBrands(Context context) {
        ArrayList<String> brands = new ArrayList<>();
        Collections.addAll(brands, context.getResources().getStringArray(R.array.brands));
        return brands;
    }
}
