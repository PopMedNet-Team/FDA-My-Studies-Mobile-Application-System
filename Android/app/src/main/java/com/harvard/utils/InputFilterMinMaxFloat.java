package com.harvard.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Naveen Raj on 07/19/2017.
 */

public class InputFilterMinMaxFloat implements InputFilter {

    private Float min, max;

    public InputFilterMinMaxFloat(Float min, Float max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMaxFloat(String min, String max) {
        this.min = Float.valueOf(min);
        this.max = Float.valueOf(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            if ((dest.toString() + source.toString()).equalsIgnoreCase("-") || (dest.toString() + source.toString()).equalsIgnoreCase(".")) {
                return null;
            }
            float input = Float.parseFloat(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {

        }
        return "";
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}