package com.erg.morsytalky.util;

import android.content.Context;
import android.util.Size;

import com.erg.morsytalky.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Constants {
    /*VIBRATION*/
    public static final long VIBRATE_TIME = 12;
    public static final long SPECIAL_VIBRATE_TIME = 70;
    public static final long SPECIAL_MIN_VIBRATE_TIME = 50;
    public static final long MIN_VIBRATE_TIME = 10;

    public static final char SPACE = ' ';
    public static final String REGEX_SPACE = "\\s+";

    /*FLASH*/
    public final static long TIME_UNIT = 270L;
    public final static long DOT_DELAY = TIME_UNIT * 2;
    public final static long DASH_DELAY = TIME_UNIT * 3;
    public final static long INTRA_LETTER_DELAY = TIME_UNIT;
    public final static long INTER_LETTER_DELAY = TIME_UNIT * 3;
    public final static long INTER_WORD_DELAY = TIME_UNIT * 7;

    /*TEXT SIZES*/
    public static final int MIN_TEXT_SIZE = 12;
    public static final int TEXT_SIZE = 19;

    /*Blank*/
    public static final int BLANK = '-';

    /*TIME DELAY*/
    public final static long DEFAULT_TIME = 1000;

    public static ArrayList<String> getBrands(Context context) {
        ArrayList<String> brands = new ArrayList<>();
        Collections.addAll(brands, context.getResources().getStringArray(R.array.brands));
        return brands;
    }

    public static Character[] english = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            ',', '.', '?' };

    public static String[] morse = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
            ".---", "-.-", ".-..", "--", "-.", "---", ".---.", "--.-", ".-.",
            "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.",
            "-----", "--..--", ".-.-.-", "..--.." };
}
