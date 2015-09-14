package com.vnp.core.common;

import java.io.File;

import android.content.Context;

public class VnpFileCache {
	private File cacheDir;

	public VnpFileCache(Context context) {
		String path = "Android/data/" + context.getPackageName() + "/LazyList";
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), path);
		} else {
			cacheDir = context.getCacheDir();
		}

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	public File getFile(String url) {

		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		for (File f : files) {
			f.delete();
		}
	}
}
