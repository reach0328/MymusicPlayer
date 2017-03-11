package com.android.jh.mymusicplayer.util.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final int TAB_COUNT = 4;

    List<Fragment> fragmentList = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fm) {
        fragmentList.add(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment =null;
        switch (position){
            case 0 : fragment = fragmentList.get(0); break;
            case 1 : fragment = fragmentList.get(1); break;
            case 2 : fragment = fragmentList.get(2); break;
            case 3 : fragment = fragmentList.get(3); break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}