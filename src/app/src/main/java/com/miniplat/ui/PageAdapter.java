package com.miniplat.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentStatePagerAdapter {
    private List<BaseFragment> fragments = new ArrayList();

    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(BaseFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", fragments.size());
        fragment.setArguments(bundle);

        fragments.add(fragment);
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void onSubDestroy() {
        for (int i = 0; i < getCount(); i++) {
            BaseFragment fragment = getItem(i);
            fragment.onParentDestroy();
        }
    }
}
