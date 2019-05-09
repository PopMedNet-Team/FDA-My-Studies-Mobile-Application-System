/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.studyAppModule;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;

import com.harvard.fda.R;

public class CustomDialogClass<V> extends Dialog implements View.OnClickListener {

    private NumberPicker mHourPicker = null;
    private NumberPicker minPicker = null;
    private AppCompatTextView mDoneBtn;
    private final ProfileFragment profileFragment;
    private final String[] mins15 = {"00", "15", "30", "45"};


    public CustomDialogClass(Activity a, ProfileFragment profileFragment) {
        super(a);
        this.profileFragment = profileFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cutom_dialog);
        mHourPicker = (NumberPicker) findViewById(R.id.picker_hour);
        minPicker = (NumberPicker) findViewById(R.id.picker_min);
        mDoneBtn = (AppCompatTextView) findViewById(R.id.doneBtn);
        mDoneBtn.setOnClickListener(this);
        // if hr=24 then setvalue 1 means min arrays 0'th pos value
        minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (mHourPicker.getValue() == 0 && minPicker.getValue() == 1) {
                    minPicker.setValue(2);
                }
            }
        });
        mHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (mHourPicker.getValue() == 0 && minPicker.getValue() == 1) {
                    minPicker.setValue(2);
                }
            }
        });

        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(23);
        mHourPicker.setValue(0);
        minPicker.setValue(1);
        setMinsPicker();
        mHourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

    }

    private void setMinsPicker() {
        int ml = mins15.length;
        // Prevent ArrayOutOfBoundExceptions by setting
        // values array to null so its not checked
        minPicker.setDisplayedValues(null);
        // 1 means value is '0'
        minPicker.setMinValue(1);
        minPicker.setMaxValue(ml);
        minPicker.setDisplayedValues(mins15);
    }

    @Override
    public void onClick(View v) {
        String selected_value = mHourPicker.getValue() + ":" + mins15[minPicker.getValue() - 1];
        profileFragment.updatePickerTime(selected_value);
        dismiss();
    }
}