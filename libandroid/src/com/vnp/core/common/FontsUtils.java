package com.vnp.core.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontsUtils {
	private static FontsUtils utils = new FontsUtils();
	private Context context;
	private Map<String, Typeface> map = new HashMap<String, Typeface>();

	public static FontsUtils getInstance() {
		if (utils == null) {
			utils = new FontsUtils();
		}

		return utils;
	}

	public static final String TOBOTO_LIGHT = "Roboto-Light.ttf";
	public static final String TOBOTO_Regular = "Roboto-Regular.ttf";
	public static final String TOBOTO_Medium = "Roboto-Medium.ttf";
	public static final String TOBOTO_Thin = "Roboto-Thin.ttf";

	public void setTextFontsRobotoLight(TextView textView) {
		if (map.containsKey(TOBOTO_LIGHT))
			textView.setTypeface(map.get(TOBOTO_LIGHT));
	}

	public void setTextFontsRobotoRegular(TextView textView) {
		if (map.containsKey(TOBOTO_Regular))
			textView.setTypeface(map.get(TOBOTO_Regular));
	}

	public void setTextFontsRobotoMedium(TextView textView) {
		if (map.containsKey(TOBOTO_Medium))
			textView.setTypeface(map.get(TOBOTO_Medium));
	}

	public void setTextFontsRobotoThin(TextView textView) {
		if (map.containsKey(TOBOTO_Thin))
			textView.setTypeface(map.get(TOBOTO_Thin));
	}

	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
		}

		addFont(TOBOTO_LIGHT);
		addFont(TOBOTO_Regular);
		addFont(TOBOTO_Medium);
		addFont(TOBOTO_Thin);
	}

	private void addFont(String fontName) {
		if (!map.containsKey(fontName)) {
			Typeface typeface = setTypefaceFromAsset(String.format("fonts/%s", fontName));
			if (typeface != null) {
				map.put(fontName, typeface);
			}
		}
	}

	private FontsUtils() {

	}

	public Typeface setTypefaceFromAsset(String fileAsset) {
		try {
			AssetManager assertManager = context.getAssets();
			Typeface tf = Typeface.createFromAsset(assertManager, fileAsset);
			return tf;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * <?xml version="1.0" encoding="utf-8"?> <resources> <style
	 * name="MyAppTheme" parent="@android:style/Theme.Holo.Light"> <item
	 * name="android:typeface">serif</item> </style> </resources>
	 */
	/**
	 * 
	 * @param defaultFontNameToOverride
	 *            typeface SERIF NORMAL(DEFAULT) SANS MONOSPACE
	 * @param customFontFileNameInAssets
	 *            typeface "fonts/Roboto-Regular.ttf"
	 *            textStyle???????????????????????????
	 */
	public boolean onCreateForChangeDefault(String defaultFontNameToOverride, String customFontFileNameInAssets) {
		boolean success = false;
		// "SERIF", "fonts/Roboto-Regular.ttf"
		try {
			final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

			final java.lang.reflect.Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
			defaultFontTypefaceField.setAccessible(true);
			defaultFontTypefaceField.set(null, customFontTypeface);
			success = true;
		} catch (Exception e) {
			LogUtils.e("fontsetup", "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
			success = false;
		}

		return success;
	}

}
