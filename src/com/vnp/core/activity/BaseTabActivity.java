/**
 * 
 */
package com.vnp.core.activity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

/**
 * class base for all activity
 * 
 * @author tvuong1pc
 * 
 */
public abstract class BaseTabActivity extends TabActivity implements
		OnTabChangeListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getLayout() != 0)
			setContentView(getLayout());
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