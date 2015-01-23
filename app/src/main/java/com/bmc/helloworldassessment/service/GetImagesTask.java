package com.bmc.helloworldassessment.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bmc.helloworldassessment.misc.Constants;
import com.bmc.helloworldassessment.misc.Location;
import com.bmc.helloworldassessment.utils.Utils;

import java.util.ArrayList;

public class GetImagesTask extends AsyncTask<ArrayList<Location>, Void, Void> {

    private static final String TAG = GetImagesTask.class.getSimpleName();

    private Context mContext;

    public GetImagesTask(Context context) {
        mContext = context;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(ArrayList<Location>... locations) {

        for (Location location : locations[0]) {
            SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

            // Get the bitmap converted from the office's provided url location
            Bitmap bitmap = Utils.urlToBitmap(mContext, location.getImageUrl());
            // Convert the bitmap into a base64 encoded string
            String encodedBitmap = Utils.bitmapToBase64String(bitmap);
            // Store the string in SharedPrefs with the key being the office name
            prefs.edit().putString(location.getName(), encodedBitmap).apply();
        }
        return null;
    }
}
