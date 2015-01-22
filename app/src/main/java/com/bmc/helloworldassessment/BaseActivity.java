package com.bmc.helloworldassessment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public abstract class BaseActivity extends ActionBarActivity {

    public abstract int getLayoutResource();
    public abstract String getTitleResource();
    public abstract boolean setHomeAsUpEnabled();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getTitleResource());
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(setHomeAsUpEnabled());
        getSupportActionBar().setDisplayHomeAsUpEnabled(setHomeAsUpEnabled());
    }
}
