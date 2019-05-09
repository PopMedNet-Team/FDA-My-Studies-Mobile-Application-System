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
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

/**
 * Created by Naveen Raj on 10/26/2016.
 */
public class MultiChoiceImageQuestionBody<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<T> result;
    private MultiChoiceImageAnswerFormat format;
    private ChoiceCustomImage<T>[] choices;
    private T currentSelected;
    Context mContext;

    public MultiChoiceImageQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (MultiChoiceImageAnswerFormat) this.step.getAnswerFormat1();
        this.choices = format.getChoicechoices();

        // Restore results
        T resultValue = this.result.getResult();
        if (resultValue != null) {
            for (ChoiceCustomImage<T> choice : choices) {
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
        layoutParams.leftMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_right);
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
        final ImageView[] pervioustxtview = new ImageView[1];
        View imagechoicequestionlayout = inflater.inflate(R.layout.imagechoicequestionlayout, parent, false);
        LinearLayout linearLayout = (LinearLayout) imagechoicequestionlayout.findViewById(R.id.imagecontainer);
        final TextView desc = (TextView) imagechoicequestionlayout.findViewById(R.id.desc);
        linearLayout.setWeightSum(choices.length);
        for (int i = 0; i < choices.length; i++) {
            final ChoiceCustomImage<T> item = choices[i];
            final ImageView imageView = new ImageView(inflater.getContext());
            imageView.setId(i);
            final LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            layoutParams.leftMargin = 20;
            layoutParams.rightMargin = 20;
            layoutParams.topMargin = 20;
            layoutParams.bottomMargin = 20;
            imageView.setLayoutParams(layoutParams);

            if (currentSelected != null && currentSelected.equals(item.getValue())) {
                byte[] imageByteArray = Base64.decode(item.getSelectedImage(), Base64.DEFAULT);
                Glide.with(inflater.getContext())
                        .load(imageByteArray)
                        .into(imageView);
                currentSelected = item.getValue();
                pervioustxtview[0] = imageView;
                desc.setText(item.getText());
            } else {
                byte[] imageByteArray = Base64.decode(item.getImage(), Base64.DEFAULT);
                Glide.with(inflater.getContext())
                        .load(imageByteArray)
                        .into(imageView);
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pervioustxtview[0] != null) {
                        byte[] imageByteArray = Base64.decode(choices[pervioustxtview[0].getId()].getImage(), Base64.DEFAULT);
                        Glide.with(inflater.getContext())
                                .load(imageByteArray)
                                .into(pervioustxtview[0]);
                    }
                    byte[] imageByteArray = Base64.decode(choices[imageView.getId()].getSelectedImage(), Base64.DEFAULT);
                    Glide.with(inflater.getContext())
                            .load(imageByteArray)
                            .into(imageView);
                    imageView.setLayoutParams(layoutParams);
                    pervioustxtview[0] = imageView;
                    currentSelected = (choices[imageView.getId()].getValue());
                    desc.setText(choices[imageView.getId()].getText());
                }
            });
            linearLayout.addView(imageView);
        }

        return imagechoicequestionlayout;
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
        if (currentSelected == null || currentSelected.equals("")) {
            return new BodyAnswer(false, org.researchstack.backbone.R.string.rsb_invalid_answer_choice);
        } else {
            return BodyAnswer.VALID;
        }
    }

}