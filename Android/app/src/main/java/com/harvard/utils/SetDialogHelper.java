package com.harvard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.harvard.R;


public class SetDialogHelper {

    /**
     * To show alert
     *
     * @param context        Activity context
     * @param message        Msg to display
     * @param finish         whether to finish the activity
     * @param positiveButton Name of pos button
     */
    public static void setNeutralDialog(final Context context, String message, final boolean finish, String positiveButton, String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (finish) {
                            ((Activity) context).finish();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * To show alert which will finish the activity with RESULT_OK
     *
     * @param context        Activity context
     * @param message        Msg to display
     * @param positiveButton Name of pos button
     */
    public static void setDialogResultOK(final Context context, String message, String positiveButton) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setTitle(context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());
        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).setResult(Activity.RESULT_OK);
                        ((Activity) context).finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * To show alert which will have both pos button --> RESULT_OK and neg button  --> RESULT_NOT_OK
     *
     * @param context        Activity context
     * @param message        Msg to display
     * @param positiveButton Name of pos button
     * @param negativeButton Name of neg button
     */
    public static void setDialogResultOkWithCancel(final Context context, String message, String positiveButton, String negativeButton) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setTitle(context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());
        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).setResult(Activity.RESULT_OK);
                        ((Activity) context).finish();
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
