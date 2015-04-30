package com.vnp.core.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vnp.core.view.CustomLinearLayoutView;

public class BaseViewPagerAdapter extends PagerAdapter {
	List<Object> list = new ArrayList<Object>();

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		Object data = list.get(position);
		CustomLinearLayoutView view = getView(collection.getContext(), data);
		view.setData(data);
		view.setGender();
		((ViewPager) collection).addView(view);
		return view;
	}

	public CustomLinearLayoutView getView(Context context, Object data) {
		return new CustomLinearLayoutView(context) {

			@Override
			public void showHeader(boolean isShowHeader) {

			}

			@Override
			public void setGender() {

			}

		};
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	public void setListData(List<Object> list2) {
		list.addAll(list2);
	}

}