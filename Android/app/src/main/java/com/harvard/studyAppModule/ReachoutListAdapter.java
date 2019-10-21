package com.harvard.studyAppModule;

import android.content.Context;
import android.content.Intent;
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

public class ReachoutListAdapter extends RecyclerView.Adapter<ReachoutListAdapter.Holder> {
    private final Context mContext;
    private final ArrayList<String> mItems = new ArrayList<>();

    public ReachoutListAdapter(Context context, ArrayList<String> items) {
        this.mContext = context;
        this.mItems.addAll(items);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reachout_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        final RelativeLayout mContainer;
        final AppCompatTextView mReachoutTitle;


        Holder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mReachoutTitle = (AppCompatTextView) itemView.findViewById(R.id.reachoutTitle);
            setFont();

        }

        private void setFont() {
            try {
                mReachoutTitle.setTypeface(AppController.getTypeface(mContext, "regular"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final int i = holder.getAdapterPosition();
        try {
            holder.mReachoutTitle.setText(mItems.get(position));

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "GOTO Resources Details Screen " + i, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0)
                {
                    Intent intent = new Intent(mContext, FeedbackActivity.class);
                    mContext.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(mContext, ContactUsActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });


    }
}