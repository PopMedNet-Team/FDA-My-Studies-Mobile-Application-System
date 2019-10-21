package com.harvard.studyAppModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.gatewayModule.GatewayActivity;
import com.harvard.utils.AppController;

import java.util.ArrayList;

public class StudySignInActivity extends AppCompatActivity {

    private RecyclerView mStudyRecyclerView;
    private AppCompatTextView mFDAListenTitle;
    private RelativeLayout mFilterBtn;
    private RelativeLayout mResourceBtn;
    private AppCompatTextView mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_study);
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();
        setRecyclerView();
        Intent intent = new Intent(StudySignInActivity.this, GatewayActivity.class);
        startActivity(intent);
    }

    private void initializeXMLId() {


        mFDAListenTitle = (AppCompatTextView) findViewById(R.id.fda_listen);
        mFilterBtn = (RelativeLayout) findViewById(R.id.filterBtn);
        mResourceBtn = (RelativeLayout) findViewById(R.id.resourceBtn);
        mStudyRecyclerView = (RecyclerView) findViewById(R.id.studyRecyclerView);
        mSignInButton = (AppCompatTextView) findViewById(R.id.signInButton);
    }

    private void setTextForView() {
        mFDAListenTitle.setText(getResources().getString(R.string.fda_listens));
    }

    private void setFont() {
        try {
            mFDAListenTitle.setTypeface(AppController.getTypeface(StudySignInActivity.this, "bold"));
            mSignInButton.setTypeface(AppController.getTypeface(StudySignInActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StudySignInActivity.this, getResources().getString(R.string.filter_clicked), Toast.LENGTH_LONG).show();
            }
        });
        mResourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StudySignInActivity.this, getResources().getString(R.string.resource_btn_clicked), Toast.LENGTH_LONG).show();
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StudySignInActivity.this, getResources().getString(R.string.sign_in_btn_clicked), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setRecyclerView() {
        mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(StudySignInActivity.this));
        mStudyRecyclerView.setNestedScrollingEnabled(false);
        ArrayList<String> mVideoList = new ArrayList<>();
        mVideoList.add("abc");
        mVideoList.add("abc");
        mVideoList.add("abc");
        mVideoList.add("abc");
        StudySignInListAdapter studyVideoAdapter = new StudySignInListAdapter(StudySignInActivity.this, mVideoList);
        mStudyRecyclerView.setAdapter(studyVideoAdapter);
    }

}