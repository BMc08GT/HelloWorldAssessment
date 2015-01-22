package com.bmc.helloworldassessment.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.activity.OfficeDetailsActivity;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.model.OfficeSummary;
import com.bmc.helloworldassessment.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OfficeSummaryAdapter extends RecyclerView.Adapter<OfficeSummaryAdapter.ViewHolder> {
    private List<OfficeSummary> offices;
    private ArrayList<Location> locations;
    private int rowlayout;;
    private Context mContext;
    private android.location.Location userLocation;

    public OfficeSummaryAdapter(
            List<OfficeSummary> offices, int rowlayout, Context context,
            android.location.Location userLocation, ArrayList<Location> locations) {
        this.offices = offices;
        this.rowlayout = rowlayout;
        this.mContext = context;
        this.userLocation = userLocation;
        this.locations = locations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowlayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        OfficeSummary office = offices.get(i);
        viewHolder.officeName.setText(office.officeName);
        viewHolder.officeAddress.setText(office.officeAddress);
        // if a location has been found, set the text with the distance to office
        // otherwise set the view's visibility to GONE
        if (userLocation != null) {
            viewHolder.distanceToOffice.setText(office.distanceToOffice + " miles away");
        } else {
            viewHolder.distanceToOffice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return offices == null ? 0 : offices.size();
    }

    public void sortByDistance() {
        Comparator<OfficeSummary> distanceComparator = new Comparator<OfficeSummary>() {
            @Override
            public int compare(OfficeSummary lhs, OfficeSummary rhs) {
                double dist1 = lhs.distanceToOffice;
                double dist2 = rhs.distanceToOffice;
                return (int) (dist1 -dist2);
            }
        };
        Collections.sort(offices, distanceComparator);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView officeName;
        public TextView officeAddress;
        public TextView distanceToOffice;

        public ViewHolder(View itemView) {
            super(itemView);

            officeName = (TextView) itemView.findViewById(R.id.office_name);
            officeAddress = (TextView) itemView.findViewById(R.id.office_address);
            distanceToOffice = (TextView) itemView.findViewById(R.id.distance);

            // Set all aspects of the viewHolder to listen for clicks
            itemView.setOnClickListener(this);
            officeName.setOnClickListener(this);
            officeAddress.setOnClickListener(this);
            distanceToOffice.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Location location = locations.get(
                    Utils.getLocationIndex(officeName.getText().toString(), locations));

            Intent detailsIntent = new Intent(mContext, OfficeDetailsActivity.class);

            // Create a bundle to pass to OfficeDetailsActivity and add it to the intent
            Bundle extras = new Bundle();
            extras.putSerializable(OfficeDetailsActivity.EXTRA_LOCATION, location);
            extras.putParcelable(OfficeDetailsActivity.EXTRA_LAST_LOCATION, userLocation);
            detailsIntent.putExtras(extras);

            // Start the office details activity
            mContext.startActivity(detailsIntent);
        }
    }

}
