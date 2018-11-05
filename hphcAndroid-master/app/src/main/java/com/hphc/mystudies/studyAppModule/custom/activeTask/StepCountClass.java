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
package com.hphc.mystudies.studyAppModule.custom.activeTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hphc.mystudies.R;
import com.hphc.mystudies.studyAppModule.custom.QuestionStepCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

public class StepCountClass implements StepBody, SensorEventListener {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private StepResult<String> result;
    private SensorManager sensorManager;
    private int stepCount = 0;
    private boolean next;
    private Context mContext;
    private Handler handler;
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    public StepCountClass(Step step, StepResult result) {
        QuestionStepCustom questionStepCustom = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(questionStepCustom) : result;
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
            result.setResult(Integer.toString(stepCount));
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
        stepCount = (int) sensorEvent.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
