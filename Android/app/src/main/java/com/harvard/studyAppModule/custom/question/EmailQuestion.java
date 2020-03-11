package com.harvard.studyAppModule.custom.question;

import android.content.res.Resources;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harvard.R;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ViewUtils;

import rx.functions.Action1;

/**
 * Created by Naveen Raj on 08/01/2017.
 */

public class EmailQuestion implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<String> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText editText;

    public EmailQuestion(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View body = inflater.inflate(R.layout.rsb_item_edit_text_compact, parent, false);

        editText = (EditText) body.findViewById(R.id.value);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (step.getPlaceholder() != null) {
            editText.setHint(step.getPlaceholder());
        } else {
            editText.setHint(R.string.rsb_hint_step_body_text);
        }

        TextView title = (TextView) body.findViewById(R.id.label);

        if (viewType == VIEW_TYPE_COMPACT) {
            title.setText(step.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        // Restore previous result
        String stringResult = result.getResult();
        if (!TextUtils.isEmpty(stringResult)) {
            editText.setText(stringResult);
        }

        // Set result on text change
        RxTextView.textChanges(editText).subscribe(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence text) {
                result.setResult(text.toString());
            }
        });

        // Format EditText from TextAnswerFormat
        EmailAnswerFormatCustom format = (EmailAnswerFormatCustom) step.getAnswerFormat1();


        if (format.getMaxEmailLength() > TextAnswerFormat.UNLIMITED_LENGTH) {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaxEmailLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        body.setLayoutParams(layoutParams);

        return body;
    }


    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        EmailAnswerFormatCustom format = (EmailAnswerFormatCustom) step.getAnswerFormat1();
        if (!format.isAnswerValid(editText.getText().toString())) {
            return BodyAnswer.INVALID;
        }

        return BodyAnswer.VALID;
    }

}
