package com.aretha.test;

import android.app.TabActivity;
import android.os.Bundle;

import com.aretha.slidemenu.SlideMenu;
import com.ict.library.R;

public class BaseMusicSlideMenuActivity extends TabActivity {
	private SlideMenu mSlideMenu;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mactivity_slidemenu_day);
	}

	public void openMenuLeft() {
		getSlideMenu().open(false, true);
	}

	public void openMenuRight() {
		getSlideMenu().open(true, true);
	}

	public SlideMenu getSlideMenu() {
		return mSlideMenu;
	}

}
