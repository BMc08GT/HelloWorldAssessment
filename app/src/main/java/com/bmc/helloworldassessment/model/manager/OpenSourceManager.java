package com.bmc.helloworldassessment.model.manager;

import android.content.Context;
import android.content.Intent;

import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.activity.WebViewActivity;
import com.bmc.helloworldassessment.model.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmc on 1/23/15.
 */
public class OpenSourceManager {

    private static OpenSourceManager sInstance;
    private List<Library> libraries;

    public static OpenSourceManager getInstance() {
        if (sInstance == null) {
            sInstance = new OpenSourceManager();
        }
        return sInstance;
    }

    public List<Library> getLibraries(Context context) {
        if (libraries == null) {
            libraries = new ArrayList<>();

            String[] libraryNames = context.getResources().getStringArray(R.array.open_source_library_names);
            String[] libraryLicenses = context.getResources().getStringArray(R.array.open_source_library_licenses);

            for (int i = 0; i < libraryNames.length; i++) {
                String libraryName = libraryNames[i];
                String libraryLicense = libraryLicenses[i];
                Intent webIntent = new Intent(context, WebViewActivity.class);
                webIntent.putExtra(WebViewActivity.EXTRA_URL, libraryLicense);
                webIntent.putExtra(WebViewActivity.EXTRA_TITLE, libraryName);

                Library library = new Library();
                library.name = libraryName;
                library.intent = webIntent;
                libraries.add(library);
            }
        }
        return libraries;
    }
}
