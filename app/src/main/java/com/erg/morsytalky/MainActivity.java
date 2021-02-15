package com.erg.morsytalky;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.erg.morsytalky.controller.helpers.MessagesHelper;
import com.erg.morsytalky.model.adapters.AdapterViewPager;
import com.erg.morsytalky.views.CustomViewPager;
import com.google.android.material.tabs.TabLayout;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    public CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        String[] tabsTitles = getResources().getStringArray(R.array.tabs_titles);
        tabLayout.setupWithViewPager(viewPager);
        AdapterViewPager adapterViewPager = new AdapterViewPager(
                getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabsTitles);
        viewPager.setAdapter(adapterViewPager);
        Log.d(TAG, "onCreate: Done!");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkCameraHardware(this)) {
            Log.d(TAG, "Check CameraHardware: this device has a camera");
        } else {
            MessagesHelper.showInfoMessageWithFinishContext(this,
                    getString(R.string.camera_unavailable));
            Log.d(TAG, "Check CameraHardware: this device DON'T has a camera");
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}