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

package com.harvard.fda.studyAppModule.custom.question;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;


/**
 * Created by Naveen Raj on 04/14/2017.
 */

public class HeightQuestion implements StepBody {
    private QuestionStepCustom step;
    private StepResult<Double> result;
    boolean next;
    private EditText editText;
    private HeightAnswerFormat format;
    private NumberPicker cmPicker;
    private NumberPicker feet, inches;
    private TextView unit;
    private Double currentSelected;


    public HeightQuestion(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(this.step) : result;
        this.format = (HeightAnswerFormat) this.step.getAnswerFormat1();

        if (result != null) {
            currentSelected = this.result.getResult();
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

        TextView label = (TextView) inflater.inflate(org.researchstack.backbone.R.layout.rsb_item_text_view_title_compact,
                compactView,
                false);
        label.setText(step.getTitle());

        compactView.addView(label, 0);

        return compactView;
    }


    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            return initViewDefault(inflater, parent);
        } else if (viewType == VIEW_TYPE_COMPACT) {
            return initViewCompact(inflater, parent);
        } else {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initViewDefault(LayoutInflater inflater, ViewGroup parent) {
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View heightquestionlayout = null;
        if (format.getMeasurementSystem().equalsIgnoreCase("Metric")) {
            heightquestionlayout = inflater.inflate(R.layout.heightquestionlayout, parent, false);
            cmPicker = (NumberPicker) heightquestionlayout.findViewById(R.id.height);
            unit = (TextView) heightquestionlayout.findViewById(R.id.unit);

            String[] numberpickervalue = new String[300];
            for (int i = 0; i < numberpickervalue.length; i++) {
                numberpickervalue[i] = "" + i;
            }

            cmPicker.setMinValue(0);
            cmPicker.setMaxValue(299);
            cmPicker.setWrapSelectorWheel(false);
            cmPicker.setDisplayedValues(numberpickervalue);
            cmPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    currentSelected = Double.parseDouble("" + picker.getValue());
                }
            });
            unit.setText(inflater.getContext().getResources().getString(R.string.cm));

            if (currentSelected != null) {
                cmPicker.setValue(currentSelected.intValue());
            } else {
                cmPicker.setValue(1);
            }
            currentSelected = Double.parseDouble("" + cmPicker.getValue());
        } else {
            heightquestionlayout = inflater.inflate(R.layout.heightquestionuslayout, parent, false);
            feet = (NumberPicker) heightquestionlayout.findViewById(R.id.feet);
            inches = (NumberPicker) heightquestionlayout.findViewById(R.id.inches);

            String[] numberpickervalue = new String[10];
            for (int i = 0; i < numberpickervalue.length; i++) {
                numberpickervalue[i] = "" + i;
            }

            feet.setMinValue(0);
            feet.setMaxValue(9);
            feet.setWrapSelectorWheel(false);
            feet.setDisplayedValues(numberpickervalue);
            feet.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    currentSelected = Double.parseDouble(((feet.getValue() * 30.48) + (inches.getValue() * 2.54)) + "");
                }
            });

            String[] inchespicker = new String[12];
            for (int i = 0; i < inchespicker.length; i++) {
                inchespicker[i] = "" + i;
            }

            inches.setMinValue(0);
            inches.setMaxValue(11);
            inches.setWrapSelectorWheel(false);
            inches.setDisplayedValues(inchespicker);
            inches.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    currentSelected = Double.parseDouble(((feet.getValue() * 30.48d) + (inches.getValue() * 2.54d)) + "");
                }
            });


            if (currentSelected != null) {
                feet.setValue((int) (currentSelected / 30.48d));
                double inchesRem = currentSelected - Double.parseDouble("" + (Double.parseDouble("" + feet.getValue()) * 30.48d));
                inches.setValue((int) Math.round(inchesRem / 2.54d));
            } else {
                feet.setValue(1);
                inches.setValue(1);
            }

            currentSelected = Double.parseDouble(((feet.getValue() * 30.48d) + (inches.getValue() * 2.54d)) + "");

        }
        linearLayout.addView(heightquestionlayout);
        return linearLayout;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            result.setResult(currentSelected);
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        return BodyAnswer.VALID;
    }
}
