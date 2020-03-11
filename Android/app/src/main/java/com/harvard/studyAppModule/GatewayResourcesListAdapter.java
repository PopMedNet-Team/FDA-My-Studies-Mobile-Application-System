package com.harvard.studyAppModule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.harvard.R;
import com.harvard.studyAppModule.studyModel.Resource;
import com.harvard.utils.AppController;

import java.util.ArrayList;

import io.realm.RealmList;

public class GatewayResourcesListAdapter extends RecyclerView.Adapter<GatewayResourcesListAdapter.Holder> {
    private final Context mContext;
    private final ArrayList<Resource> mItems = new ArrayList<>();
    Fragment fragment;


    public GatewayResourcesListAdapter(Context context, RealmList<Resource> items, Fragment fragment) {
        this.mContext = context;
        this.mItems.addAll(items);
        this.fragment = fragment;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resources_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        final RelativeLayout mContainer;
        final AppCompatTextView mResourcesTitle;


        Holder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mResourcesTitle = (AppCompatTextView) itemView.findViewById(R.id.resourcesTitle);
            setFont();

        }

        private void setFont() {
            try {
                mResourcesTitle.setTypeface(AppController.getTypeface(mContext, "regular"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final int i = holder.getAdapterPosition();
        try {
            holder.mResourcesTitle.setText(mItems.get(i).getTitle());

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItems.get(i).getType() != null) {
                        Intent intent = new Intent(mContext, GatewayResourcesWebViewActivity.class);
                        intent.putExtra("title", "" + mItems.get(i).getTitle().toString());
                        intent.putExtra("type", "" + mItems.get(i).getType().toString());
                        intent.putExtra("content", "" + mItems.get(i).getContent().toString());
                        mContext.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(int count) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        builder.setTitle(mContext.getResources().getString(R.string.prject_name));
        builder.setMessage(mContext.getResources().getString(R.string.delete_study_data));
        // withdrawalType ask_user
        if (count == 3) {
            builder.setNeutralButton(mContext.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("true");
                    dialog.cancel();
                }
            });

            builder.setPositiveButton(mContext.getResources().getString(R.string.retain_my_data_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("false");
                }
            });


            builder.setNegativeButton(mContext.getResources().getString(R.string.cancel_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog diag = builder.create();
            diag.show();
        } else if (count == 2) {
            //withdrawalType delete_data
            builder.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("true");
                }
            });


            builder.setNegativeButton(mContext.getResources().getString(R.string.cancel_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog diag = builder.create();
            diag.show();
        } else if (count == 1) {
            // withdrawalType no_action
            builder.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("false");
                }
            });


            builder.setNegativeButton(mContext.getResources().getString(R.string.cancel_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog diag = builder.create();
            diag.show();
        }
    }


}