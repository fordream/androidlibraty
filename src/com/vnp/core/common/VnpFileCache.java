package com.vnp.core.common;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class VnpFileCache {
	private File cacheDir;

	public VnpFileCache(Context context, String nameCahes) {
		String path = "Android/data/" + context.getPackageName() + "/LazyList/" + nameCahes;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), path);
		} else {
			cacheDir = context.getCacheDir();
		}

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		/**
		 * add new by tvuong
		 */

		pathFileExternalMemory = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/files/";
		pathCacheExternalMemory = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/cache/";
		pathFileInternalMemory = context.getFilesDir().getPath() + "/";

	}

	public File getFile(String url) {

		String filename = String.valueOf(url.hashCode())+".png";
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		for (File f : files) {
			f.delete();
		}
	}

	/**
	 * add new by tvuong
	 */
	/** 1MB block. */
	private static final int MB = 1048576;
	private String pathFileExternalMemory = "";
	private String pathCacheExternalMemory = "";
	private String pathFileInternalMemory = "";

	public String getPathFileExternalMemory() {
		return pathFileExternalMemory;
	}

	public String getPathCacheExternalMemory() {
		return pathCacheExternalMemory;
	}

	public String getPathFileInternalMemory() {
		return pathFileInternalMemory;
	}

	public boolean checkSDAvaiable(String path) {
		boolean isSDCardExisted = false;
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}

		File file = new File(path, "test" + System.currentTimeMillis() + ".txt");

		try {
			isSDCardExisted = file.createNewFile();
		} catch (IOException e1) {
			isSDCardExisted = false;
		}
		file.delete();
		return isSDCardExisted;
	}

	public double checkSDFreeMB() {

		if (!checkSDAvaiable(getPathCacheExternalMemory())) {
			return 0;
		} else {
			StatFs stat = new StatFs(getPathCacheExternalMemory());
			double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();

			return sdAvailSize / MB;
		}
	}

	public long avaiableInternalStoreMemorySize() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
		return (bytesAvailable / 1048576);
	}
}
