/**
 * 
 */
package com.vnp.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author tvuong1pc
 * 
 */
public abstract class CustomLinearLayoutView extends LinearLayout {
	private Object data;

	/**
	 * 
	 * @param isShowHeader
	 */
	public abstract void showHeader(boolean isShowHeader);

	/**
	 * @param context
	 */
	public CustomLinearLayoutView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CustomLinearLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(int res) {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(res, this);

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

	public void setData(Object data) {
		// todo
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public abstract void setGender();

}