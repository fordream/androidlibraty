package com.vnp.core.common.imgloader.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.vnp.core.common.CommonAndroid;
import com.vnp.core.common.VnpBitmapUtils;
import com.vnp.core.common.VnpFileCache;
import com.vnp.core.common.VnpMemoryCache;
import com.vnp.core.service.HttpsRestClient;
import com.vnp.core.service.RequestMethod;

public class ImageLoader {

	/**
	 * 
	 * @param url
	 * @param imageView
	 * @param round
	 * @param requimentSize
	 */
	public void displayImage(String url, ImageView imageView, boolean round, int requimentSize) {
		if (imageView == null) {
			return;
		}
		round = true;
		PhotoToLoad p = new PhotoToLoad(url, imageView, round, requimentSize);
		imageViews.put(imageView, p.getName());
		Bitmap bitmap = memoryCache.get(p.getName());
		if (!p.setImageBitmap(bitmap)) {
			executorService.submit(new PhotosLoader(p));
		}
	}

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	private VnpMemoryCache memoryCache = new VnpMemoryCache();
	private VnpFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private Handler handler = new Handler();
	private Context context;

	private Bitmap getBitmap(PhotoToLoad photoToLoad) {
		File f = fileCache.getFile(photoToLoad.getName());

		// from SD cache
		Bitmap b = decodeFile(f, photoToLoad);
		if (b != null) {
			return b;
		}

		String url = photoToLoad.getUrl();
		try {
			if (url.startsWith("http:")) {
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
				return decodeFile(f, photoToLoad);
			} else if (url.startsWith("https:")) {
				HttpsRestClient client = new HttpsRestClient(context, url);
				return decodeFile(client.executeDownloadFile(RequestMethod.GET, f), photoToLoad);
			} else if (url != null && url.startsWith("file:///android_asset")) {
				return null;
			} else if (url != null && url.startsWith("file://")) {
				url = url.substring(url.indexOf("file://") + 7, url.length());
				return decodeFile(new File(url), photoToLoad);
			} else if (url != null && url.startsWith("content://")) {
				try {
					return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(url));
				} catch (Exception e) {
					return null;
				}
			} else {
				try {
					int contact_id = Integer.parseInt(url);
					CommonAndroid.getBitmapFromContactId(context, url, f);
					return decodeFile(f, photoToLoad);
				} catch (Exception exception) {
					Bitmap bitmap = CommonAndroid.base64ToBitmap(url);

					if (bitmap != null) {
						CommonAndroid.saveBitmapTofile(bitmap, f);
					}

					return decodeFile(f, photoToLoad);
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError) {

				memoryCache.clear();
			}
			return null;
		}
	}

	public void copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception exception) {

		}
	}

	// decodes image and scales it to reduce memory consumption
	public Bitmap decodeFile(File f, PhotoToLoad photoToLoad) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			int REQUIRED_SIZE = 200;

			if (photoToLoad.getRequimentSize() > 0) {
				REQUIRED_SIZE = photoToLoad.getRequimentSize();
			}
			int width_tmp = o.outWidth, height_tmp = o.outHeight;

			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE && height_tmp / 2 < REQUIRED_SIZE) {
					// scale *= 2;
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();

			if (photoToLoad.isRound()) {
				int widthForScale = bitmap.getWidth();
				widthForScale = 100;
				bitmap = VnpBitmapUtils.createScaledBitmap(bitmap, widthForScale, widthForScale, ScalingLogic.CROP);
				bitmap = VnpBitmapUtils.getRoundedCornerBitmap(context, bitmap, bitmap.getWidth() / 2, true, true, true, true);
			}

			return bitmap;
		} catch (Exception e) {
		}
		return null;
	}

	class PhotosLoader implements Runnable {

		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				Bitmap bmp = getBitmap(photoToLoad);
				memoryCache.put(photoToLoad.getName(), bmp);
				if (imageViewReused(photoToLoad)) {
					return;
				}

				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.getImageView());
		if (tag == null || tag != null && !tag.equals(photoToLoad.getName())) {
			return true;
		}
		return false;
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)) {
				return;
			}
			photoToLoad.setImageBitmap(bitmap);
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

	public void updateContext(Context context2) {
		if (context == null) {
			context = context2;
		}
	}

	/**
	 * 
	 */
	private static ImageLoader instance = new ImageLoader();

	private ImageLoader() {

	}

	public static ImageLoader getInstance(Context context) {
		instance.init(context);
		return instance;
	}

	private void init(Context xcontext) {
		if (this.context == null && xcontext != null) {
			this.context = xcontext;
			if (fileCache == null) {
				fileCache = new VnpFileCache(context, "img");
			}

			if (executorService == null) {
				executorService = Executors.newFixedThreadPool(5);
			}
		}
	}
}