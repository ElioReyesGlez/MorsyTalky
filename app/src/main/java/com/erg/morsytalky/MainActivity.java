package com.erg.morsytalky;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.erg.morsytalky.model.adapters.AdapterViewPager;
import com.google.android.material.tabs.TabLayout;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        String[] tabsTitles = getResources().getStringArray(R.array.tabs_titles);
        tabLayout.setupWithViewPager(viewPager);
        AdapterViewPager adapterViewPager = new AdapterViewPager(
                getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabsTitles);
        viewPager.setAdapter(adapterViewPager);
        Log.d(TAG, "onCreate: Done!");
    }
}