package com.harvard.gatewayModule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.harvard.R;
import com.harvard.utils.AppController;

public class GatewayPagerAdapter extends PagerAdapter {

    private int mSize;
    private AppCompatImageView mBgImg;
    private AppCompatTextView mWebsite;
    private AppCompatTextView mWelcome;
    private AppCompatTextView mDesc;
    private AppCompatTextView mWatchVideoLabel;
    private Context mContext;

    public GatewayPagerAdapter() {
        mSize = 4;
    }

    public GatewayPagerAdapter(int count) {
        mSize = count;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = inflater.getContext();
        switch (position) {
            case 0:
                View view = inflater.inflate(R.layout.gateway_item1, null);
                initializeXMLId(0, view);
                setFont(0, view);
                collection.addView(view);
                return view;
            case 1:
                View view1 = inflater.inflate(R.layout.gateway_item2, null);
                initializeXMLId(1, view1);
                mWelcome.setText(mContext.getResources().getString(R.string.gateway_welcome1));
                mDesc.setText(mContext.getResources().getString(R.string.gateway_des1));
                setFont(1, view1);
                collection.addView(view1);
                return view1;
            case 2:
                View view2 = inflater.inflate(R.layout.gateway_item2, null);
                initializeXMLId(2, view2);
                mWelcome.setText(mContext.getResources().getString(R.string.gateway_welcome2));
                mDesc.setText(mContext.getResources().getString(R.string.gateway_des2));
                setFont(2, view2);
                setView(2);
                collection.addView(view2);
                return view2;
            case 3:
                View view3 = inflater.inflate(R.layout.gateway_item2, null);
                initializeXMLId(2, view3);
                mWelcome.setText(mContext.getResources().getString(R.string.gateway_welcome3));
                mDesc.setText(mContext.getResources().getString(R.string.gateway_des3));
                setFont(2, view3);
                setView(3);
                collection.addView(view3);
                return view3;
            default:
                return null;


        }
    }


    private void initializeXMLId(int pos, View view) {
        if (pos == 0) {
            mWebsite = (AppCompatTextView) view.findViewById(R.id.website);
            mWelcome = (AppCompatTextView) view.findViewById(R.id.welcome);
            mDesc = (AppCompatTextView) view.findViewById(R.id.desc);
            RelativeLayout mWatchVideo = (RelativeLayout) view.findViewById(R.id.watch_video);
            mWatchVideoLabel = (AppCompatTextView) view.findViewById(R.id.watchVideoLabel);

            mWatchVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=6FGGquOrVic"));
                    mContext.startActivity(intent);
                }
            });
        } else {
            mBgImg = (AppCompatImageView) view.findViewById(R.id.bgImg);
            mWebsite = (AppCompatTextView) view.findViewById(R.id.website);
            mWelcome = (AppCompatTextView) view.findViewById(R.id.welcome);
            mDesc = (AppCompatTextView) view.findViewById(R.id.desc);
        }

        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + mContext.getString(R.string.website)));
                mContext.startActivity(browserIntent);
            }
        });
    }

    private void setFont(int pos, View view) {
        try {
            if (pos == 0) {
                mWebsite.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
                mWelcome.setTypeface(AppController.getTypeface(view.getContext(), "bold"));
                mDesc.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
                mWatchVideoLabel.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
            } else {
                mWebsite.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
                mWelcome.setTypeface(AppController.getTypeface(view.getContext(), "thin"));
                mDesc.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView(int pos) {

        if (pos == 2) {
            mBgImg.setImageResource(R.drawable.img_02);
        }
        if (pos == 3) {
            mBgImg.setImageResource(R.drawable.img_03);
        }

    }



}