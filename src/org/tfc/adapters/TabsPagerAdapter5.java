package org.tfc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class TabsPagerAdapter5 extends FragmentPagerAdapter {

    public TabsPagerAdapter5(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                // AdminList fragment activity
                return new SubsDataFragment();
            case 1:
                // SubsList fragment activity
                return new SubsEventUserListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
