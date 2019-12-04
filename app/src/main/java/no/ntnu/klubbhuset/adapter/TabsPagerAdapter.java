package no.ntnu.klubbhuset.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.ui.userviews.home.HomeFragment;
import no.ntnu.klubbhuset.ui.userviews.mymemberships.MyMemberhipsFragment;
import no.ntnu.klubbhuset.ui.userviews.profile.ProfileFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_home,
            R.string.tab_text_memberships,
            R.string.tab_text_profile};
    private final Context mContext;

    public TabsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case 0:
                return HomeFragment.newInstance();

            case 1:
                return MyMemberhipsFragment.newInstance();

            case 2:
                return  ProfileFragment.newInstance();

        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}