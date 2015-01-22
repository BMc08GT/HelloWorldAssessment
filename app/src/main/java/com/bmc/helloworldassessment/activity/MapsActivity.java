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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.model.adapter.OfficeSummaryAdapter;
import com.bmc.helloworldassessment.model.manager.OfficeSummaryManager;
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
import com.google.maps.android.ui.IconGenerator;

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
    public int getTitleResource() {
        return R.string.title_activity_maps;
    }

    @Override
    public boolean getHomeAsUpNavigation() {
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
                R.layout.office_summary_item, this, mLastLocation != null, locations);
        mRecyclerView.setAdapter(mAdapter);
        if (mLastLocation != null) {
            // If we have a last location,
            // sort the list by distance from user to office
            mAdapter.sortByDistance();
        }
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to unregister receiver. Not registered?");
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

    @SuppressLint("InflateParams")
    private void setUpMap() {
        // Setup icon generator to create a marker using company color
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setColor(getResources().getColor(R.color.orange));

        for (Location location : locations) {
            // Create marker option
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            marker.title(location.getName());
            marker.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));

            // Add the marker to the map
            mMap.addMarker(marker);
        }

        // Set infoWindow adapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);
                final ImageView image = (ImageView) view.findViewById(R.id.office_image);
                final TextView name = (TextView) view.findViewById(R.id.name);
                int index = Utils.getLocationIndex(marker.getTitle(), locations);
                if (index != -1) {
                    image.setImageDrawable(Utils.urlToDrawable(MapsActivity.this,
                            locations.get(index).getImageUrl()));
                    name.setText(locations.get(index).getName());
                }

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
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
