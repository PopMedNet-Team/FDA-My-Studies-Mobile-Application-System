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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Naveen Raj on 05/13/2017.
 */

public class MultiChoiceTextQuestionBody<T> implements StepBody, CompoundButton.OnCheckedChangeListener {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<T[]> result;
    private MultiChoiceTextAnswerFormat format;
    private ChoiceTextExclusive<T>[] choices;
    private Set<T> currentSelected;
    private T exclusiveValue;
    private int exclusivePosition;

    public MultiChoiceTextQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (MultiChoiceTextAnswerFormat) this.step.getAnswerFormat1();
        this.choices = format.getTextChoices();

        // Restore results
        currentSelected = new HashSet<>();

        T[] resultArray = this.result.getResult();
        if (resultArray != null && resultArray.length > 0) {
            currentSelected.addAll(Arrays.asList(resultArray));
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
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
        final ArrayList<AppCompatCheckBox> selectedcheckbox = new ArrayList<>();
        final ArrayList<CompoundButton.OnCheckedChangeListener> checkedChangeListenerArrayList = new ArrayList<>();
        RadioGroup radioGroup = new RadioGroup(inflater.getContext());
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        radioGroup.setDividerDrawable(ContextCompat.getDrawable(parent.getContext(),
                R.drawable.rsb_divider_empty_8dp));

        for (int i = 0; i < choices.length; i++) {
            final ChoiceTextExclusive<T> item = choices[i];

            // Create & add the View to our body-view
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.checkboxdesc, radioGroup, false);
            final AppCompatCheckBox checkBox = (AppCompatCheckBox) linearLayout.findViewById(R.id.checkbox);
            final TextView descTxt = (TextView) linearLayout.findViewById(R.id.desc);
            checkBox.setText(item.getText());
            descTxt.setText(item.getDetail());
            checkBox.setId(i);
            radioGroup.addView(linearLayout);

            // Set initial state
            if (currentSelected != null && currentSelected.contains(item.getValue())) {
                checkBox.setChecked(true);
                selectedcheckbox.add(checkBox);
            }

            // Update result when value changes
            CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (currentSelected.contains(exclusiveValue)) {
                            for (int i = 0; i < selectedcheckbox.size(); i++) {
                                selectedcheckbox.get(i).setOnCheckedChangeListener(null);
                                selectedcheckbox.get(i).setChecked(false);
                                selectedcheckbox.get(i).setOnCheckedChangeListener(checkedChangeListenerArrayList.get(selectedcheckbox.get(i).getId()));
                            }
                            selectedcheckbox.clear();
                            currentSelected.clear();
                        }


                        if (item.isExclusive()) {
                            exclusiveValue = item.getValue();
                            for (int i = 0; i < selectedcheckbox.size(); i++) {
                                selectedcheckbox.get(i).setOnCheckedChangeListener(null);
                                selectedcheckbox.get(i).setChecked(false);
                                selectedcheckbox.get(i).setOnCheckedChangeListener(checkedChangeListenerArrayList.get(selectedcheckbox.get(i).getId()));
                            }
                            selectedcheckbox.clear();
                            currentSelected.clear();
                        }
                        selectedcheckbox.add(checkBox);
                        currentSelected.add(item.getValue());
                    } else {
                        selectedcheckbox.remove(checkBox);
                        currentSelected.remove(item.getValue());
                    }
                }
            };
            checkedChangeListenerArrayList.add(onCheckedChangeListener);
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        return radioGroup;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

        TextView label = (TextView) inflater.inflate(R.layout.rsb_item_text_view_title_compact,
                compactView,
                false);
        label.setText(step.getTitle());

        compactView.addView(label, 0);

        return compactView;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            currentSelected.clear();
            result.setResult((T[]) currentSelected.toArray());
        } else {
            result.setResult((T[]) currentSelected.toArray());
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (currentSelected.isEmpty()) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
        } else {
            return BodyAnswer.VALID;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}