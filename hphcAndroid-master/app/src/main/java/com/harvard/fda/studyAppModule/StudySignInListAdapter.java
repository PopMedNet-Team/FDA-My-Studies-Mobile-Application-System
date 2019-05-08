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
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.utils.AppController;

import java.util.ArrayList;

public class StudySignInListAdapter extends RecyclerView.Adapter<StudySignInListAdapter.Holder> {
    private final Context mContext;
    private ArrayList<String> mItems = new ArrayList<>();

    public StudySignInListAdapter(Context context, ArrayList<String> items) {
        this.mContext = context;
        this.mItems.addAll(items);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_sign_in_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        RelativeLayout mContainer;
        AppCompatImageView mStateIcon;
        AppCompatTextView mState;
        AppCompatTextView mStudyTitle;
        AppCompatTextView mStudyTitleLatin;
        AppCompatTextView mSponser;


        Holder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mStateIcon = (AppCompatImageView) itemView.findViewById(R.id.stateIcon);
            mState = (AppCompatTextView) itemView.findViewById(R.id.state);
            mStudyTitle = (AppCompatTextView) itemView.findViewById(R.id.study_title);
            mStudyTitleLatin = (AppCompatTextView) itemView.findViewById(R.id.study_title_latin);
            mSponser = (AppCompatTextView) itemView.findViewById(R.id.sponser);
            setFont();
        }

        private void setFont() {
            try {
                mState.setTypeface(AppController.getTypeface(mContext, "medium"));
                mStudyTitle.setTypeface(AppController.getTypeface(mContext, "medium"));
                mStudyTitleLatin.setTypeface(AppController.getTypeface(mContext, "regular"));
                mSponser.setTypeface(AppController.getTypeface(mContext, "regular"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final int i = holder.getAdapterPosition();
        try {
            // changing the bg color of the round shape
            GradientDrawable bgShape = (GradientDrawable) holder.mStateIcon.getBackground();
            if (i == 3)
                bgShape.setColor(mContext.getResources().getColor(R.color.colorPrimary));
            else
                bgShape.setColor(mContext.getResources().getColor(R.color.bullet_green_color));

            if (i == 3)
                holder.mState.setText(mContext.getResources().getString(R.string.upcoming_caps));
            else
                holder.mState.setText(mContext.getResources().getString(R.string.active1));


            holder.mStateIcon.setImageResource(R.drawable.bullet);

            if (i == 1)
                holder.mStudyTitle.setText(mContext.getResources().getString(R.string.study_for_fitness) + " ");
            else
                holder.mStudyTitle.setText(mContext.getResources().getString(R.string.study_pregnant_women) + " ");
            holder.mStudyTitleLatin.setText("Lorem ipsum dolor sit amet ");
            String category = mContext.getResources().getString(R.string.pregnancy);
            if (i == 1)
                category = mContext.getResources().getString(R.string.fitness);
            holder.mSponser.setText(mContext.getResources().getString(R.string.sponsor_name) + "  |  " + category);

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "GOTO Details Screen", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}