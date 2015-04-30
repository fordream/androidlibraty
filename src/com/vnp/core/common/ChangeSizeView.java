package com.vnp.core.common;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.ict.library.R;

public class ChangeSizeView {
	private static ChangeSizeView instance = new ChangeSizeView();

	public boolean hasView(View view) {
		return views.contains(view);
	}

	public static ChangeSizeView getInstance() {

		if (instance == null)
			instance = new ChangeSizeView();
		return instance;
	}

	private List<View> views = new ArrayList<View>();

	public void startChangLayoutSize(int fromY, final int toY, final View view) {
		if (views.contains(view)) {
			return;
		} else {
			views.add(view);
			changLayoutSize(fromY, toY, view);
		}
	}

	/**
	 * 
	 * @param fromY
	 * @param toY
	 * @param view
	 */
	private void changLayoutSize(int fromY, final int toY, final View view) {
		if (fromY < 0 || toY < 0) {
			views.remove(view);
			return;
		}

		int dy = (int) view.getContext().getResources().getDimension(R.dimen.dimen_3dp);
		if (fromY < toY) {
			fromY = fromY + dy;

			if (fromY > toY) {
				fromY = toY;
			}
		} else if (fromY > toY) {
			fromY = fromY - dy;

			if (fromY < toY) {
				fromY = toY;
			}
		}

		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = fromY;
		view.setLayoutParams(layoutParams);

		if (Math.abs(toY - fromY) > 0) {
			final int newFromY = fromY;
			view.postDelayed(new Runnable() {

				@Override
				public void run() {
					changLayoutSize(newFromY, toY, view);
				}
			}, 10);
		} else {
			views.remove(view);
		}
	}

	public ChangeSizeView() {
	}

}
