package com.erg.morsytalky.model.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.erg.morsytalky.fragments.ReceiverFragment;
import com.erg.morsytalky.fragments.TransmitterFragment;

public class AdapterViewPager extends FragmentPagerAdapter {

    private final String[] tabTitles;
    private ReceiverFragment receiverFragment = null;
    private TransmitterFragment transmitterFragment = null;

    public AdapterViewPager(@NonNull FragmentManager fm, int behavior, String[] tabsTitles) {
        super(fm, behavior);
        this.tabTitles = tabsTitles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                transmitterFragment = TransmitterFragment.newInstance();
                return transmitterFragment;
            case 1:
                receiverFragment = ReceiverFragment.newInstance(transmitterFragment);
                return receiverFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
