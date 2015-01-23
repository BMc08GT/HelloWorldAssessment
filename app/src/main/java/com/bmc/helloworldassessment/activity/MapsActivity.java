package com.bmc.helloworldassessment.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.model.adapter.OfficeSummaryAdapter;
import com.bmc.helloworldassessment.model.manager.OfficeSummaryManager;
import com.bmc.helloworldassessment.service.GetImagesTask;
import com.bmc.helloworldassessment.service.GetLocationsService;
import com.bmc.helloworldassessment.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Location> locations;
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
                GetImagesTask task = new GetImagesTask(MapsActivity.this);
                task.execute(locations);

                addAdapter();
                setUpMap();
            }
        }
    };

    @Override
    public int getLayoutResource() {
        return R.layout.activity_maps;
    }

    @Override
    public String getTitleResource() {
        return getString(R.string.title_activity_maps);
    }

    @Override
    public boolean setHomeAsUpEnabled() {
        return false;
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
                OfficeSummaryManager.getInstance().getOfficeSummaries(locations, mLastLocation),
                R.layout.office_summary_item, this, mLastLocation, locations);
        if (mLastLocation != null) {
            // If we have a last location,
            // sort the list by distance from user to office
            mAdapter.sortByDistance();
            mAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter(GetLocationsService.ACTION_CHECK_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to unregister receiver. Not registered?");
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to unregister receiver. Not registered?");
        }
        mGoogleApiClient.disconnect();
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

    @SuppressLint("InflateParams")
    private void setUpMap() {
        for (Location location : locations) {
            // Create marker option
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            marker.title(location.getName());
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));

            // Add the marker to the map
            mMap.addMarker(marker);
        }

        // Disable marker clickevent to hide the button presence in btm right
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker arg0) {
                return true;
            }
        });
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
