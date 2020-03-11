package com.harvard.studyAppModule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.studyAppModule.events.GetResourceListEvent;
import com.harvard.studyAppModule.studyModel.Resource;
import com.harvard.studyAppModule.studyModel.StudyResource;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.WCPConfigEvent;

import java.util.HashMap;

import io.realm.RealmList;


public class ResourcesFragment<T> extends Fragment implements ApiCall.OnAsyncRequestComplete {

    private RecyclerView mStudyRecyclerView;
    private Context mContext;
    private RealmList<Resource> mResourceArrayList;
    private int RESOURCE_REQUEST_CODE = 213;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resources, container, false);
        initializeXMLId(view);
        // currently hide this and create temp block then pass to adapter
        // temp block(later u can remove)
        Resource r = new Resource();
        r.setTitle(mContext.getResources().getString(R.string.app_glossary));
        r.setType("pdf");
        r.setContent("");
        mResourceArrayList = new RealmList<>();
        mResourceArrayList.add(r);
        mSetResourceAdapter();
        /// till here/////
        return view;
    }

    private void initializeXMLId(View view) {
        mStudyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == RESOURCE_REQUEST_CODE) {
            if (response != null) {
                StudyResource studyResource = (StudyResource) response;
                mResourceArrayList = studyResource.getResources();
                if (mResourceArrayList != null && mResourceArrayList.size() != 0) {
                    mSetResourceAdapter();
                }
            }
        }
    }

    private void mSetResourceAdapter() {
        mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        GatewayResourcesListAdapter gatewayResourcesListAdapter = new GatewayResourcesListAdapter(getActivity(), mResourceArrayList, this);
        mStudyRecyclerView.setAdapter(gatewayResourcesListAdapter);
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(mContext, errormsg);
        } else if (responseCode == RESOURCE_REQUEST_CODE) {
            Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
        }
    }

}
