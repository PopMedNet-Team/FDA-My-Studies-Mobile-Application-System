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

package com.harvard.fda.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.SplashActivity;
import com.harvard.fda.notificationModule.NotificationModuleSubscriber;
import com.harvard.fda.offlineModule.model.OfflineData;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.StandaloneActivity;
import com.harvard.fda.studyAppModule.StudyActivity;
import com.harvard.fda.utils.realm.RealmEncryptionHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by Timson on 5/5/2015.
 */
public class AppController extends Application {


    private static SharedPreferenceHelper sSharedPreferenceHelper;
    private static JsonFormatHelper sJsonFormatHelper;
    private static SetDialogHelper sSetDialogHelper;
    private static ProgressDialogHelper sProgressDialogHelper;
    private static RealmConfiguration config;
    private static KeyStore mKeyStore;
    private static final String TAG = "FDAKeystore";
    private static String mKeystoreValue = null;
    private static byte[] mIvBytes = null;

    public static SharedPreferenceHelper getHelperSharedPreference() {
        if (sSharedPreferenceHelper == null)
            sSharedPreferenceHelper = new SharedPreferenceHelper();
        return sSharedPreferenceHelper;
    }

    public static JsonFormatHelper getHelperJsonFormat() {
        if (sJsonFormatHelper == null)
            sJsonFormatHelper = new JsonFormatHelper();
        return sJsonFormatHelper;
    }

    public static ProgressDialogHelper getHelperProgressDialog() {
        if (sProgressDialogHelper == null)
            sProgressDialogHelper = new ProgressDialogHelper();
        return sProgressDialogHelper;
    }

    public static SetDialogHelper getHelperSetDialog() {
        if (sSetDialogHelper == null)
            sSetDialogHelper = new SetDialogHelper();
        return sSetDialogHelper;
    }

    public static boolean getHelperIsValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static void getHelperSessionExpired(Context context, String msg) {
        SharedPreferences settings = SharedPreferenceHelper.getPreferences(context);
        settings.edit().clear().apply();
// delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        if (pass != null)
            AppController.deleteKey("passcode_" + pass);
        DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
        Realm realm = getRealmobj(context);
        dbServiceSubscriber.deleteDb(context);
        try {
            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, realm);
            notificationModuleSubscriber.cancleActivityLocalNotification(context);
            notificationModuleSubscriber.cancleResourcesLocalNotification(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.cancelNotificationTurnOffNotification(context);
        dbServiceSubscriber.closeRealmObj(realm);

        // clear notifications from notification tray
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();

        if (AppConfig.AppType.equalsIgnoreCase(context.getString(R.string.app_gateway))) {
            Intent intent = new Intent(context, StudyActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            context.startActivity(mainIntent);
            ((Activity) context).finish();
        } else {
            Intent intent = new Intent(context, StandaloneActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            context.startActivity(mainIntent);
            ((Activity) context).finish();
        }
    }

    public static void getHelperHideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }


    public static Typeface getTypeface(Context context, String whichType) {
        // whichTyp ====> bold/light/medium/regular/thin
        Typeface retTypeface = null;
        try {
            if (whichType.equalsIgnoreCase("bold")) {
                retTypeface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Roboto-Bold.ttf");
            } else if (whichType.equalsIgnoreCase("light")) {
                retTypeface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Roboto-Light.ttf");
            } else if (whichType.equalsIgnoreCase("medium")) {
                retTypeface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Roboto-Medium.ttf");
            } else if (whichType.equalsIgnoreCase("regular")) {
                retTypeface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Roboto-Regular.ttf");
            } else if (whichType.equalsIgnoreCase("thin")) {
                retTypeface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Roboto-Thin.ttf");
            } else {
                return retTypeface;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retTypeface;
    }

    public static Realm getRealmobj(Context context) {
        if (config == null) {
            RealmEncryptionHelper realmEncryptionHelper = RealmEncryptionHelper.initHelper(context, context.getString(R.string.app_name));
            byte[]key = realmEncryptionHelper.getEncryptKey();
            config = new RealmConfiguration.Builder()
                    .encryptionKey(key)
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }
        return Realm.getInstance(config);
    }

    public static void clearDBfile() {
        if (config != null) {
            Realm.compactRealm(config);
        }
    }

    public static SimpleDateFormat getNotificationDateFormat() {
        return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public static SimpleDateFormat getDateFormatUTC() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return simpleDateFormat;
    }

    public static SimpleDateFormat getDateFormatUTC1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return simpleDateFormat;
    }

    public static SimpleDateFormat getDateFormatType1() {
        return new SimpleDateFormat("MMM dd yyyy");
    }

    public static SimpleDateFormat getDateFormatType12() {
        return new SimpleDateFormat("MMM dd yyyy hh:mma");
    }

    public static SimpleDateFormat getDateFormatType4() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static String getDateFormatType3() {
        Date date = new Date();
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
    }

    public static SimpleDateFormat getDateFormatType2() {
        return new SimpleDateFormat("hha, MMM dd yyyy");
    }

    public static SimpleDateFormat getDateFormatType9() {
        return new SimpleDateFormat("hh:mma, MMM dd yyyy");
    }

    public static SimpleDateFormat getDateFormatType10() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static SimpleDateFormat getDateFormatType11() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static SimpleDateFormat getDateFormatFormatIn() {
        return new SimpleDateFormat("MM yyyy");
    }

    public static SimpleDateFormat getDateFormatFormatOut() {
        return new SimpleDateFormat("MMM yyyy");
    }

    public static SimpleDateFormat getDateFormatFormatInType1() {
        return new SimpleDateFormat("dd MM yyyy");
    }

    public static SimpleDateFormat getDateFormatFormatOutType1() {
        return new SimpleDateFormat("dd, MMM yyyy");
    }

    public static SimpleDateFormat getDateFormatYearFormat() {
        return new SimpleDateFormat("yyyy");
    }

    public static SimpleDateFormat getHourMinuteSecondFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    public static SimpleDateFormat getLabkeyDateFormat() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//"2017/06/15 08:27:07"
    }


    public static SimpleDateFormat getHourAMPMFormat1() {
        return new SimpleDateFormat("hh:mmaa");
    }

    public static SimpleDateFormat getDDFormat() {
        return new SimpleDateFormat("dd");
    }

    public static SimpleDateFormat getEEFormat() {
        return new SimpleDateFormat("EE");
    }

    public static SimpleDateFormat getHourAMPMMonthDayYearFormat() {
        return new SimpleDateFormat("hh:mmaa, MMM dd yyyy");
    }


    public static AlertDialog upgrade(int resultCode, boolean forceUpgrade, Context context, String version, String message) {
        AlertDialog alertDialog;
        if (forceUpgrade) {
            alertDialog = setNeutralDialog(resultCode, forceUpgrade, context, context.getResources().getString(R.string.force_upgrade), false, context.getResources().getString(R.string.upgrade), context.getResources().getString(R.string.upgrade), version, message);
        } else {
            alertDialog = setNeutralDialog(resultCode, forceUpgrade, context, context.getResources().getString(R.string.normal_upgrade), false, context.getResources().getString(R.string.upgrade), context.getResources().getString(R.string.upgrade), version, message);
        }
        return alertDialog;
    }

    public static AlertDialog setNeutralDialog(final int resultCode, final boolean forceUpgrade, final Context context, String message, final boolean finish, String positiveButton, String title, String version, String versionMessage) {
        if (!forceUpgrade) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message).setCancelable(false)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (finish) {
                                ((Activity) context).finish();
                            }
                        }
                    });


            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    final String appPackageName = context.getPackageName();
                    try {
                        ((Activity) context).startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), resultCode);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        ((Activity) context).startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), resultCode);
                    }
                    if (finish) {
                        ((Activity) context).finish();
                    }
                    if (!forceUpgrade)
                        alertDialog.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    ((SplashActivity) context).loadsplash();
                }
            });
            return alertDialog;
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setCancelable(false);

// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.force_upgrade_lay, null);
            TextView title1 = (TextView) dialogView.findViewById(R.id.title);
            TextView upgrade = (TextView) dialogView.findViewById(R.id.upgrade);
            TextView desc = (TextView) dialogView.findViewById(R.id.desc);
            desc.setText(versionMessage);
            upgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String appPackageName = context.getPackageName();
                    try {
                        ((Activity) context).startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), resultCode);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        ((Activity) context).startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), resultCode);
                    }
                }
            });
            dialogBuilder.setView(dialogView);
            title1.setText(version);

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            return alertDialog;

        }
    }



    /**
     * KEYSTORE RELATED CODES HERE
     */

    public static void keystoreInitilize(Context context) {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        String secretKeyVal = refreshKeys("key");
        if (secretKeyVal == null) {
//            String alias = getRandomString();
            // to identify specific key and get that using key_
            String alias1 = "key_" + secretKeyToString();
            // genarate random iv and to get using iv_
            // keystore, store the secretkey; differentiate by key_
            createNewKeys(context, alias1);
        }
        String ivBytesVal = refreshKeys("iv");
        if (ivBytesVal == null) {
            // genarate random iv and to get using iv_
            String alias2 = "iv_" + ivBytesToString();
            // keystore, store the ivBytes string; differentiate by iv_
            createNewKeys(context, alias2);
        }
    }

    // get the stored keystore values
    public static String refreshKeys(String concatedString) {

        try {
            Enumeration<String> aliases;
            if (mKeyStore == null) {
                Log.e("keystore", "null");
                mKeyStore = KeyStore.getInstance("AndroidKeyStore");
                mKeyStore.load(null);
            }
            aliases = mKeyStore.aliases();
            String val;
            while (aliases.hasMoreElements()) {
                val = aliases.nextElement();
                Log.e("rajeesh", "refreshKeys Keystore...." + val);
                if (val.contains(concatedString)) {
                    String splitString[] = val.split("_");
                    return splitString[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // first time create new keystore value
    public static void createNewKeys(Context context, String alias) {
        try {
            // Create new key if needed
            if (!mKeyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    // delete the keystore value
    public static void deleteKey(String key) {
        try {
            mKeyStore.deleteEntry(key);
        } catch (KeyStoreException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }


    // convert  String to SecretKey
    public static SecretKey stringToSecretKey(String abc) {
        byte[] encodedKey = Base64.decode(abc, Base64.DEFAULT);
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return originalKey;
    }

    // convert SecretKey to String
    public static String secretKeyToString() {
        String keyConvertString = "";
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(256);
            SecretKey skey = kgen.generateKey();
            keyConvertString = Base64.encodeToString(skey.getEncoded(), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyConvertString;
    }

    // convert ivBytes to String
    public static String ivBytesToString() {
        String ivBytesConvertString = "";
        try {
            SecureRandom rand = new SecureRandom();
            byte[] ivBytes = new byte[16];
            rand.nextBytes(ivBytes);
            ivBytesConvertString = new String(Base64.encode(ivBytes, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ivBytesConvertString;
    }

    // convert  String to ivBytes
    public static byte[] stringToIvBytes(String val) {
        byte[] ivBytes = null;
        try {
            ivBytes = val.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ivBytes;
    }

    // encrypt the pdf file and return File
    public static File genarateEncryptedConsentPDF(String filePath, String timeStamp) {
        try {
            FileInputStream fis = new FileInputStream(new File(filePath + timeStamp + ".pdf"));
            File encryptFile = new File(filePath + File.separator + timeStamp + ".txt");
            int read;
            if (!encryptFile.exists())
                encryptFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(encryptFile);
            Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            String val = AppController.refreshKeys("key");
            SecretKey skey = AppController.stringToSecretKey(val);

            String val2 = AppController.refreshKeys("iv");
            byte[] ivBytes = AppController.stringToIvBytes(val2);

            try {
                encipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(Base64.decode(ivBytes, Base64.DEFAULT)));
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            CipherInputStream cis = new CipherInputStream(fis, encipher);
            while ((read = cis.read()) != -1) {
                fos.write((char) read);
                fos.flush();
            }
            fos.close();
            return encryptFile;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    // decrypt the pdf file and return CipherInputStream
    public static CipherInputStream genarateDecryptedConsentPDF(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            String getStringTypeKey = AppController.refreshKeys("key");
            SecretKey skey = AppController.stringToSecretKey(getStringTypeKey);
            String getStringIvByte = AppController.refreshKeys("iv");
            byte[] ivBytes = AppController.stringToIvBytes(getStringIvByte);
            try {
                encipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(Base64.decode(ivBytes, Base64.DEFAULT)));
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            CipherInputStream cis = new CipherInputStream(fis, encipher);
            return cis;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    // convert cipherInputStreamConvertToByte to byte []
    public static byte[] cipherInputStreamConvertToByte(CipherInputStream cis) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = cis.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            byte[] cipherByteArray = baos.toByteArray();
            return cipherByteArray;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /////////// offline data storing
    /*
    number------- > auto increment
    httpMethod--- > post/get
    url---------- > server url
    serverType--- > registraion/response/wcp
    userProfileId-> userId (only for profile scenario handling other case pass "" )
    studyId-------> handling bookmark duplicate(only using the class studyinfo other class pass it "")
    activityId----> handling SurveyActivitiesFragment duplication other class pass it ""
     */
    public static void pendingService(Context context,int number, String httpMethod, String url, String normalParam, String jsonParam, String serverType, String userProfileId, String studyId, String activityId) {

        try {
            OfflineData offlineData = new OfflineData();

            offlineData.setNumber(number);
            offlineData.setDate(new Date());
            offlineData.setHttpMethod(httpMethod);
            offlineData.setUrl(url);
            offlineData.setNormalParam(normalParam);
            offlineData.setJsonParam(jsonParam);
            offlineData.setServerType(serverType);
            offlineData.setUserProfileId(userProfileId);
            offlineData.setStudyId(studyId);
            offlineData.setActivityId(activityId);
            offlineData.setStatus(false);
            DBServiceSubscriber db = new DBServiceSubscriber();
            db.saveOfflineData(context,offlineData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}