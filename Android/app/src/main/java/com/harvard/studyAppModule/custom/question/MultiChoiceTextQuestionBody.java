package com.harvard.studyAppModule.custom.question;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.harvard.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
    EditText otherText;
    OtherOptionModel mOtherOptionModel;
    String OtherOptionValue = "";
    boolean OtherOptionMandatory = false;
    boolean OtherOptionText = false;
    ArrayList<String> exclusivelist;


    public MultiChoiceTextQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (MultiChoiceTextAnswerFormat) this.step.getAnswerFormat1();
        mOtherOptionModel = new OtherOptionModel();
        this.choices = format.getTextChoices();
        exclusivelist = new ArrayList<>();

        for (int i = 0; i < choices.length; i++) {
            if (choices[i].getOther() != null) {
                ChoiceTextOtherOption choiceTextOtherOption = new ChoiceTextOtherOption();
                choiceTextOtherOption.setPlaceholder(choices[i].getOther().getPlaceholder());
                choiceTextOtherOption.setMandatory(choices[i].getOther().isMandatory());
                choiceTextOtherOption.setTextfieldReq(choices[i].getOther().isTextfieldReq());
                choices[i].setOther(choiceTextOtherOption);
            }
        }

        // Restore results
        currentSelected = new LinkedHashSet<>();

        ArrayList list = null;
        T[] resultArray = this.result.getResult();
        if (resultArray != null && resultArray.length > 0) {
            currentSelected.addAll(Arrays.asList(resultArray));


            list = new ArrayList(Arrays.asList(resultArray));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(list.get(i).toString());

                        mOtherOptionModel.setOther(jsonObject.getString("other"));
                        try {
                            mOtherOptionModel.setText(jsonObject.getString("text"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        currentSelected.remove(new Gson().toJson(mOtherOptionModel));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (int i = 0; i < choices.length; i++) {
            if (list != null && list.contains(choices[i].getValue().toString()) && choices[i].isExclusive()) {
                exclusiveValue = choices[i].getValue();
            }

            if (choices[i].getOther() != null) {
                OtherOptionValue = choices[i].getValue().toString();
                OtherOptionMandatory = choices[i].getOther().isMandatory();
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

    private View initViewDefault(final LayoutInflater inflater, final ViewGroup parent) {
        final ArrayList<AppCompatCheckBox> selectedcheckbox = new ArrayList<>();
        final ArrayList<CompoundButton.OnCheckedChangeListener> checkedChangeListenerArrayList = new ArrayList<>();
        final RadioGroup radioGroup = new RadioGroup(inflater.getContext());
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        radioGroup.setDividerDrawable(ContextCompat.getDrawable(parent.getContext(),
                R.drawable.rsb_divider_empty_8dp));

        if (choices.length > 6) {
            SearchView editText = new SearchView(inflater.getContext());
            editText.setIconifiedByDefault(false);
            editText.setIconified(false);
            editText.clearFocus();
            LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            editText.setLayoutParams(layoutParams);
            editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    for (int i = 0; i < choices.length; i++) {
                        if (!choices[i].getValue().toString().equalsIgnoreCase(OtherOptionValue)) {
                            if (choices[i].getText().toLowerCase().contains(s.toString().toLowerCase())) {
                                radioGroup.findViewWithTag(i).setVisibility(View.VISIBLE);
                            } else {
                                radioGroup.findViewWithTag(i).setVisibility(View.GONE);
                            }
                        }
                    }
                    return false;
                }
            });


            radioGroup.addView(editText);
        }
        otherText = new EditText(inflater.getContext());
        for (int i = 0; i < choices.length; i++) {
            final ChoiceTextExclusive<T> item = choices[i];

            // Create & add the View to our body-view
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.checkboxdesc, radioGroup, false);
            final AppCompatCheckBox checkBox = (AppCompatCheckBox) linearLayout.findViewById(R.id.checkbox);
            final TextView descTxt = (TextView) linearLayout.findViewById(R.id.desc);
            checkBox.setText(item.getText());
            descTxt.setText(item.getDetail());
            checkBox.setId(i);


            if (item.getOther() != null && item.getOther().isTextfieldReq()) {
                LinearLayout.MarginLayoutParams otherTextlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                otherText.setLayoutParams(otherTextlayoutParams);
                linearLayout.addView(otherText);
                otherText.setHint(item.getOther().getPlaceholder());
                otherText.setVisibility(View.GONE);
                OtherOptionText = true;
            }
            linearLayout.setTag(i);
            radioGroup.addView(linearLayout);


            // Set initial state
            if (currentSelected != null && currentSelected.contains(item.getValue())) {
                checkBox.setChecked(true);
                selectedcheckbox.add(checkBox);

                if (item.getValue().toString().equalsIgnoreCase(OtherOptionValue)) {
                    otherText.setVisibility(View.VISIBLE);
                    otherText.setText(mOtherOptionModel.getText());
                }
            }


            // Update result when value changes
            CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        AppController.getHelperHideKeyboard((Activity) inflater.getContext());
                        if (currentSelected.contains(exclusiveValue)) {
                            for (int i = 0; i < selectedcheckbox.size(); i++) {
                                selectedcheckbox.get(i).setOnCheckedChangeListener(null);
                                selectedcheckbox.get(i).setChecked(false);
                                selectedcheckbox.get(i).setOnCheckedChangeListener(checkedChangeListenerArrayList.get(selectedcheckbox.get(i).getId()));
                            }
                            otherText.setVisibility(View.GONE);
                            otherText.setText("");

                            selectedcheckbox.clear();
                            currentSelected.clear();
                            currentSelected.remove(new Gson().toJson(mOtherOptionModel));
                            mOtherOptionModel.setOther(null);
                            mOtherOptionModel.setText(null);
                        }


                        if (item.isExclusive()) {
                            exclusiveValue = item.getValue();
                            for (int i = 0; i < selectedcheckbox.size(); i++) {
                                selectedcheckbox.get(i).setOnCheckedChangeListener(null);
                                selectedcheckbox.get(i).setChecked(false);
                                selectedcheckbox.get(i).setOnCheckedChangeListener(checkedChangeListenerArrayList.get(selectedcheckbox.get(i).getId()));
                            }

                            otherText.setVisibility(View.GONE);
                            otherText.setText("");

                            selectedcheckbox.clear();
                            currentSelected.clear();
                        }
                        selectedcheckbox.add(checkBox);



                        currentSelected.add(item.getValue());
                        if (item.getOther() != null) {
                            otherText.setVisibility(View.VISIBLE);
                            otherText.requestFocus();

                            mOtherOptionModel.setOther(item.getText().toString());
                        }

                    } else {
                        selectedcheckbox.remove(checkBox);
                        currentSelected.remove(item.getValue());
                        if (item.getOther() != null) {
                            AppController.getHelperHideKeyboard((Activity) inflater.getContext());
                            currentSelected.remove(new Gson().toJson(mOtherOptionModel));
                            mOtherOptionModel.setOther(null);
                            mOtherOptionModel.setText(null);
                            otherText.setVisibility(View.GONE);
                            otherText.setText("");
                        }
                    }
                }
            };
            checkedChangeListenerArrayList.add(onCheckedChangeListener);
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }


        if (mOtherOptionModel != null && mOtherOptionModel.getText() != null) {
            otherText.setText(mOtherOptionModel.getText());
            otherText.setVisibility(View.VISIBLE);
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
            if (mOtherOptionModel != null) {
                if (OtherOptionText) {
                    if (currentSelected.contains(OtherOptionValue)) {
                        mOtherOptionModel.setText(otherText.getText().toString());
                        currentSelected.add((T) new Gson().toJson(mOtherOptionModel));
                    } else {
                        mOtherOptionModel.setText(otherText.getText().toString());
                        currentSelected.remove((T) new Gson().toJson(mOtherOptionModel));
                    }
                } else if (mOtherOptionModel.getOther() != null) {
                    currentSelected.add((T) new Gson().toJson(mOtherOptionModel));
                }
            }

            result.setResult((T[]) currentSelected.toArray());
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (currentSelected.isEmpty()) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
        } else if (OtherOptionMandatory && currentSelected.contains(OtherOptionValue) && otherText.getText().toString().equalsIgnoreCase("")) {
            return new BodyAnswer(false, R.string.otherValuetxt);
        } else {
            return BodyAnswer.VALID;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}