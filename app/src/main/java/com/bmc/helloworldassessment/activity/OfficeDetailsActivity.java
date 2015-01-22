package com.bmc.helloworldassessment.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.utils.Utils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

public class OfficeDetailsActivity extends BaseActivity {

    private static final String TAG = OfficeDetailsActivity.class.getSimpleName();

    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_LAST_LOCATION = "last_location";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mOfficeLocation;
    private android.location.Location mLastLocation;

    private FloatingActionsMenu mFam;
    private boolean mFamExpanded = false;

    public int getLayoutResource() {
        return R.layout.activity_details;
    }

    @Override
    public String getTitleResource() {
        // Set title to title_activity_maps here and
        // override the title in onCreate/onResume using mLocation.getName()
        return "";
    }

    @Override
    public boolean setHomeAsUpEnabled() {
        return true;
    }

    public void getLocationDetails() {
        Bundle extras = getIntent().getExtras();
        mOfficeLocation = (Location) extras.getSerializable(EXTRA_LOCATION);
        mLastLocation = extras.getParcelable(EXTRA_LAST_LOCATION);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getLocationDetails();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setMinimumHeight(500);
        Drawable mActionBarBackgroundDrawable = Utils.urlToDrawable(this, mOfficeLocation.getImageUrl());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(mActionBarBackgroundDrawable);
        } else {
            toolbar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        }
        setupCard();
        setupFabs();
        setUpMapIfNeeded();
    }

    @SuppressLint("InflateParams")
    private void setupCard() {
        TextView officeName = (TextView) findViewById(R.id.office_name);
        TextView officeAddress = (TextView) findViewById(R.id.office_address);
        TextView officeDistance = (TextView) findViewById(R.id.distance);
        TextView officePhone = (TextView) findViewById(R.id.office_phone);
        TextView officeFax = (TextView) findViewById(R.id.office_fax);

        officeName.setText(mOfficeLocation.getName());
        officeAddress.setText(Utils.constructAddress(
                mOfficeLocation.getAddress(),
                mOfficeLocation.getAddress2(),
                mOfficeLocation.getCity(),
                mOfficeLocation.getState(),
                mOfficeLocation.getZipCode()));
        if (mLastLocation == null) {
            officeDistance.setVisibility(View.GONE);
        } else {
            officeDistance.setText(Utils.distanceBetween(
                    mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                    mOfficeLocation.getLatitude(), mOfficeLocation.getLongitude()) + " miles away");
        }
        officePhone.setText(mOfficeLocation.getPhone());
        officeFax.setText(mOfficeLocation.getFax());
    }

    private void setupFabs() {
        mFam = (FloatingActionsMenu) findViewById(R.id.fabs);
        FloatingActionButton mCallFab = (FloatingActionButton) findViewById(R.id.call);
        FloatingActionButton mNavigateFab = (FloatingActionButton) findViewById(R.id.navigate);
        mFam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFamExpanded) {
                    mFamExpanded = false;
                    mFam.collapse();
                } else {
                    mFamExpanded = true;
                    mFam.expand();
                }
            }
        });
        mCallFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: verify if they want DIAL or CALL
                Intent callOfficeIntent = new Intent(Intent.ACTION_DIAL);
                callOfficeIntent.setData(Uri.parse("tel:" + mOfficeLocation.getPhone()));
                callOfficeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callOfficeIntent);
            }
        });
        mNavigateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(
                        "http://maps.google.com/maps?daddr=%f,%f (%s)",
                        mOfficeLocation.getLatitude(), mOfficeLocation.getLongitude(),
                        mOfficeLocation.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));

        // Create marker option
        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.title(mOfficeLocation.getName());
        marker.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));


        // Add the marker to the map
        mMap.addMarker(marker);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker arg0) {
                return true;
            }
        });
    }
}
