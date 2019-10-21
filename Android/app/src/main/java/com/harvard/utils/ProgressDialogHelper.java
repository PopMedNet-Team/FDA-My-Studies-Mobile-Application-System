package com.harvard.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Created by Naveen Raj on 07/26/2016.
 * <p/>
 * This is a Progress dialog helper class
 */
public class ProgressDialogHelper {
    private ProgressDialog mRingProgressDialog;

    /**
     * To show a progress dialog
     *
     * @param context    Activity context
     * @param title      Title to display
     * @param msg        Msg to display
     * @param cancelable True or False
     */
    public void showProgress(Context context, String title, String msg, boolean cancelable) {
//        showCustomProgress(context, R.drawable.loader, cancelable);
        showProgressOnly(context, false);
    }

    /**
     * To show a progress dialog with title and msg
     *
     * @param context    activity context
     * @param title      title to display null if don't need to show title
     * @param msg        msg to display
     * @param cancelable is cancelable or not
     */
    private void showProgressDefault(Context context, String title, String msg, boolean cancelable) {
        if (mRingProgressDialog == null) {
            mRingProgressDialog = new ProgressDialog(context);
            mRingProgressDialog.setCancelable(cancelable);
            mRingProgressDialog.setTitle(title);
            mRingProgressDialog.setMessage(msg);
            mRingProgressDialog.show();
        }
    }

    /**
     * To show progress dialog without title nor msg
     *
     * @param context    activity context
     * @param cancelable is cancelable or not
     */
    private void showProgressOnly(Context context, boolean cancelable) {
        if (mRingProgressDialog == null) {
            mRingProgressDialog = new ProgressDialog(context);
            mRingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mRingProgressDialog.setCancelable(cancelable);
            mRingProgressDialog.show();
            mRingProgressDialog.setContentView(new ProgressBar(context));
        }
    }

    /**
     * To show a custom spinner
     *
     * @param context    activity context
     * @param id         drawable resource of custom spinner
     * @param cancelable is cancelable or not
     */

    private void showCustomProgress(Context context, int id, boolean cancelable) {
        mRingProgressDialog = new ProgressDialog(context);
        mRingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mRingProgressDialog.setCancelable(cancelable);
        mRingProgressDialog.show();
        ProgressBar progressDialog = new ProgressBar(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getdrawable(context, id));
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams layoutParamsprogress = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsprogress.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressDialog.setLayoutParams(layoutParamsprogress);
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.addView(progressDialog);
        mRingProgressDialog.setContentView(relativeLayout);
    }


    /**
     * only for swipe to refresh time
     */

    public void showSwipeListCustomProgress(Context context, int id, boolean cancelable) {
        mRingProgressDialog = new ProgressDialog(context);
        mRingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mRingProgressDialog.setCancelable(cancelable);
        mRingProgressDialog.show();
        ProgressBar progressDialog = new ProgressBar(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getdrawable(context, id));
        progressDialog.getIndeterminateDrawable().setColorFilter(
                context.getResources().getColor(android.R.color.darker_gray),
                android.graphics.PorterDuff.Mode.SRC_IN);
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams layoutParamsprogress = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsprogress.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressDialog.setLayoutParams(layoutParamsprogress);
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.addView(progressDialog);
        mRingProgressDialog.setContentView(relativeLayout);
    }

    /**
     * dismiss a dialog
     */
    public void dismissDialog() {
        if (mRingProgressDialog != null && mRingProgressDialog.isShowing()) {
            mRingProgressDialog.dismiss();
            mRingProgressDialog = null;
        }
    }

    private static Drawable getdrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
