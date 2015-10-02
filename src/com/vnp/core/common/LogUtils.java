package com.vnp.core.common;

import android.util.Log;

public class LogUtils {
	public static boolean ENABLE = true;

	public static void e(String tag, String messgae) {
		if (ENABLE) {
			Log.e(tag, "-------------------Start----------------------");
			Log.e(tag, messgae + "");
			Log.e(tag, "-------------------End----------------------");
		}
	}

	public static void e(String tag, Exception e) {
		if (ENABLE) {
			Log.e(tag, "-------------------Start----------------------");
			Log.e(tag, "error", e);
			Log.e(tag, "-------------------End----------------------");
		}
	}

	public static void e(String tag, Throwable e) {
		if (ENABLE) {
			Log.e(tag, "-------------------Start----------------------");
			Log.e(tag, "error", e);
			Log.e(tag, "-------------------End----------------------");
		}
	}

	public static void d(String tag, String messgae) {
		if (ENABLE) {
			Log.d(tag, "-------------------Start----------------------");
			Log.d(tag, messgae + "");
			Log.d(tag, "-------------------End----------------------");
		}
	}

	public static void d(String tag, Exception e) {
		if (ENABLE) {
			Log.d(tag, "-------------------Start----------------------");
			Log.d(tag, "error", e);
			Log.d(tag, "-------------------End----------------------");
		}
	}

	public static void i(String tag, String messgae) {
		if (ENABLE) {
			Log.i(tag, "-------------------Start----------------------");
			Log.i(tag, messgae + "");
			Log.i(tag, "-------------------End----------------------");
		}
	}

	public static void i(String tag, Exception e) {
		if (ENABLE) {
			Log.i(tag, "-------------------Start----------------------");
			Log.i(tag, "error", e);
			Log.i(tag, "-------------------End----------------------");
		}
	}

	public static void v(String tag, String messgae) {
		if (ENABLE) {
			Log.v(tag, "-------------------Start----------------------");
			Log.v(tag, messgae + "");
			Log.v(tag, "-------------------End----------------------");
		}
	}

	public static void v(String tag, Exception e) {
		if (ENABLE) {
			Log.v(tag, "-------------------Start----------------------");
			Log.v(tag, "error", e);
			Log.v(tag, "-------------------End----------------------");
		}
	}

	public static void wtf(String tag, String messgae) {
		if (ENABLE) {
			Log.wtf(tag, "-------------------Start----------------------");
			Log.wtf(tag, messgae + "");
			Log.wtf(tag, "-------------------End----------------------");
		}
	}

	public static void wtf(String tag, Exception e) {
		if (ENABLE) {
			Log.wtf(tag, "-------------------Start----------------------");
			Log.wtf(tag, "error", e);
			Log.wtf(tag, "-------------------End----------------------");
		}
	}

}
