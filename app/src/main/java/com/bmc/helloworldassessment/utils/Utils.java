package com.bmc.helloworldassessment.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bmc.helloworldassessment.misc.Location;
import com.koushikdutta.ion.Ion;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static String getUserAgentString(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.packageName + "/" + pi.versionName;
        } catch (PackageManager.NameNotFoundException nnfe) {
            return null;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static double distanceBetween(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        DecimalFormat df = new DecimalFormat("###.##");
        return Double.parseDouble(df.format(earthRadius * c));
    }

    public static Bitmap urlToBitmap(Context context, String imageUrl) {
        try {
            return Ion.with(context).load(imageUrl).asBitmap().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getLocationIndex(String title, ArrayList<Location> locations) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getName().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    public static String constructAddress(
            String address1, String address2, String city, String state, int zipCode) {

        return address1 + "\n" +
                (address2 == null || address2.isEmpty() ? "" : address2 + "\n") +
                city + ", " + state + " " + zipCode;
    }
}
