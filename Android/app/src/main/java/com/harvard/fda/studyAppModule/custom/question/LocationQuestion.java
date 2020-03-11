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

package com.harvard.fda.studyAppModule.custom.question;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;
import com.harvard.fda.utils.AppController;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Naveen Raj on 01/31/2017.
 */
public class LocationQuestion implements StepBody, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GpsStatus.Listener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStepCustom step;
    private StepResult<String> result;
    private LocationAnswerFormat format;
    private LatLng currentSelected;
    Context context;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    boolean currentlocation = true;
    EditText search;

    public LocationQuestion(Step step, StepResult result) {
        this.step = (QuestionStepCustom) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (LocationAnswerFormat) this.step.getAnswerFormat1();

        String resultValue = this.result.getResult();
        if (resultValue != null) {
            String[] resultValuesplit = resultValue.split(",");
            currentSelected = new LatLng(Double.parseDouble(resultValuesplit[0]), Double.parseDouble(resultValuesplit[1]));
        }


    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            return initViewDefault(inflater, parent);
        } else if (viewType == VIEW_TYPE_COMPACT) {
            return initViewCompact(inflater, parent);
        } else {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initViewDefault(final LayoutInflater inflater, ViewGroup parent) {
        context = inflater.getContext();


        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View locationquestionlayout = inflater.inflate(R.layout.locationquestionlayout, parent, false);
        final FrameLayout frame = (FrameLayout) locationquestionlayout.findViewById(R.id.locationmap);
        int frameId = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(context, context.getString(R.string.mapCount), "1"));
        frameId++;
        frame.setId(frame.getId() + (frameId + 1));
        AppController.getHelperSharedPreference().writePreference(context, context.getString(R.string.mapCount), "" + frameId);
        search = (EditText) locationquestionlayout.findViewById(R.id.search);


        final SupportMapFragment mSupportMapFragment = new SupportMapFragment();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FragmentManager fragmentManager = ((AppCompatActivity) inflater.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(frame.getId(), mSupportMapFragment,""+frame.getId());
                fragmentTransaction.commit();


                mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        mGoogleMap = googleMap;
                        if (currentSelected != null) {
                            googleMap.addMarker(new MarkerOptions().position(currentSelected).title(context.getResources().getString(R.string.selected_location)));
                            MapsInitializer.initialize(inflater.getContext());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentSelected, 5.0f));
                            search.setText(getCompleteAddressString(currentSelected.latitude, currentSelected.longitude));
                        }

                        if (currentSelected == null && format.isUseCurrentLocation()) {
                            if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                                mGoogleApiClient = new GoogleApiClient.Builder(context)
                                        .addApi(LocationServices.API)
                                        .addConnectionCallbacks(LocationQuestion.this)
                                        .addOnConnectionFailedListener(LocationQuestion.this).build();

                                LocationManager mService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                                mService.addGpsStatusListener(LocationQuestion.this);
                                try {
                                    mGoogleApiClient.connect();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                    @Override
                                    public void onMapLoaded() {
                                        mGoogleMap.setMyLocationEnabled(true);
                                    }
                                });
                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.permission_deny), Toast.LENGTH_SHORT).show();
                            }
                        }
                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                currentSelected = latLng;
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(latLng).title(context.getResources().getString(R.string.selected_location)));
                                search.setText(getCompleteAddressString(currentSelected.latitude, currentSelected.longitude));
                            }
                        });

                        search.setOnEditorActionListener(
                                new EditText.OnEditorActionListener() {
                                    @Override
                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                        // Identifier of the action. This will be either the identifier you supplied,
                                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                            LatLng latLng = getLocationFromAddress(v.getText().toString());
                                            if (latLng != null) {
                                                googleMap.clear();
                                                googleMap.addMarker(new MarkerOptions().position(latLng).title(context.getResources().getString(R.string.setected_location)));
                                                currentSelected = latLng;
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentSelected, 5.0f));
                                            } else {
                                                Toast.makeText(context, context.getResources().getString(R.string.unable_search_loc), Toast.LENGTH_SHORT).show();
                                            }
                                            return true;
                                        }
                                        // Return true if you have consumed the action, else false.
                                        return false;
                                    }
                                });
                    }
                });
            }
        }, 100);


        try {
            linearLayout.removeAllViewsInLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        linearLayout.addView(locationquestionlayout);
        return linearLayout;
    }

    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }


    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

        TextView label = (TextView) inflater.inflate(org.researchstack.backbone.R.layout.rsb_item_text_view_title_compact,
                compactView,
                false);
        label.setText(step.getTitle());

        compactView.addView(label, 0);

        return compactView;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped || currentSelected == null) {
            result.setResult(null);
        } else {
            result.setResult(currentSelected.latitude + "," + currentSelected.longitude);
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (currentSelected == null) {
            return BodyAnswer.INVALID;
        } else {
            return BodyAnswer.VALID;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every workout_minsond
        mLocationRequest.setSmallestDisplacement(.5f);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentlocation && currentSelected == null) {
            currentlocation = false;
            currentSelected = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(context.getResources().getString(R.string.selected_location)));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentSelected, 5.0f));
            search.setText(getCompleteAddressString(currentSelected.latitude, currentSelected.longitude));
        }
    }
}
