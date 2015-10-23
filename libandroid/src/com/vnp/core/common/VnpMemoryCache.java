package com.vnp.core.common;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

public class VnpMemoryCache {
	private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	public Bitmap get(String name) {
		Bitmap bitmap = null;
		if (!CommonAndroid.isBlank(name)) {
			name = "" + name.hashCode();
		}

		if (!cache.containsKey(name)) {

		} else {
			SoftReference<Bitmap> ref = cache.get(name);
			bitmap = ref.get();
		}
		return bitmap;
	}

	public void put(String name, Bitmap bitmap) {
		if (!CommonAndroid.isBlank(name)) {
			name = "" + name.hashCode();
		}
		if (bitmap != null) {
			cache.put(name, new SoftReference<Bitmap>(bitmap));
		}

	}

	public void clear() {
		cache.clear();
	}
}