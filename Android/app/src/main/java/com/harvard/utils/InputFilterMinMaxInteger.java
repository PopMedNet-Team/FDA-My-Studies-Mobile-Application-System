package com.harvard.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Naveen Raj on 07/19/2017.
 */

public class InputFilterMinMaxInteger implements InputFilter {

    private int min, max;

    public InputFilterMinMaxInteger(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMaxInteger(String min, String max) {
        this.min = Integer.valueOf(min);
        this.max = Integer.valueOf(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            if ((dest.toString() + source.toString()).equalsIgnoreCase("-")) {
                return null;
            }
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}