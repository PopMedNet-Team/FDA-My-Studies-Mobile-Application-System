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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.studyModel.Notification;
import com.harvard.fda.studyAppModule.studyModel.Study;
import com.harvard.fda.studyAppModule.studyModel.StudyList;
import com.harvard.fda.utils.AppController;

import io.realm.Realm;
import io.realm.RealmList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.Holder> {
    private final Context mContext;
    private RealmList<Notification> mItems;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    public NotificationListAdapter(Context context, RealmList<Notification> notifications, Realm realm) {
        this.mContext = context;
        this.mItems = notifications;
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = realm;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        RelativeLayout mContainer;
        AppCompatTextView mNotificationMsg;
        AppCompatTextView mDayTimeDisplay;


        Holder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mNotificationMsg = (AppCompatTextView) itemView.findViewById(R.id.notification_msg);
            mDayTimeDisplay = (AppCompatTextView) itemView.findViewById(R.id.dayTimeDisplay);

            setFont();

        }

        private void setFont() {
            try {
                mNotificationMsg.setTypeface(AppController.getTypeface(mContext, "regular"));
                mDayTimeDisplay.setTypeface(AppController.getTypeface(mContext, "medium"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        try {
            holder.mNotificationMsg.setText(mItems.get(holder.getAdapterPosition()).getMessage());
            holder.mDayTimeDisplay.setText(AppController.getDateFormatType1().format(AppController.getDateFormat().parse(mItems.get(holder.getAdapterPosition()).getDate())));

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), "").equalsIgnoreCase("")) {
                        if (mItems.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("Gateway")) {
                            if (mItems.get(holder.getAdapterPosition()).getSubtype().equalsIgnoreCase("Study")) {

                                Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                                if (mStudy != null) {
                                    RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                    studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, mRealm);
                                    boolean isStudyAvailable = false;
                                    for (int i = 0; i < studyListArrayList.size(); i++) {
                                        if (mItems.get(holder.getAdapterPosition()).getStudyId().equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                                            try {
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.title), "" + studyListArrayList.get(i).getTitle());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.bookmark), "" + studyListArrayList.get(i).isBookmarked());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.status), "" + studyListArrayList.get(i).getStatus());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.studyStatus), "" + studyListArrayList.get(i).getStudyStatus());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.position), "" + i);
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.enroll), "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.rejoin), "" + studyListArrayList.get(i).getSetting().getRejoin());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.studyVersion), "" + studyListArrayList.get(i).getStudyVersion());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(mContext.getString(R.string.active)) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                                Intent intent = new Intent(mContext, SurveyActivity.class);
                                                intent.putExtra("studyId", mItems.get(holder.getAdapterPosition()).getStudyId());
                                                mContext.startActivity(intent);
                                            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(mContext.getString(R.string.paused))) {
                                                Toast.makeText(mContext, R.string.study_paused, Toast.LENGTH_SHORT).show();
                                            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(mContext.getString(R.string.closed))) {
                                                Toast.makeText(mContext, R.string.study_resume, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Intent intent = new Intent(mContext.getApplicationContext(), StudyInfoActivity.class);
                                                intent.putExtra("studyId", studyListArrayList.get(i).getStudyId());
                                                intent.putExtra("title", studyListArrayList.get(i).getTitle());
                                                intent.putExtra("bookmark", studyListArrayList.get(i).isBookmarked());
                                                intent.putExtra("status", studyListArrayList.get(i).getStatus());
                                                intent.putExtra("studyStatus", studyListArrayList.get(i).getStudyStatus());
                                                intent.putExtra("position", "" + i);
                                                intent.putExtra("enroll", "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                                intent.putExtra("rejoin", "" + studyListArrayList.get(i).getSetting().getRejoin());
                                                mContext.startActivity(intent);
                                            }
                                            isStudyAvailable = true;
                                            break;
                                        }
                                    }
                                    if (!isStudyAvailable) {
                                        Toast.makeText(mContext, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                }
                            } else if (mItems.get(holder.getAdapterPosition()).getSubtype().equalsIgnoreCase("Resource")) {
                                Intent intent = new Intent();
                                intent.putExtra("action", "refresh");
                                ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                                ((Activity) mContext).finish();
                            }
                        } else if (mItems.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("Study")) {
                            Log.e("study", "" + mItems.get(holder.getAdapterPosition()).getStudyId() + "  " + holder.getAdapterPosition() + "  " + mItems.get(holder.getAdapterPosition()).getSubtype());
                            if (mItems.get(holder.getAdapterPosition()).getSubtype().equalsIgnoreCase("Activity") || mItems.get(holder.getAdapterPosition()).getSubtype().equalsIgnoreCase("Resource")) {
                                Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                                if (mStudy != null) {
                                    RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                    studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, mRealm);
                                    boolean isStudyAvailable = false;
                                    boolean isStudyJoined = false;
                                    for (int i = 0; i < studyListArrayList.size(); i++) {
                                        Log.e("studylist ", "" + studyListArrayList.get(i).getStudyId());
                                        if (mItems.get(holder.getAdapterPosition()).getStudyId().equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                                            isStudyAvailable = true;
                                            try {
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.title), "" + studyListArrayList.get(i).getTitle());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.bookmark), "" + studyListArrayList.get(i).isBookmarked());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.status), "" + studyListArrayList.get(i).getStatus());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.studyStatus), "" + studyListArrayList.get(i).getStudyStatus());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.position), "" + i);
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.enroll), "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                                AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.rejoin), "" + studyListArrayList.get(i).getSetting().getRejoin());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(mContext.getString(R.string.active)) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                                Intent intent = new Intent(mContext, SurveyActivity.class);
                                                intent.putExtra("studyId", mItems.get(holder.getAdapterPosition()).getStudyId());
                                                intent.putExtra("from", "NotificationActivity");
                                                intent.putExtra("to", mItems.get(holder.getAdapterPosition()).getSubtype());
                                                mContext.startActivity(intent);
                                                isStudyJoined = true;
                                                break;
                                            } else {
                                                isStudyJoined = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (!isStudyAvailable) {
                                        Toast.makeText(mContext, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                    } else if (!isStudyJoined) {
                                        Toast.makeText(mContext, R.string.studyNotJoined, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(mContext, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}