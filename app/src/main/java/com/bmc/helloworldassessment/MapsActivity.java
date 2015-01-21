package com.bmc.helloworldassessment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.model.adapter.OfficeSummaryAdapter;
import com.bmc.helloworldassessment.model.manager.OfficeSummaryManager;
import com.bmc.helloworldassessment.service.GetLocationsService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class MapsActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Location> locations;
    private double[] userLocation;
    private android.location.Location mLastLocation;


    private RecyclerView mRecyclerView;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(GetLocationsService.ACTION_CHECK_FINISHED)) {
                Bundle extras = intent.getExtras();

                int numLocations = extras.getInt("size", 0);
                locations = (ArrayList<Location>) intent.getSerializableExtra("locations");
                Log.d(TAG, "number of locations=" + numLocations);
                for (Location location : locations) {
                    Log.d(TAG, "location name: " + location.getName());
                }
                addAdapter();
                setUpMap();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleApiClient();
        setUpMapIfNeeded();
        setupRecyclerView();
        registerReceivers();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.office_summaries_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void addAdapter() {
        OfficeSummaryAdapter mAdapter = new OfficeSummaryAdapter(
                OfficeSummaryManager.getInstance().getOfficeSummaries(locations, getUserLocation()), R.layout.office_summary_item, this);
        mAdapter.sortByDistance();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter(GetLocationsService.ACTION_CHECK_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void startLocationService() {
        Intent i = new Intent(this, GetLocationsService.class);
        i.setAction(GetLocationsService.ACTION_CHECK);
        startService(i);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                startLocationService();
            }
        }
    }

    private void setUpMap() {
        for (Location location : locations) {
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(location.getLatitude(), location.getLongitude())).title(location.getName()));
        }
    }

    public double[] getUserLocation() {
        if (mLastLocation != null) {
            userLocation =  new double[2];
            userLocation[0] = mLastLocation.getLatitude();
            userLocation[1] = mLastLocation.getLongitude();
        }
        return userLocation;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
