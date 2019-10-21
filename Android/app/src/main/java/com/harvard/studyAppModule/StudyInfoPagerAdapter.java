package com.harvard.studyAppModule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.harvard.R;
import com.harvard.studyAppModule.studyModel.StudyInfo;
import com.harvard.utils.AppController;

import io.realm.RealmList;

public class StudyInfoPagerAdapter extends PagerAdapter {

    private int mSize;
    private AppCompatTextView mTitle;
    private AppCompatTextView mDesc;
    private RelativeLayout mWatchVideo;
    private AppCompatTextView mWatchVideoLabel;
    private AppCompatTextView mJoinButton;
    private AppCompatTextView mVisitWebsite;
    private RelativeLayout mVisitWebsiteButtonLayout;
    private AppCompatTextView mVisitWebsiteButton;
    private RelativeLayout mLernMoreButtonLayout;
    private AppCompatTextView mLernMoreButton;
    private Context mContext;
    private RealmList<StudyInfo> mInfo;
    private AppCompatImageView mBgImg;
    private String studyId;

    public StudyInfoPagerAdapter(Context context, RealmList<StudyInfo> info, String studyId) {
        mSize = info.size();
        this.mContext = context;
        this.mInfo = info;
        this.studyId = studyId;

    }


    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInfo.get(position).getType().equalsIgnoreCase("video")) {
            View view = inflater.inflate(R.layout.study_info_item1, null);
            initializeXMLId(position, view);
            setFont(position, view);
            bindEvents(position);
            setData(position);
            collection.addView(view);
            return view;
        } else {
            View view1 = inflater.inflate(R.layout.study_info_item2, null);
            initializeXMLId(position, view1);
            setFont(position, view1);
            bindEvents(position);
            setData(position);
            collection.addView(view1);
            return view1;
        }
    }

    private void setData(int pos) {
        mTitle.setText(mInfo.get(pos).getTitle());
        mDesc.setText(Html.fromHtml(mInfo.get(pos).getText()));
        Glide.with(mContext).load(mInfo.get(pos).getImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBgImg);

    }

    private void initializeXMLId(int pos, View view) {
        if (mInfo.get(pos).getType().equalsIgnoreCase("video")) {
            mTitle = (AppCompatTextView) view.findViewById(R.id.title);
            mDesc = (AppCompatTextView) view.findViewById(R.id.desc);
            mWatchVideo = (RelativeLayout) view.findViewById(R.id.watch_video);
            mWatchVideoLabel = (AppCompatTextView) view.findViewById(R.id.watchVideoLabel);
            mBgImg = (AppCompatImageView) view.findViewById(R.id.bgImg);
        } else {
            mTitle = (AppCompatTextView) view.findViewById(R.id.title);
            mDesc = (AppCompatTextView) view.findViewById(R.id.desc);
            mBgImg = (AppCompatImageView) view.findViewById(R.id.bgImg);
        }
    }

    private void setFont(int pos, View view) {
        try {
            if (mInfo.get(pos).getType().equalsIgnoreCase("video")) {
                mTitle.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
                mDesc.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
                mWatchVideoLabel.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
            } else {
                mTitle.setTypeface(AppController.getTypeface(view.getContext(), "thin"));
                mDesc.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents(final int pos) {
        if (mInfo.get(pos).getType().equalsIgnoreCase("video")) {
            if(mInfo.get(pos).getLink().equalsIgnoreCase(""))
            {
                mWatchVideo.setVisibility(View.INVISIBLE);
                mWatchVideo.setClickable(false);
            }
            else {
                mWatchVideo.setVisibility(View.VISIBLE);
                mWatchVideo.setClickable(true);
                mWatchVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mInfo.get(pos).getLink()));
                        mContext.startActivity(intent);
                }
            });

        }
        }
    }


   }