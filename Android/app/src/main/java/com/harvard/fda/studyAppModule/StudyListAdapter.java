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
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.studyModel.StudyList;
import com.harvard.fda.studyAppModule.survayScheduler.model.CompletionAdeherenceCalc;
import com.harvard.fda.utils.AppController;

import java.util.ArrayList;

import io.realm.RealmList;

public class StudyListAdapter extends RecyclerView.Adapter<StudyListAdapter.Holder> {
    private final Context mContext;
    private RealmList<StudyList> mItems;
    StudyFragment studyFragment;
    ArrayList<CompletionAdeherenceCalc> completionAdeherenceCalcs;
    private boolean mClick = true;

    public StudyListAdapter(Context context, RealmList<StudyList> items, StudyFragment studyFragment, ArrayList<CompletionAdeherenceCalc> completionAdeherenceCalcs) {
        this.mContext = context;
        this.mItems = items;
        this.studyFragment = studyFragment;
        this.completionAdeherenceCalcs = completionAdeherenceCalcs;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemCount() {
        try {
            if (mItems == null) return 0;
            return mItems.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder {

        RelativeLayout mContainer;
        AppCompatImageView mStateIcon;
        AppCompatTextView mState;
        AppCompatImageView mStatusImg;
        AppCompatImageView mStudyImg;
        AppCompatTextView mImgTitle;
        AppCompatImageView mStatusImgRight;
        AppCompatTextView mStatus;
        AppCompatTextView mStudyTitle;
        AppCompatTextView mStudyTitleLatin;
        AppCompatTextView mSponser;
        AppCompatTextView mCompletion;
        AppCompatTextView mCompletionVal;
        AppCompatTextView mAdherence;
        AppCompatTextView mAdherenceVal;
        ProgressBar mProgressBar1;
        ProgressBar mProgressBar2;


        Holder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mStateIcon = (AppCompatImageView) itemView.findViewById(R.id.stateIcon);
            mStudyImg = (AppCompatImageView) itemView.findViewById(R.id.studyImg);
            mImgTitle = (AppCompatTextView) itemView.findViewById(R.id.mImgTitle);
            mState = (AppCompatTextView) itemView.findViewById(R.id.state);
            mStatusImg = (AppCompatImageView) itemView.findViewById(R.id.statusImg);
            mStatusImgRight = (AppCompatImageView) itemView.findViewById(R.id.statusImgRight);
            mStatus = (AppCompatTextView) itemView.findViewById(R.id.status);
            mStudyTitle = (AppCompatTextView) itemView.findViewById(R.id.study_title);
            mStudyTitleLatin = (AppCompatTextView) itemView.findViewById(R.id.study_title_latin);
            mSponser = (AppCompatTextView) itemView.findViewById(R.id.sponser);
            mCompletion = (AppCompatTextView) itemView.findViewById(R.id.completion);
            mCompletionVal = (AppCompatTextView) itemView.findViewById(R.id.completion_val);
            mAdherence = (AppCompatTextView) itemView.findViewById(R.id.adherence);
            mAdherenceVal = (AppCompatTextView) itemView.findViewById(R.id.adherence_val);
            mProgressBar1 = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            mProgressBar2 = (ProgressBar) itemView.findViewById(R.id.progressBar2);
            setFont();
        }

        private void setFont() {
            try {
                mImgTitle.setTypeface(AppController.getTypeface(mContext, "medium"));
                mState.setTypeface(AppController.getTypeface(mContext, "medium"));
                mStatus.setTypeface(AppController.getTypeface(mContext, "bold"));
                mStudyTitle.setTypeface(AppController.getTypeface(mContext, "medium"));
                mStudyTitleLatin.setTypeface(AppController.getTypeface(mContext, "regular"));
                mSponser.setTypeface(AppController.getTypeface(mContext, "regular"));
                mCompletion.setTypeface(AppController.getTypeface(mContext, "regular"));
                mCompletionVal.setTypeface(AppController.getTypeface(mContext, "bold"));
                mAdherence.setTypeface(AppController.getTypeface(mContext, "regular"));
                mAdherenceVal.setTypeface(AppController.getTypeface(mContext, "bold"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        if (!AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), "").equalsIgnoreCase("")) {
            holder.mStatus.setVisibility(View.VISIBLE);
            holder.mStatusImg.setVisibility(View.VISIBLE);
            holder.mCompletionVal.setVisibility(View.VISIBLE);
            holder.mCompletion.setVisibility(View.VISIBLE);
            holder.mAdherenceVal.setVisibility(View.VISIBLE);
            holder.mAdherence.setVisibility(View.VISIBLE);
            holder.mStatusImgRight.setVisibility(View.VISIBLE);
            holder.mProgressBar1.setVisibility(View.VISIBLE);
            holder.mProgressBar2.setVisibility(View.VISIBLE);


            if (mItems.get(position).getStudyStatus() != null) {
                if (mItems.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.COMPLETED)) {
                    holder.mStatusImg.setImageResource(R.drawable.completed_icn1);
                    holder.mStatus.setText(R.string.completed);
                } else if (mItems.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.NOT_ELIGIBLE)) {
                    holder.mStatusImg.setImageResource(R.drawable.not_eligible_icn1);
                    holder.mStatus.setText(R.string.not_eligible);
                } else if (mItems.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                    holder.mStatusImg.setImageResource(R.drawable.in_progress_icn);
                    if (mItems.get(position).getStatus().equalsIgnoreCase("closed")) {
                        holder.mStatus.setText(R.string.partial_participation);
                    } else {
                        holder.mStatus.setText(R.string.in_progress);

                    }
                } else if (mItems.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.YET_TO_JOIN)) {
                    holder.mStatusImg.setImageResource(R.drawable.yet_to_join_icn1);
                    if (mItems.get(position).getStatus().equalsIgnoreCase("closed")) {
                        holder.mStatus.setText(R.string.no_participation);
                    } else {
                        holder.mStatus.setText(R.string.yet_to_join);

                    }
                } else if (mItems.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
                    holder.mStatusImg.setImageResource(R.drawable.withdrawn_icn1);
                    holder.mStatus.setText(R.string.withdrawn);
                } else {
                    holder.mStatusImg.setImageResource(R.drawable.yet_to_join_icn1);
                    holder.mStatus.setText(R.string.yet_to_join);
                }
            } else {
                holder.mStatusImg.setImageResource(R.drawable.yet_to_join_icn1);
                holder.mStatus.setText(R.string.yet_to_join);
//                mItems.get(position).setStudyStatus("Yet to Start");
            }

            if (mItems.get(position).isBookmarked()) {
                holder.mStatusImgRight.setImageResource(R.drawable.star_yellow);
            } else {
                holder.mStatusImgRight.setImageResource(R.drawable.star_grey);
            }

            if (mItems.get(position).getStatus().equalsIgnoreCase("closed")) {
                holder.mStatusImgRight.setVisibility(View.GONE);
            }

            if (completionAdeherenceCalcs.size() > 0) {
                try {
                    holder.mCompletionVal.setText("" + ((int) completionAdeherenceCalcs.get(holder.getAdapterPosition()).getCompletion()) + " %");
                    holder.mAdherenceVal.setText("" + ((int) completionAdeherenceCalcs.get(holder.getAdapterPosition()).getAdherence()) + " %");
                    holder.mProgressBar1.setProgress((int) completionAdeherenceCalcs.get(holder.getAdapterPosition()).getCompletion());
                    holder.mProgressBar2.setProgress((int) completionAdeherenceCalcs.get(holder.getAdapterPosition()).getAdherence());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

        } else {
            holder.mStatus.setVisibility(View.GONE);
            holder.mStatusImg.setVisibility(View.GONE);
            holder.mCompletionVal.setVisibility(View.GONE);
            holder.mCompletion.setVisibility(View.GONE);
            holder.mAdherenceVal.setVisibility(View.GONE);
            holder.mAdherence.setVisibility(View.GONE);
            holder.mStatusImgRight.setVisibility(View.GONE);
            holder.mProgressBar1.setVisibility(View.GONE);
            holder.mProgressBar2.setVisibility(View.GONE);
        }

        holder.mState.setText(mItems.get(position).getStatus().toUpperCase());
        GradientDrawable bgShape = (GradientDrawable) holder.mStateIcon.getBackground();
        if (mItems.get(position).getStatus().equalsIgnoreCase("active")) {
            bgShape.setColor(mContext.getResources().getColor(R.color.bullet_green_color));
        } else if (mItems.get(position).getStatus().equalsIgnoreCase("upcoming")) {
            bgShape.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (mItems.get(position).getStatus().equalsIgnoreCase("closed")) {
            bgShape.setColor(mContext.getResources().getColor(R.color.red));
        } else if (mItems.get(position).getStatus().equalsIgnoreCase("paused")) {
            bgShape.setColor(mContext.getResources().getColor(R.color.rectangle_yellow));
        }



        Glide.with(mContext).load(mItems.get(position).getLogo())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mStudyImg);

        holder.mStudyTitle.setText(mItems.get(position).getTitle());
        holder.mStudyTitleLatin.setText(Html.fromHtml(mItems.get(position).getTagline()));
        try {
            holder.mImgTitle.setText(mItems.get(position).getCategory().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sponser = "";
        sponser = mItems.get(position).getSponsorName();
        holder.mSponser.setText(sponser);

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
                    try {
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.title), "" + mItems.get(holder.getAdapterPosition()).getTitle());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.bookmark), "" + mItems.get(holder.getAdapterPosition()).isBookmarked());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.status), "" + mItems.get(holder.getAdapterPosition()).getStatus());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.studyStatus), "" + mItems.get(holder.getAdapterPosition()).getStudyStatus());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.position), "" + holder.getAdapterPosition());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.enroll), "" + mItems.get(holder.getAdapterPosition()).getSetting().isEnrolling());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.rejoin), "" + mItems.get(holder.getAdapterPosition()).getSetting().getRejoin());
                        AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.studyVersion), "" + mItems.get(holder.getAdapterPosition()).getStudyVersion());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mItems.get(position).getStatus().equalsIgnoreCase(mContext.getString(R.string.active)) && mItems.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                        studyFragment.getStudyUpdate(mItems.get(holder.getAdapterPosition()).getStudyId(), mItems.get(holder.getAdapterPosition()).getStudyVersion(), mItems.get(holder.getAdapterPosition()).getTitle(), "", "", "", "");
                    }
                    else {
                        Intent intent = new Intent(mContext.getApplicationContext(), StudyInfoActivity.class);
                        intent.putExtra("studyId", mItems.get(holder.getAdapterPosition()).getStudyId());
                        intent.putExtra("title", mItems.get(holder.getAdapterPosition()).getTitle());
                        intent.putExtra("bookmark", mItems.get(holder.getAdapterPosition()).isBookmarked());
                        intent.putExtra("status", mItems.get(holder.getAdapterPosition()).getStatus());
                        intent.putExtra("studyStatus", mItems.get(holder.getAdapterPosition()).getStudyStatus());
                        intent.putExtra("position", "" + holder.getAdapterPosition());
                        intent.putExtra("enroll", "" + mItems.get(holder.getAdapterPosition()).getSetting().isEnrolling());
                        intent.putExtra("rejoin", "" + mItems.get(holder.getAdapterPosition()).getSetting().getRejoin());
                        ((StudyActivity) mContext).startActivityForResult(intent, 100);
                    }
                }
            }
        });

        holder.mStatusImgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItems.get(holder.getAdapterPosition()).isBookmarked()) {
                    studyFragment.updatebookmark(false, holder.getAdapterPosition(), mItems.get(holder.getAdapterPosition()).getStudyId(), mItems.get(holder.getAdapterPosition()).getStudyStatus());
                } else {
                    studyFragment.updatebookmark(true, holder.getAdapterPosition(), mItems.get(holder.getAdapterPosition()).getStudyId(), mItems.get(holder.getAdapterPosition()).getStudyStatus());
                }
            }
        });
    }

    public void modifyAdapter(RealmList<StudyList> searchResultList, ArrayList<CompletionAdeherenceCalc> completionAdeherenceCalcs) {
        mItems = searchResultList;
        this.completionAdeherenceCalcs = completionAdeherenceCalcs;
    }

}