package com.harvard.studyAppModule;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harvard.R;

import java.util.ArrayList;

public class CustomActivitiesDailyDialogClass extends Dialog implements View.OnClickListener {

    public Context mContext;
    private int limit;
    private int mSelectedTime;
    private int mSelectedDateBefore = 0;
    private ArrayList<String> mScheduledTime;
    private  boolean mIsClickableItem;
    private DialogClick mDialogClick;


    public CustomActivitiesDailyDialogClass(Context mContext, ArrayList<String> mScheduledTime, int mSelectedTime, boolean isClickableItem, DialogClick dialogClick) {
        super(mContext);
        this.mContext = mContext;
        this.mScheduledTime = mScheduledTime;
        this.mSelectedTime = mSelectedTime;
        this.mIsClickableItem = isClickableItem;
        this.mDialogClick = dialogClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cutom_activities_daily_dialog);
        // for dialog screen to get full width using this
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        RelativeLayout mClossBtnLayout = (RelativeLayout) findViewById(R.id.mClossBtnLayout);
        mClossBtnLayout.setOnClickListener(this);
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_layout_hours);
        for (int i = 0; i < mScheduledTime.size(); i++) {
            TextView textDynamic = new TextView(getContext());
            textDynamic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            textDynamic.setText(mScheduledTime.get(i));
            textDynamic.setBackgroundColor(Color.WHITE);
            if (i == mSelectedTime) {
                mSelectedDateBefore = 1;
                textDynamic.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else if (mSelectedDateBefore == 0) {
                textDynamic.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryBg));
            } else {
                textDynamic.setTextColor(mContext.getResources().getColor(R.color.colorSecondary));
            }
            textDynamic.setTextSize(16);
            textDynamic.setGravity(Gravity.CENTER);
            textDynamic.setPadding(0, 20, 0, 20);
            if (mIsClickableItem) {
                final int finalI = i;
                textDynamic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogClick.clicked(finalI);
                        dismiss();
                    }
                });
            }
            l.addView(textDynamic);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public interface DialogClick {
        void clicked(int positon);
    }
}