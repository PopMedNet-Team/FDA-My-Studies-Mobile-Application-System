package com.harvard.studyAppModule.custom.activeTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

/**
 * Created by Naveen Raj on 11/16/2016.
 */
public class StepCountClass implements StepBody, SensorEventListener {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<String> result;
    MediaRecorder mediaRecorder;
    SensorManager sensorManager;
    int step_count = 0;
    boolean next;
    Context mContext;
    Handler handler;
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText editText;
    String AudioSavePathInDevice = null;

    public StepCountClass(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(this.step) : result;
    }

    @Override
    public View getBodyView(int viewType, final LayoutInflater inflater, ViewGroup parent) {
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        mContext = inflater.getContext();
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Toast.makeText(inflater.getContext(), R.string.start_walking, Toast.LENGTH_SHORT).show();
        next = false;
        sensorManager = (SensorManager) inflater.getContext().getSystemService(Context.SENSOR_SERVICE);

        final Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(inflater.getContext(), R.string.count_sensor_not_available, Toast.LENGTH_LONG).show();
        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                next = true;
                Toast.makeText(inflater.getContext(), R.string.stop_walking, Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(StepCountClass.this, countSensor);
                // Actions to do after 10 seconds
            }
        }, 10000);

        return linearLayout;
    }


    @Override
    public StepResult getStepResult(boolean skipped) {
        if (handler != null) {
            Toast.makeText(mContext, R.string.stop_in_between, Toast.LENGTH_SHORT).show();
            handler.removeCallbacksAndMessages(null);
        }
        if (skipped) {
            result.setResult(null);
        } else {
            result.setResult("" + step_count);
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (!next)
            return BodyAnswer.INVALID;
        return BodyAnswer.VALID;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        step_count = (int) sensorEvent.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
