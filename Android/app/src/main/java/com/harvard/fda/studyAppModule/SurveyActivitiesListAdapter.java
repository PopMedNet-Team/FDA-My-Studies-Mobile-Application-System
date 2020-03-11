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

package com.harvard.fda.studyAppModule;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.fda.studyAppModule.survayScheduler.SurvayScheduler;
import com.harvard.fda.studyAppModule.survayScheduler.model.ActivityStatus;
import com.harvard.fda.utils.AppController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SurveyActivitiesListAdapter extends RecyclerView.Adapter<SurveyActivitiesListAdapter.Holder> implements CustomActivitiesDailyDialogClass.DialogClick {
    private final Context mContext;
    public ArrayList<ActivitiesWS> items;
    private SurveyActivitiesFragment mSurveyActivitiesFragment;
    public ArrayList<String> mStatus;
    private String TEXT_EVERY = " every ";
    private String TEXT_EVERY_MONTH = " every month";
    public ArrayList<ActivityStatus> mCurrentRunStatusForActivities;
    private boolean mClick = true;
    private int mSelectedTime = -1;
    boolean paused;

    public SurveyActivitiesListAdapter(Context context, ArrayList<ActivitiesWS> items, ArrayList<String> status, ArrayList<ActivityStatus> currentRunStatusForActivities, SurveyActivitiesFragment surveyActivitiesFragment, boolean paused) {
        this.mContext = context;
        this.items = items;
        this.mStatus = status;
        this.mSurveyActivitiesFragment = surveyActivitiesFragment;
        this.mCurrentRunStatusForActivities = currentRunStatusForActivities;
        this.paused = paused;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_activities_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void clicked(int positon) {

    }

    class Holder extends RecyclerView.ViewHolder {
        final RelativeLayout mStateLayout;
        final AppCompatTextView mState;
        final RelativeLayout mContainer;
        final AppCompatImageView mSurveyIcon;
        final AppCompatTextView mWhenWasSurvey;
        final AppCompatTextView mSurveyTitle;
        final AppCompatTextView mTime;
        final AppCompatTextView mDate;
        final AppCompatTextView mProcess;
        final AppCompatTextView mRun;
        final View mHrLine1;
        final RelativeLayout mContainer2;
        final AppCompatTextView more;


        Holder(View itemView) {
            super(itemView);

            mStateLayout = (RelativeLayout) itemView.findViewById(R.id.stateLayout);
            mState = (AppCompatTextView) itemView.findViewById(R.id.state);
            mRun = (AppCompatTextView) itemView.findViewById(R.id.run);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mSurveyIcon = (AppCompatImageView) itemView.findViewById(R.id.surveyIcon);
            mWhenWasSurvey = (AppCompatTextView) itemView.findViewById(R.id.whenWasSurvey);
            mSurveyTitle = (AppCompatTextView) itemView.findViewById(R.id.surveyTitle);
            mTime = (AppCompatTextView) itemView.findViewById(R.id.time);
            mDate = (AppCompatTextView) itemView.findViewById(R.id.date);
            mProcess = (AppCompatTextView) itemView.findViewById(R.id.process);
            mHrLine1 = itemView.findViewById(R.id.hrLine1);
            mContainer2 = (RelativeLayout) itemView.findViewById(R.id.container2);
            more = (AppCompatTextView) itemView.findViewById(R.id.more);
            setFont();
        }

        private void setFont() {
            try {
                mState.setTypeface(AppController.getTypeface(mContext, "medium"));
                mWhenWasSurvey.setTypeface(AppController.getTypeface(mContext, "bold"));
                mSurveyTitle.setTypeface(AppController.getTypeface(mContext, "bold"));
                mTime.setTypeface(AppController.getTypeface(mContext, "regular"));
                mDate.setTypeface(AppController.getTypeface(mContext, "regular"));
                mProcess.setTypeface(AppController.getTypeface(mContext, "medium"));
                mRun.setTypeface(AppController.getTypeface(mContext, "medium"));
                more.setTypeface(AppController.getTypeface(mContext, "medium"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ArrayList<Integer> timePos = new ArrayList<>();

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final ArrayList<String> mScheduledTime = new ArrayList<>();
        timePos.add(-1);
        GradientDrawable bgShape = (GradientDrawable) holder.mProcess.getBackground();
        if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)) {
            holder.mState.setText(R.string.current1);
        } else if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
            holder.mState.setText(R.string.upcoming1);
        } else {
            holder.mState.setText(R.string.past);
        }
        if (holder.getAdapterPosition() == 0 || !mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(mStatus.get(holder.getAdapterPosition() - 1))) {
            holder.mStateLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mStateLayout.setVisibility(View.GONE);
        }

        if (items.get(holder.getAdapterPosition()).getActivityId().equalsIgnoreCase("") || (items.get(holder.getAdapterPosition()).getActivityId().equalsIgnoreCase(null))) {
            holder.mContainer2.setVisibility(View.VISIBLE);
            holder.mContainer.setVisibility(View.GONE);
            holder.mHrLine1.setVisibility(View.GONE);
        } else {
            holder.mContainer2.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.VISIBLE);
            holder.mHrLine1.setVisibility(View.VISIBLE);
            if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING) || mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_COMPLETED)) {
                holder.mProcess.setVisibility(View.GONE);
            } else if (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START)) {
                holder.mProcess.setVisibility(View.VISIBLE);
                holder.mProcess.setText(R.string.start);
                bgShape.setColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else if (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)) {
                holder.mProcess.setVisibility(View.VISIBLE);
                holder.mProcess.setText(R.string.resume);
                bgShape.setColor(mContext.getResources().getColor(R.color.rectangle_yellow));
            } else if (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)) {
                holder.mProcess.setVisibility(View.VISIBLE);
                holder.mProcess.setText(R.string.completed2);
                bgShape.setColor(mContext.getResources().getColor(R.color.bullet_green_color));
            } else if (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) {
                holder.mProcess.setVisibility(View.VISIBLE);
                holder.mProcess.setText(R.string.incompleted2);
                bgShape.setColor(mContext.getResources().getColor(R.color.red));
            } else {
                holder.mProcess.setVisibility(View.VISIBLE);
            }

            if (items.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("questionnaire")) {
                holder.mSurveyIcon.setImageResource(R.drawable.survey_icn_small);
            } else if (items.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("task")) {
                holder.mSurveyIcon.setImageResource(R.drawable.task_icn_small);
            } else {
                holder.mSurveyIcon.setVisibility(View.INVISIBLE);
            }

            if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
                holder.mRun.setVisibility(View.GONE);
            } else {
                if (mCurrentRunStatusForActivities.get(position).getCurrentRunId() == 0) {
                    holder.mProcess.setVisibility(View.GONE);
                }
                holder.mRun.setVisibility(View.VISIBLE);
                holder.mRun.setText(mContext.getResources().getString(R.string.run) + ": " + mCurrentRunStatusForActivities.get(position).getCurrentRunId() + "/" + mCurrentRunStatusForActivities.get(position).getTotalRun() + ", " + mCurrentRunStatusForActivities.get(position).getCompletedRun() + " " + mContext.getResources().getString(R.string.done2) + ", " + mCurrentRunStatusForActivities.get(position).getMissedRun() + " " + mContext.getResources().getString(R.string.missed));
            }
            // completed status incomplete/complete settings
            if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_COMPLETED)) {
                int val = mCurrentRunStatusForActivities.get(position).getMissedRun();
                if (val > 0) {
                    holder.mProcess.setVisibility(View.VISIBLE);
                    holder.mProcess.setText(R.string.incompleted2);
                    bgShape.setColor(mContext.getResources().getColor(R.color.red));
                } else {
                    //completed
                    holder.mProcess.setVisibility(View.VISIBLE);
                    holder.mProcess.setText(R.string.completed2);
                    bgShape.setColor(mContext.getResources().getColor(R.color.bullet_green_color));
                }
            }

            holder.mSurveyTitle.setText(items.get(position).getTitle());
            if (items.get(position).getFrequency().getType().equalsIgnoreCase("Manually Schedule")) {
                holder.mWhenWasSurvey.setText(mContext.getResources().getString(R.string.as_scheduled));
            } else if (items.get(position).getFrequency().getType().equalsIgnoreCase("One time")) {
                holder.mWhenWasSurvey.setText(mContext.getResources().getString(R.string.onetime));
            } else {
                holder.mWhenWasSurvey.setText(items.get(position).getFrequency().getType());
            }
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
            SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatType1();
            SimpleDateFormat simpleDateFormat2 = AppController.getDateFormatType9();
            SimpleDateFormat simpleDateFormat5 = AppController.getDateFormatUTC1();
            try {
                if (!items.get(position).getStartTime().equalsIgnoreCase("")) {
                    startDate = simpleDateFormat5.parse(items.get(position).getStartTime().split("\\.")[0]);
                } else {
                    startDate = new Date();
                }
                endDate = simpleDateFormat5.parse(items.get(position).getEndTime().split("\\.")[0]);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (items.get(position).getFrequency().getType().equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_DAILY)) {
                try {
                    String abc = "";
                    if (!items.get(position).getFrequency().getRuns().isEmpty()) {

                        for (int i = 0; i < items.get(position).getFrequency().getRuns().size(); i++) {

                            try {
                                String myDateString = items.get(position).getFrequency().getRuns().get(i).getStartTime().toString();
                                SimpleDateFormat sdf = AppController.getHourMinuteSecondFormat();
                                Date date = sdf.parse(myDateString);
                                SimpleDateFormat dateFormat = AppController.getHourAMPMFormat1();
                                String formattedDate = dateFormat.format(date).toString();
                                if (i == 0)
                                    abc = formattedDate;
                                else
                                    abc = abc + "<font color=\"#8c95a3\"> | </font>" + formattedDate;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!abc.isEmpty()) {
                        holder.mTime.setText(Html.fromHtml(abc), TextView.BufferType.SPANNABLE);
                        holder.mTime.setVisibility(View.VISIBLE);
                    }
                    holder.mDate.setText(simpleDateFormat1.format(startDate) + " " + mContext.getResources().getString(R.string.to) + " " + simpleDateFormat1.format(endDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.more.setVisibility(View.GONE);
            } else if (items.get(position).getFrequency().getType().equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_MONTHLY)) {
                try {
                    String myDateString = items.get(position).getStartTime().toString();
                    Date date = simpleDateFormat5.parse(myDateString.split("\\.")[0]);
                    SimpleDateFormat dateFormat1 = AppController.getHourAMPMFormat1();
                    String formattedDate1 = dateFormat1.format(date).toString();
                    SimpleDateFormat dateFormat2 = AppController.getDDFormat();
                    String formattedDate2 = dateFormat2.format(date).toString();
                    String text = formattedDate1 + " " + mContext.getResources().getString(R.string.on) + " " + formattedDate2 + TEXT_EVERY_MONTH;
                    holder.mTime.setText(text);
                    holder.mTime.setVisibility(View.VISIBLE);

                    holder.mDate.setText(simpleDateFormat1.format(startDate) + " " + mContext.getResources().getString(R.string.to) + " " + simpleDateFormat1.format(endDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.more.setVisibility(View.GONE);
            } else if (items.get(position).getFrequency().getType().equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_WEEKLY)) {
                try {
                    String myDateString = items.get(position).getStartTime().toString();
                    Date date = simpleDateFormat5.parse(myDateString.split("\\.")[0]);
                    SimpleDateFormat dateFormat1 = AppController.getHourAMPMFormat1();
                    String formattedDate1 = dateFormat1.format(date).toString();
                    SimpleDateFormat dateFormat2 = AppController.getEEFormat();
                    String formattedDate2 = dateFormat2.format(date).toString();
                    String text = formattedDate1 + TEXT_EVERY + formattedDate2;
                    holder.mTime.setText(text);
                    holder.mTime.setVisibility(View.VISIBLE);

                    holder.mDate.setText(simpleDateFormat1.format(startDate) + " " + mContext.getResources().getString(R.string.to) + " " + simpleDateFormat1.format(endDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.more.setVisibility(View.GONE);
            } else if (items.get(position).getFrequency().getType().equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_ONE_TIME)) {
                try {
                    if (endDate != null) {
                        holder.mDate.setText(simpleDateFormat2.format(startDate) + " - " + simpleDateFormat2.format(endDate));
                    } else {
                        holder.mDate.setText(mContext.getResources().getString(R.string.from) + " : " + simpleDateFormat2.format(startDate));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.mTime.setVisibility(View.GONE);
                holder.more.setVisibility(View.GONE);
            } else if (items.get(position).getFrequency().getType().equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                try {


                    /// Scheduled
                    if (!items.get(position).getFrequency().getRuns().isEmpty()) {
                        int size = items.get(position).getFrequency().getRuns().size();
                        String startTime = "";
                        String endTime = "";
                        String finalTime;
                        int finalpos = 0;
                        int pos = -1;
                        for (int i = 0; i < size; i++) {
                            if (!items.get(position).getFrequency().getRuns().get(i).getStartTime().toString().isEmpty())
                                startTime = getDateFormatedString(items.get(position).getFrequency().getRuns().get(i).getStartTime().toString().split("\\.")[0]);
                            if (!items.get(position).getFrequency().getRuns().get(i).getEndTime().toString().isEmpty())
                                endTime = getDateFormatedString(items.get(position).getFrequency().getRuns().get(i).getEndTime().toString().split("\\.")[0]);
                            pos = checkCurrentTimeInBetweenDates(items.get(position).getFrequency().getRuns().get(i).getStartTime().split("\\.")[0], items.get(position).getFrequency().getRuns().get(i).getEndTime().split("\\.")[0], i);
                            finalTime = startTime + " - " + endTime;
                            mScheduledTime.add(finalTime);

                            if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_COMPLETED)) {
                                try {
                                    finalpos = size - 1;
                                    holder.mDate.setText(simpleDateFormat2.format(simpleDateFormat5.parse(items.get(position).getFrequency().getRuns().get(i).getStartTime().toString().split("\\.")[0])) + " - " + simpleDateFormat2.format(simpleDateFormat5.parse(items.get(position).getFrequency().getRuns().get(i).getEndTime().toString().split("\\.")[0])));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (i == 0) {
                                    // if only 0 then show
                                    holder.mDate.setText(simpleDateFormat2.format(simpleDateFormat5.parse(items.get(position).getFrequency().getRuns().get(i).getStartTime().toString().split("\\.")[0])) + " - " + simpleDateFormat2.format(simpleDateFormat5.parse(items.get(position).getFrequency().getRuns().get(i).getEndTime().toString().split("\\.")[0])));
                                }

                                if (pos > 0) {
                                    finalpos = pos;
                                    try {
                                        final Date d1 = simpleDateFormat5.parse(items.get(position).getFrequency().getRuns().get(i).getStartTime().toString().split("\\.")[0]);
                                        final Date d2 = simpleDateFormat5.parse(items.get(position).getFrequency().getRuns().get(i).getEndTime().toString().split("\\.")[0]);
                                        holder.mDate.setText(simpleDateFormat2.format(d1) + " - " + simpleDateFormat2.format(d2));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        }
                        timePos.add(position, finalpos);

                        try {
                            if (mScheduledTime.size() > 1) {
                                int pickerSize = mScheduledTime.size() - 1;
                                String val = "<u>" + "+" + pickerSize + " " + mContext.getResources().getString(R.string.more) + "</u>";
                                holder.more.setText(Html.fromHtml(val));
                                holder.more.setVisibility(View.VISIBLE);
                            } else {
                                holder.more.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    holder.mTime.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClick) {
                        mClick = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mClick = true;
                            }
                        }, 1500);
                        if (paused) {
                            Toast.makeText(mContext, R.string.study_Joined_paused, Toast.LENGTH_SHORT).show();
                        } else {
                            if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT) && (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS) || mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START))) {
                                if (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).isRunIdAvailable()) {
                                    mSurveyActivitiesFragment.getActivityInfo(items.get(holder.getAdapterPosition()).getActivityId(), mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId(), mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus(), items.get(holder.getAdapterPosition()).getBranching(), items.get(holder.getAdapterPosition()).getActivityVersion(), mCurrentRunStatusForActivities.get(holder.getAdapterPosition()), items.get(holder.getAdapterPosition()));
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.survey_message), Toast.LENGTH_SHORT).show();
                                }
                            } else if (mStatus.get(holder.getAdapterPosition()).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
                                Toast.makeText(mContext, R.string.upcoming_event, Toast.LENGTH_SHORT).show();
                            } else if (mCurrentRunStatusForActivities.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) {
                                Toast.makeText(mContext, R.string.incomple_event, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, R.string.completed_event, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p = 0;
                    try {
                        p = timePos.get(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CustomActivitiesDailyDialogClass c = new CustomActivitiesDailyDialogClass(mContext, mScheduledTime, p, false, SurveyActivitiesListAdapter.this);
                    c.show();
                }
            });
        }
    }

    private String getDateFormatedString(String startTime) {
        try {
            SimpleDateFormat sdf = AppController.getDateFormatUTC1();
            Date date = sdf.parse(startTime);
            SimpleDateFormat dateFormat1 = AppController.getHourAMPMMonthDayYearFormat();
            String formattedDate = dateFormat1.format(date).toString();
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private int checkCurrentTimeInBetweenDates(String date1, String date2, int i) {
        int pos = 0;
        try {
            if (!date1.isEmpty() && !date2.isEmpty()) {
                Date time1 = AppController.getDateFormatUTC1().parse(date1);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(time1);

                Date time2 = AppController.getDateFormatUTC1().parse(date2);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(time2);

                Calendar current = Calendar.getInstance();
                Date x = current.getTime();
                if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                    pos = i;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pos;
    }

}