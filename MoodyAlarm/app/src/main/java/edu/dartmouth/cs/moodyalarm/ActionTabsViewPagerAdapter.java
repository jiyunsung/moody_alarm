package edu.dartmouth.cs.moodyalarm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by vivianjiang on 2/26/18.
 */

public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public static final int WEATHER = 0;
    public static final int DAY = 1;

    public static final String UI_TAB_WEATHER = "WEATHER";
    public static final String UI_TAB_DAY = "DAY";


    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    public Fragment getItem(int pos){
        return fragments.get(pos);
    }

    public int getCount(){
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case WEATHER:
                return UI_TAB_WEATHER;
            case DAY:
                return UI_TAB_DAY;
            default:
                break;
        }
        return null;
    }
}


