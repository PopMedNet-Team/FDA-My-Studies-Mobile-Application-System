package com.harvard.studyAppModule.custom.question;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.harvard.R;
import com.harvard.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.util.ArrayList;


/**
 * Created by Naveen Raj on 01/30/2017.
 */
public class ScaleTextQuestion implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<String> result;
    private ScaleTextAnswerFormat format;
    private TextView mcurrentvalue;
    private String currentSelected;
    private SeekBar mSeekBar;
    private int min;
    private int value;
    private ChoiceTextExclusive[] choiceTextExclusives;
    ArrayList<String> valuelist;

    public ScaleTextQuestion(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (ScaleTextAnswerFormat) this.step.getAnswerFormat1();
        choiceTextExclusives = format.getChoiceTextExclusive();
        valuelist = new ArrayList<>();
        for (ChoiceTextExclusive choiceTextExclusive : choiceTextExclusives) {
            valuelist.add("" + choiceTextExclusive.getValue());
        }
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

    private View initViewDefault(LayoutInflater inflater, ViewGroup parent) {
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        final int max = choiceTextExclusives.length;
        min = 1;

        View seekbarlayout;
        if (!format.isVertical()) {
            seekbarlayout = inflater.inflate(R.layout.seekbar_horizontal_layout, parent, false);
            mSeekBar = (SeekBar) seekbarlayout.findViewById(R.id.seekbar);
            mSeekBar.setMax((max - min));
        } else {
            seekbarlayout = inflater.inflate(R.layout.seekbar_text_vertical_layout, parent, false);
            mSeekBar = (SeekBar) seekbarlayout.findViewById(R.id.seekbar);
            LinearLayout scalevsaluelayout = (LinearLayout) seekbarlayout.findViewById(R.id.scaleValue);
            scalevsaluelayout.setWeightSum(choiceTextExclusives.length);

            for (int i = choiceTextExclusives.length - 1; i >= 0; i--) {
                LinearLayout linearLayout1 = new LinearLayout(inflater.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                params.gravity = Gravity.CENTER;
                linearLayout1.setLayoutParams(params);
                TextView textView = new TextView(inflater.getContext());
                LinearLayout.LayoutParams txtparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txtparams.gravity = Gravity.CENTER;
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(txtparams);
                textView.setText(choiceTextExclusives[i].getText());
                linearLayout1.addView(textView);

                scalevsaluelayout.addView(linearLayout1);
            }

            mSeekBar.setMax((max - min));
        }

        TextView mintitle = (TextView) seekbarlayout.findViewById(R.id.mintitle);
        TextView mindesc = (TextView) seekbarlayout.findViewById(R.id.mindesc);
        TextView maxtitle = (TextView) seekbarlayout.findViewById(R.id.maxtitle);
        TextView maxdesc = (TextView) seekbarlayout.findViewById(R.id.maxdesc);
        mcurrentvalue = (TextView) seekbarlayout.findViewById(R.id.currentvalue);


        mintitle.setText(String.valueOf(1));
        maxtitle.setText(String.valueOf(choiceTextExclusives.length));

        mindesc.setText(choiceTextExclusives[0].getText());
        maxdesc.setText(choiceTextExclusives[max-1].getText());


        mcurrentvalue.setText(String.valueOf(choiceTextExclusives[0].getText()));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setvaluetotxt();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (currentSelected != null) {
            int Selected = valuelist.indexOf("" + currentSelected);

            mSeekBar.setProgress(Selected);
        } else {
            int defaultval;
            if (format.getDefaultval() != null && !format.getDefaultval().equalsIgnoreCase("")) {
                try {
                    defaultval = Integer.parseInt(format.getDefaultval());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    defaultval = 0;
                }
            } else {
                defaultval = 0;
            }

            mSeekBar.setProgress(((defaultval - min)));
        }
        if (format.isVertical()) {
            setvaluetotxt();
        }

        linearLayout.removeAllViewsInLayout();
        linearLayout.addView(seekbarlayout);
        return linearLayout;
    }

    private void setvaluetotxt() {
        value = (mSeekBar.getProgress());
        mcurrentvalue.setText(String.valueOf(choiceTextExclusives[value].getText()));
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
            result.setResult("" + choiceTextExclusives[value].getValue());
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        return BodyAnswer.VALID;
    }
}
