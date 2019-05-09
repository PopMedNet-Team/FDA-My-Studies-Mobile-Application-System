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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.harvard.fda.R;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.activityBuilder.model.ActivityRun;
import com.harvard.fda.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.fda.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.fda.studyAppModule.studyModel.ChartDataSource;
import com.harvard.fda.studyAppModule.studyModel.DashboardData;
import com.harvard.fda.studyAppModule.studyModel.RunChart;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.CustomMarkerView;
import com.harvard.fda.utils.TempGraphHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.os.Build.VERSION_CODES.M;

public class ChartActivity extends AppCompatActivity {
    LinearLayout chartlayout;
    String[] day = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    String[] monthfull = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String[] month = {"JAN", ".", ".", "APR", ".", ".", "JUL", ".", ".", "OCT", ".", "DEC"};

    LinearLayout linearLayout1;
    LinearLayout.LayoutParams layoutParams;

    private String dateType = "day";
    private String MONTH = "month";
    private String DAY = "day";
    private String WEEK = "week";
    private String YEAR = "year";
    private String RUN = "run";

    DashboardData dashboardData;
    ArrayList<String> mFromDayVals = new ArrayList<>();
    ArrayList<String> dateTypeArray = new ArrayList<>();
    ArrayList<String> mToDayVals = new ArrayList<>();

    Date starttime, endtime;
    RelativeLayout backBtn;
    RelativeLayout mShareBtn;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    private static final int PERMISSION_REQUEST_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(ChartActivity.this);
        dashboardData = dbServiceSubscriber.getDashboardDataFromDB(getIntent().getStringExtra("studyId"), mRealm);
        chartlayout = (LinearLayout) findViewById(R.id.chartlayout);
        backBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mShareBtn = (RelativeLayout) findViewById(R.id.mShareBtn);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenshotWritingPermission();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (dashboardData != null) {
            for (int i = 0; i < dashboardData.getDashboard().getCharts().size(); i++) {
                LinearLayout linearLayout = new LinearLayout(ChartActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 30, 10, 30);
                linearLayout.setLayoutParams(layoutParams);

                TextView textView = new TextView(ChartActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setPadding(10, 10, 10, 10);
                textView.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                textView.setTextColor(Color.BLACK);
                textView.setText(dashboardData.getDashboard().getCharts().get(i).getDisplayName());

                final LineChart chart = new LineChart(ChartActivity.this);
                chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(false);

                CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
                chart.setMarkerView(mv);

                final List<String> filteredXValues = new ArrayList<>();
                final List<Entry> entryList = new ArrayList<>();


                // Keep track fo maxMoleDiameter for TempGraphHelper.updateLineChart();
                float maxValue = 0;

                final ChartDataSource chartDataSource = dashboardData.getDashboard().getCharts().get(i).getDataSource();
                final RealmResults<StepRecordCustom> stepRecordCustomList;
                stepRecordCustomList = dbServiceSubscriber.getResult(getIntent().getStringExtra("studyId") + "_STUDYID_" + chartDataSource.getActivity().getActivityId(), chartDataSource.getKey(), null, null, mRealm);

                // Offset each xPosition by one to compensate for
                if (!chartDataSource.getTimeRangeType().equalsIgnoreCase("runs") && !chartDataSource.getTimeRangeType().equalsIgnoreCase("hours_of_day")) {
                    String type;
                    if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_week")) {
                        type = WEEK;
                    } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_month")) {
                        type = MONTH;
                    } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("weeks_of_month")) {
                        type = MONTH;
                    } else {
                        type = YEAR;
                    }
                    addTimeLayout(type, chartDataSource.getTimeRangeType(), chart, stepRecordCustomList, filteredXValues, entryList, null, i, dashboardData.getDashboard().getCharts().get(i).getConfiguration().getSettings().get(0).getBarColor(), chartDataSource.getActivity().getActivityId());

                    for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
                        if (stepRecordCustomList.get(j).getCompleted().before(endtime) && stepRecordCustomList.get(j).getCompleted().after(starttime)) {
                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);

                                String Id[] = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }


                                if (answer == null || answer.equalsIgnoreCase("")) {
                                    answer = "0";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (answer.equalsIgnoreCase("")) {
                                    answer = "0";
                                }
                            }
                            if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_week")) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                                filteredXValues.clear();
                                for (int k = 0; k < day.length; k++) {
                                    String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                                    if (s.equalsIgnoreCase(day[k])) {
                                        entryList.add(new Entry(Float.parseFloat(answer), k, data));
                                    }
                                    filteredXValues.add(day[k]);
                                }
                            } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("days_of_month")) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                                int month = starttime.getMonth(), year = starttime.getYear();
                                filteredXValues.clear();
                                for (int k = 1; k <= numberOfDaysInMonth(month, year); k++) {
                                    String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                                    if (s.equalsIgnoreCase("" + k)) {
                                        entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
                                    }
                                    if (k % 5 == 0) {
                                        filteredXValues.add("" + k);
                                    } else {
                                        filteredXValues.add("");
                                    }
                                }
                            } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("weeks_of_month")) {
                                Calendar cal = Calendar.getInstance();
                                cal.setFirstDayOfWeek(Calendar.SUNDAY);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                                filteredXValues.clear();
                                int weekno = numberOfWeeksInMonth(simpleDateFormat.format(starttime));
                                for (int k = 1; k <= weekno; k++) {
                                    cal.setTime(stepRecordCustomList.get(j).getCompleted());
                                    int week = cal.get(Calendar.WEEK_OF_MONTH);
                                    if (k == week) {
                                        entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
                                    }
                                    filteredXValues.add("W" + k);
                                }
                            } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("months_of_year")) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                                filteredXValues.clear();
                                for (int k = 0; k < month.length; k++) {
                                    String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                                    if (s.equalsIgnoreCase(monthfull[k])) {
                                        entryList.add(new Entry(Float.parseFloat(answer), k, data));
                                    }
                                    filteredXValues.add(month[k]);
                                }
                            }
                        }
                    }
                } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("runs")) {
                    RealmResults<ActivityRun> activityRuns = dbServiceSubscriber.getAllActivityRunFromDB(getIntent().getStringExtra("studyId"), chartDataSource.getActivity().getActivityId(), mRealm);
                    linearLayout1 = new LinearLayout(ChartActivity.this);
                    SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                    dateType = RUN;
                    dateTypeArray.add(RUN);
                    mFromDayVals.add(simpleDateFormat.format(new Date()));
                    mToDayVals.add(simpleDateFormat.format(new Date()));
                    filteredXValues.clear();
                    ArrayList<RunChart> runCharts = new ArrayList<>();
                    List<List<RunChart>> lists = new ArrayList<>();
                    for (int k = 0; k < activityRuns.size(); k++) {
                        lists.clear();
                        Date runCompletedDate = null;
                        String runAnswer = null;
                        String runAnswerData = null;
                        for (int l = 0; l < stepRecordCustomList.size(); l++) {
                            if (stepRecordCustomList.get(l).taskId.contains("_")) {
                                String taskId[] = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
                                String runId = taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());
                                JSONObject jsonObject;
                                String answer = "";
                                String data = "";
                                try {
                                    jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                                    String Id[] = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                                    ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                    if (activityObj.getType().equalsIgnoreCase("task")) {
                                        JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                        answer = answerjson.getString("duration");
                                        answer = Double.toString(Integer.parseInt(answer) / 60f);
                                        data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                    } else {
                                        answer = jsonObject.getString("answer");
                                        data = "";
                                    }
                                    if (answer == null || answer.equalsIgnoreCase("")) {
                                        answer = "0";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    if (answer.equalsIgnoreCase("")) {
                                        answer = "0";
                                    }
                                }

                                if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {

                                    runAnswer = answer;
                                    runAnswerData = data;
                                    runCompletedDate = stepRecordCustomList.get(l).completed;
                                    break;
                                }
                            }
                        }
                        RunChart runChart = new RunChart();
                        runChart.setCompletedDate(runCompletedDate);
                        runChart.setResult(runAnswer);
                        runChart.setResultData(runAnswerData);
                        runChart.setRunId("" + activityRuns.get(k).getRunId());
                        runChart.setStartDate(activityRuns.get(k).getStartDate());
                        runChart.setEnddDate(activityRuns.get(k).getEndDate());
                        runCharts.add(runChart);
                    }
                    //new chart
                    lists = split(runCharts, 5);
                    filteredXValues.clear();
                    entryList.clear();
                    if (lists.size() > 0) {
                        for (int l = 0; l < lists.get(0).size(); l++) {
                            if (lists.get(0).get(l).getResult() != null)
                                entryList.add(new Entry(Float.parseFloat(lists.get(0).get(l).getResult()), Integer.parseInt(lists.get(0).get(l).getRunId()) - 1, lists.get(0).get(l).getResultData()));
                            filteredXValues.add("" + (Integer.parseInt(lists.get(0).get(l).getRunId())));
                        }
                    }
                    addTimeLayoutRuns(lists, RUN, chartDataSource.getTimeRangeType(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, i, dashboardData.getDashboard().getCharts().get(i).getConfiguration().getSettings().get(0).getBarColor(), chartDataSource.getActivity().getActivityId(), 0);
                } else if (chartDataSource.getTimeRangeType().equalsIgnoreCase("hours_of_day")) {
                    final RealmResults<ActivityRun> activityRuns = dbServiceSubscriber.getAllActivityRunforDate(chartDataSource.getActivity().getActivityId(), getIntent().getStringExtra("studyId"), new Date(), mRealm);
                    addTimeLayout(DAY, chartDataSource.getTimeRangeType(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, i, dashboardData.getDashboard().getCharts().get(i).getConfiguration().getSettings().get(0).getBarColor(), chartDataSource.getActivity().getActivityId());
                    filteredXValues.clear();
                    for (int k = 0; k < activityRuns.size(); k++) {
                        for (int l = 0; l < stepRecordCustomList.size(); l++) {
                            if (stepRecordCustomList.get(l).getCompleted().before(endtime) && stepRecordCustomList.get(l).getCompleted().after(starttime)) {
                                if (stepRecordCustomList.get(l).taskId.contains("_")) {
                                    String taskId[] = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
                                    String runId = taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());

                                    JSONObject jsonObject;
                                    String answer = "";
                                    String data = "";
                                    try {
                                        jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                                        String Id[] = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                                        ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                        if (activityObj.getType().equalsIgnoreCase("task")) {
                                            JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                            answer = answerjson.getString("duration");
                                            answer = Double.toString(Integer.parseInt(answer) / 60f);
                                            data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                        } else {
                                            answer = jsonObject.getString("answer");
                                            data = "";
                                        }
                                        if (answer == null || answer.equalsIgnoreCase("")) {
                                            answer = "0";
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        if (answer.equalsIgnoreCase("")) {
                                            answer = "0";
                                        }
                                    }

                                    if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {
                                        entryList.add(new Entry(Float.parseFloat(answer), k, data));
                                    }
                                }
                            }
                        }
                        filteredXValues.add("" + (k + 1));
                    }
                }
                // Update chart w/ our data
                TempGraphHelper.updateLineChart(chart, (int) maxValue, entryList, filteredXValues, dashboardData.getDashboard().getCharts().get(i).getConfiguration().getSettings().get(0).getBarColor());

                // Move to "end" of chart
                linearLayout.addView(textView);
                if (linearLayout1 != null)
                    linearLayout.addView(linearLayout1);
                linearLayout.addView(chart);
                chartlayout.addView(linearLayout);
            }
        }
    }


    private void screenshotWritingPermission() {
        // checking the permissions
        if ((ActivityCompat.checkSelfPermission(ChartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(ChartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(permission)) {
                ActivityCompat.requestPermissions((Activity) ChartActivity.this, permission, PERMISSION_REQUEST_CODE);
            } else {
                // sharing pdf creating
                shareFunctionality();
            }
        } else {
            // sharing pdf creating
            shareFunctionality();
        }
    }

    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(ChartActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(ChartActivity.this, getResources().getString(R.string.permission_enable_message_screenshot), Toast.LENGTH_LONG).show();
                } else {
                    shareFunctionality();
                }
                break;
        }
    }


    private void shareFunctionality() {


        Bitmap returnedBitmap = Bitmap.createBitmap(chartlayout.getWidth(), chartlayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = chartlayout.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        chartlayout.draw(canvas);

        saveBitmap(returnedBitmap);
    }

    private void saveBitmap(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Android/FDA/Screenshot");
        myDir.mkdirs();
        String fname = getIntent().getStringExtra("studyName") + "_Chart.png";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMail(file, fname.split("\\.")[0]);
    }

    public void sendMail(File file, String subject) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setData(Uri.parse("mailto:"));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.setType("text/plain");
        Uri fileUri = FileProvider.getUriForFile(ChartActivity.this, "com.myfileprovider", file);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(shareIntent);
    }

    public <RunChart extends Object> List<List<RunChart>> split(ArrayList<RunChart> list, int targetSize) {
        List<List<RunChart>> lists = new ArrayList<List<RunChart>>();
        for (int i = 0; i < list.size(); i += targetSize) {
            lists.add(list.subList(i, Math.min(i + targetSize, list.size())));
        }
        return lists;
    }

    public static int numberOfDaysInMonth(int month, int year) {
        Calendar monthStart = new GregorianCalendar(year, month, 1);
        return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int numberOfWeeksInMonth(String monthval) {
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        Date date = null;
        try {
            date = format.parse(monthval);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int start = c.get(Calendar.WEEK_OF_MONTH);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        int end = c.get(Calendar.WEEK_OF_MONTH);

        return (end - start + 1);
    }

    private void addTimeLayoutRuns(final List<List<RunChart>> lists, String RUN, String charttype, final LineChart chart, final RealmResults<StepRecordCustom> stepRecordCustomList, final List<String> filteredXValues, final List<Entry> entryList, final RealmResults<ActivityRun> activityRuns, int position, final String barColor, String activityId, int index) {
        if (lists.size() > 0) {
            linearLayout1 = new LinearLayout(ChartActivity.this);
            linearLayout1.setGravity(Gravity.CENTER);
            linearLayout1.setLayoutParams(layoutParams);
            final TextView textView1 = new TextView(ChartActivity.this);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams1.setMargins(50, 10, 50, 10);
            textView1.setLayoutParams(layoutParams1);
            textView1.setPadding(10, 10, 10, 10);
            textView1.setTextColor(Color.BLACK);
            textView1.setGravity(View.TEXT_ALIGNMENT_CENTER);

            final ArrayList<String> runtxt = new ArrayList<>();

            SimpleDateFormat runchartdate = AppController.getDateFormatFormatOutType1();
            for (int i = 0; i < lists.size(); i++) {
                runtxt.add(runchartdate.format(lists.get(i).get(0).getStartDate()) + "-" + runchartdate.format(lists.get(i).get(lists.get(i).size() - 1).getEnddDate()));
            }

            textView1.setText(runtxt.get(0).toString());

            textView1.setTag(charttype);
            textView1.setTag(R.string.charttag, activityId);
            textView1.setTag(R.string.runchartindex, 0);
            textView1.setTag(R.string.runchartmaxindex, lists.size() - 1);

            final ImageView rightArrow = new ImageView(ChartActivity.this);
            rightArrow.setImageResource(R.drawable.arrow2_right);
            rightArrow.setPadding(10, 10, 10, 10);
            rightArrow.setTag(position);

            final ImageView leftArrow = new ImageView(ChartActivity.this);
            leftArrow.setTag(position);

            leftArrow.setImageResource(R.drawable.arrow2_left);
            leftArrow.setPadding(10, 10, 10, 10);

            leftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) > 0) {
                        textView1.setTag(R.string.runchartindex, Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) - 1);
                        textView1.setText(runtxt.get(Integer.parseInt("" + textView1.getTag(R.string.runchartindex))));
                        refreshdataRun(filteredXValues, entryList, lists, Integer.parseInt("" + textView1.getTag(R.string.runchartindex)), chart, barColor);
                    }

                }
            });


            rightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) < Integer.parseInt("" + textView1.getTag(R.string.runchartmaxindex))) {
                        textView1.setTag(R.string.runchartindex, Integer.parseInt("" + textView1.getTag(R.string.runchartindex)) + 1);
                        textView1.setText(runtxt.get(Integer.parseInt("" + textView1.getTag(R.string.runchartindex))));
                        refreshdataRun(filteredXValues, entryList, lists, Integer.parseInt("" + textView1.getTag(R.string.runchartindex)), chart, barColor);
                    }
                }
            });

            linearLayout1.addView(leftArrow);
            linearLayout1.addView(textView1);
            linearLayout1.addView(rightArrow);
        }
    }

    private void refreshdataRun(List<String> filteredXValues, List<Entry> entryList, List<List<RunChart>> lists, int index, LineChart chart, String barColor) {
        chart.clear();
        filteredXValues.clear();
        entryList.clear();
        for (int l = 0; l < lists.get(index).size(); l++) {
            if (lists.get(index).get(l).getResult() != null)
                entryList.add(new Entry(Float.parseFloat(lists.get(index).get(l).getResult()), Integer.parseInt(lists.get(index).get(l).getRunId()) - Integer.parseInt(lists.get(index).get(0).getRunId()), lists.get(index).get(0).getResultData()));
            filteredXValues.add("" + (Integer.parseInt(lists.get(index).get(l).getRunId())));
        }
        TempGraphHelper.updateLineChart(chart, 0, entryList, filteredXValues, barColor);
    }

    public void addTimeLayout(String time, String charttype, final LineChart chart, final RealmResults<StepRecordCustom> stepRecordCustomList, final List<String> filteredXValues, final List<Entry> entryList, final RealmResults<ActivityRun> activityRuns, int position, final String barColor, final String activityId) {
        linearLayout1 = new LinearLayout(ChartActivity.this);
        linearLayout1.setGravity(Gravity.CENTER);
        linearLayout1.setLayoutParams(layoutParams);
        final TextView textView1 = new TextView(ChartActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams1.setMargins(50, 10, 50, 10);
        textView1.setLayoutParams(layoutParams1);
        textView1.setPadding(10, 10, 10, 10);
        textView1.setTextColor(Color.BLACK);
        textView1.setGravity(View.TEXT_ALIGNMENT_CENTER);
        if (time.equalsIgnoreCase(DAY))
            setDay(textView1);
        else if (time.equalsIgnoreCase(WEEK))
            setWeek(textView1);
        else if (time.equalsIgnoreCase(MONTH))
            setMonth(textView1);
        else
            setYear(textView1);

        textView1.setTag(charttype);
        textView1.setTag(R.string.charttag, activityId);

        final ImageView rightArrow = new ImageView(ChartActivity.this);
        rightArrow.setImageResource(R.drawable.arrow2_right);
        rightArrow.setPadding(10, 10, 10, 10);
        rightArrow.setTag(position);

        final ImageView leftArrow = new ImageView(ChartActivity.this);
        leftArrow.setTag(position);

        leftArrow.setImageResource(R.drawable.arrow2_left);
        leftArrow.setPadding(10, 10, 10, 10);

        if (dashboardData.getDashboard().getCharts().get(position).isScrollable()) {
            rightArrow.setVisibility(View.VISIBLE);
            leftArrow.setVisibility(View.VISIBLE);
        } else {
            rightArrow.setVisibility(View.GONE);
            leftArrow.setVisibility(View.GONE);
        }

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(DAY)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatFormatOutType1();
                        Date selectedStartDAte = simpleDateFormat.parse(mFromDayVals.get((int) leftArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat.parse(mToDayVals.get((int) leftArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, -1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, -1);
                        mFromDayVals.set((int) leftArrow.getTag(), simpleDateFormat.format(calendarStart.getTime()));
                        mToDayVals.set((int) leftArrow.getTag(), simpleDateFormat.format(calendarEnd.getTime()));

                        textView1.setText(simpleDateFormat1.format(calendarStart.getTime()));
                        refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(WEEK)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVals.get((int) leftArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVals.get((int) leftArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, -7);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, -7);
                        mFromDayVals.set((int) leftArrow.getTag(), simpleDateFormat1.format(calendarStart.getTime()));
                        mToDayVals.set((int) leftArrow.getTag(), simpleDateFormat1.format(calendarEnd.getTime()));

                        textView1.setText(simpleDateFormat.format(calendarStart.getTime()) + " - " + simpleDateFormat.format(calendarEnd.getTime()));
                        refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(MONTH)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOut();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVals.get((int) leftArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVals.get((int) leftArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.MONTH, -1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.MONTH, -1);
                        mFromDayVals.set((int) leftArrow.getTag(), simpleDateFormat1.format(calendarStart.getTime()));
                        mToDayVals.set((int) leftArrow.getTag(), simpleDateFormat1.format(calendarEnd.getTime()));

                        textView1.setText(simpleDateFormat.format(calendarStart.getTime()));
                        refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dateTypeArray.get((int) leftArrow.getTag()).equalsIgnoreCase(YEAR)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatYearFormat();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVals.get((int) leftArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVals.get((int) leftArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.YEAR, -1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.YEAR, -1);
                        mFromDayVals.set((int) leftArrow.getTag(), simpleDateFormat1.format(calendarStart.getTime()));
                        mToDayVals.set((int) leftArrow.getTag(), simpleDateFormat1.format(calendarEnd.getTime()));

                        textView1.setText(simpleDateFormat.format(calendarStart.getTime()));
                        refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(DAY)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVals.get((int) rightArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVals.get((int) rightArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, 1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, 1);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVals.set((int) rightArrow.getTag(), simpleDateFormat1.format(calendarStart.getTime()));
                            mToDayVals.set((int) rightArrow.getTag(), simpleDateFormat1.format(calendarEnd.getTime()));

                            textView1.setText(simpleDateFormat.format(calendarStart.getTime()));
                            refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(WEEK)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
                        Date selectedStartDAte = simpleDateFormat1.parse(mFromDayVals.get((int) rightArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat1.parse(mToDayVals.get((int) rightArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.DATE, 7);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.DATE, 7);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVals.set((int) rightArrow.getTag(), simpleDateFormat1.format(calendarStart.getTime()));
                            mToDayVals.set((int) rightArrow.getTag(), simpleDateFormat1.format(calendarEnd.getTime()));

                            if (calendarEnd.getTime().after(new Date())) {
                                textView1.setText(simpleDateFormat.format(calendarStart.getTime()) + " - " + simpleDateFormat.format(new Date()));
                                refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), new Date(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                            } else {
                                textView1.setText(simpleDateFormat.format(calendarStart.getTime()) + " - " + simpleDateFormat.format(calendarEnd.getTime()));
                                refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                            }

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(MONTH)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatFormatOut();
                        Date selectedStartDAte = simpleDateFormat.parse(mFromDayVals.get((int) rightArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat.parse(mToDayVals.get((int) rightArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.MONTH, 1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.MONTH, 1);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVals.set((int) rightArrow.getTag(), simpleDateFormat.format(calendarStart.getTime()));
                            mToDayVals.set((int) rightArrow.getTag(), simpleDateFormat.format(calendarEnd.getTime()));

                            textView1.setText(simpleDateFormat1.format(calendarStart.getTime()));
                            refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (dateTypeArray.get((int) rightArrow.getTag()).equalsIgnoreCase(YEAR)) {
                    try {
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatYearFormat();
                        Date selectedStartDAte = simpleDateFormat.parse(mFromDayVals.get((int) rightArrow.getTag()));
                        Date selectedEndDate = simpleDateFormat.parse(mToDayVals.get((int) rightArrow.getTag()));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(selectedStartDAte);
                        calendarStart.add(Calendar.YEAR, 1);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(selectedEndDate);
                        calendarEnd.add(Calendar.YEAR, 1);
                        if (!calendarStart.getTime().after(new Date())) {
                            mFromDayVals.set((int) rightArrow.getTag(), simpleDateFormat.format(calendarStart.getTime()));
                            mToDayVals.set((int) rightArrow.getTag(), simpleDateFormat.format(calendarEnd.getTime()));

                            textView1.setText(simpleDateFormat1.format(calendarStart.getTime()));
                            refreshchartdata("" + textView1.getTag(), calendarStart.getTime(), calendarEnd.getTime(), chart, stepRecordCustomList, filteredXValues, entryList, activityRuns, barColor, "" + textView1.getTag(R.string.charttag));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        linearLayout1.addView(leftArrow);
        linearLayout1.addView(textView1);
        linearLayout1.addView(rightArrow);
    }

    private void refreshchartdata(String tag, Date startTime, Date endtime, final LineChart chart, RealmResults<StepRecordCustom> stepRecordCustomList, List<String> filteredXValues, List<Entry> entryList, RealmResults<ActivityRun> activityRuns, final String barColor, final String activityId) {
        chart.clear();
        entryList.clear();
        filteredXValues.clear();
        if (!tag.equalsIgnoreCase("hours_of_day")) {
            if (tag.equalsIgnoreCase("days_of_week")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                for (int k = 0; k < day.length; k++) {
                    for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
                        if (stepRecordCustomList.get(j).getCompleted().before(endtime) && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                                String Id[] = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());

                            if (s.equalsIgnoreCase(day[k])) {
                                entryList.add(new Entry(Float.parseFloat(answer), k, data));
                            }
                        }
                    }
                    filteredXValues.add(day[k]);
                }
            } else if (tag.equalsIgnoreCase("days_of_month")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                int month = startTime.getMonth(), year = startTime.getYear();
                for (int k = 1; k <= numberOfDaysInMonth(month, year); k++) {
                    for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
                        if (stepRecordCustomList.get(j).getCompleted().before(endtime) && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                                String Id[] = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                            if (s.equalsIgnoreCase("" + k)) {
                                entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
                            }
                        }
                    }
                    if (k % 5 == 0) {
                        filteredXValues.add("" + k);
                    } else {
                        filteredXValues.add("");
                    }
                }
            } else if (tag.equalsIgnoreCase("weeks_of_month")) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.SUNDAY);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                for (int k = 1; k <= numberOfWeeksInMonth(simpleDateFormat.format(startTime)); k++) {
                    for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
                        if (stepRecordCustomList.get(j).getCompleted().before(endtime) && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                                String Id[] = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            cal.setTime(stepRecordCustomList.get(j).getCompleted());
                            int week = cal.get(Calendar.WEEK_OF_MONTH);
                            if (k == week) {
                                entryList.add(new Entry(Float.parseFloat(answer), k - 1, data));
                            }
                        }
                    }
                    filteredXValues.add("W" + k);
                }
            } else if (tag.equalsIgnoreCase("months_of_year")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                for (int k = 0; k < month.length; k++) {
                    for (int j = 0, size = stepRecordCustomList.size(); j < size; j++) {
                        if (stepRecordCustomList.get(j).getCompleted().before(endtime) && stepRecordCustomList.get(j).getCompleted().after(startTime)) {
                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(j).result);
                                String Id[] = stepRecordCustomList.get(j).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String s = simpleDateFormat.format(stepRecordCustomList.get(j).getCompleted());
                            if (s.equalsIgnoreCase(monthfull[k])) {
                                entryList.add(new Entry(Float.parseFloat(answer), k, data));
                            }
                        }
                    }
                    filteredXValues.add(month[k]);
                }
            }
        } else if (tag.equalsIgnoreCase("hours_of_day")) {
            activityRuns = dbServiceSubscriber.getAllActivityRunforDate(activityId, getIntent().getStringExtra("studyId"), startTime, mRealm);
            for (int k = 0; k < activityRuns.size(); k++) {
                for (int l = 0; l < stepRecordCustomList.size(); l++) {
                    if (stepRecordCustomList.get(l).getCompleted().before(endtime) && stepRecordCustomList.get(l).getCompleted().after(startTime)) {
                        if (stepRecordCustomList.get(l).taskId.contains("_")) {
                            String taskId[] = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
                            String runId = taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());

                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                                Log.e("jsonObject", jsonObject.toString());
                                String Id[] = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }
                                if (answer == null || answer.equalsIgnoreCase("")) {
                                    answer = "0";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (answer.equalsIgnoreCase("")) {
                                    answer = "0";
                                }

                            }

                            if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {
                                entryList.add(new Entry(Float.parseFloat(answer), k, data));

                            }
                        }
                    }
                }
                filteredXValues.add("" + (k + 1));
            }
        } else {
            activityRuns = dbServiceSubscriber.getAllActivityRunFromDB(getIntent().getStringExtra("studyId"), activityId, mRealm);
            for (int k = 0; k < activityRuns.size(); k++) {
                for (int l = 0; l < stepRecordCustomList.size(); l++) {
                    if (stepRecordCustomList.get(l).getCompleted().before(endtime) && stepRecordCustomList.get(l).getCompleted().after(startTime)) {
                        if (stepRecordCustomList.get(l).taskId.contains("_")) {
                            String taskId[] = stepRecordCustomList.get(l).taskId.split("_STUDYID_");
                            String runId = taskId[1].substring(taskId[1].lastIndexOf("_") + 1, taskId[1].length());

                            JSONObject jsonObject;
                            String answer = "";
                            String data = "";
                            try {
                                jsonObject = new JSONObject(stepRecordCustomList.get(l).result);
                                String Id[] = stepRecordCustomList.get(l).activityID.split("_STUDYID_");
                                ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(Id[1], Id[0], mRealm);
                                if (activityObj.getType().equalsIgnoreCase("task")) {
                                    JSONObject answerjson = new JSONObject(jsonObject.getString("answer"));
                                    answer = answerjson.getString("duration");
                                    answer = Double.toString(Integer.parseInt(answer) / 60f);
                                    data = "min \nfor\n" + answerjson.getString("value") + " kicks";
                                } else {
                                    answer = jsonObject.getString("answer");
                                    data = "";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (runId.equalsIgnoreCase("" + activityRuns.get(k).getRunId())) {
                                entryList.add(new Entry(Float.parseFloat(answer), k, data));

                            }
                        }
                    }
                }
                filteredXValues.add("" + (k + 1));
            }
        }

        // Update chart w/ our data
        TempGraphHelper.updateLineChart(chart, 0, entryList, filteredXValues, barColor);
    }


    private void setDay(TextView textView1) {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String mFromDayVal = simpleDateFormat1.format(calendar.getTime());
        mFromDayVals.add(mFromDayVal);
        starttime = calendar.getTime();


        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        String mToDayVal = simpleDateFormat1.format(calendar1.getTime());
        mToDayVals.add(mToDayVal);
        endtime = calendar1.getTime();

        textView1.setText(simpleDateFormat.format(calendar.getTime()));
        dateType = DAY;
        dateTypeArray.add(DAY);
    }

    private void setWeek(TextView textView1) {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormatFormatOutType1();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String mFromDayVal = simpleDateFormat1.format(calendar.getTime());
        starttime = calendar.getTime();
        mFromDayVals.add(mFromDayVal);

        textView1.setText(simpleDateFormat.format(calendar.getTime()) + " - " + simpleDateFormat.format(new Date()));
        calendar.add(Calendar.DATE, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        String mToDayVal = simpleDateFormat1.format(calendar.getTime());
        endtime = calendar.getTime();
        mToDayVals.add(mToDayVal);

        dateType = WEEK;
        dateTypeArray.add(WEEK);
    }

    private void setMonth(TextView textView1) {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatFormatOut();
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String mFromDayVal = simpleDateFormat.format(calendar.getTime());
        starttime = calendar.getTime();
        mFromDayVals.add(mFromDayVal);

        String mToDayVal = simpleDateFormat.format(new Date());
        textView1.setText(simpleDateFormat1.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        mToDayVal = simpleDateFormat.format(calendar.getTime());
        endtime = calendar.getTime();
        mToDayVals.add(mToDayVal);

        dateType = MONTH;
        dateTypeArray.add(MONTH);
    }


    private void setYear(TextView textView1) {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
        SimpleDateFormat simpleDateFormat1 = AppController.getDateFormatYearFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 1);// this takes current date
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String mFromDayVal = simpleDateFormat.format(calendar.getTime());
        starttime = calendar.getTime();
        mFromDayVals.add(mFromDayVal);

        String mToDayVal = simpleDateFormat.format(new Date());
        textView1.setText(simpleDateFormat1.format(calendar.getTime()));

        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        mToDayVal = simpleDateFormat.format(calendar.getTime());
        endtime = calendar.getTime();
        mToDayVals.add(mToDayVal);
        dateType = YEAR;
        dateTypeArray.add(YEAR);
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }
}
