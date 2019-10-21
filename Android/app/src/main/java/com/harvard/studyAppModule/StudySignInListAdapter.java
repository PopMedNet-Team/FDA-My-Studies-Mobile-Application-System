package com.harvard.studyAppModule;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.utils.AppController;

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