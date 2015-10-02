package com.vnp.core.common;

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
import android.widget.ImageView;

import com.vnp.core.service.HttpsRestClient;
import com.vnp.core.service.RequestMethod;

public class ImageLoader {

	/**
	 * 
	 * @param object
	 *            : View, Activity, Dialog
	 * @param resId
	 * @param resImgBase
	 * @param url
	 * @param isRound
	 */
	public void display(Object object, int resId, int resImgBase, String url, boolean isRound, int requimentSize) {
		ImageView imgv = null;
		if (object instanceof Activity && CommonAndroid.getView((Activity) object, resId) instanceof ImageView) {
			imgv = CommonAndroid.getView((Activity) object, resId);
		}

		if (object instanceof View && CommonAndroid.getView((View) object, resId) instanceof ImageView) {
			imgv = CommonAndroid.getView((View) object, resId);
		}

		if (object instanceof Dialog && CommonAndroid.getView((Dialog) object, resId) instanceof ImageView) {
			imgv = CommonAndroid.getView((Dialog) object, resId);
		}

		if (imgv != null) {
			if (resImgBase > 0) {
				imgv.setImageResource(resImgBase);
			}

			displayImage(url, imgv, isRound, requimentSize);
		}
	}

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

		PhotoToLoad p = new PhotoToLoad(url, imageView, round, requimentSize);
		imageViews.put(imageView, p.getName());
		Bitmap bitmap = memoryCache.get(p.getName());
		if (!p.setImageBitmap(bitmap)) {
			executorService.submit(new PhotosLoader(p));
		}
	}

	/**
	 * 
	 * @param object
	 * @param resId
	 * @param resImgBase
	 * @param url
	 * @param isRound
	 */
	public void display(Object object, int resId, int resImgBase, String url, boolean isRound) {
		display(object, resId, resImgBase, url, isRound, 0);
	}

	/**
	 * 
	 * @param url
	 * @param imageView
	 * @param round
	 */

	public void displayImage(String url, ImageView imageView, boolean round) {
		displayImage(url, imageView, round, 0);
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

		String url = photoToLoad.url;
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
			LogUtils.e("TAGEXE", ex);
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

			if (photoToLoad.requimentSize > 0) {
				REQUIRED_SIZE = photoToLoad.requimentSize;
			}
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

			if (photoToLoad.isRound) {
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

		public String getName() {
			return url + isRound + requimentSize;
		}

		public boolean setImageBitmap(Bitmap bitmap) {
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
				return true;
			}

			return false;
		}

		public String url;
		public ImageView imageView;
		public boolean isRound;
		public int requimentSize;

		public PhotoToLoad(String u, ImageView i, boolean isRound, int requimentSize) {
			url = u;
			imageView = i;
			this.isRound = isRound;
			this.requimentSize = requimentSize;

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
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || tag != null && !tag.equals(photoToLoad.getName()))
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
			if (fileCache == null)
				fileCache = new VnpFileCache(context, "quare");
			if (executorService == null)
				executorService = Executors.newFixedThreadPool(50);
		}
	}
}