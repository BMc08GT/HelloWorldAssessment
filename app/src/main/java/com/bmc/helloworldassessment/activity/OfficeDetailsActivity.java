package com.bmc.helloworldassessment.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.misc.Constants;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.utils.Utils;
import com.bmc.helloworldassessment.view.NotifyScrollView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class OfficeDetailsActivity extends BaseActivity implements NotifyScrollView.Callback {

    private static final String TAG = OfficeDetailsActivity.class.getSimpleName();

    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_LAST_LOCATION = "last_location";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mOfficeLocation;
    private android.location.Location mLastLocation;

    private NotifyScrollView mNotifyScrollView;

    private FrameLayout mImageFrameLayout;
    private ImageView mImageView;

    private LinearLayout mContentLinearLayout;

    private LinearLayout mToolbarLinearLayout;
    private Toolbar mToolbar;

    private FloatingActionsMenu mFam;
    private boolean mFamExpanded = false;

    public int getLayoutResource() {
        return R.layout.activity_details;
    }

    @Override
    public String getTitleResource() {
        // Set title to title_activity_maps here and
        // override the title in onCreate/onResume using mLocation.getName()
        return mOfficeLocation.getName();
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

        // view matching
        mNotifyScrollView = (NotifyScrollView) findViewById(R.id.notify_scroll_view);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.image_frame_layout);
        mImageView = (ImageView) findViewById(R.id.image_view);

        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_linear_layout);

        mToolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbar_linear_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupNotifyScrollView();
        setupToolbar();
        setupCard();
        setupFabs();
        setUpMapIfNeeded();
    }

    private void setupNotifyScrollView() {
        mNotifyScrollView.setCallback(this);

        ViewTreeObserver viewTreeObserver = mNotifyScrollView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // get size
                    int toolbarLinearLayoutHeight = mToolbarLinearLayout.getHeight();
                    int imageHeight = mImageView.getHeight();

                    // adjust image frame layout height
                    ViewGroup.LayoutParams layoutParams = mImageFrameLayout.getLayoutParams();
                    if (layoutParams.height != imageHeight) {
                        layoutParams.height = imageHeight;
                        mImageFrameLayout.setLayoutParams(layoutParams);
                    }

                    // adjust top margin of content linear layout
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mContentLinearLayout.getLayoutParams();
                    if (marginLayoutParams.topMargin != toolbarLinearLayoutHeight + imageHeight) {
                        marginLayoutParams.topMargin = toolbarLinearLayoutHeight + imageHeight;
                        mContentLinearLayout.setLayoutParams(marginLayoutParams);
                    }

                    // call onScrollChanged to update initial properties.
                    onScrollChanged(0, 0, 0, 0);
                }
            });
        }
    }

    private void setupToolbar() {
        Bitmap bitmap = null;
        Drawable mActionBarBackgroundDrawable;
        if (!Utils.isConnected(this)) {
            // attempt to retrieve the saved bitmap
            SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
            String encodedBitmap = prefs.getString(mOfficeLocation.getName(), null);
            if (encodedBitmap != null) {
                bitmap = Utils.base64StringToBitmap(encodedBitmap);
            }
        } else {
            // use Ion to fetch the bitmap from json url
            bitmap = Utils.urlToBitmap(this, mOfficeLocation.getImageUrl());
        }
        // check if bitmap is available for use
        // otherwise use a ColorDrawable
        if (bitmap != null) {
            mActionBarBackgroundDrawable = new BitmapDrawable(
                    getResources(), bitmap);
        } else {
            mActionBarBackgroundDrawable = new ColorDrawable(R.color.blue);
        }

        mImageView.setImageDrawable(mActionBarBackgroundDrawable);
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
        // Create LatLng instance
        LatLng position = new LatLng(mOfficeLocation.getLatitude(), mOfficeLocation.getLongitude());

        // Move the camera to office's location and set zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));

        // Create marker option
        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.title(mOfficeLocation.getName());
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));


        // Add the marker to the map
        mMap.addMarker(marker);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker arg0) {
                return true;
            }
        });
    }

    @Override
    public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        // get scroll y
        int scrollY = mNotifyScrollView.getScrollY();

        // calculate new y (for toolbar translation)
        float newY = Math.max(mImageView.getHeight(), scrollY);

        // translate toolbar linear layout and image frame layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mToolbarLinearLayout.setTranslationY(newY);
            mImageFrameLayout.setTranslationY(scrollY * 0.5f);
        } else {
            ViewCompat.setTranslationY(mToolbarLinearLayout, newY);
            ViewCompat.setTranslationY(mImageFrameLayout, scrollY * 0.5f);
        }
    }
}
