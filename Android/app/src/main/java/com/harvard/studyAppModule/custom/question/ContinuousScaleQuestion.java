package com.harvard.studyAppModule.custom.question;

import android.content.res.Resources;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harvard.R;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.harvard.utils.VerticalSeekBar;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.text.NumberFormat;

/**
 * Created by Naveen Raj on 04/13/2017.
 */

public class ContinuousScaleQuestion<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<Double> result;
    private ContinousScaleAnswerFormat format;
    private TextView mcurrentvalue;
    private Double currentSelected;
    private SeekBar mSeekBar;
    private int stepSection, min;
    private double value;

    public ContinuousScaleQuestion(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (ContinousScaleAnswerFormat) this.step.getAnswerFormat1();

        Double resultValue = this.result.getResult();
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

        final int max = format.getMaxValue();
        min = format.getMinValue();
        stepSection = format.getMaxfraction();

        View seekbarlayout;

        if (!format.isVertical()) {
            seekbarlayout = inflater.inflate(R.layout.seekbar_horizontal_layout, parent, false);
            mSeekBar = (SeekBar) seekbarlayout.findViewById(R.id.seekbar);
        } else {
            seekbarlayout = inflater.inflate(R.layout.seekbar_vertical_layout, parent, false);
            mSeekBar = (VerticalSeekBar) seekbarlayout.findViewById(R.id.seekbar);
        }
        if (stepSection != 0) {
            mSeekBar.setMax((max - min) * (stepSection * 10));
        } else {
            mSeekBar.setMax((max - min));
        }

        TextView mintitle = (TextView) seekbarlayout.findViewById(R.id.mintitle);
        TextView mindesc = (TextView) seekbarlayout.findViewById(R.id.mindesc);
        ImageView minimage = (ImageView) seekbarlayout.findViewById(R.id.minimage);

        TextView maxtitle = (TextView) seekbarlayout.findViewById(R.id.maxtitle);
        TextView maxdesc = (TextView) seekbarlayout.findViewById(R.id.maxdesc);
        ImageView maximage = (ImageView) seekbarlayout.findViewById(R.id.maximage);


        mcurrentvalue = (TextView) seekbarlayout.findViewById(R.id.currentvalue);

        mindesc.setText(format.getMinDesc());
        maxdesc.setText(format.getMaxDesc());

        mintitle.setText(String.valueOf(min));
        maxtitle.setText(String.valueOf(max));

        if (!format.getMinImage().equalsIgnoreCase("")) {
            byte[] imageByteArray = Base64.decode(format.getMinImage(), Base64.DEFAULT);
            Glide.with(inflater.getContext())
                    .load(imageByteArray)
                    .into(minimage);
        } else {
            minimage.setVisibility(View.INVISIBLE);
        }
        if (!format.getMaxImage().equalsIgnoreCase("")) {
            byte[] imageByteArray = Base64.decode(format.getMaxImage(), Base64.DEFAULT);
            Glide.with(inflater.getContext())
                    .load(imageByteArray)
                    .into(maximage);
        } else {
            maximage.setVisibility(View.INVISIBLE);
        }


        mcurrentvalue.setText(String.valueOf(min));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setvaluetotext();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (currentSelected != null) {
            double Selected;
            if (stepSection != 0) {
                Selected = ((currentSelected - Double.parseDouble("" + min)) * Double.parseDouble("" + stepSection * 10));
            } else {
                Selected = ((currentSelected - Double.parseDouble("" + min)));
            }

            mSeekBar.setProgress((int) Selected);
        } else {
            double defaultval = 0;
            if (format.getDefaultval() != null && !format.getDefaultval().equalsIgnoreCase("")) {
                try {
                    defaultval = Double.parseDouble(format.getDefaultval());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    defaultval = 0;
                }
            } else {
                defaultval = 0;
            }
            if (stepSection != 0) {
                mSeekBar.setProgress((int) ((defaultval - min) * (stepSection * 10)));
            } else {
                mSeekBar.setProgress((int) ((defaultval - min)));
            }
        }
        if (format.isVertical()) {
            setvaluetotext();
        }

        linearLayout.removeAllViewsInLayout();
        linearLayout.addView(seekbarlayout);
        return linearLayout;
    }

    private void setvaluetotext() {

        if (stepSection != 0) {
            value = Double.parseDouble("" + min) + Double.parseDouble("" + mSeekBar.getProgress()) / Double.parseDouble("" + (stepSection * 10));
        } else {
            value = Double.parseDouble("" + min) + Double.parseDouble("" + mSeekBar.getProgress());
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(stepSection);

        mcurrentvalue.setText(String.valueOf(nf.format(value)));
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
            result.setResult(value);
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        return BodyAnswer.VALID;
    }
}
