package com.vnp.core.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

/**
 * 
 * @author acv-dev-android-02
 * 
 */
public class Emoticons {
	private Emoticons() {
	}

	private Context context;

	private Map<String, Bitmap> map = new HashMap<String, Bitmap>();
	public static Emoticons instance = new Emoticons();

	public static Emoticons getInstance() {
		return instance;
	}

	public void load(Context context) {
		this.context = context;
	}

	public boolean load(String fileAsset) {
		boolean loadSucces = false;
		if (!map.containsKey(fileAsset)) {
			Bitmap bitmap = CommonAndroid.getBitmapFromAsset(fileAsset, context);
			if (bitmap != null) {
				map.put(fileAsset, bitmap);
				loadSucces = true;
			}
		} else {
			loadSucces = true;
		}

		return loadSucces;
	}

	public void showEmoticons(TextView textView) {
		LogUtils.e("textxxx", map.keySet().toString());
	}
}