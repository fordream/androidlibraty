/**
 * 
 */
package com.vnp.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.ict.library.R;
import com.vnp.core.base.BaseFragment;

/**
 * class base for all activity
 * 
 * @author tvuong1pc
 * 
 */
public abstract class BaseTabFragmentActivity extends FragmentActivity
		implements OnTabChangeListener {
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getLayout() != 0)
			setContentView(getLayout());
		// setContentView(R.layout.tabsfragment);

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

	}

	public void addTab(String tabSpec, String indicator,
			Class<BaseFragment> baseFragment) {
		mTabHost.addTab(mTabHost.newTabSpec(tabSpec).setIndicator(indicator),
				baseFragment, null);
	}

	public void addTab(String tabSpec, View indicator,
			Class<BaseFragment> baseFragment) {
		mTabHost.addTab(mTabHost.newTabSpec(tabSpec).setIndicator(indicator),
				baseFragment, null);
	}

	public abstract int getLayout();


	@Override
	protected void onResume() {
		super.onResume();

	}

	/**
	 * convert view from resource
	 * 
	 * @param res
	 * @return
	 */
	public <T extends View> T getView(int res) {
		@SuppressWarnings("unchecked")
		T view = (T) findViewById(res);
		return view;
	}

	public TabHost getTabHost() {
		return (TabHost) findViewById(android.R.id.tabhost);
	}

	public void addTab(Class<?> activity, String tabSpect, String indicator) {
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec firstTabSpec = tabHost.newTabSpec(tabSpect);
		firstTabSpec.setIndicator(indicator).setContent(
				new Intent(this, activity));
		tabHost.addTab(firstTabSpec);
	}

	public void addTab(Class<?> activity, String tabSpect, View indicator) {
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec firstTabSpec = tabHost.newTabSpec(tabSpect);
		firstTabSpec.setIndicator(indicator).setContent(
				new Intent(this, activity));
		tabHost.addTab(firstTabSpec);
	}

	protected Context getContext() {
		return this;
	}

	protected Activity getActivity() {
		return this;
	}

	@Override
	public void onTabChanged(String tabId) {

	}

}