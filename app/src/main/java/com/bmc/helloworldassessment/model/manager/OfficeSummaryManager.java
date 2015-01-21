package com.bmc.helloworldassessment.model.manager;

import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.model.OfficeSummary;
import com.bmc.helloworldassessment.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OfficeSummaryManager {

    private static OfficeSummaryManager sInstance;
    private List<OfficeSummary> offices;
    public static OfficeSummaryManager getInstance() {
        if (sInstance == null) {
            sInstance = new OfficeSummaryManager();
        }
        return sInstance;
    }

    public List<OfficeSummary> getOfficeSummaries(
            ArrayList<Location> locations, double[] indices) {
        if (offices == null) {
            offices = new ArrayList<>();

            int numberOfOffices = locations.size();

            for (int i = 0; i < numberOfOffices; i++) {
                OfficeSummary office = new OfficeSummary();
                office.officeName = locations.get(i).getName();
                office.officeAddress = constructAddress(
                        locations.get(i).getAddress(),
                        locations.get(i).getAddress2(),
                        locations.get(i).getCity(),
                        locations.get(i).getState(),
                        locations.get(i).getZipCode());
                // If user location was pinpointed, show distance to office
                if (indices != null) {
                    office.distanceToOffice = Utils.distanceBetween(
                            indices[0], indices[1],
                            locations.get(i).getLatitude(), locations.get(i).getLongitude());
                }

                offices.add(office);
            }
        }
        return offices;
    }

    public String constructAddress(
            String address1, String address2, String city, String state, int zipCode) {

        return address1 + "\n" +
                (address2 == null || address2.isEmpty() ? "" : address2 + "\n") +
                city + ", " + state + " " + zipCode;
    }
}