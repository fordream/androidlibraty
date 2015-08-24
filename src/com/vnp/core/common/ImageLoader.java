package com.vnp.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ict.library.R;
import com.vnp.core.service.HttpsRestClient;
import com.vnp.core.service.RequestMethod;

public class ImageLoader {
	public void display(Activity activityOrViewOrDialod, int resId, int resImgBase, String url, boolean isRound) {
		ImageView imgv = null;
		if (CommonAndroid.getView(activityOrViewOrDialod, resId) instanceof ImageView) {
			imgv = CommonAndroid.getView(activityOrViewOrDialod, resId);
		}

		if (imgv != null) {
			if (resImgBase > 0) {
				imgv.setImageResource(resImgBase);
			}
			displayImage(url, imgv, isRound);
		}
	}

	public void display(View activityOrViewOrDialod, int resId, int resImgBase, String url, boolean isRound) {
		ImageView imgv = null;
		if (CommonAndroid.getView(activityOrViewOrDialod, resId) instanceof ImageView) {
			imgv = CommonAndroid.getView(activityOrViewOrDialod, resId);
		}

		if (imgv != null) {
			if (resImgBase > 0) {
				imgv.setImageResource(resImgBase);
			}
			displayImage(url, imgv, isRound);
		}
	}

	public void display(Dialog activityOrViewOrDialod, int resId, int resImgBase, String url, boolean isRound) {
		ImageView imgv = null;
		if (CommonAndroid.getView(activityOrViewOrDialod, resId) instanceof ImageView) {
			imgv = CommonAndroid.getView(activityOrViewOrDialod, resId);
		}

		if (imgv != null) {
			if (resImgBase > 0) {
				imgv.setImageResource(resImgBase);
			}
			displayImage(url, imgv, isRound);
		}
	}

	public void displayImageCover(String url, ImageView imageView) {
		if (!CommonAndroid.isBlank(url)) {
			displayImage(url, imageView, false);
		}
	}

	public void showLogoDichvu(final ImageView menu_right_item_img_icon, final String avatar) {
		menu_right_item_img_icon.setImageResource(R.drawable.btn_crop_operator);
		displayImage(avatar, menu_right_item_img_icon, false);

	}

	public void showLogoTinTuc(ImageView tintuc_item_img_icon, String images) {
		tintuc_item_img_icon.setImageResource(R.drawable.btn_crop_operator);
		displayImage(images, tintuc_item_img_icon, false);
	}

	// **

	public void displayImageInfor(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.btn_crop_operator);
		if (!CommonAndroid.isBlank(url)) {
			displayImage(url, imageView, true);
		}
	}

	public void showAvatarContact(final ImageView menu_right_item_img_icon, final String avatar, final String contact_id, int resAvatar) {

		menu_right_item_img_icon.setImageResource(resAvatar);
		if (CommonAndroid.isBlank(avatar)) {
			if (CommonAndroid.isBlank(contact_id)) {
			} else {
				displayImage(contact_id, menu_right_item_img_icon, true);
			}
		} else {
			displayImage(avatar, menu_right_item_img_icon, true);
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
			fileCache = new FileCache(context);
			MAX_WIDTH = (int) context.getResources().getDimension(R.dimen.dimen_100dp);
			executorService = Executors.newFixedThreadPool(5);
		}
	}

	private MemoryCache memoryCache = new MemoryCache();
	private FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private Handler handler = new Handler();// handler to display images in UI
											// thread
	private Context context;
	private static int MAX_WIDTH = 150;

	final int stub_id = 0;

	private Map<String, String> roundMap = new HashMap<String, String>();

	public void displayImage(String url, ImageView imageView, boolean round) {

		if (round && !CommonAndroid.isBlank(url)) {
			if (roundMap.containsKey(url)) {
				roundMap.remove(url);
			}

			roundMap.put(url, "");
		}

		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, imageView);
			if (stub_id != 0) {
				imageView.setImageResource(stub_id);
			}
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {

		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f, url);
		if (b != null) {
			return b;
		}

		// from web
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
				return decodeFile(f, url);
			} else if (url.startsWith("https:")) {
				HttpsRestClient client = new HttpsRestClient(context, url);
				return decodeFile(client.executeDownloadFile(RequestMethod.GET, f), url);
			} else if (url != null && url.startsWith("file:///android_asset")) {
				return null;
			} else if (url != null && url.startsWith("file://")) {
				url = url.substring(url.indexOf("file://") + 7, url.length());
				return decodeFile(new File(url), url);
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
					return decodeFile(f, url);
				} catch (Exception exception) {
					// base 64
					Bitmap bitmap = CommonAndroid.base64ToBitmap(url);

					if (bitmap != null) {
						CommonAndroid.saveBitmapTofile(bitmap, f);
					}

					// TODO
					return decodeFile(f, url);
				}

				// copy(new File(url), f);
				// return decodeFile(f, url);
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
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
	public Bitmap decodeFile(File f, String url) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 480;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE | height_tmp / 2 < REQUIRED_SIZE)
					break;
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
			if (roundMap.containsKey(url)) {
				bitmap = createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getWidth(), ScalingLogic.CROP);
				bitmap = getRoundedCornerBitmap(context, bitmap, bitmap.getWidth() / 2, true, true, true, true);
			}
			return bitmap;
		} catch (Exception e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
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
				Bitmap bmp = getBitmap(photoToLoad.url);
				memoryCache.put(photoToLoad.url, bmp);
				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else {
				if (stub_id != 0)
					photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
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

	// ---------------------------------------------------------
	// CLASS
	// ---------------------------------------------------------

	public class FileCache {

		private File cacheDir;

		public FileCache(Context context) {
			// Find the dir to save cached images
			String path = "Android/data/" + context.getPackageName() + "/LazyList";
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				// LazyList
				cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), path);
			} else {
				cacheDir = context.getCacheDir();
			}
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}

		public File getFile(String url) {
			// I identify images by hashcode. Not a perfect solution, good for
			// the demo.
			String filename = String.valueOf(url.hashCode());
			File f = new File(cacheDir, filename);
			return f;

		}

		public void clear() {
			File[] files = cacheDir.listFiles();
			for (File f : files)
				f.delete();
		}

	}

	public class MemoryCache {
		private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

		public Bitmap get(String id) {
			if (!cache.containsKey(id))
				return null;
			SoftReference<Bitmap> ref = cache.get(id);
			return ref.get();
		}

		public void put(String id, Bitmap bitmap) {
			cache.put(id, new SoftReference<Bitmap>(bitmap));
		}

		public void clear() {
			cache.clear();
		}
	}

	public void updateContext(Context context2) {
		if (context == null) {
			context = context2;
		}
	}

	public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
		Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
		Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
		return scaledBitmap;
	}

	public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		return new Rect(0, 0, srcWidth, srcHeight);
	}

	public static enum ScalingLogic {
		CROP, FIT
	}

	public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		if (scalingLogic == ScalingLogic.FIT) {
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;
			return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
		} else {
			return new Rect(0, 0, dstWidth, dstHeight);
		}
	}

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int roundDip, boolean roundTL, boolean roundTR, boolean roundBL, boolean roundBR) {
		try {
			int w_default = bitmap.getWidth();
			int h_default = bitmap.getHeight();
			// Log.e("image_size1", "w=" + w_default + "::h=" + h_default);
			int w = w_default;
			int h = h_default;
			Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, w, h);
			final RectF rectF = new RectF(rect);
			final float roundPx = convertDipToPixels(roundDip, context);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// draw round
																	// 4Corner

			if (!roundTL) {
				Rect rectTL = new Rect(0, 0, w / 2, h / 2);
				canvas.drawRect(rectTL, paint);
			}
			if (!roundTR) {
				Rect rectTR = new Rect(w / 2, 0, w, h / 2);
				canvas.drawRect(rectTR, paint);
			}
			if (!roundBR) {
				Rect rectBR = new Rect(w / 2, h / 2, w, h);
				canvas.drawRect(rectBR, paint);
			}
			if (!roundBL) {
				Rect rectBL = new Rect(0, h / 2, w / 2, h);
				canvas.drawRect(rectBL, paint);
			}

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static int convertDipToPixels(float dips, Context appContext) {
		return (int) (dips * appContext.getResources().getDisplayMetrics().density + 0.5f);
	}
}