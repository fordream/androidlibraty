package com.vnp.core.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

@SuppressWarnings("deprecation")
public class BaseGroupActivity extends ActivityGroup {
	private boolean canFinish = true;

	// Keep this in a static variable to make it accessible for all the nested
	// activities, lets them manipulate the view
	// public static FirstGroup group;

	public boolean isCanFinish() {
		return canFinish;
	}

	public void setCanFinish(boolean canFinish) {
		this.canFinish = canFinish;
	}

	private FrameLayout frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		setContentView(R.layout.group_main);
//		frame = (FrameLayout) findViewById(R.id.frame);
//		addView("MainActivity", MainActivity.class, null);
//		addView("s", Main1Activity.class, null);
	}

	// Need to keep track of the history if you want the back-button to work
	// properly, don't use this if your activities requires a lot of memory.
	private List<View> history = new ArrayList<View>();

	public void addView(String name, Class<?> activity, Bundle extras) {
		replaceView(createView(name, activity, extras));
	}

	private View createView(String name, Class<?> activity, Bundle extras) {
		Intent intent = new Intent(this, activity);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		if (extras != null) {
			intent.putExtras(extras);
		}

		View view = getLocalActivityManager().startActivity(name, intent).getDecorView();
		return view;
	}

	private void replaceView(View v) {
		// Adds the old one to history
		history.add(v);
		// Changes this Groups View to the new View.
		if (frame == null)
			setContentView(v);
		else
			frame.addView(v);
	}

	public void onBackPressed() {
		if (history.size() > 1) {
			View currentView = history.get(history.size() - 1);
			history.remove(history.size() - 1);
			View view = history.get(history.size() - 1);
			if (frame == null)
				setContentView(view);
			else {
				frame.removeView(currentView);
				// frame.addView(view);
			}
		} else {
			if (canFinish) {
				finish();
			} else {
				Activity activity = getParent();
				if (activity != null) {
					activity.onBackPressed();
				}
			}
		}
	}

	public void onBackPressed(Bundle extras, String actionForSendBoardCast) {
		onBackPressed();
		if (actionForSendBoardCast != null) {
			Intent intent = new Intent(actionForSendBoardCast);
			if (extras != null) {
				intent.putExtras(extras);
			}

			sendBroadcast(intent);
		}

	}

	protected Context getContext() {
		return this;
	}

	protected Activity getActivity() {
		return this;
	}
}