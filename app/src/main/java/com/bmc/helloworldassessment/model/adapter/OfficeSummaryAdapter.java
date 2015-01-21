package com.bmc.helloworldassessment.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.model.OfficeSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by bmc on 1/20/15.
 */
public class OfficeSummaryAdapter extends RecyclerView.Adapter<OfficeSummaryAdapter.ViewHolder> {
    private List<OfficeSummary> offices;
    private int rowlayout;
    private Context context;

    public OfficeSummaryAdapter(List<OfficeSummary> offices, int rowlayout, Context context) {
        this.offices = offices;
        this.rowlayout = rowlayout;
        this.context = context;
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
        viewHolder.distanceToOffice.setText(office.distanceToOffice + " miles away");
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView officeName;
        public TextView officeAddress;
        public TextView distanceToOffice;

        public ViewHolder(View itemView) {
            super(itemView);
            officeName = (TextView) itemView.findViewById(R.id.office_name);
            officeAddress = (TextView) itemView.findViewById(R.id.office_address);
            distanceToOffice = (TextView) itemView.findViewById(R.id.distance);
        }

    }

}
