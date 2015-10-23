package com.vnp.core.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;

public class BaseApplication extends Application {
	private HashMap<View, Bitmap> hashMap = new HashMap<View, Bitmap>();
	private HashMap<String, Bitmap> hashMapPath = new HashMap<String, Bitmap>();

	@Override
	public void onCreate() {
		super.onCreate();

	}

	// =============Bitmap=================
	private void unloadBackground(View view) {
		view.setBackgroundDrawable(null);
		Bitmap bitmap = hashMap.get(view);
		if (bitmap != null) {
			bitmap.recycle();
		}

		hashMap.remove(view);
	}

	public void setBackgroundDrawableFromResource(final View view,
			final int sourceid) {

		new AsyncTask<String, String, Object>() {
			Bitmap bitmap;

			@Override
			protected Object doInBackground(String... params) {
				Bitmap bitmap2 = hashMapPath.get(sourceid + "");

				if (bitmap2 != null && !bitmap2.isRecycled()) {
					bitmap = bitmap2;
					return null;
				} else if (bitmap2 != null && bitmap2.isRecycled()) {
					hashMapPath.remove(sourceid + "");
				}

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory
						.decodeStream(getResources().openRawResource(sourceid),
								null, options);

				options.inSampleSize = ((options.outWidth > 100) ? (options.outWidth / 100 + 1)
						: 1);
				options.inJustDecodeBounds = false;

				bitmap = BitmapFactory.decodeStream(getResources()
						.openRawResource(sourceid), null, options);

				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				if (bitmap != null) {
					unloadBackground(view);
					hashMap.put(view, bitmap);
					hashMapPath.put(sourceid + "", bitmap);
				}
				view.setBackgroundDrawable(new BitmapDrawable(bitmap));
			}
		}.execute("");

	}


	public void setBackgroundDrawableFromSdcard(View view, String pathSdcard) {

		InputStream in1 = null;
		try {
			File tmp = new File(pathSdcard);
			in1 = new FileInputStream(tmp);
			if (pathSdcard.endsWith(".nomedia")) {
				in1.read(tmp.getName().replace(".nomedia", "").getBytes());
			}

			BitmapFactory.Options o = new BitmapFactory.Options();

			Bitmap bitmap = BitmapFactory.decodeStream(in1, null, o);
			if (bitmap != null) {
				bitmap = Bitmap.createScaledBitmap(bitmap,
						bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
			}

			if (bitmap != null) {
				unloadBackground(view);
				hashMap.put(view, bitmap);
			}

			view.setBackgroundDrawable(new BitmapDrawable(bitmap));
		} catch (final Exception e) {
			view.setBackgroundDrawable(null);
		}
	}

	public void clearCache(View viewGroup) {
		unloadBackground(viewGroup);
		if (viewGroup instanceof ViewGroup) {
			int childCount = ((ViewGroup) viewGroup).getChildCount();
			for (int i = 0; i < childCount; i++) {
				View view = ((ViewGroup) viewGroup).getChildAt(i);
				unloadBackground(view);
			}
		}
	}

	// =============End Bitmap=================
	public void killApplication() {
		killApplication(android.os.Process.myPid());
	}

	public void killApplication(int id) {
		android.os.Process.killProcess(id);
	}


}