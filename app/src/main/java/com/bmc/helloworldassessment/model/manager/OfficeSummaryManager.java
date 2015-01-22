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
            ArrayList<Location> locations, android.location.Location position) {
        if (offices == null) {
            offices = new ArrayList<>();

            int numberOfOffices = locations.size();

            for (int i = 0; i < numberOfOffices; i++) {
                OfficeSummary office = new OfficeSummary();
                office.officeName = locations.get(i).getName();
                office.officeAddress = Utils.constructAddress(
                        locations.get(i).getAddress(),
                        locations.get(i).getAddress2(),
                        locations.get(i).getCity(),
                        locations.get(i).getState(),
                        locations.get(i).getZipCode());
                // If user location was pinpointed, show distance to office
                if (position != null) {
                    office.distanceToOffice = Utils.distanceBetween(
                            position.getLatitude(), position.getLongitude(),
                            locations.get(i).getLatitude(), locations.get(i).getLongitude());
                }

                offices.add(office);
            }
        }
        return offices;
    }
}
