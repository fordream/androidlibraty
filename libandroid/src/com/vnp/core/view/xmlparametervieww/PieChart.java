//package com.vnp.core.view.xmlparametervieww;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.util.AttributeSet;
//import android.widget.EditText;
//
//import com.ict.library.R;
//
//public class PieChart extends EditText {
//	public PieChart(Context context, AttributeSet attrs) {
//		super(context, attrs);
//
//		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0);
//		int txtType = -1;
//
//		try {
//			mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
//			mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
//			txtType = a.getInteger(R.styleable.PieChart_txtType, 0);
//		} finally {
//			a.recycle();
//		}
//
//		if (txtType > -1) {
//		}
//	}
//
//	boolean mShowText;
//	int mTextPos;
//	int txtType;
//
//	public boolean isShowText() {
//		return mShowText;
//	}
//
//	public void setShowText(boolean showText) {
//		mShowText = showText;
//		invalidate();
//		requestLayout();
//	}
//}