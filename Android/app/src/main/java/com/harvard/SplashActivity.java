package com.harvard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.harvard.gatewayModule.GatewayActivity;
import com.harvard.offlineModule.auth.SyncAdapterManager;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.userModule.NewPasscodeSetupActivity;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Version.VersionChecker;

import io.fabric.sdk.android.services.common.CommonUtils;


public class SplashActivity extends AppCompatActivity implements VersionChecker.Upgrade {

    private static final int UPGRADE = 100;
    private static final int APP_UPDATES = 100;
    private static final int PASSCODE_RESPONSE = 101;
    String version = "", updatedversion = "";
    private static AlertDialog alertDialog;
    AppVersionData appVersionData;
    VersionChecker versionChecker;
    String newVersion = "";
    private boolean force = false;
    private static final int RESULT_CODE_UPGRADE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        version = currentVersion();
        if (CommonUtils.isRooted(SplashActivity.this)) {
            Toast.makeText(SplashActivity.this, getResources().getString(R.string.rooted_device), Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
        } else {
            // sync registration
            SyncAdapterManager.init(this);
            AppController.keystoreInitilize(SplashActivity.this);
            versionChecker = new VersionChecker(SplashActivity.this, SplashActivity.this);
            versionChecker.execute();
        }
        AppController.getHelperSharedPreference().writePreference(SplashActivity.this, getString(R.string.json_object_filter), "");
    }

    public void loadsplash() {
        new LongOperation().execute();
    }

    private void startmain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getResources().getString(R.string.userid), "").equalsIgnoreCase("") && AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getResources().getString(R.string.verified), "").equalsIgnoreCase("true")) {
                    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                        Intent intent = new Intent(SplashActivity.this, StudyActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, StandaloneActivity.class);
                        startActivity(intent);
                    }
                } else {
                    SharedPreferences settings = SharedPreferenceHelper.getPreferences(SplashActivity.this);
                    settings.edit().clear().apply();
                    // delete passcode from keystore
                    String pass = AppController.refreshKeys("passcode");
                    if (pass != null)
                        AppController.deleteKey("passcode_" + pass);
                    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                        Intent intent = new Intent(SplashActivity.this, GatewayActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, StandaloneActivity.class);
                        startActivity(intent);
                    }
                }
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_UPGRADE) {
            if (versionChecker.currentVersion() != null && versionChecker.currentVersion().equalsIgnoreCase(newVersion)) {
                if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                    Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                    intent.putExtra("from", "signin");
                    startActivityForResult(intent, PASSCODE_RESPONSE);
                } else {
                    loadsplash();
                }
            } else {
                if (force) {
                    Toast.makeText(SplashActivity.this, "Please update the app to continue using", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                        Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                        intent.putExtra("from", "signin");
                        startActivityForResult(intent, PASSCODE_RESPONSE);
                    } else {
                        loadsplash();
                    }
                }
            }
        } else if (requestCode == PASSCODE_RESPONSE) {
            loadsplash();
        }
    }

    private String currentVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), getResources().getString(R.string.usepasscode), "").equalsIgnoreCase("yes")) {
                while (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), "passcodeAnswered", "no").equalsIgnoreCase("no")) {
                    if (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), "passcodeAnswered", "no").equalsIgnoreCase("yes")) {
                        break;
                    }
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            startmain();
        }

        @Override
        protected void onPreExecute() {

        }

    }


    @Override
    public void isUpgrade(boolean b, String newVersion, final boolean force) {
        this.newVersion = newVersion;
        this.force = force;
        if (b) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this, R.style.MyAlertDialogStyle);
            alertDialogBuilder.setTitle("Upgrade");
            alertDialogBuilder.setMessage("Please upgrade the app to continue.").setCancelable(false)
                    .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)), RESULT_CODE_UPGRADE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (force) {
                                Toast.makeText(SplashActivity.this, "Please update the app to continue using", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                                    Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                                    intent.putExtra("from", "signin");
                                    startActivityForResult(intent, PASSCODE_RESPONSE);
                                } else {
                                    loadsplash();
                                }
                            }
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                intent.putExtra("from", "signin");
                startActivityForResult(intent, PASSCODE_RESPONSE);
            } else {
                loadsplash();
            }
        }
    }

}
