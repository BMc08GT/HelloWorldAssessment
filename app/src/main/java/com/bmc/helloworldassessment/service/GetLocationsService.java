package com.bmc.helloworldassessment.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.utils.HttpRequestExecutor;
import com.bmc.helloworldassessment.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class GetLocationsService extends IntentService {

    private static final String TAG = GetLocationsService.class.getSimpleName();

    public static final String ACTION_CHECK_FINISHED = "com.bmc.helloworldassessment.action.LOCATION_CHECK_FINISHED";
    public static final String ACTION_CHECK = "com.bmc.helloworldassessment.action.CHECK";

    private HttpRequestExecutor mHttpExecutor;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GetLocationsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            mHttpExecutor = new HttpRequestExecutor();
        }

        if (Utils.isConnected(this)) {
            // User is connected so pull locations from json
            Intent finishedIntent = new Intent(ACTION_CHECK_FINISHED);
            ArrayList<Location> locations;
            try {
                locations = getCurrentLocationsAndFillIntent(finishedIntent);
            } catch (IOException io) {
                Log.d(TAG, "Could not check for locations", io);
                locations = null;
            }

            if (locations == null || mHttpExecutor.isAborted()) {
                sendBroadcast(finishedIntent);
                return;
            }

            sendBroadcast(finishedIntent);
        }
    }

    // HttpRequestExecutor.abort() may cause network activity, which must not happen in the
    // main thread. Spawn off the cleanup into a separate thread to avoid crashing due to
    // NetworkOnMainThreadException.
    private void cleanupHttpExecutor(final HttpRequestExecutor executor) {
        final Thread abortThread = new Thread(new Runnable() {
            @Override
            public void run() {
                executor.abort();
            }
        });
        abortThread.start();
    }

    private void addRequestHeaders(HttpRequestBase request) {
        String userAgent = Utils.getUserAgentString(this);
        if (userAgent != null) {
            request.addHeader("User-Agent", userAgent);
        }
        request.addHeader("Cache-Control", "no-cache");
    }

    private ArrayList<Location> getCurrentLocationsAndFillIntent(Intent intent) throws IOException {
        // Server Uri pulled in from config
        URI serverUri = URI.create(getResources().getString(R.string.config_serverUri));
        HttpPost request = new HttpPost(serverUri);

        addRequestHeaders(request);

        HttpEntity entity = mHttpExecutor.execute(request);
        if (entity == null || mHttpExecutor.isAborted()) {
            return null;
        }

        String json = EntityUtils.toString(entity, "UTF-8");
        ArrayList<Location> locations = parseJSON(json);

        if (mHttpExecutor.isAborted()) {
            Log.d(TAG, "getLocations - HttpExecutor is aborted");
            return null;
        }

        // Create a bundle to pass back to MapsActivity
        Bundle extras = new Bundle();
        extras.putSerializable("locations", locations);
        extras.putInt("size", locations.size());

        intent.putExtras(extras);
        return locations;
    }

    private ArrayList<Location> parseJSON(String jsonString) {
        ArrayList<Location> locations = new ArrayList<>();
        try {
            if (jsonString != null) {
                JSONObject result = new JSONObject(jsonString);
                JSONArray locationList = result.getJSONArray("locations");
                int locationSize = locationList.length();

                Log.d(TAG, "Got location list with " + locationSize + " entries");

                for (int i = 0; i < locationSize; i++) {
                    if (mHttpExecutor.isAborted()) {
                        Log.d(TAG, "ParseJson - HttpExecutor aborted");
                        break;
                    }
                    if (locationList.isNull(i)) {
                        continue;
                    }
                    JSONObject item = locationList.getJSONObject(i);
                    Location location = parseLocationJSONObject(item);
                    if (location != null) {
                        locations.add(location);
                    }
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Error in json result", e);
        }
        return locations;
    }

    private Location parseLocationJSONObject(JSONObject item) throws JSONException {
        return new Location.Builder()
                .setName(item.getString("name"))
                .setAddress(item.getString("address"))
                .setAddress2(item.getString("address2"))
                .setCity(item.getString("city"))
                .setState(item.getString("state"))
                .setZipCode(item.getInt("zip_postal_code"))
                .setPhoneNumber(item.getString("phone"))
                .setFaxNumber(item.getString("fax"))
                .setLatitude(item.getDouble("latitude"))
                .setLongitude(item.getDouble("longitude"))
                .setImage(item.getString("office_image"))
                .build();
    }


}
