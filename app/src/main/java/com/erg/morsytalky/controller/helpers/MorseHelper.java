package com.erg.morsytalky.controller.helpers;

import com.erg.morsytalky.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

import static com.erg.morsytalky.util.Constants.BLANK;
import static com.erg.morsytalky.util.Constants.REGEX_SPACE;
import static com.erg.morsytalky.util.Constants.SPACE;

public class MorseHelper {

    public static String getMorseFromString(String message) {
        StringBuilder morse = new StringBuilder();
        String[] splitMessage = message.toLowerCase().split(REGEX_SPACE);
        ArrayList<Character> auxList = new ArrayList<>(Arrays.asList(Constants.english));

        for (String word: splitMessage){
            for (char token : word.toCharArray()) {
                int charIndex = auxList.indexOf(token);
                if (charIndex != -1) {
                    morse.append(Constants.morse[charIndex]);
                }
            }
            morse.append(SPACE);
        }
        return morse.toString();
    }

    public static char getCharFromMorse(String morse) {
        ArrayList<String> morseList = new ArrayList<>(Arrays.asList(Constants.morse));
        ArrayList<Character> characters = new ArrayList<>(Arrays.asList(Constants.english));
        if (!morse.isEmpty()) {
            int morseIndex = morseList.indexOf(morse);
            if (morseIndex != -1 ) {
                return characters.get(morseIndex);
            }
        }
        return BLANK;
    }
}
