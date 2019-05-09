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

package com.harvard.fda.studyAppModule.consent.ConsentSharingStepCustom;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.harvard.fda.R;

import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

/**
 * Created by Naveen Raj on 09/04/2017.
 */

public class SingleChoiceSharingStepBody<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private ConsentSharingStepCustom step;
    private StepResult<T> result;
    private ChoiceAnswerFormat format;
    private Choice<T>[] choices;
    private T currentSelected;

    public SingleChoiceSharingStepBody(Step step, StepResult result) {
        this.step = (ConsentSharingStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (ChoiceAnswerFormat) this.step.getAnswerFormat();
        this.choices = format.getChoices();

        // Restore results
        T resultValue = this.result.getResult();
        if (resultValue != null) {
            for (Choice<T> choice : choices) {
                if (choice.getValue().equals(resultValue)) {
                    currentSelected = choice.getValue();
                }
            }
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

    private View initViewDefault(final LayoutInflater inflater, ViewGroup parent) {

        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) LinearLayout.LayoutParams.WRAP_CONTENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 20);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        TextView label = new TextView(inflater.getContext());
        label.setText("Load more");
        label.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
        label.setLayoutParams(params);

        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), LoadMoreActivity.class);
                intent.putExtra("htmlcontent", "" + step.getLoadmoretxt());
                inflater.getContext().startActivity(intent);
            }
        });

        linearLayout.addView(label);

        RadioGroup radioGroup = new RadioGroup(parent.getContext());
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        radioGroup.setDividerDrawable(ContextCompat.getDrawable(parent.getContext(),
                R.drawable.rsb_divider_empty_8dp));



        for (int i = 0; i < choices.length; i++) {
            Choice choice = choices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.rsb_item_radio,
                    radioGroup,
                    false);
            radioButton.setText(choice.getText());
            radioButton.setId(i);

            if (currentSelected != null) {
                radioButton.setChecked(currentSelected.equals(choice.getValue()));
            }

            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Choice<T> choice = choices[checkedId];
                currentSelected = choice.getValue();
            }
        });

        linearLayout.addView(radioGroup);
        return linearLayout;
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
            result.setResult(null);
        } else {
            result.setResult(currentSelected);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (currentSelected == null) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
        } else {
            return BodyAnswer.VALID;
        }
    }

}
