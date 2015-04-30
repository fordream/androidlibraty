package com.vnp.core.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.vnp.core.common.CommonAndroid;

public abstract class BaseAdialog extends Dialog {

	public BaseAdialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public BaseAdialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		mContext = context;
	}

	public void setText(int registerfacebooktwitterEdt1, String name) {
		((TextView) findViewById(registerfacebooktwitterEdt1)).setText(name);
	}

	public String getTextStr(int res) {
		return ((TextView) findViewById(res)).getText().toString().trim();
	}

	private Context mContext;

	public Context getmContext() {
		return mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		if (getLayout() != 0) {
			setContentView(getLayout());
		}
	}

	public abstract int getLayout();

}