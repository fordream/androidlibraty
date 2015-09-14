package com.vnp.core.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vnp.core.service.HttpsRestClient;
import com.vnp.core.service.RequestMethod;

import android.content.Context;

public class ImageLoaderToCache {
	private List<String> loading = new ArrayList<String>();
	private ExecutorService executorService;

	public void load(String url) {

		if (!CommonAndroid.isBlank(url) && !loading.contains(url)) {
			loading.add(url);
			executeLoadUrl(url);
		}
	}

	private void executeLoadUrl(final String url) {
		File f = vnpFileCache.getFile(url);
		if (f.exists()) {
			return;
		}
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				getitmap(url);
				loading.remove(url);
			}
		});
	}

	private void getitmap(String url) {
		File f = vnpFileCache.getFile(url);
		if (!f.exists()) {
			if (url.startsWith("http:")) {
				try {
					URL imageUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
					conn.setConnectTimeout(30000);
					conn.setReadTimeout(30000);
					conn.setInstanceFollowRedirects(true);
					InputStream is = conn.getInputStream();
					OutputStream os = new FileOutputStream(f);
					CopyStream(is, os);
					is.close();
					os.close();
				} catch (Exception exception) {
					f.delete();
				}
			} else if (url.startsWith("https:")) {
				HttpsRestClient client = new HttpsRestClient(context, url);
				client.executeDownloadFile(RequestMethod.GET, f);
			}

		}
	}

	private void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	private VnpFileCache vnpFileCache;
	private Context context;

	private void init(Context context) {
		if (context != null) {
			this.context = context;
			vnpFileCache = new VnpFileCache(context);
		}

		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(5);
		}
	}

	public static ImageLoaderToCache instance(Context context) {
		instance.init(context);
		return instance;
	}

	private static ImageLoaderToCache instance = new ImageLoaderToCache();

	private ImageLoaderToCache() {
	}

}