package com.harvard.studyAppModule;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.R;
import com.harvard.userModule.TermsPrivacyPolicyActivity;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.VerificationStepActivity;
import com.harvard.userModule.event.GetTermsAndConditionEvent;
import com.harvard.userModule.event.RegisterUserEvent;
import com.harvard.userModule.event.UpdateUserProfileEvent;
import com.harvard.userModule.model.TermsAndConditionData;
import com.harvard.userModule.webserviceModel.RegistrationData;
import com.harvard.userModule.webserviceModel.UpdateUserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.SetDialogHelper;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.harvard.BuildConfig.VERSION_NAME;


public class SignupFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
    private static final int GET_TERMS_AND_CONDITION = 3;
    private static final int UPDATE_USER_PROFILE = 100;
    private AppCompatEditText mFirstName;
    private AppCompatEditText mLastName;
    private AppCompatEditText mEmail;
    private AppCompatEditText mPassword;
    private AppCompatEditText mConfirmPassword;
    private Switch mSwitch;
    private AppCompatTextView mFirstNameLabel;
    private AppCompatTextView mLastNameLabel;
    private AppCompatTextView mEmailLabel;
    private AppCompatTextView mPasswordLabel;
    private AppCompatTextView mConfirmPasswordLabel;
    private AppCompatTextView mTouchIdLabel;
    private AppCompatTextView mAgreeLabel;
    private AppCompatCheckBox mAgree;
    private AppCompatTextView mSubmitBtn;
    private int REGISTRATION_REQUEST = 2;
    private Context mContext;
    private boolean mClicked;

    private TermsAndConditionData mTermsAndConditionData;
    private String mUserAuth;
    private String mUserID;
    RegistrationData mRegistrationData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_signup, container, false);
        mClicked = false;
        initializeXMLId(view);
        customTextView(mAgreeLabel);
        setFont();
        bindEvents();
        mTermsAndConditionData=new TermsAndConditionData();
        mTermsAndConditionData.setPrivacy(getString(R.string.privacyurl));
        mTermsAndConditionData.setTerms(getString(R.string.termsurl));
        return view;
    }

    private void initializeXMLId(View view) {
        mFirstNameLabel = (AppCompatTextView) view.findViewById(R.id.first_name_label);
        mFirstName = (AppCompatEditText) view.findViewById(R.id.edittxt_first_name);
        mLastNameLabel = (AppCompatTextView) view.findViewById(R.id.last_name_label);
        mLastName = (AppCompatEditText) view.findViewById(R.id.edittxt_last_name);
        mEmailLabel = (AppCompatTextView) view.findViewById(R.id.email_label);
        mEmail = (AppCompatEditText) view.findViewById(R.id.edittxt_email);
        mPasswordLabel = (AppCompatTextView) view.findViewById(R.id.password_label);
        mPassword = (AppCompatEditText) view.findViewById(R.id.edittxt_password);
        mConfirmPasswordLabel = (AppCompatTextView) view.findViewById(R.id.confirm_password_label);
        mConfirmPassword = (AppCompatEditText) view.findViewById(R.id.edittxt_confirm_password);
        mTouchIdLabel = (AppCompatTextView) view.findViewById(R.id.touch_id_label);
        mAgreeLabel = (AppCompatTextView) view.findViewById(R.id.agree_label);
        mSwitch = (Switch) view.findViewById(R.id.switch_touch);
        mAgree = (AppCompatCheckBox) view.findViewById(R.id.agreeButton);
        mSubmitBtn = (AppCompatTextView) view.findViewById(R.id.submitButton);
    }

    // set link for privacy and policy
    private void customTextView(AppCompatTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(mContext.getResources().getString(R.string.i_agree) + " ");
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimaryBlack)), 0, spanTxt.length(), 0);
        spanTxt.append(mContext.getResources().getString(R.string.terms2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (mTermsAndConditionData != null) {
                    Intent termsIntent = new Intent(mContext, TermsPrivacyPolicyActivity.class);
                    termsIntent.putExtra("title", getResources().getString(R.string.terms));
                    termsIntent.putExtra("url", mTermsAndConditionData.getTerms());
                    startActivity(termsIntent);
                }
            }
        }, spanTxt.length() - mContext.getResources().getString(R.string.terms2).length(), spanTxt.length(), 0);

        spanTxt.append(" " + mContext.getResources().getString(R.string.and));
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimaryBlack)), 20, spanTxt.length(), 0);

        spanTxt.append(" " + mContext.getResources().getString(R.string.privacy_policy2));
        String temp = " " + mContext.getResources().getString(R.string.privacy_policy2);
        spanTxt.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (mTermsAndConditionData != null) {
                    Intent termsIntent = new Intent(mContext, TermsPrivacyPolicyActivity.class);
                    termsIntent.putExtra("title", getResources().getString(R.string.privacy_policy));
                    termsIntent.putExtra("url", mTermsAndConditionData.getPrivacy());
                    startActivity(termsIntent);
                }
            }
        }, spanTxt.length() - temp.length(), spanTxt.length(), 0);

        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);

        mEmail.requestFocus();
    }

    private void setFont() {
        try {
            mFirstNameLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mFirstName.setTypeface(AppController.getTypeface(mContext, "regular"));
            mLastNameLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mLastName.setTypeface(AppController.getTypeface(mContext, "regular"));
            mEmailLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mEmail.setTypeface(AppController.getTypeface(mContext, "regular"));
            mPasswordLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mPassword.setTypeface(AppController.getTypeface(mContext, "regular"));
            mConfirmPasswordLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mConfirmPassword.setTypeface(AppController.getTypeface(mContext, "regular"));
            mTouchIdLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mAgreeLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mAgree.setTypeface(AppController.getTypeface(mContext, "regular"));
            mSubmitBtn.setTypeface(AppController.getTypeface(mContext, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClicked == false) {
                    mClicked = true;
                    callRegisterUserWebService();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClicked = false;
                        }
                    }, 2000);
                }

            }
        });

    }

    private void callRegisterUserWebService() {
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~]).{8,64}$";
        if (mPassword.getText().toString().equalsIgnoreCase("") && mEmail.getText().toString().equalsIgnoreCase("") && mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
        } else if (mEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
        } else if (!AppController.getHelperIsValidEmail(mEmail.getText().toString())) {
            Toast.makeText(mContext, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
        } else if (mPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
        } else if (!mPassword.getText().toString().matches(PASSWORD_PATTERN)) {
            SetDialogHelper.setNeutralDialog(mContext, getResources().getString(R.string.password_validation), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
        } else if (checkPasswordContainsEmailID(mEmail.getText().toString(), mPassword.getText().toString())) {
            Toast.makeText(mContext, getResources().getString(R.string.password_contain_email), Toast.LENGTH_SHORT).show();
        } else if (mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.confirm_password_empty), Toast.LENGTH_SHORT).show();
        } else if (!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            Toast.makeText(mContext, getResources().getString(R.string.password_mismatch_error), Toast.LENGTH_SHORT).show();
        } else if (!mAgree.isChecked()) {
            Toast.makeText(mContext, getResources().getString(R.string.terms_and_condition_validation), Toast.LENGTH_SHORT).show();
        } else {
            try {
                AppController.getHelperHideKeyboard((Activity) mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppController.getHelperProgressDialog().showProgress(getContext(), "", "", false);
            RegisterUserEvent registerUserEvent = new RegisterUserEvent();
            HashMap<String, String> params = new HashMap<>();
            params.put("emailId", mEmail.getText().toString());
            params.put("password", mPassword.getText().toString());
            params.put("appId", BuildConfig.APPLICATION_ID);
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.REGISTER_USER, REGISTRATION_REQUEST, getContext(), RegistrationData.class, params, null, null, false, this);
            registerUserEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performRegistration(registerUserEvent);
        }
    }

    private boolean checkPasswordContainsEmailID(String email, String password) {
        if (password.contains(email)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == REGISTRATION_REQUEST) {
            mRegistrationData = (RegistrationData) response;
            if (mRegistrationData != null) {
                mUserID = mRegistrationData.getUserId();
                mUserAuth = mRegistrationData.getAuth();
                new GetFCMRefreshToken().execute();
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == GET_TERMS_AND_CONDITION) {
            mTermsAndConditionData = (TermsAndConditionData) response;
        } else if (responseCode == UPDATE_USER_PROFILE) {
            UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
            if (updateUserProfileData != null) {
                if (updateUserProfileData.getMessage().equalsIgnoreCase("success")) {
                    signup(mRegistrationData);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signup(RegistrationData registrationData) {
        if (registrationData != null && registrationData.isVerified()) {
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.userid), "" + registrationData.getUserId());
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.auth), "" + registrationData.getAuth());
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.verified), "" + registrationData.isVerified());
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.refreshToken), "" + registrationData.getRefreshToken());
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.email), "" + mEmail.getText().toString());

            if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                ((StudyActivity) mContext).loadstudylist();
            } else {
                Intent intent = new Intent(mContext, StandaloneActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                ((Activity)mContext).finish();
            }
        } else {
            Intent intent = new Intent(mContext, VerificationStepActivity.class);
            intent.putExtra("userid", registrationData.getUserId());
            intent.putExtra("from", "SignupFragment");
            intent.putExtra("type", "Signup");
            intent.putExtra("auth", registrationData.getAuth());
            intent.putExtra("verified", registrationData.isVerified());
            intent.putExtra("email", mEmail.getText().toString());
            startActivity(intent);
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == UPDATE_USER_PROFILE) {
            if (statusCode.equalsIgnoreCase("401")) {
                Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
                AppController.getHelperSessionExpired(mContext, errormsg);
            } else {
                Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
        }
    }


    private void callUpdateProfileWebService(String deviceToken) {
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("auth", mUserAuth);
        params.put("userId", mUserID);

        JSONObject jsonObjBody = new JSONObject();
        JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("os", "android");
            infoJson.put("appVersion", VERSION_NAME);
            infoJson.put("deviceToken", deviceToken);

            jsonObjBody.put("info", infoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject settingJson = new JSONObject();
        try {
            settingJson.put("passcode", true);
            settingJson.put("remoteNotifications", true);
            settingJson.put("localNotifications", true);
            jsonObjBody.put("settings", settingJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_USER_PROFILE, UPDATE_USER_PROFILE, mContext, UpdateUserProfileData.class, null, params, jsonObjBody, false, this);
        updateUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
    }

    private class GetFCMRefreshToken extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = "";
            if (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) {
                boolean regIdStatus = false;
                while (!regIdStatus) {
                    token = AppController.getHelperSharedPreference().readPreference(mContext, "deviceToken", "");
                    if (!token.equalsIgnoreCase(""))
                        regIdStatus = true;
                }
            } else {
                AppController.getHelperSharedPreference().writePreference(mContext, "deviceToken", FirebaseInstanceId.getInstance().getToken());
                token = AppController.getHelperSharedPreference().readPreference(mContext, "deviceToken", "");
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            callUpdateProfileWebService(token);
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        }
    }

}