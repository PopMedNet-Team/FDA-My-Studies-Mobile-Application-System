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

package com.harvard.fda.studyAppModule;

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

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.events.GetResourceListEvent;
import com.harvard.fda.studyAppModule.studyModel.Resource;
import com.harvard.fda.studyAppModule.studyModel.StudyResource;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;

import java.util.HashMap;

import io.realm.RealmList;


public class ResourcesFragment<T> extends Fragment {

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
        return view;
    }

    private void initializeXMLId(View view) {
        mStudyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
    }


    private void mSetResourceAdapter() {
        mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        GatewayResourcesListAdapter gatewayResourcesListAdapter = new GatewayResourcesListAdapter(getActivity(), mResourceArrayList, this);
        mStudyRecyclerView.setAdapter(gatewayResourcesListAdapter);
    }


}
