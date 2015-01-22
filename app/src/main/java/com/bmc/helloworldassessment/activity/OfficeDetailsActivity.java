package com.bmc.helloworldassessment.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;

public class OfficeDetailsActivity extends BaseActivity implements OnScrollChangedCallback {

    private static final String TAG = OfficeDetailsActivity.class.getSimpleName();

    public static final String EXTRA_LOCATION = "location";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mOfficeLocation;

    @Override
    public int getLayoutResource() {
        return R.layout.activity_details;
    }

    @Override
    public int getTitleResource() {
        // Set title to title_activity_maps here and override the title in onCreate/onResume
        // using mLocation.getName()
        return R.string.title_activity_maps;
    }

    @Override
    public boolean getHomeAsUpNavigation() {
        return true;
    }

    public void getLocationDetails() {
        Bundle extras = getIntent().getExtras();
        mOfficeLocation = (Location) extras.getSerializable(EXTRA_LOCATION);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getLocationDetails();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(mOfficeLocation.getName());
        getSupportActionBar().setBackgroundDrawable(
                Utils.urlToDrawable(this, mOfficeLocation.getImageUrl()));
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_detail))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @SuppressLint("InflateParams")
    private void setUpMap() {
        // Setup icon generator to create a marker using company color
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setColor(getResources().getColor(R.color.orange));

        // Create LatLng instance
        LatLng position = new LatLng(mOfficeLocation.getLatitude(), mOfficeLocation.getLongitude());

        // Move the camera to office's location and set zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.0f));

        // Create marker option
        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.title(mOfficeLocation.getName());
        marker.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));

        // Add the marker to the map
        mMap.addMarker(marker);
    }

    @Override
    public void onScroll(int i, int i2) {

    }
}
