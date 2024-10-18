package com.habibur.breakdown_assistance.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.habibur.breakdown_assistance.ui.fragments.HomeFragment;
import com.habibur.breakdown_assistance.ui.fragments.LocationFragment;
import com.habibur.breakdown_assistance.ui.fragments.PanelFragment;
import com.habibur.breakdown_assistance.ui.fragments.ProfileFragment;
import com.habibur.breakdown_assistance.ui.fragments.ServicesFragment;

public class FragmentUtils {

    public static String getTopFragment(FragmentManager fragmentManager) {
        String[] fragment = {null};

        int last = fragmentManager.getFragments().size() - 1;

        if (last >= 0) {
            Fragment topFragment = fragmentManager.getFragments().get(last);

            if (topFragment instanceof HomeFragment)
                fragment[0] = HomeFragment.class.getSimpleName();
            else if (topFragment instanceof ServicesFragment)
                fragment[0] = ServicesFragment.class.getSimpleName();
            else if (topFragment instanceof LocationFragment)
                fragment[0] = LocationFragment.class.getSimpleName();
            else if (topFragment instanceof PanelFragment)
                fragment[0] = PanelFragment.class.getSimpleName();
            else if (topFragment instanceof ProfileFragment)
                fragment[0] = ProfileFragment.class.getSimpleName();
        }

        return fragment[0];
    }
}
