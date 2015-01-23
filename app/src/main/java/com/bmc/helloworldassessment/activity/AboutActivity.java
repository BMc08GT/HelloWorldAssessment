package com.bmc.helloworldassessment.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.model.adapter.OpenSourceAdapter;
import com.bmc.helloworldassessment.model.manager.OpenSourceManager;

public class AboutActivity extends BaseActivity {

    @Override
    public int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    public String getTitleResource() {
        return getString(R.string.about);
    }

    @Override
    public boolean setHomeAsUpEnabled() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.license_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        OpenSourceAdapter mAdapter = new OpenSourceAdapter(
                OpenSourceManager.getInstance().getLibraries(this), R.layout.license_item, this);
        mRecyclerView.setAdapter(mAdapter);

    }
}
