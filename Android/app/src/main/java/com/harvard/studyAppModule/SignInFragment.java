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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.R;
import com.harvard.userModule.ForgotPasswordActivity;
import com.harvard.userModule.NewPasscodeSetupActivity;
import com.harvard.userModule.TermsPrivacyPolicyActivity;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.VerificationStepActivity;
import com.harvard.userModule.event.GetTermsAndConditionEvent;
import com.harvard.userModule.event.GetUserProfileEvent;
import com.harvard.userModule.event.LoginEvent;
import com.harvard.userModule.event.UpdateUserProfileEvent;
import com.harvard.userModule.model.TermsAndConditionData;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.userModule.webserviceModel.UpdateUserProfileData;
import com.harvard.userModule.webserviceModel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.harvard.BuildConfig.VERSION_NAME;


public class SignInFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
    private static final int UPDATE_USER_PROFILE = 100;
    private static final int USER_PROFILE_REQUEST = 101;
    private static final int PASSCODE_RESPONSE = 102;
    private AppCompatEditText mEmail;
    private AppCompatEditText mPassword;
    private AppCompatTextView mEmailLabel;
    private AppCompatTextView mPasswordLabel;
    private AppCompatTextView mSignInLabel;
    private AppCompatTextView mForgotPasswordLabel;
    private AppCompatTextView mNewUsrSignUp;
    private int LOGIN_REQUEST = 1;
    private Context mContext;
    private boolean mClicked;
    private String mUserID;
    private String mUserAuth;
    private String mRefreshToken;
    LoginData loginData;
    private AppCompatTextView mAgreeLabel;
    private int GET_TERMS_AND_CONDITION = 3;
    private TermsAndConditionData mTermsAndConditionData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_sign_in, container, false);
        mClicked = false;
        initializeXMLId(view);
        customTextViewAgree(mAgreeLabel);
        setFont();
        customTextView();
        bindEvents();
        mTermsAndConditionData=new TermsAndConditionData();
        mTermsAndConditionData.setPrivacy(getString(R.string.privacyurl));
        mTermsAndConditionData.setTerms(getString(R.string.termsurl));
        return view;
    }

    private void initializeXMLId(View view) {
        mEmailLabel = (AppCompatTextView) view.findViewById(R.id.email_label);
        mEmail = (AppCompatEditText) view.findViewById(R.id.edittxt_email);
        mPasswordLabel = (AppCompatTextView) view.findViewById(R.id.password_label);
        mPassword = (AppCompatEditText) view.findViewById(R.id.edittxt_password);
        mSignInLabel = (AppCompatTextView) view.findViewById(R.id.signInButton);
        mForgotPasswordLabel = (AppCompatTextView) view.findViewById(R.id.forgot_password);
        mNewUsrSignUp = (AppCompatTextView) view.findViewById(R.id.newUsrSignUp);
        mAgreeLabel = (AppCompatTextView) view.findViewById(R.id.agree_terms);
    }


    @SuppressWarnings("deprecation")
    private void customTextView() {
        mEmail.requestFocus();
        String html = mContext.getResources().getString(R.string.new_user) + " <font color=\"#007cba\">" + mContext.getResources().getString(R.string.sign_up) + "</font>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mNewUsrSignUp.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            mNewUsrSignUp.setText(Html.fromHtml(html));
        }
    }


    // set link for privacy and policy
    private void customTextViewAgree(AppCompatTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(mContext.getResources().getString(R.string.you_agree_this_app) + "\n");
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
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimaryBlack)), spanTxt.length() - " and".length(), spanTxt.length(), 0);

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

            mEmailLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mEmail.setTypeface(AppController.getTypeface(mContext, "regular"));
            mPasswordLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mPassword.setTypeface(AppController.getTypeface(mContext, "regular"));
            mSignInLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mForgotPasswordLabel.setTypeface(AppController.getTypeface(mContext, "regular"));
            mNewUsrSignUp.setTypeface(AppController.getTypeface(mContext, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void bindEvents() {
        mSignInLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClicked == false) {
                    mClicked = true;
                    callLoginWebService();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClicked = false;
                        }
                    }, 2000);
                }

            }
        });

        mForgotPasswordLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmail.setText("");
                mPassword.setText("");
                mEmail.requestFocus();
                Intent intent = new Intent(mContext, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        mNewUsrSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Go to SignUp ...", Toast.LENGTH_SHORT).show();
                ((StudyActivity) getContext()).loadsignup();
            }
        });

    }


    private void callLoginWebService() {
        if (mEmail.getText().toString().equalsIgnoreCase("") && mPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
        } else if (mEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
        } else if (!AppController.getHelperIsValidEmail(mEmail.getText().toString())) {
            Toast.makeText(mContext, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
        } else if (mPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(mContext, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
        } else {
            try {
                AppController.getHelperHideKeyboard((Activity) mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
            LoginEvent loginEvent = new LoginEvent();
            HashMap<String, String> params = new HashMap<>();
            params.put("emailId", mEmail.getText().toString());
            params.put("password", mPassword.getText().toString());
            params.put("appId", BuildConfig.APPLICATION_ID);

            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.LOGIN, LOGIN_REQUEST, mContext, LoginData.class, params, null, null, false, this);
            loginEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performLogin(loginEvent);
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == LOGIN_REQUEST) {
            //FilterActivity Screen json object clearing
            AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.json_object_filter), "");
            loginData = (LoginData) response;
            if (loginData != null) {
                mUserID = loginData.getUserId();
                mUserAuth = loginData.getAuth();
                mRefreshToken=loginData.getRefreshToken();
                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.refreshToken), loginData.getRefreshToken());
                new GetFCMRefreshToken().execute();
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == UPDATE_USER_PROFILE) {
            UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
            if (updateUserProfileData != null) {
                if (updateUserProfileData.getMessage().equalsIgnoreCase("success")) {
                    callUserProfileWebService();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == USER_PROFILE_REQUEST) {
            UserProfileData userProfileData = (UserProfileData) response;
            if (userProfileData != null) {
                if (userProfileData.getSettings().isPasscode()) {
                    AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.initialpasscodeset), "no");
                    if (loginData.isVerified()) {
                        AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.userid), "" + loginData.getUserId());
                        AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.auth), "" + loginData.getAuth());
                        AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.verified), "" + loginData.isVerified());
                        AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.email), "" + mEmail.getText().toString());
                        AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.refreshToken), loginData.getRefreshToken());
                        Intent intent = new Intent(mContext, NewPasscodeSetupActivity.class);
                        intent.putExtra("from", "signin");
                        startActivityForResult(intent, PASSCODE_RESPONSE);
                    } else {
                        Intent intent = new Intent(mContext, VerificationStepActivity.class);
                        intent.putExtra("from", "SignInFragment");
                        intent.putExtra("type", "Signin");
                        intent.putExtra("userid", loginData.getUserId());
                        intent.putExtra("auth", loginData.getAuth());
                        intent.putExtra("verified", loginData.isVerified());
                        intent.putExtra("email", mEmail.getText().toString());
                        intent.putExtra("password", mPassword.getText().toString());
                        startActivity(intent);
                    }
                } else {
                    login();
                }
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
            }
        } else if (GET_TERMS_AND_CONDITION == responseCode) {
            mTermsAndConditionData = (TermsAndConditionData) response;
        }
    }

    private void callUserProfileWebService() {
        HashMap<String, String> header = new HashMap<>();
        header.put("auth", mUserAuth);
        header.put("userId", mUserID);
        GetUserProfileEvent getUserProfileEvent = new GetUserProfileEvent();
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.GET_USER_PROFILE, USER_PROFILE_REQUEST, mContext, UserProfileData.class, null, header, null, false, this);
        getUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserProfile(getUserProfileEvent);
    }

    private void login() {
        if (loginData.getResetPassword()) {
            Intent intent = new Intent(mContext, ChangePasswordActivity.class);
            intent.putExtra("userid", loginData.getUserId());
            intent.putExtra("password", mPassword.getText().toString());
            intent.putExtra("auth", loginData.getAuth());
            intent.putExtra("verified", loginData.isVerified());
            intent.putExtra("email", mEmail.getText().toString());
            startActivity(intent);
        } else if (loginData.isVerified()) {
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.userid), "" + loginData.getUserId());
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.auth), "" + loginData.getAuth());
            AppController.getHelperSharedPreference().writePreference(getContext(), getString(R.string.verified), "" + loginData.isVerified());
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
            intent.putExtra("from", "SignInFragment");
            intent.putExtra("type", "Signin");
            intent.putExtra("userid", loginData.getUserId());
            intent.putExtra("auth", loginData.getAuth());
            intent.putExtra("verified", loginData.isVerified());
            intent.putExtra("email", mEmail.getText().toString());
            intent.putExtra("password", mPassword.getText().toString());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PASSCODE_RESPONSE) {
            login();
        }
    }
}
