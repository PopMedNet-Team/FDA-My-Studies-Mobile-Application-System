package com.harvard.studyAppModule;

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

import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.studyModel.Notification;
import com.harvard.studyAppModule.studyModel.Study;
import com.harvard.studyAppModule.studyModel.StudyList;
import com.harvard.utils.AppController;

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
                            if (mItems.get(holder.getAdapterPosition()).getSubtype().equalsIgnoreCase("Activity") || mItems.get(holder.getAdapterPosition()).getSubtype().equalsIgnoreCase("Resource")) {
                                Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                                if (mStudy != null) {
                                    RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                    studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, mRealm);
                                    boolean isStudyAvailable = false;
                                    boolean isStudyJoined = false;
                                    for (int i = 0; i < studyListArrayList.size(); i++) {
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