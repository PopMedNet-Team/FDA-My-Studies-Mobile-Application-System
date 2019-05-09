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
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.studyModel.StudyInfo;
import com.harvard.fda.utils.AppController;

import java.util.ArrayList;

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