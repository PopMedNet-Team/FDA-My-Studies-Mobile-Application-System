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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harvard.fda.R;

import java.util.ArrayList;


public class ReachoutFragment<T> extends Fragment {

    private RecyclerView mReachoutRecyclerView;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reachout, container, false);
        initializeXMLId(view);
        setRecyclearView();
        return view;
    }

    private void initializeXMLId(View view) {
        mReachoutRecyclerView = (RecyclerView) view.findViewById(R.id.reachoutRecyclerView);
    }


    private void setRecyclearView() {
        mReachoutRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mReachoutRecyclerView.setNestedScrollingEnabled(false);
        ArrayList<String> reachoutList = new ArrayList<>();
        reachoutList.add(getString(R.string.anonymous_feedback));
        reachoutList.add(getString(R.string.need_help));
        ReachoutListAdapter reachoutListAdapter = new ReachoutListAdapter(getActivity(), reachoutList);
        mReachoutRecyclerView.setAdapter(reachoutListAdapter);
    }

}
