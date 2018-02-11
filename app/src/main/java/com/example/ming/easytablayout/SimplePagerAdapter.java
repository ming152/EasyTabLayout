package com.example.ming.easytablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ming on 2018/2/11.
 */

public class SimplePagerAdapter extends FragmentPagerAdapter {
    List<SimpleFragment> fragments;
    private String[] mTabTitle;
    public SimplePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments == null ? null : fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTabTitle == null || position >= mTabTitle.length) {
            return super.getPageTitle(position);
        }
        return mTabTitle[position];
    }

    public void setFragmentList(List<SimpleFragment> fragmentList){
        this.fragments = fragmentList;
    }

    public void setTabTitle(String[] tabTitle) {
        this.mTabTitle = tabTitle;
    }
}
