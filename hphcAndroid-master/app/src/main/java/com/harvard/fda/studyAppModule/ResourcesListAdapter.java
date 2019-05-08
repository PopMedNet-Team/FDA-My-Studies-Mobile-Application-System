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

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.studyModel.Resource;
import com.harvard.fda.utils.AppController;

import java.util.ArrayList;

import io.realm.RealmList;

public class ResourcesListAdapter extends RecyclerView.Adapter<ResourcesListAdapter.Holder> {
    private final Context mContext;
    private final ArrayList<Resource> mItems = new ArrayList<>();
    Fragment fragment;


    public ResourcesListAdapter(Context context, RealmList<Resource> items, Fragment fragment) {
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
                        Intent intent = new Intent(mContext, ResourcesWebViewActivity.class);
                        intent.putExtra("studyId", ((SurveyActivity) mContext).getStudyId());
                        intent.putExtra("title", "" + mItems.get(i).getTitle().toString());
                        intent.putExtra("type", "" + mItems.get(i).getType().toString());
                        intent.putExtra("content", "" + mItems.get(i).getContent().toString());
                        mContext.startActivity(intent);
                    } else if (mItems.get(i).getTitle().equalsIgnoreCase(view.getResources().getString(R.string.about_study1))) {
                        Intent intent = new Intent(mContext, StudyInfoActivity.class);
                        intent.putExtra("studyId", ((SurveyActivity) mContext).getStudyId());
                        intent.putExtra("title", ((SurveyActivity) mContext).getTitle1());
                        intent.putExtra("bookmark", ((SurveyActivity) mContext).getBookmark());
                        intent.putExtra("status", ((SurveyActivity) mContext).getStatus());
                        intent.putExtra("studyStatus", ((SurveyActivity) mContext).getStudyStatus());
                        intent.putExtra("position", "" + ((SurveyActivity) mContext).getPosition());
                        intent.putExtra("enroll", "" + ((SurveyActivity) mContext).getTitle1());
                        intent.putExtra("rejoin", "" + ((SurveyActivity) mContext).getTitle1());
                        intent.putExtra("about_this_study", true);
                        (mContext).startActivity(intent);

                    } else if (mItems.get(i).getTitle().equalsIgnoreCase(view.getResources().getString(R.string.consent_pdf))) {
                        try {
                            Intent intent = new Intent(mContext, PDFDisplayActivity.class);
                            intent.putExtra("studyId", ((SurveyActivity) mContext).getStudyId());
                            intent.putExtra("title", ((SurveyActivity) mContext).getTitle1());
                            (mContext).startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (mItems.get(i).getTitle().equalsIgnoreCase(view.getResources().getString(R.string.leave_study))) {

                        String message = ((SurveyResourcesFragment) fragment).getLeaveStudyMessage();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builder.setTitle(mContext.getResources().getString(R.string.leave_study) + "?");
                        builder.setMessage(message);
                        builder.setPositiveButton(mContext.getResources().getString(R.string.proceed_caps), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                typeUserShowDialog();
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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void typeUserShowDialog() {
        try {
            String withdrawalType = ((SurveyResourcesFragment) fragment).getType();
            switch (withdrawalType) {
                case "ask_user":
                    showDialog(3);
                    break;
                case "delete_data":
                    showDialog(2);

                    break;
                case "no_action":
                    showDialog(1);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(int count) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        builder.setTitle(mContext.getResources().getString(R.string.important_note));
        // withdrawalType ask_user
        if (count == 3) {
            builder.setMessage(mContext.getResources().getString(R.string.leave_study_retained_or_deleted_message));


            builder.setPositiveButton(mContext.getResources().getString(R.string.retain_my_data_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("false");
                }
            });


            builder.setNeutralButton(mContext.getResources().getString(R.string.cancel_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setNegativeButton(mContext.getResources().getString(R.string.delete_my_data_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("true");
                    dialog.cancel();
                }
            });

            AlertDialog diag = builder.create();
            diag.show();
        } else if (count == 2) {
            //withdrawalType delete_data
            builder.setMessage(mContext.getResources().getString(R.string.leave_study_deleted_message));
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
            builder.setMessage(mContext.getResources().getString(R.string.leave_study_retained_message));
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