package com.bmc.helloworldassessment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by bmc on 1/21/15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    public abstract int getLayoutResource();
    public abstract int getTitleResource();
    public abstract boolean getHomeAsUpNavigation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
    }

    @Override
    protected void onResume() {
        super.onResume();

        initActionBar();
    }

    private void initActionBar() {
        getSupportActionBar().setTitle(getTitleResource());
        setHomeAsUpNavigation(getHomeAsUpNavigation());
    }
    public void setHomeAsUpNavigation(boolean homeAsUpNavigation) {
        getSupportActionBar().setDisplayShowHomeEnabled(homeAsUpNavigation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpNavigation);
    }
}
