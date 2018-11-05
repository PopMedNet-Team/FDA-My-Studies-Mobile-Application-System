/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.studyAppModule;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hphc.mystudies.R;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivitiesWS;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivityListData;
import com.hphc.mystudies.studyAppModule.custom.Result.StepRecordCustom;
import com.hphc.mystudies.studyAppModule.events.GetActivityListEvent;
import com.hphc.mystudies.studyAppModule.studyModel.DashboardData;
import com.hphc.mystudies.studyAppModule.studyModel.ResponseInfoActiveTaskModel;
import com.hphc.mystudies.studyAppModule.studyModel.Statistics;
import com.hphc.mystudies.studyAppModule.surveyScheduler.SurveyScheduler;
import com.hphc.mystudies.studyAppModule.surveyScheduler.model.CompletionAdeherenceCalc;
import com.hphc.mystudies.userModule.webserviceModel.Studies;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.apiHelper.ConnectionDetector;
import com.hphc.mystudies.webserviceModule.apiHelper.HttpRequest;
import com.hphc.mystudies.webserviceModule.apiHelper.Responsemodel;
import com.hphc.mystudies.webserviceModule.events.WCPConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.os.Build.VERSION_CODES.M;

public class SurveyDashboardFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
    private static final int DASHBOARD_INFO = 111;
    private Context mContext;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle1;
    private RelativeLayout mShareBtn;
    private AppCompatTextView mCompletionValue;
    private com.hphc.mystudies.studyAppModule.circularProgressBar.DonutProgress mProgressBar1;
    private AppCompatTextView mAdherenceValue;
    private com.hphc.mystudies.studyAppModule.circularProgressBar.DonutProgress mProgressBar2;
    private AppCompatTextView mStatisticsLabel;

    private RelativeLayout monthLayout;
    private RelativeLayout mWeekLayout;
    private RelativeLayout mDayLayout;
    private AppCompatTextView mDayLabel;
    private AppCompatTextView mWeekLabel;
    private AppCompatTextView monthLabel;
    private AppCompatTextView mChangeDateLabel;
    private RelativeLayout mPreviousDateLayout;
    private RelativeLayout mNextDateLayout;

    private LinearLayout mTotalStaticsLayout;
    private AppCompatImageView mStatsIcon;
    private AppCompatTextView mTotalHoursSleep;
    private AppCompatTextView mTotalHoursSleepVal;
    private RelativeLayout mTrendLayout;
    private AppCompatTextView mTrends;
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    // store the value eg: 21, Apr 2017
    private String mFromDayVal;
    private String mToDayVal;
    View view;
    RelativeLayout mStatisticsLayout;
    RelativeLayout mChangeDateLayout;
    DashboardData dashboardData;
    private String dateType = "day";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String WEEK = "week";
    private AppCompatTextView mStudyStatusLabel;
    private AppCompatTextView mStudyStatus;
    private AppCompatTextView mParticipationStatusLabel;
    private AppCompatTextView mParticipationStatus;
    private AppCompatTextView mCompletionText1;
    private AppCompatTextView mCompletionPercentage;
    private AppCompatTextView mCompletionText2;
    private AppCompatTextView mAdherenceText1;
    private AppCompatTextView mAdherencePercentage;
    private AppCompatTextView mAdherenceText2;
    private ScrollView mScrollView;
    private RelativeLayout middleView;
    private HorizontalScrollView mHScrollView;
    private AppCompatImageView mRightArrow;
    private AppCompatImageView mPreviousArrow;
    private AppCompatTextView mNoStatsAvailable;
    Studies mStudies;
    ArrayList<ResponseInfoActiveTaskModel> mArrayList;
    ArrayList<String> mArrayListDup;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    private static final int PERMISSION_REQUEST_CODE = 2000;
    private static final String REGULAR = "regular";
    private static final String MEDIUM = "medium";
    private static final String STUDYID_TO_SPLIT = "_STUDYID_";
    private static final String ANSWER = "answer";
    private static final String TIMEOUT = "timeout";
    private static final String HTTP_NOT_OK = "http_not_ok";
    private static final String SESSION_EXPIRED = "session expired";
    private static final String VALUE = "value";
    private static final String DURATION = "duration";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_survey_dashboard, container, false);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj();
        initializeXMLId(view);
        setFont();
        setCurrentDateMonthYear();
        // default for day settings
        mFromDayVal = setCurrentDay();
        mToDayVal = mFromDayVal;
        //first 2 digit set color
        mChangeDateLabel.setText(setColorSpannbleString(mFromDayVal, 2));
        setColorForSelectedDayMonthYear(mDayLayout);
        bindEvents();
        setStudyStatus();
        setParticipationStatus();
        getDashboardData();
        return view;
    }

    private void setStudyStatus() {
        String status = ((SurveyActivity) mContext).getStatus();
        mStudyStatus.setText(status);
        if (status.equalsIgnoreCase("active")) {
            mStudyStatus.setTextColor(mContext.getResources().getColor(R.color.bullet_green_color));
        } else if (status.equalsIgnoreCase("upcoming")) {
            mStudyStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (status.equalsIgnoreCase("closed")) {
            mStudyStatus.setTextColor(mContext.getResources().getColor(R.color.red));
        } else if (status.equalsIgnoreCase("paused")) {
            mStudyStatus.setTextColor(mContext.getResources().getColor(R.color.rectangle_yellow));
        }
    }

    private void setParticipationStatus() {
        String mParticipationStatusVal = ((SurveyActivity) mContext).getStudyStatus();
        if (mParticipationStatusVal != null) {
            if (mParticipationStatusVal.equalsIgnoreCase(StudyFragment.COMPLETED)) {
                mParticipationStatus.setText(R.string.completed);
                mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.bullet_green_color));
            } else if (mParticipationStatusVal.equalsIgnoreCase(StudyFragment.NOT_ELIGIBLE)) {
                mParticipationStatus.setText(R.string.not_eligible);
                mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.red));
            } else if (mParticipationStatusVal.equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                mParticipationStatus.setText(R.string.in_progress);
                mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.rectangle_yellow));
            } else if (mParticipationStatusVal.equalsIgnoreCase(StudyFragment.YET_TO_JOIN)) {
                mParticipationStatus.setText(R.string.yet_to_join);
                mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else if (mParticipationStatusVal.equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
                mParticipationStatus.setText(R.string.withdrawn);
                mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.colorSecondary));
            } else {
                mParticipationStatus.setText(R.string.yet_to_join);
                mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        } else {
            mParticipationStatus.setText(R.string.yet_to_join);
            mParticipationStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
    }

    private void getDashboardData() {
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        GetActivityListEvent getActivityListEvent = new GetActivityListEvent();
        HashMap<String, String> header = new HashMap();
        String url = URLs.DASHBOARD_INFO + "?studyId=" + ((SurveyActivity) mContext).getStudyId();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, DASHBOARD_INFO, mContext, DashboardData.class, null, header, null, false, this);

        getActivityListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetActivityList(getActivityListEvent);
    }

    private void initializeXMLId(View view) {
        mBackBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
        mTitle1 = (AppCompatTextView) view.findViewById(R.id.mTitle1);
        mShareBtn = (RelativeLayout) view.findViewById(R.id.mShareBtn);
        mCompletionValue = (AppCompatTextView) view.findViewById(R.id.mCompletionValue);
        mProgressBar1 = (com.hphc.mystudies.studyAppModule.circularProgressBar.DonutProgress) view.findViewById(R.id.progressBar1);
        mAdherenceValue = (AppCompatTextView) view.findViewById(R.id.mAdherenceValue);
        mProgressBar2 = (com.hphc.mystudies.studyAppModule.circularProgressBar.DonutProgress) view.findViewById(R.id.progressBar2);
        mStatisticsLabel = (AppCompatTextView) view.findViewById(R.id.mStatisticsLabel);

        monthLayout = (RelativeLayout) view.findViewById(R.id.mMonthLayout);
        mWeekLayout = (RelativeLayout) view.findViewById(R.id.mWeekLayout);
        mDayLayout = (RelativeLayout) view.findViewById(R.id.mDayLayout);
        mDayLabel = (AppCompatTextView) view.findViewById(R.id.mDayLabel);
        mWeekLabel = (AppCompatTextView) view.findViewById(R.id.mWeekLabel);
        monthLabel = (AppCompatTextView) view.findViewById(R.id.mMonthLabel);
        mChangeDateLabel = (AppCompatTextView) view.findViewById(R.id.mChangeDateLabel);
        mPreviousDateLayout = (RelativeLayout) view.findViewById(R.id.mPreviousDateLayout);
        mNextDateLayout = (RelativeLayout) view.findViewById(R.id.mNextDateLayout);


        mTotalStaticsLayout = (LinearLayout) view.findViewById(R.id.mTotalStaticsLayout);
        mTrendLayout = (RelativeLayout) view.findViewById(R.id.mTrendLayout);
        mTrends = (AppCompatTextView) view.findViewById(R.id.mTrends);

        mStatisticsLayout = (RelativeLayout) view.findViewById(R.id.mStatisticsLayout);
        mChangeDateLayout = (RelativeLayout) view.findViewById(R.id.mChangeDateLayout);


        mStudyStatusLabel = (AppCompatTextView) view.findViewById(R.id.mStudyStatusLabel);
        mStudyStatus = (AppCompatTextView) view.findViewById(R.id.mStudyStatus);
        mParticipationStatusLabel = (AppCompatTextView) view.findViewById(R.id.mParticipationStatusLabel);
        mParticipationStatus = (AppCompatTextView) view.findViewById(R.id.mParticipationStatus);
        mCompletionText1 = (AppCompatTextView) view.findViewById(R.id.mCompletionText1);
        mCompletionPercentage = (AppCompatTextView) view.findViewById(R.id.mCompletionPercentage);
        mCompletionText2 = (AppCompatTextView) view.findViewById(R.id.mCompletionText2);
        mAdherenceText1 = (AppCompatTextView) view.findViewById(R.id.mAdherenceText1);
        mAdherencePercentage = (AppCompatTextView) view.findViewById(R.id.mAdherencePercentage);
        mAdherenceText2 = (AppCompatTextView) view.findViewById(R.id.mAdherenceText2);
        mScrollView = (ScrollView) view.findViewById(R.id.mScrollView);
        middleView = (RelativeLayout) view.findViewById(R.id.middleView);
        mHScrollView = (HorizontalScrollView) view.findViewById(R.id.mHScrollView);
        mRightArrow = (AppCompatImageView) view.findViewById(R.id.mRightArrow);
        mPreviousArrow = (AppCompatImageView) view.findViewById(R.id.mPreviousArrow);
        mNoStatsAvailable = (AppCompatTextView) view.findViewById(R.id.mNoStatsAvailable);
    }


    // DAY, WEEK, YEAR dynamically change bg and text color
    private void setColorForSelectedDayMonthYear(RelativeLayout layout) {

        if (layout == mDayLayout) {
            mDayLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
            mDayLabel.setTextColor(getResources().getColor(R.color.white));
            GradientDrawable layoutBgShape = (GradientDrawable) mDayLayout.getBackground();
            layoutBgShape.setColor(getResources().getColor(R.color.colorPrimary));
            mWeekLayout.setBackgroundResource(0);
            monthLayout.setBackgroundResource(0);

            mWeekLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
            monthLabel.setTextColor(getResources().getColor(R.color.colorSecondary));


        } else if (layout == mWeekLayout) {
            mWeekLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
            mWeekLabel.setTextColor(getResources().getColor(R.color.white));
            GradientDrawable layoutBgShape = (GradientDrawable) mWeekLayout.getBackground();
            layoutBgShape.setColor(getResources().getColor(R.color.colorPrimary));
            mDayLayout.setBackgroundResource(0);
            monthLayout.setBackgroundResource(0);

            mDayLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
            monthLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
        } else if (layout == monthLayout) {
            monthLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
            monthLabel.setTextColor(getResources().getColor(R.color.white));
            GradientDrawable layoutBgShape = (GradientDrawable) monthLayout.getBackground();
            layoutBgShape.setColor(getResources().getColor(R.color.colorPrimary));
            mWeekLayout.setBackgroundResource(0);
            mDayLayout.setBackgroundResource(0);

            mDayLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
            mWeekLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
        }

    }

    private void setFont() {
        try {
            mTitle1.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mCompletionValue.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mAdherenceValue.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mStatisticsLabel.setTypeface(AppController.getTypeface(getActivity(), MEDIUM));
            mDayLabel.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mWeekLabel.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            monthLabel.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mTrends.setTypeface(AppController.getTypeface(getActivity(), MEDIUM));
            mChangeDateLabel.setTypeface(AppController.getTypeface(getActivity(), MEDIUM));
            mStudyStatusLabel.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mStudyStatus.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mParticipationStatusLabel.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mParticipationStatus.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mCompletionText1.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mCompletionPercentage.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mCompletionText2.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mAdherenceText1.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mAdherencePercentage.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mAdherenceText2.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
            mNoStatsAvailable.setTypeface(AppController.getTypeface(getActivity(), MEDIUM));
        } catch (Exception e) {
        }
    }

    private void setCurrentDateMonthYear() {
        // Get current date by calender
        try {
            final Calendar c = Calendar.getInstance();
            mCurrentYear = c.get(Calendar.YEAR);
            mCurrentMonth = c.get(Calendar.MONTH);
            mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenshotWritingPermission(view);
            }
        });

        mDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dateType.equalsIgnoreCase(DAY)) {
                    setDay();
                    addViewStatisticsValuesRefresh();
                }
            }
        });

        mWeekLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dateType.equalsIgnoreCase(WEEK)) {
                    setWeek();
                    addViewStatisticsValuesRefresh();
                }
            }
        });
        monthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!dateType.equalsIgnoreCase(MONTH)) {
                        setMonth();
                        addViewStatisticsValuesRefresh();
                    }
                } catch (Exception e) {
                }

            }
        });
        mPreviousDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dateType.equalsIgnoreCase(DAY)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatFormatOutType1();
                        Date selectedStartDAte = simpleDateFormat.parse(mFromDayVal);
                        Date selectedEndDate = simpleDateFormat.parse(mToDayVal);
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, -1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, -1);
                        mFromDayVal = simpleDateFormat.format(calendarStart.getTime());
                        mToDayVal = simpleDateFormat.format(calendarEnd.getTime());

                        mChangeDateLabel.setText(simpleDateFormat1.format(calendarStart.getTime()));

                    } catch (ParseException e) {
                    }
                } else if (dateType.equalsIgnoreCase(WEEK)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVal);
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVal);
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, -7);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, -7);
                        mFromDayVal = simpleDateFormat1.format(calendarStart.getTime());
                        mToDayVal = simpleDateFormat1.format(calendarEnd.getTime());

                        mChangeDateLabel.setText(simpleDateFormat.format(calendarStart.getTime()) + " - " + simpleDateFormat.format(calendarEnd.getTime()));
                    } catch (ParseException e) {
                    }
                } else if (dateType.equalsIgnoreCase(MONTH)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOut();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVal);
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVal);
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.MONTH, -1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.MONTH, -1);
                        mFromDayVal = simpleDateFormat1.format(calendarStart.getTime());
                        mToDayVal = simpleDateFormat1.format(calendarEnd.getTime());

                        mChangeDateLabel.setText(simpleDateFormat.format(calendarStart.getTime()));

                    } catch (ParseException e) {
                    }
                }
                addViewStatisticsValuesRefresh();

            }
        });
        mNextDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dateType.equalsIgnoreCase(DAY)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVal);
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVal);
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, 1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, 1);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVal = simpleDateFormat1.format(calendarStart.getTime());
                            mToDayVal = simpleDateFormat1.format(calendarEnd.getTime());

                            mChangeDateLabel.setText(simpleDateFormat.format(calendarStart.getTime()));
                            addViewStatisticsValuesRefresh();
                        }
                    } catch (ParseException e) {
                    }
                } else if (dateType.equalsIgnoreCase(WEEK)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVal);
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVal);
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, 7);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, 7);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVal = simpleDateFormat1.format(calendarStart.getTime());
                            mToDayVal = simpleDateFormat1.format(calendarEnd.getTime());

                            if (calendarEnd.getTime().after(new Date())) {
                                mChangeDateLabel.setText(simpleDateFormat.format(calendarStart.getTime()) + " - " + simpleDateFormat.format(new Date()));
                            } else {
                                mChangeDateLabel.setText(simpleDateFormat.format(calendarStart.getTime()) + " - " + simpleDateFormat.format(calendarEnd.getTime()));
                            }
                            addViewStatisticsValuesRefresh();
                        }
                    } catch (ParseException e) {
                    }
                } else if (dateType.equalsIgnoreCase(MONTH)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatFormatOut();
                        Date selectedStartDAte = simpleDateFormat.parse(mFromDayVal);
                        Date selectedEndDate = simpleDateFormat.parse(mToDayVal);
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.MONTH, 1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.MONTH, 1);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVal = simpleDateFormat.format(calendarStart.getTime());
                            mToDayVal = simpleDateFormat.format(calendarEnd.getTime());

                            mChangeDateLabel.setText(simpleDateFormat1.format(calendarStart.getTime()));
                            addViewStatisticsValuesRefresh();
                        }
                    } catch (ParseException e) {
                    }
                }

            }
        });
        mTrendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dashboardData != null && dashboardData.getDashboard().getCharts().size() > 0) {
                    Intent intent = new Intent(mContext, ChartActivity.class);
                    intent.putExtra("studyId", ((SurveyActivity) mContext).getStudyId());
                    intent.putExtra("studyName", ((SurveyActivity) mContext).getTitle1());
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_charts_display), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void screenshotWritingPermission(View view) {
        // checking the permissions
        if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(permission)) {
                ActivityCompat.requestPermissions((Activity) mContext, permission, PERMISSION_REQUEST_CODE);
            } else {
                // sharing pdf creating
                shareFunctionality(view);
            }
        } else {
            // sharing pdf creating
            shareFunctionality(view);
        }
    }

    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(mContext, getResources().getString(R.string.permission_enable_message_screenshot), Toast.LENGTH_LONG).show();
            } else {
                shareFunctionality(view);
            }

        }
    }

    private void shareFunctionality(View v) {
        View v1 = v.getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bm = v1.getDrawingCache();
        saveBitmap(bm);
    }

    private void saveBitmap(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Android/FDA/Screenshot");
        myDir.mkdirs();
        String fname = ((SurveyActivity) mContext).getTitle1() + "_Dashboard.png";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
        sendMail(file, fname.split("\\.")[0]);
    }

    public void sendMail(File file, String subject) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setData(Uri.parse("mailto:"));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.setType("text/plain");
        Uri fileUri = FileProvider.getUriForFile(mContext, "com.myfileprovider", file);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(shareIntent);
    }


    private String setCurrentDay() {
        try {
            int month = mCurrentMonth + 1;
            String originDate = mCurrentDay + " " + month + " " + mCurrentYear;
            SimpleDateFormat formatIn = AppController.getDateFormatFormatInType1();
            SimpleDateFormat formatOut = AppController.getDateFormatFormatOutType1();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatIn.parse(originDate));
            String newDate = formatOut.format(calendar.getTime());
            return newDate;
        } catch (ParseException e) {
            return "";
        }
    }


    private Spannable setColorSpannbleString(String str, int endVal) {
        Spannable wordtoSpan = new SpannableString(str);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, endVal, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;

    }


    // Statistics Dynamically genarate
    private void addViewStatisticsValues() {

        if (dashboardData != null && dashboardData.getDashboard().getStatistics().size() > 0) {
            setDay();
            for (int i = 0; i < dashboardData.getDashboard().getStatistics().size(); i++) {
                RelativeLayout activitiesLayout = (RelativeLayout) view.inflate(getActivity(), R.layout.content_survey_dashboard_statistics, null);
                addViewStatisticsInitializeXMLId(activitiesLayout);
                addViewStatisticsSetFont();
                addViewStatisticsSetText(dashboardData.getDashboard().getStatistics().get(i), activitiesLayout);
                mTotalStaticsLayout.addView(activitiesLayout);
            }
        } else {
            setWeekUnSelected();
            drawableImageColorChange();
            mChangeDateLabel.setText(getResources().getString(R.string.date_range));
            for (int i = 0; i < 3; i++) {
                RelativeLayout activitiesLayout = (RelativeLayout) view.inflate(getActivity(), R.layout.content_survey_dashboard_statistics, null);
                RelativeLayout rel = (RelativeLayout) activitiesLayout.findViewById(R.id.mRectBoxLayout);
                rel.setBackground(getResources().getDrawable(R.color.colorSecondaryBg));
                mTotalStaticsLayout.addView(activitiesLayout);
            }
            disableHorizontalView(middleView);
            mHScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mNoStatsAvailable.setVisibility(View.VISIBLE);
        }

    }

    private void setWeekUnSelected() {
        try {
            mWeekLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
            mWeekLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
            GradientDrawable layoutBgShape = (GradientDrawable) mWeekLayout.getBackground();
            layoutBgShape.setColor(getResources().getColor(R.color.colorSecondaryBg));
            mDayLayout.setBackgroundResource(0);
            monthLayout.setBackgroundResource(0);
            mDayLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
            monthLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
        } catch (Resources.NotFoundException e) {
        }
    }

    private void drawableImageColorChange() {
        try {
            Resources res = getResources();
            final Drawable drawableRight = res.getDrawable(R.drawable.arrow2_right);
            drawableRight.setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
            mRightArrow.setBackgroundDrawable(drawableRight);

            final Drawable drawableLeft = res.getDrawable(R.drawable.arrow2_left);
            drawableLeft.setColorFilter(getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
            mPreviousArrow.setBackgroundDrawable(drawableLeft);

        } catch (Exception e) {
        }
    }

    private static void disableHorizontalView(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableHorizontalView((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    private void addViewStatisticsValuesRefresh() {

        if (dashboardData != null) {
            if (dashboardData.getDashboard().getStatistics().size() > 0) {
                for (int i = 0; i < dashboardData.getDashboard().getStatistics().size(); i++) {
                    addViewStatisticsSetText(dashboardData.getDashboard().getStatistics().get(i), mTotalStaticsLayout.getChildAt(i));
                }
            } else {
                setWeekUnSelected();
                drawableImageColorChange();
                mChangeDateLabel.setText(getResources().getString(R.string.date_range));
                for (int i = 0; i < 3; i++) {
                    RelativeLayout activitiesLayout = (RelativeLayout) view.inflate(getActivity(), R.layout.content_survey_dashboard_statistics, null);
                    RelativeLayout rel = (RelativeLayout) activitiesLayout.findViewById(R.id.mRectBoxLayout);
                    rel.setBackground(getResources().getDrawable(R.color.colorSecondaryBg));
                    mTotalStaticsLayout.addView(activitiesLayout);
                }
                disableHorizontalView(middleView);
                mHScrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                mNoStatsAvailable.setVisibility(View.VISIBLE);
            }
        }

    }

    private void addViewStatisticsInitializeXMLId(View view) {
        mStatsIcon = (AppCompatImageView) view.findViewById(R.id.mStatsIcon);
        mTotalHoursSleep = (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleep);
        mTotalHoursSleepVal = (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleepVal);
    }

    private void addViewStatisticsSetFont() {

        mTotalHoursSleep.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
        mTotalHoursSleepVal.setTypeface(AppController.getTypeface(getActivity(), REGULAR));
    }

    private void addViewStatisticsSetText(Statistics statistics, View view) {

        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
        switch (statistics.getStatType()) {
            case "Activity":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_activity));
                break;
            case "Sleep":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_sleep));
                break;
            case "Weight":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_weight));
                break;
            case "Heart Rate":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_heart_rate));
                break;
            case "Nutrition":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_nutrition));
                break;
            case "Blood Glucose":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_glucose));
                break;
            case "Active Task":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_active_task));
                break;
            case "Baby Kicks":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_baby_kicks));
                break;
            case "Other":
                mStatsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_other));
                break;
            default:
                break;
        }

        AppCompatTextView totalHoursSleep = (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleep);
        AppCompatTextView totalHoursSleepVal = (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleepVal);
        AppCompatTextView mUnit = (AppCompatTextView) view.findViewById(R.id.mUnit);
        totalHoursSleep.setText(statistics.getDisplayName());
        mUnit.setText(statistics.getUnit());


        RealmResults<StepRecordCustom> stepRecordCustomList = null;
        try {
            stepRecordCustomList = dbServiceSubscriber.getResultForStat(((SurveyActivity) mContext).getStudyId() + STUDYID_TO_SPLIT + statistics.getDataSource().getActivity().getActivityId(), statistics.getDataSource().getKey(), simpleDateFormat.parse(mFromDayVal), simpleDateFormat.parse(mToDayVal), mRealm);
        } catch (ParseException e) {
        }

        ArrayList<Double> resultlist = new ArrayList<>();
        if (stepRecordCustomList != null) {
            for (int i = 0; i < stepRecordCustomList.size(); i++) {
                if (stepRecordCustomList.get(i) != null) {
                    JSONObject formResultObj = null;
                    try {
                        formResultObj = new JSONObject(stepRecordCustomList.get(i).result);
                        String answer;
                        String Id[] = stepRecordCustomList.get(i).activityID.split(STUDYID_TO_SPLIT);
                        ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                        if (activityObj.getType().equalsIgnoreCase("task")) {
                            JSONObject answerjson = new JSONObject(formResultObj.getString(ANSWER));
                            answer = answerjson.getString(DURATION);
                            answer = Double.toString(Integer.parseInt(answer) / 60f);
                        } else {
                            answer = formResultObj.getString(ANSWER);
                        }
                        resultlist.add(Double.parseDouble(answer));
                    } catch (JSONException e) {
                    }
                }
            }
        }
        String result = "N/A";
        if (resultlist.size() > 0) {
            if (statistics.getCalculation().equalsIgnoreCase("Minimum")) {
                result = "" + Collections.min(resultlist);
            } else if (statistics.getCalculation().equalsIgnoreCase("Maximum")) {
                result = "" + Collections.max(resultlist);
            } else if (statistics.getCalculation().equalsIgnoreCase("Average")) {
                result = Double.toString(calculateAverage(resultlist));
            } else if (statistics.getCalculation().equalsIgnoreCase("Summation")) {
                double val = 0.0;
                for (int i = 0; i < resultlist.size(); i++) {
                    val += Double.parseDouble("" + resultlist.get(i));
                }
                result = Double.toString(val);
            }
            result = String.format("%.2f", Double.parseDouble(result));
        }
        totalHoursSleepVal.setText(result);
    }

    private double calculateAverage(ArrayList<Double> marks) {
        Double sum = 0.0;
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == DASHBOARD_INFO) {
            if (response != null) {
                dashboardData = (DashboardData) response;
                dashboardData.setStudyId(((SurveyActivity) mContext).getStudyId());

                mScrollView.setVisibility(View.VISIBLE);
                dbServiceSubscriber.saveStudyDashboardToDB(dashboardData);
                new ProcessData().execute();
            } else
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == DASHBOARD_INFO) {
            if (statusCode.equalsIgnoreCase("401")) {
                Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
                AppController.getHelperSessionExpired(mContext, errormsg);
            } else {
                mScrollView.setVisibility(View.VISIBLE);
                dashboardData = dbServiceSubscriber.getDashboardDataFromDB(((SurveyActivity) mContext).getStudyId(), mRealm);
                if (dashboardData != null) {
                    new ProcessData().execute();
                } else {
                    Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class ProcessData extends AsyncTask<String, Void, String> {
        CompletionAdeherenceCalc completionAdeherenceCalc;

        @Override
        protected String doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, mRealm);
            completionAdeherenceCalc = survayScheduler.completionAndAdherenceCalculation(((SurveyActivity) mContext).getStudyId(), mContext);
            if (completionAdeherenceCalc.isNoCompletedAndMissed()) {
                mCompletionValue.setText("-- ");
                mProgressBar1.setProgress(0);
                mAdherenceValue.setText("-- ");
                mProgressBar2.setProgress(0);
            } else {
                mCompletionValue.setText(Integer.toString((int) completionAdeherenceCalc.getCompletion()));
                mProgressBar1.setProgress((int) completionAdeherenceCalc.getCompletion());
                mAdherenceValue.setText(Integer.toString((int) completionAdeherenceCalc.getAdherence()));
                mProgressBar2.setProgress((int) completionAdeherenceCalc.getAdherence());
            }

            mTitle1.setText(((SurveyActivity) mContext).getTitle1());

            mArrayList = new ArrayList<>();
            mArrayListDup = new ArrayList<>();
            if (dashboardData != null) {
                for (int i = 0; i < dashboardData.getDashboard().getStatistics().size(); i++) {
                    ResponseInfoActiveTaskModel responseInfoActiveTaskModel = new ResponseInfoActiveTaskModel();
                    if (!mArrayListDup.contains(dashboardData.getDashboard().getStatistics().get(i).getDataSource().getActivity().getActivityId())) {
                        responseInfoActiveTaskModel.setActivityId(dashboardData.getDashboard().getStatistics().get(i).getDataSource().getActivity().getActivityId());
                        responseInfoActiveTaskModel.setKey(dashboardData.getDashboard().getStatistics().get(i).getDataSource().getKey());
                        mArrayList.add(responseInfoActiveTaskModel);
                        mArrayListDup.add(dashboardData.getDashboard().getStatistics().get(i).getDataSource().getActivity().getActivityId());
                    }
                }
                for (int i = 0; i < dashboardData.getDashboard().getCharts().size(); i++) {
                    ResponseInfoActiveTaskModel responseInfoActiveTaskModel = new ResponseInfoActiveTaskModel();
                    if (!mArrayListDup.contains(dashboardData.getDashboard().getCharts().get(i).getDataSource().getActivity().getActivityId())) {
                        responseInfoActiveTaskModel.setActivityId(dashboardData.getDashboard().getCharts().get(i).getDataSource().getActivity().getActivityId());
                        responseInfoActiveTaskModel.setKey(dashboardData.getDashboard().getCharts().get(i).getDataSource().getKey());
                        mArrayList.add(responseInfoActiveTaskModel);
                        mArrayListDup.add(dashboardData.getDashboard().getCharts().get(i).getDataSource().getActivity().getActivityId());
                    }
                }
            }


            mStudies = dbServiceSubscriber.getStudies(((SurveyActivity) mContext).getStudyId(), mRealm);
            if (mArrayList.size() > 0)
                new ResponseData(((SurveyActivity) mContext).getStudyId(), mArrayList.get(0), mStudies.getParticipantId(), 0).execute();
            else
                addViewStatisticsValues();
        }

        @Override
        protected void onPreExecute() {
        }
    }


    private void setDay() {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mFromDayVal = simpleDateFormat1.format(calendar.getTime());


        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        mToDayVal = simpleDateFormat1.format(calendar1.getTime());

        mChangeDateLabel.setText(simpleDateFormat.format(calendar.getTime()));
        setColorForSelectedDayMonthYear(mDayLayout);
        dateType = DAY;
    }

    private void setWeek() {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mFromDayVal = simpleDateFormat1.format(calendar.getTime());

        String text = simpleDateFormat.format(calendar.getTime()) + " - " + simpleDateFormat.format(new Date());
        calendar.add(Calendar.DATE, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        mToDayVal = simpleDateFormat1.format(calendar.getTime());

        mChangeDateLabel.setText(text);
        setColorForSelectedDayMonthYear(mWeekLayout);
        dateType = WEEK;
    }

    private void setMonth() {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatFormatOut();
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        mFromDayVal = simpleDateFormat.format(calendar.getTime());

        mToDayVal = simpleDateFormat.format(new Date());
        String text = simpleDateFormat1.format(calendar.getTime());

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        mToDayVal = simpleDateFormat.format(calendar.getTime());

        mChangeDateLabel.setText(text);
        setColorForSelectedDayMonthYear(monthLayout);
        dateType = MONTH;
    }

    @Override
    public void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }

    private class ResponseData extends AsyncTask<String, Void, String> {

        String participateId;
        ResponseInfoActiveTaskModel responseInfoActiveTaskModel;
        String response = null;
        String responseCode = null;
        String studyId;
        int position;
        Responsemodel mResponseModel;
        String id;
        String stepKey;
        String queryParam = "*";

        ResponseData(String studyId, ResponseInfoActiveTaskModel responseInfoActiveTaskModel, String participateId, int position) {
            this.studyId = studyId;
            this.responseInfoActiveTaskModel = responseInfoActiveTaskModel;
            this.participateId = participateId;
            this.position = position;
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionDetector connectionDetector = new ConnectionDetector(mContext);

            if (connectionDetector.isConnectingToInternet()) {
                mResponseModel = HttpRequest.getRequest(URLs.PROCESSRESPONSEDATA + "sql=SELECT%20" + queryParam + "%20FROM%20%22" + id + "%22&participantId=" + participateId, new HashMap<String, String>(), "Response");
                responseCode = mResponseModel.getResponseCode();
                response = mResponseModel.getResponseData();
                if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase(TIMEOUT)) {
                    response = TIMEOUT;
                } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
                    response = "error";
                } else if (Integer.parseInt(responseCode) >= 201 && Integer.parseInt(responseCode) < 300 && response.equalsIgnoreCase("")) {
                    response = "No data";
                } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500 && response.equalsIgnoreCase(HTTP_NOT_OK)) {
                    response = "client error";
                } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600 && response.equalsIgnoreCase(HTTP_NOT_OK)) {
                    response = "server error";
                } else if (response.equalsIgnoreCase(HTTP_NOT_OK)) {
                    response = "Unknown error";
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = SESSION_EXPIRED;
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK && !response.equalsIgnoreCase("")) {
                } else {
                    response = getString(R.string.unknown_error);
                }
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
            id = responseInfoActiveTaskModel.getActivityId();
            stepKey = responseInfoActiveTaskModel.getKey();
            ActivityListData activityListData = dbServiceSubscriber.getActivities(studyId, mRealm);
            if (activityListData != null) {
                RealmList<ActivitiesWS> activitiesWSes = activityListData.getActivities();
                for (int i = 0; i < activitiesWSes.size(); i++) {
                    if (activitiesWSes.get(i).getActivityId().equalsIgnoreCase(responseInfoActiveTaskModel.getActivityId()) && activitiesWSes.get(i).getType().equalsIgnoreCase("task")) {
                        id = responseInfoActiveTaskModel.getActivityId() + responseInfoActiveTaskModel.getKey();
                        queryParam = "%22count%22,%22Created%22,%22duration%22";
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                if (response.equalsIgnoreCase(SESSION_EXPIRED)) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    AppController.getHelperSessionExpired(mContext, SESSION_EXPIRED);
                } else if (response.equalsIgnoreCase(TIMEOUT)) {
                    addViewStatisticsValues();
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(responseCode) == 500) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(mResponseModel.getResponseData()));
                        String exception = String.valueOf(jsonObject.get("exception"));
                        if (exception.contains("Query or table not found")) {
                            if (mArrayList.size() > (position + 1))
                                new ResponseData(((SurveyActivity) mContext).getStudyId(), mArrayList.get((position + 1)), mStudies.getParticipantId(), position + 1).execute();
                            else {
                                addViewStatisticsValues();
                                AppController.getHelperProgressDialog().dismissDialog();
                            }
                        } else {
                            addViewStatisticsValues();
                            AppController.getHelperProgressDialog().dismissDialog();
                        }
                    } catch (JSONException e) {
                        addViewStatisticsValues();
                        AppController.getHelperProgressDialog().dismissDialog();
                    }
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getLabkeyDateFormat();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = (JSONObject) new JSONObject(String.valueOf(jsonArray.get(i))).get("data");
                            Type type = new TypeToken<Map<String, Object>>() {
                            }.getType();
                            Map<String, Object> myMap = gson.fromJson(String.valueOf(jsonObject1), type);
                            StepRecordCustom stepRecordCustom = new StepRecordCustom();
                            Date completedDate = new Date();
                            int duration = 0;
                            try {
                                Object completedDateValMap = gson.toJson(myMap.get("Created"));
                                Map<String, Object> completedDateVal = gson.fromJson(String.valueOf(completedDateValMap), type);
                                completedDate = simpleDateFormat.parse(String.valueOf(completedDateVal.get(VALUE)));
                            } catch (JsonSyntaxException | ParseException e) {
                            }

                            try {
                                Object durationValMap = gson.toJson(myMap.get(DURATION));
                                Map<String, Object> completedDateVal = gson.fromJson(String.valueOf(durationValMap), type);
                                duration = (int) Double.parseDouble("" + completedDateVal.get(VALUE));
                            } catch (Exception e) {
                            }
                            for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                String key = entry.getKey();
                                String valueobj = gson.toJson(entry.getValue());
                                Map<String, Object> vauleMap = gson.fromJson(String.valueOf(valueobj), type);
                                Object value = vauleMap.get(VALUE);
                                if (!key.equalsIgnoreCase("container")
                                        && !key.equalsIgnoreCase("ParticipantId")
                                        && !key.equalsIgnoreCase("EntityId")
                                        && !key.equalsIgnoreCase("Modified")
                                        && !key.equalsIgnoreCase("lastIndexed")
                                        && !key.equalsIgnoreCase("ModifiedBy")
                                        && !key.equalsIgnoreCase("CreatedBy")
                                        && !key.equalsIgnoreCase("Key")
                                        && !key.equalsIgnoreCase(DURATION)
                                        && !key.equalsIgnoreCase(stepKey + "Id")
                                        && !key.equalsIgnoreCase("Created")) {
                                    int runId = dbServiceSubscriber.getActivityRunForStatsAndCharts(responseInfoActiveTaskModel.getActivityId(), studyId, completedDate, mRealm);
                                    if (key.equalsIgnoreCase("count")) {
                                        stepRecordCustom.setStepId(stepKey);
                                        stepRecordCustom.setTaskStepID(studyId + STUDYID_TO_SPLIT + responseInfoActiveTaskModel.getActivityId() + "_" + runId + "_" + stepKey);
                                    } else {
                                        stepRecordCustom.setStepId(key);
                                        stepRecordCustom.setTaskStepID(studyId + STUDYID_TO_SPLIT + responseInfoActiveTaskModel.getActivityId() + "_" + runId + "_" + key);
                                    }
                                    stepRecordCustom.setStudyId(studyId);
                                    stepRecordCustom.setActivityID(studyId + STUDYID_TO_SPLIT + responseInfoActiveTaskModel.getActivityId());
                                    stepRecordCustom.setTaskId(studyId + STUDYID_TO_SPLIT + responseInfoActiveTaskModel.getActivityId() + "_" + runId);

                                    stepRecordCustom.setCompleted(completedDate);
                                    stepRecordCustom.setStarted(completedDate);
                                    JSONObject jsonObject2 = new JSONObject();
                                    try {
                                        Date anchordate = AppController.getLabkeyDateFormat().parse("" + value);
                                        value = AppController.getDateFormat().format(anchordate);
                                    } catch (ParseException e) {
                                    }

                                    ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(responseInfoActiveTaskModel.getActivityId(), studyId, mRealm);
                                    if (activityObj.getType().equalsIgnoreCase("task")) {
                                        JSONObject jsonObject3 = new JSONObject();
                                        jsonObject3.put(VALUE, value);
                                        jsonObject3.put(DURATION, duration);

                                        jsonObject2.put(ANSWER, jsonObject3);
                                    } else {
                                        jsonObject2.put(ANSWER, value);
                                    }


                                    stepRecordCustom.setResult(String.valueOf(jsonObject2));
                                    Number currentIdNum = dbServiceSubscriber.getStepRecordCustomId(mRealm);
                                    if (currentIdNum == null) {
                                        stepRecordCustom.setId(1);
                                    } else {
                                        stepRecordCustom.setId(currentIdNum.intValue() + 1);
                                    }
                                    dbServiceSubscriber.updateStepRecord(stepRecordCustom);
                                }
                            }
                        }
                        if (mArrayList.size() > (position + 1))
                            new ResponseData(((SurveyActivity) mContext).getStudyId(), mArrayList.get((position + 1)), mStudies.getParticipantId(), position + 1).execute();
                        else {
                            addViewStatisticsValues();
                            AppController.getHelperProgressDialog().dismissDialog();
                        }
                    } catch (Exception e) {
                        addViewStatisticsValues();
                        AppController.getHelperProgressDialog().dismissDialog();
                    }
                } else {
                    addViewStatisticsValues();
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_retrieve_data), Toast.LENGTH_SHORT).show();
                }
            } else {
                addViewStatisticsValues();
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(mContext, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
