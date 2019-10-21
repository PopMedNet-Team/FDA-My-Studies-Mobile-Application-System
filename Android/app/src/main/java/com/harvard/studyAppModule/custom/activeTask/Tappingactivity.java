package com.harvard.studyAppModule.custom.activeTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.studyAppModule.activityBuilder.CustomSurveyViewTaskActivity;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.harvard.utils.ActiveTaskService;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

/**
 * Created by Naveen Raj on 11/14/2016.
 */
public class Tappingactivity implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<TappingResultFormat> result;
    MediaRecorder mediaRecorder;
    Handler handler;
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText editText;
    private int tappingcount = 0;
    private Context mContext;
    private TappingAnswerFormat mTappingAnswerFormat;
    private Thread mThread;
    private boolean timeup = false;
    private EditText kickcounter;
    private Intent serviceintent;
    private ImageView tapButton;
    private ImageView editButton;
    private TextView mTimer;
    private int maxTime;
    private RelativeLayout timereditlayout;
    private int finalSecond;

    public Tappingactivity(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(this.step) : result;
        mTappingAnswerFormat = (TappingAnswerFormat) ((QuestionStepCustom) step).getAnswerFormat1();
    }

    @Override
    public View getBodyView(int viewType, final LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.content_fetal_kick_counter, null);
        mContext = inflater.getContext();
        tapButton = (ImageView) view.findViewById(R.id.tapbutton);
        editButton = (ImageView) view.findViewById(R.id.editButton);
        final ImageView startTimer = (ImageView) view.findViewById(R.id.startTimer);
        mTimer = (TextView) view.findViewById(R.id.mTimer);
        final TextView starttxt = (TextView) view.findViewById(R.id.starttxt);
        final TextView mTapStart = (TextView) view.findViewById(R.id.mTapStart);
        kickcounter = (EditText) view.findViewById(R.id.kickcounter);

        timereditlayout = view.findViewById(R.id.timereditlayout);

        kickcounter.setEnabled(false);
        final int[] second = {0};

        maxTime = mTappingAnswerFormat.getDuration();

        final int maxcount = mTappingAnswerFormat.getKickCount();

        mTimer.setText(formathrs(second[0]));
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                if (!timeup && second[0] <= maxTime) {
                                    mTimer.setText(formathrs(second[0]));
                                    second[0]++;
                                } else {
                                    tapButton.setEnabled(false);
                                    timereditlayout.setVisibility(View.GONE);
                                    timeup = true;
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        mTimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!timeup)
                    tapButton.setEnabled(true);
                else {
                    tapButton.setEnabled(false);
                    timereditlayout.setVisibility(View.GONE);
                }
            }
        });

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateservice(maxTime);
                IntentFilter filter = new IntentFilter();
                filter.addAction("com.harvard.ActiveTask");
                mContext.registerReceiver(receiver, filter);


                if (!kickcounter.isEnabled()) {
                    kickcounter.setEnabled(true);
                }

                starttxt.setVisibility(View.GONE);
                mTapStart.setVisibility(View.VISIBLE);

                startTimer.setVisibility(View.GONE);
                tapButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
            }
        });
        mTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] duration = mTimer.getText().toString().split(":");
                if (timeup) {
                    MyTimePickerDialog mTimePicker = new MyTimePickerDialog(mContext, new MyTimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                            // TODO Auto-generated method stub
                            String hrs;
                            String min;
                            String sec;
                            if (hourOfDay < 10) {
                                hrs = "0" + hourOfDay;
                            } else {
                                hrs = "" + hourOfDay;
                            }
                            if (minute < 10) {
                                min = "0" + minute;
                            } else {
                                min = "" + minute;
                            }
                            if (seconds < 10) {
                                sec = "0" + seconds;
                            } else {
                                sec = "" + seconds;
                            }
                            finalSecond = (hourOfDay * 60 * 60) + (minute * 60) + (seconds);
                            if (finalSecond <= maxTime) {
                                mTimer.setText(hrs + ":" + min + ":" + sec);
                            } else {
                                Toast.makeText(inflater.getContext(), "Max duration you can enter is " + formathrs(maxTime), Toast.LENGTH_SHORT).show();
                                finalSecond = (Integer.parseInt(duration[0]) * 60 * 60) + (Integer.parseInt(duration[1]) * 60) + (Integer.parseInt(duration[2]));
                            }
                        }
                    }, Integer.parseInt(duration[0]), Integer.parseInt(duration[1]), Integer.parseInt(duration[2]), true);
                    mTimePicker.show();
                }
            }
        });


        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickcounter.setFocusable(false);
                kickcounter.setFocusableInTouchMode(false);
                kickcounter.setFocusable(true);
                kickcounter.setFocusableInTouchMode(true);
                editButton.setVisibility(View.VISIBLE);
                if (!kickcounter.getText().toString().equalsIgnoreCase("")) {
                    tappingcount = Integer.parseInt(kickcounter.getText().toString());
                } else {
                    tappingcount = 0;
                }

                if (!timeup) {
                    kickcounter.setText(String.valueOf(tappingcount + 1));
                    if (Integer.parseInt(kickcounter.getText().toString()) >= maxcount) {
                        timeup = true;
                        endAlert("You have recorded " + kickcounter.getText().toString() + " kicks in " + mTimer.getText().toString() +". Proceed to submitting count and time?");
                        timereditlayout.setVisibility(View.GONE);
                        try {
                            mContext.stopService(serviceintent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            mContext.unregisterReceiver(receiver);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(inflater.getContext(), "max kick you can enter is " + maxcount, Toast.LENGTH_SHORT).show();
                }
            }
        });

        final String[] pervioustxt = {""};
        kickcounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pervioustxt[0] = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equalsIgnoreCase("") && Integer.parseInt(s.toString()) > maxcount) {
                    Toast.makeText(inflater.getContext(), "max kick you can enter is " + maxcount, Toast.LENGTH_SHORT).show();
                    kickcounter.setText(pervioustxt[0]);
                    kickcounter.setSelection(kickcounter.getText().toString().length());
                }
            }
        });

        kickcounter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editButton.setVisibility(View.GONE);
                return false;
            }
        });

        return view;
    }


    private String formathrs(int second) {
        String hrs;
        String min;
        String sec;
        if (second / 3600 < 10) {
            hrs = "0" + (second / 3600);
        } else {
            hrs = "" + (second / 3600);
        }
        if (((second % 3600) / 60) < 10) {
            min = "0" + ((second % 3600) / 60);
        } else {
            min = "" + ((second % 3600) / 60);
        }
        if ((second % 60) < 10) {
            sec = "0" + (second % 60);
        } else {
            sec = "" + (second % 60);
        }

        return hrs + ":" + min + ":" + sec;
    }


    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            TappingResultFormat tappingResultFormat = new TappingResultFormat();
            tappingResultFormat.setDuration("" + finalSecond);
            tappingResultFormat.setValue(Double.parseDouble(kickcounter.getText().toString()));
            result.setResult(tappingResultFormat);
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (!timeup) {
            return BodyAnswer.INVALID;
        } else {
            return BodyAnswer.VALID;
        }
    }


    private void activateservice(long sec) {
        serviceintent = new Intent(mContext, ActiveTaskService.class);
        if (!isMyServiceRunning(ActiveTaskService.class)) {
            serviceintent.putExtra("remaining_sec", "" + sec);
            mContext.startService(serviceintent);
        } else {
            try {
                mContext.stopService(serviceintent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mContext.unregisterReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceintent.putExtra("remaining_sec", "" + sec);
            mContext.startService(serviceintent);
        }

        ((CustomSurveyViewTaskActivity) mContext).serviceStarted(receiver, serviceintent);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Integer.parseInt(intent.getStringExtra("sec")) >= maxTime) {
                try {
                    mContext.stopService(serviceintent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mContext.unregisterReceiver(receiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                timeup = true;
                tapButton.setEnabled(false);
                timereditlayout.setVisibility(View.GONE);
                endAlert("You have recorded " + kickcounter.getText().toString() + " kicks in " + mTimer.getText().toString() + ". Proceed to submitting count and time?");
                mTimer.setText(formathrs(Integer.parseInt(intent.getStringExtra("sec"))));
            }
            finalSecond = Integer.parseInt(intent.getStringExtra("sec"));
            if (!timeup)
                mTimer.setText(formathrs(Integer.parseInt(intent.getStringExtra("sec"))));
        }
    };


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void endAlert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setTitle(mContext.getApplicationInfo().loadLabel(mContext.getPackageManager()).toString());
        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((CustomSurveyViewTaskActivity) mContext).onSaveStep(StepCallbacks.ACTION_NEXT, step, getStepResult(false));
                    }
                })
                .setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
