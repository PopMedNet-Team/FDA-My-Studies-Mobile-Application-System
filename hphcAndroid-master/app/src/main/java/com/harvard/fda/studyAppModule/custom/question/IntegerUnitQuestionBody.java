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

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;
import com.harvard.fda.utils.InputFilterMinMaxInteger;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.utils.TextUtils;

/**
 * Created by Naveen Raj on 06/16/2017.
 */

public class IntegerUnitQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<Integer> result;
    private IntegerUnitAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int viewType;
    private EditText editText;
    private TextView unit;

    public IntegerUnitQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (IntegerUnitAnswerFormat) this.step.getAnswerFormat1();
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        this.viewType = viewType;

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
        View formItemView = inflater.inflate(R.layout.unit_edittext, parent, false);
        editText = (EditText) formItemView.findViewById(R.id.value);
        unit = (TextView) formItemView.findViewById(R.id.txtunit);
        setFilters(parent.getContext());

        return formItemView;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        View formItemView = inflater.inflate(R.layout.unit_title_edittext, parent, false);

        TextView title = (TextView) formItemView.findViewById(R.id.label);
        title.setText(step.getTitle());
        editText = (EditText) formItemView.findViewById(R.id.value);
        unit = (TextView) formItemView.findViewById(R.id.txtunit);

        setFilters(parent.getContext());

        return formItemView;
    }

    private void setFilters(Context context) {
        editText.setSingleLine(true);
        final int minValue = format.getMinValue();
        // allow any positive int if no max value is specified
        final int maxValue = format.getMaxValue() == 0 ? Integer.MAX_VALUE : format.getMaxValue();
        unit.setText(format.getUnit());
        editText.setPadding(0, 0, (int) unit.getPaint().measureText(unit.getText().toString()) + 10, 0);
        if (step.getPlaceholder() != null) {
            editText.setHint(step.getPlaceholder());
        } else if (maxValue == Integer.MAX_VALUE) {
            editText.setHint(context.getString(R.string.rsb_hint_step_body_int_no_max));
        } else {
            editText.setHint(context.getString(R.string.rsb_hint_step_body_int,
                    minValue,
                    maxValue));
        }

        editText.setInputType(EditorInfo.TYPE_NUMBER_FLAG_SIGNED | EditorInfo.TYPE_CLASS_NUMBER);

        if (result.getResult() != null) {
            editText.setText(String.valueOf(result.getResult()));
        }


        editText.setFilters(new InputFilter[]{new InputFilterMinMaxInteger(Integer.MIN_VALUE, Integer.MAX_VALUE)});
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            String numString = editText.getText().toString();
            if (!TextUtils.isEmpty(numString) && !editText.getText().toString().equalsIgnoreCase("-")) {
                result.setResult(Integer.valueOf(editText.getText().toString()));
            }
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (editText == null) {
            return BodyAnswer.INVALID;
        }

        return format.validateAnswer(editText.getText().toString());
    }

}
