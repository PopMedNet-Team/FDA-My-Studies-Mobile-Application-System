package com.harvard.studyAppModule.custom.question;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.harvard.R;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;
import com.harvard.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

/**
 * Created by Naveen Raj on 01/23/2017.
 */
public class valuePickerQuestion<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private QuestionStep step2;
    private StepResult<String> result;
    private ChoiceAnswerFormatCustom format;
    private ChoiceAnswerFormat format2;
    private Choice<T>[] choices;
    private String currentSelected;
    Context mContext;
    TextView textView;
    private String resultValue;

    NumberPicker numberPicker;

    public valuePickerQuestion(Step step, StepResult result) {
        if (step instanceof QuestionStepCustom) {
            this.step = (QuestionStepCustom) step;
            this.result = result == null ? new StepResult<>(step) : result;
            this.format = (ChoiceAnswerFormatCustom) this.step.getAnswerFormat1();
            this.choices = format.getChoices();
        } else {
            this.step2 = (QuestionStep) step;
            this.result = result == null ? new StepResult<>(step) : result;
            this.format2 = (ChoiceAnswerFormat) this.step.getAnswerFormat();
            this.choices = format2.getChoices();
        }
        // Restore results
        String resultValue = this.result.getResult();
        if (resultValue != null) {
            currentSelected = resultValue;
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
        final View body = inflater.inflate(R.layout.valuepicker, parent, false);


        textView = body.findViewById(R.id.textview);
        for (int i = 0; i < choices.length; i++) {
            if (currentSelected != null && currentSelected.equalsIgnoreCase(choices[i].getValue().toString())) {
                textView.setText((String) choices[i].getText());
                resultValue=choices[i].getValue().toString();

            }
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(inflater.getContext());
            }
        });



        return body;
    }

    public void showDialog(Context context){

        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);

        TextView btndialog = (TextView) dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
            }
        });
        final String[] numberpickervalue = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {
            numberpickervalue[i] = choices[i].getText();

        }


        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(context,R.layout.list_item, R.id.tv, numberpickervalue);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(numberpickervalue[position]);
                resultValue=choices[position].getValue().toString();

                dialog.dismiss();
            }
        });

        dialog.show();

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
            currentSelected = null;
            result.setResult(null);
        } else {
            result.setResult(resultValue);

        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        return BodyAnswer.VALID;
    }
}
