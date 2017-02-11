package com.androidnerdcolony.idlefactory.module;

import android.annotation.SuppressLint;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by tiger on 1/23/2017.
 */

public class ConvertNumber {
    private static final String SCALE_NAME[] = new String[]{
            "",
            "T",
            "M",
            "B",
            "aa",
            "ab",
            "ac",
            "ad",
            "ae",
            "af",
            "ag",
            "ah",
            "ai",
            "aj",
            "ak",
            "al",
            "am",
            "an",
            "ao",
            "ap",
            "aq",
            "ar",
            "as",
            "at",
            "au",
            "av",
            "aw",
            "ax",
            "ay",
            "az",
            "ba",
            "bb",
            "bc",
            "bd",
            "be",
            "bf",
            "bg",
            "bh",
            "bi",
            "bj",
            "bk",
            "bl",
            "bm",
            "bn",
            "bo",
            "bp",
            "bq",
            "br",
            "bs",
            "bt",
            "bu",
            "bv",
            "bw",
            "bx",
            "by",
            "bz",
            "ca",
            "cb",
            "cc",
            "cd",
            "ce",
            "cf",
            "cg",
            "ch",
            "ci",
            "cj",
            "ck",
            "cl",
            "cm",
            "cn"
    };
    private static final NavigableMap<Integer, String> MAP;

    static {
        MAP = new TreeMap<>();

        for (int i = 0; i < SCALE_NAME.length; i++) {
            MAP.put(i, SCALE_NAME[i]);
        }
    }

    public static String numberToString(double number) {
        int powered = 0;
        double thisNumber = number;

        while (thisNumber > 1000) {
            thisNumber = thisNumber / 1000;
            powered++;
        }


        @SuppressLint("DefaultLocale")
        String numberString = String.format("%.2f", thisNumber);


        Map.Entry<Integer, String> entry = MAP.floorEntry(powered);

        if (entry == null) {
            return String.valueOf(number);
        }

        return numberString + " " + entry.getValue();
    }
}
