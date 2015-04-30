package com.vnp.core.base;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BasePagerFragmentAdapter extends FragmentPagerAdapter {
	public void setFragments(List<Fragment> fragments) {
		this.fragments = fragments;
	}

	private List<Fragment> fragments = new ArrayList<Fragment>();

	/**
	 * @param fm
	 * @param fragments
	 */
	public BasePagerFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
}