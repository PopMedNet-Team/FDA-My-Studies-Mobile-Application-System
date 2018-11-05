/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.studyAppModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hphc.mystudies.R;
import com.hphc.mystudies.notificationModule.NotificationModuleSubscriber;
import com.hphc.mystudies.offlineModule.model.OfflineData;
import com.hphc.mystudies.passcodeModule.PasscodeSetupActivity;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.userModule.NewPasscodeSetupActivity;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.event.GetUserProfileEvent;
import com.hphc.mystudies.userModule.event.LogoutEvent;
import com.hphc.mystudies.userModule.event.UpdateUserProfileEvent;
import com.hphc.mystudies.userModule.webserviceModel.LoginData;
import com.hphc.mystudies.userModule.webserviceModel.Settings;
import com.hphc.mystudies.userModule.webserviceModel.UpdateProfileRequestData;
import com.hphc.mystudies.userModule.webserviceModel.UpdateUserProfileData;
import com.hphc.mystudies.userModule.webserviceModel.UserProfileData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.SharedPreferenceHelper;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;

import static com.hphc.mystudies.R.id.signOutButton;

public class ProfileFragment extends Fragment implements ApiCall.OnAsyncRequestComplete, CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private AppCompatEditText mFirstName;
    private AppCompatEditText mLastName;
    private AppCompatEditText mEmail;
    private AppCompatTextView mPassword;
    private Switch mSwitchUsePasscode;
    private AppCompatTextView mUsePasscodeLabel;
    private Switch mSwitchTouch;
    private AppCompatTextView mTouchIdLabel;
    private Switch mSwitchRecvPushNotifctn;
    private AppCompatTextView mRecvPushNotifctnLabel;
    private Switch mSwitchRecvStdyRemindr;
    private AppCompatTextView mRecvStdyActRemLabel;
    private RelativeLayout mPickerReminderBtn;
    private AppCompatTextView mPickerReminderLabel;
    private AppCompatTextView mReminderLabel;
    private AppCompatTextView mSignOutButton;
    private AppCompatTextView mDeleteMyAccount;
    private AppCompatTextView mFirstNameLabel;
    private AppCompatTextView mLastNameLabel;
    private AppCompatTextView mEmailLabel;
    private AppCompatTextView mPasswordLabel;
    private AppCompatTextView mHrLine12;
    private AppCompatTextView mPasscode;
    private static final int USER_PROFILE_REQUEST = 6;
    private static final int UPDATE_USER_PROFILE_REQUEST = 7;
    private static final int LOGOUT_REPSONSECODE = 100;
    private static final int DELETE_ACCOUNT_REPSONSECODE = 101;
    private static final int PASSCODE_REPSONSE = 102;
    private static final int NEW_PASSCODE_REPSONSE = 103;
    private static final int CHANGE_PASSCODE_REPSONSE = 104;
    private static final int PASSCODE_CHANGE_REPSONSE = 105;
    private UserProfileData mUserProfileData = null;
    private UpdateProfileRequestData mUpdateProfileRequestData = null;
    private static final int DELETE_ACCOUNT = 5;
    private static final String USER_ID_CONSTANT = "userId";
    private static final String REGULAR_CONSTANT = "regular";
    private static final String PROFILE_CONSTANT = "profile";
    private static final String PASSCODE_CONSTANT = "passcode";
    private static final String PASSCOD_CONSTANT = "passcode_";
    private static final String SWITCH_RECIEVE_PUSH_NOTIFICATION = "mSwitchRecvPushNotifctn";
    private int mDeleteIndexNumberDB;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj();
        initializeXMLId(view);
        setFont();
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        callUserProfileWebService();
        bindEvents();
        return view;
    }

    private void callUserProfileWebService() {
        HashMap<String, String> header = new HashMap<>();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.auth), ""));
        header.put(USER_ID_CONSTANT, AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.userid), ""));
        GetUserProfileEvent getUserProfileEvent = new GetUserProfileEvent();
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.GET_USER_PROFILE, USER_PROFILE_REQUEST, mContext, UserProfileData.class, null, header, null, false, this);
        getUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserProfile(getUserProfileEvent);
    }

    private void initializeXMLId(View view) {
        mFirstNameLabel = (AppCompatTextView) view.findViewById(R.id.first_name_label);
        mFirstName = (AppCompatEditText) view.findViewById(R.id.edittxt_first_name);
        mLastNameLabel = (AppCompatTextView) view.findViewById(R.id.last_name_label);
        mLastName = (AppCompatEditText) view.findViewById(R.id.edittxt_last_name);
        mEmailLabel = (AppCompatTextView) view.findViewById(R.id.email_label);
        mEmail = (AppCompatEditText) view.findViewById(R.id.edittxt_email);
        mPasswordLabel = (AppCompatTextView) view.findViewById(R.id.password_label);
        mPassword = (AppCompatTextView) view.findViewById(R.id.edittxt_password);
        mSwitchUsePasscode = (Switch) view.findViewById(R.id.switch_use_passcode);
        mUsePasscodeLabel = (AppCompatTextView) view.findViewById(R.id.use_passcode_label);
        mSwitchTouch = (Switch) view.findViewById(R.id.switch_touch);
        mTouchIdLabel = (AppCompatTextView) view.findViewById(R.id.touch_id_label);
        mSwitchRecvPushNotifctn = (Switch) view.findViewById(R.id.switch_recv_push_notifctn);
        mRecvPushNotifctnLabel = (AppCompatTextView) view.findViewById(R.id.recv_push_notifctn_label);
        mSwitchRecvStdyRemindr = (Switch) view.findViewById(R.id.switch_recv_stdy_actrem);
        mRecvStdyActRemLabel = (AppCompatTextView) view.findViewById(R.id.recv_stdy_actrem_label);
        mPickerReminderBtn = (RelativeLayout) view.findViewById(R.id.rel_picker_reminder);
        mPickerReminderLabel = (AppCompatTextView) view.findViewById(R.id.picker_reminder);
        mReminderLabel = (AppCompatTextView) view.findViewById(R.id.reminder_label);
        mSignOutButton = (AppCompatTextView) view.findViewById(signOutButton);
        mDeleteMyAccount = (AppCompatTextView) view.findViewById(R.id.deleteMyAccount);
        mHrLine12 = (AppCompatTextView) view.findViewById(R.id.hrline12);
        mPasscode = (AppCompatTextView) view.findViewById(R.id.edittxt_passcode);

        disableEditText();
    }


    private void setFont() {
        try {
            mFirstNameLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mFirstName.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mLastNameLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mLastName.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mEmailLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mEmail.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mPasswordLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mPassword.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mUsePasscodeLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mTouchIdLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mRecvPushNotifctnLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mRecvStdyActRemLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mReminderLabel.setTypeface(AppController.getTypeface(mContext, REGULAR_CONSTANT));
            mSignOutButton.setTypeface(AppController.getTypeface(mContext, "bold"));
            mDeleteMyAccount.setTypeface(AppController.getTypeface(mContext, "bold"));

        } catch (Exception e) {
        }
    }

    private void bindEvents() {

        mPickerReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitchRecvStdyRemindr.isChecked()) {
                    CustomDialogClass cdd = new CustomDialogClass(((Activity) mContext), ProfileFragment.this);
                    cdd.show();
                } else {
                    Toast.makeText(mContext, R.string.remainder_settings, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChangePasswordActivity.class);
                intent.putExtra("from", "ProfileFragment");

                intent.putExtra("userid", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.userid), ""));
                intent.putExtra("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.auth), ""));
                intent.putExtra("verified", AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.verified), ""));
                intent.putExtra("email", AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.email), ""));
                startActivity(intent);
            }
        });

        mPasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, PasscodeSetupActivity.class);
                intent.putExtra("from", PROFILE_CONSTANT);
                startActivityForResult(intent, PASSCODE_CHANGE_REPSONSE);
            }
        });

        mSwitchRecvPushNotifctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateUserProfileWebService(true, SWITCH_RECIEVE_PUSH_NOTIFICATION);
            }
        });

        mSwitchRecvStdyRemindr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateUserProfileWebService(true, "mSwitchRecvStdyRemindr");
            }
        });

        if (AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.usepasscode), "").equalsIgnoreCase("yes")) {
            mSwitchUsePasscode.setOnCheckedChangeListener(null);
            mSwitchUsePasscode.setChecked(true);
            mSwitchUsePasscode.setOnCheckedChangeListener(this);
            mPasscode.setEnabled(true);
            mPasscode.setTextColor(getResources().getColor(R.color.colorSecondaryStatBar));

        } else {
            mSwitchUsePasscode.setOnCheckedChangeListener(null);
            mSwitchUsePasscode.setChecked(false);
            mSwitchUsePasscode.setOnCheckedChangeListener(this);
            mPasscode.setEnabled(false);
            mPasscode.setTextColor(Color.LTGRAY);
        }
        mSwitchUsePasscode.setEnabled(true);
        mSwitchUsePasscode.setOnCheckedChangeListener(this);

        mSwitchRecvPushNotifctn.setEnabled(true);
        mSwitchRecvStdyRemindr.setEnabled(true);

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSignOutButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.sign_out)))
                    logout();
                else if (mSignOutButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.update))) {
                    callUpdateUserProfileWebService(false, SWITCH_RECIEVE_PUSH_NOTIFICATION);
                }
            }
        });

        mDeleteMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DeleteAccountActivity.class);
                startActivityForResult(intent, DELETE_ACCOUNT);
            }
        });
    }

    private void logout() {
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        LogoutEvent logoutEvent = new LogoutEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("reason", "user_action");
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.auth), ""));
        header.put(USER_ID_CONSTANT, AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.userid), ""));
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("delete", URLs.LOGOUT, LOGOUT_REPSONSECODE, mContext, LoginData.class, params, header, null, false, this);
        logoutEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performLogout(logoutEvent);
    }

    public void enableEditText() {
        mFirstName.setEnabled(true);
        mLastName.setEnabled(true);
        mSignOutButton.setText(getResources().getString(R.string.update));
        mHrLine12.setVisibility(View.VISIBLE);
        mDeleteMyAccount.setVisibility(View.VISIBLE);

        mSwitchUsePasscode.setEnabled(true);
        mSwitchTouch.setEnabled(true);
        mSwitchRecvPushNotifctn.setEnabled(true);
        mSwitchRecvStdyRemindr.setEnabled(true);
        mPickerReminderBtn.setEnabled(true);
        mSignOutButton.setVisibility(View.VISIBLE);
    }

    public void disableEditText() {
        mFirstName.setEnabled(false);
        mLastName.setEnabled(false);
        mSignOutButton.setVisibility(View.GONE);
        mSignOutButton.setText(getResources().getString(R.string.sign_out));
        mHrLine12.setVisibility(View.VISIBLE);
        mHrLine12.setVisibility(View.GONE);
        mDeleteMyAccount.setVisibility(View.VISIBLE);

        mSwitchUsePasscode.setEnabled(false);
        mSwitchTouch.setEnabled(false);
        mSwitchRecvPushNotifctn.setEnabled(false);
        mSwitchRecvStdyRemindr.setEnabled(false);
        mPickerReminderBtn.setEnabled(false);
        ((StudyActivity) mContext).disableEditTextFromFragment();
        if (mUserProfileData != null) {
            updateUI();
        }
        mSignOutButton.setVisibility(View.GONE);
    }

    public void updatePickerTime(String val) {
        mPickerReminderLabel.setText(val);

        if (!val.equalsIgnoreCase("")) {
            String hours = val.split(":")[0];
            String minutes = val.split(":")[1];
            if (("" + hours).length() > 1 && ("" + minutes).length() > 1) {
                mPickerReminderLabel.setText("" + hours + ":" + minutes);
            } else if (("" + hours).length() > 1) {
                mPickerReminderLabel.setText("" + hours + ":0" + minutes);
            } else if (("" + minutes).length() > 1) {
                mPickerReminderLabel.setText("0" + hours + ":" + minutes);
            } else {
                mPickerReminderLabel.setText("0" + hours + ":0" + minutes);
            }
        }
    }

    @Override
    public void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        disableEditText();
        super.onDestroy();
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (USER_PROFILE_REQUEST == responseCode) {
            mUserProfileData = (UserProfileData) response;
            if (mUserProfileData != null) {
                updateUI();
            } else {
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
            try {
                // if already having data then delete it (avoid duplication)
                dbServiceSubscriber.deleteUserProfileDataDuplicateRow();
                // save mUserProfileData to db
                dbServiceSubscriber.saveUserProfileData(mUserProfileData);
            } catch (Exception e) {
            }

        } else if (UPDATE_USER_PROFILE_REQUEST == responseCode) {
            Toast.makeText(mContext, getResources().getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
            try {
                Realm realm = AppController.getRealmobj();
                realm.beginTransaction();
                mUserProfileData.getSettings().setLocalNotifications(mUpdateProfileRequestData.getSettings().isLocalNotifications());
                mUserProfileData.getSettings().setPasscode(mUpdateProfileRequestData.getSettings().isPasscode());
                mUserProfileData.getSettings().setRemindersTime(mUpdateProfileRequestData.getSettings().getRemindersTime());
                mUserProfileData.getSettings().setRemoteNotifications(mUpdateProfileRequestData.getSettings().isRemoteNotifications());
                mUserProfileData.getSettings().setTouchId(mUpdateProfileRequestData.getSettings().isTouchId());
                realm.commitTransaction();
                dbServiceSubscriber.closeRealmObj(realm);
                // save mUserProfileData to db
                dbServiceSubscriber.saveUserProfileData(mUserProfileData);
                ///delete offline row sync
                dbServiceSubscriber.deleteOfflineDataRow(mDeleteIndexNumberDB);
            } catch (Exception e) {
            }

        } else if (responseCode == LOGOUT_REPSONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
                Toast.makeText(mContext, loginData.getMessage(), Toast.LENGTH_SHORT).show();
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                notificationModuleSubscriber.cancelNotificationTurnOffNotification(mContext);
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(mContext);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys(PASSCODE_CONSTANT);
                AppController.deleteKey(PASSCOD_CONSTANT + pass);
                ((StudyActivity) mContext).loadstudylist();
            } else {
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == DELETE_ACCOUNT_REPSONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
                Toast.makeText(mContext, getResources().getString(R.string.account_deletion), Toast.LENGTH_SHORT).show();
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(mContext);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys(PASSCODE_CONSTANT);
                if (pass != null)
                    AppController.deleteKey(PASSCOD_CONSTANT + pass);
                ((StudyActivity) mContext).loadstudylist();
            } else {
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void updateUI() {
        try {
            mFirstName.setText(mUserProfileData.getProfile().getFirstName());
            mLastName.setText(mUserProfileData.getProfile().getLastName());
            mEmail.setText(mUserProfileData.getProfile().getEmailId());

            mSwitchRecvPushNotifctn.setEnabled(true);
            mSwitchRecvPushNotifctn.setChecked(mUserProfileData.getSettings().isRemoteNotifications());

            mSwitchRecvStdyRemindr.setEnabled(true);
            mSwitchRecvStdyRemindr.setChecked(mUserProfileData.getSettings().isLocalNotifications());
        } catch (Exception e) {
        }

    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(mContext, errormsg);
        } else if (responseCode == USER_PROFILE_REQUEST) {
            mUserProfileData = dbServiceSubscriber.getUserProfileData(mRealm);
            if (mUserProfileData != null) {
                updateUI();
            } else {
                Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
            }
        } else if (UPDATE_USER_PROFILE_REQUEST == responseCode) {
            try {

                if (mUserProfileData != null) {
                    Realm realm = AppController.getRealmobj();
                    realm.beginTransaction();
                    mUserProfileData.getSettings().setLocalNotifications(mUpdateProfileRequestData.getSettings().isLocalNotifications());
                    mUserProfileData.getSettings().setPasscode(mUpdateProfileRequestData.getSettings().isPasscode());
                    mUserProfileData.getSettings().setRemindersTime(mUpdateProfileRequestData.getSettings().getRemindersTime());
                    mUserProfileData.getSettings().setRemoteNotifications(mUpdateProfileRequestData.getSettings().isRemoteNotifications());
                    mUserProfileData.getSettings().setTouchId(mUpdateProfileRequestData.getSettings().isTouchId());
                    realm.commitTransaction();
                    dbServiceSubscriber.closeRealmObj(realm);
                    // save mUserProfileData to db
                    dbServiceSubscriber.saveUserProfileData(mUserProfileData);
                }

            } catch (Exception e) {
            }

        }

    }

    private void callUpdateUserProfileWebService(boolean isNotificationToggle, String notificationType) {
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();

        if((isNotificationToggle && notificationType.equalsIgnoreCase(SWITCH_RECIEVE_PUSH_NOTIFICATION) && mSwitchRecvStdyRemindr.isChecked() && !mSwitchRecvPushNotifctn.isChecked()) || (isNotificationToggle && notificationType.equalsIgnoreCase("mSwitchRecvStdyRemindr") && !mSwitchRecvStdyRemindr.isChecked() && mSwitchRecvPushNotifctn.isChecked()))
        {
            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
            notificationModuleSubscriber.generateNotificationTurnOffNotification(Calendar.getInstance().getTime(), mContext);
        }
        else if(isNotificationToggle && mSwitchRecvStdyRemindr.isChecked() && mSwitchRecvPushNotifctn.isChecked())
        {
            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
            notificationModuleSubscriber.cancelNotificationTurnOffNotification(mContext);
        }

        Gson gson = new Gson();
        mUpdateProfileRequestData = new UpdateProfileRequestData();
        Settings settings = new Settings();
        settings.setLocalNotifications(mSwitchRecvStdyRemindr.isChecked());
        settings.setPasscode(mSwitchUsePasscode.isChecked());
        settings.setRemoteNotifications(mSwitchRecvPushNotifctn.isChecked());
        settings.setTouchId(mSwitchTouch.isChecked());
        mPickerReminderLabel.getText().toString();
        int time = (Integer.parseInt(mPickerReminderLabel.getText().toString().split(":")[0]) * 60) + (Integer.parseInt(mPickerReminderLabel.getText().toString().split(":")[1]));
        settings.setRemindersTime(Integer.toString(time));

        mUpdateProfileRequestData.setSettings(settings);
        String json = gson.toJson(mUpdateProfileRequestData);
        try {
            JSONObject obj = new JSONObject(json);

            HashMap<String, String> header = new HashMap<>();
            header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.auth), ""));
            header.put(USER_ID_CONSTANT, AppController.getHelperSharedPreference().readPreference(mContext, getString(R.string.userid), ""));

            /////////// offline data storing
            try {
                int number = dbServiceSubscriber.getUniqueID(mRealm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }

                String userProfileId = AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.userid), "");
                OfflineData offlineData = dbServiceSubscriber.getUserIdOfflineData(userProfileId, mRealm);
                if (offlineData != null) {
                    number = offlineData.getNumber();
                }
                mDeleteIndexNumberDB = number;
                AppController.pendingService(number, "post_object", URLs.UPDATE_USER_PROFILE, "", obj.toString(), "registration", userProfileId, "", "");
            } catch (Exception e) {
            }
            //////////

            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_USER_PROFILE, UPDATE_USER_PROFILE_REQUEST, mContext, UpdateUserProfileData.class, null, header, obj, false, ProfileFragment.this);
            updateUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_ACCOUNT) {
            if (resultCode == ((Activity) mContext).RESULT_OK) {
                ((StudyActivity) mContext).loadstudylist();
            }
        } else if (requestCode == PASSCODE_REPSONSE) {
            if (resultCode == ((Activity) mContext).RESULT_OK) {
                callUpdateUserProfileWebService(false, SWITCH_RECIEVE_PUSH_NOTIFICATION);
                // delete passcode from keystore
                String pass = AppController.refreshKeys(PASSCODE_CONSTANT);
                if (pass != null)
                    AppController.deleteKey(PASSCOD_CONSTANT + pass);

                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.usepasscode), "no");
                mPasscode.setEnabled(false);
                mPasscode.setTextColor(Color.LTGRAY);
            } else {
                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.usepasscode), "yes");
                mPasscode.setEnabled(true);
                mPasscode.setTextColor(getResources().getColor(R.color.colorSecondaryStatBar));
                mSwitchUsePasscode.setOnCheckedChangeListener(null);
                mSwitchUsePasscode.setChecked(true);
                mSwitchUsePasscode.setOnCheckedChangeListener(this);
            }
        } else if (requestCode == NEW_PASSCODE_REPSONSE) {
            if (resultCode != ((Activity) mContext).RESULT_OK) {
                mSwitchUsePasscode.setOnCheckedChangeListener(null);
                mSwitchUsePasscode.setChecked(false);
                mSwitchUsePasscode.setOnCheckedChangeListener(this);
                // delete passcode from keystore
                String pass = AppController.refreshKeys(PASSCODE_CONSTANT);
                if (pass != null)
                    AppController.deleteKey(PASSCOD_CONSTANT + pass);
                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.usepasscode), "no");
                mPasscode.setEnabled(false);
                mPasscode.setTextColor(Color.LTGRAY);
            } else {
                callUpdateUserProfileWebService(false, SWITCH_RECIEVE_PUSH_NOTIFICATION);
                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.usepasscode), "yes");
                mPasscode.setEnabled(true);
                mPasscode.setTextColor(getResources().getColor(R.color.colorSecondaryStatBar));
                mSwitchUsePasscode.setOnCheckedChangeListener(null);
                mSwitchUsePasscode.setChecked(true);
                mSwitchUsePasscode.setOnCheckedChangeListener(this);
            }
        } else if (requestCode == CHANGE_PASSCODE_REPSONSE) {
            if (resultCode == ((Activity) mContext).RESULT_OK) {
                Toast.makeText(mContext, "Passcode updated", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PASSCODE_CHANGE_REPSONSE && resultCode == ((Activity) mContext).RESULT_OK) {
            Intent intent = new Intent(((Activity) mContext), NewPasscodeSetupActivity.class);
            intent.putExtra("from", "profile_change");
            startActivityForResult(intent, CHANGE_PASSCODE_REPSONSE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Intent intent = new Intent(((Activity) mContext), NewPasscodeSetupActivity.class);
            intent.putExtra("from", PROFILE_CONSTANT);
            startActivityForResult(intent, NEW_PASSCODE_REPSONSE);
        } else {
            Intent intent = new Intent(mContext, PasscodeSetupActivity.class);
            intent.putExtra("from", PROFILE_CONSTANT);
            startActivityForResult(intent, PASSCODE_REPSONSE);
        }
    }
}
