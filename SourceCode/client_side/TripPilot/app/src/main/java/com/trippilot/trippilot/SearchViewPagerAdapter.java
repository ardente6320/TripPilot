package com.trippilot.trippilot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by arden on 2018-09-20.
 */

public class SearchViewPagerAdapter extends FragmentStatePagerAdapter {
    String data[] ={"장소","플랜"};
    SearchedLocationListView locViewFragment;
    SearchedPlanListView plViewFragment;
    int type;
    public SearchViewPagerAdapter(FragmentManager fm, int type) {
        super(fm);
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(locViewFragment ==null) {
                    locViewFragment = new SearchedLocationListView();
                    locViewFragment.setData(type,null);
                }
                return locViewFragment;
            case 1:
                if(plViewFragment == null) {
                    plViewFragment = new SearchedPlanListView();
                    plViewFragment.setType(type);
                }
                return plViewFragment;
        }
        return null;
    }
    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data[position];
    }
}
