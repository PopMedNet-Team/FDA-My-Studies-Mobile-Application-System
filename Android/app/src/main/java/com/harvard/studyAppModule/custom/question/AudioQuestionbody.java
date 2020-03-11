package com.harvard.studyAppModule.custom.question;

import android.media.MediaRecorder;
import android.os.Environment;
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

import java.io.IOException;
import java.util.Random;

/**
 * Created by Naveen Raj on 11/14/2016.
 */
public class AudioQuestionbody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<String> result;
    MediaRecorder mediaRecorder;
    boolean next;
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText editText;
    String AudioSavePathInDevice = null;

    public AudioQuestionbody(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(this.step) : result;
    }

    @Override
    public View getBodyView(int viewType, final LayoutInflater inflater, ViewGroup parent) {
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Toast.makeText(inflater.getContext(), R.string.recording, Toast.LENGTH_SHORT).show();
        AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FDA/" + CreateRandomAudioFileName(5) + ".3gp";
        MediaRecorderReady();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            next = false;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                next = true;
                Toast.makeText(inflater.getContext(), R.string.stopping, Toast.LENGTH_SHORT).show();
                mediaRecorder.stop();
                mediaRecorder.release();
                // Actions to do after 10 seconds
            }
        }, 10000);


        return linearLayout;
    }


    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            result.setResult("" + AudioSavePathInDevice);
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (next) {
            return BodyAnswer.VALID;
        }
        return BodyAnswer.INVALID;
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        Random random = new Random();
        String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

}
