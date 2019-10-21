package com.harvard.studyAppModule;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;

import com.harvard.R;

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