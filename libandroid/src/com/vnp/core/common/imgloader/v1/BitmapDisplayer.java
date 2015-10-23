package com.vnp.core.common.imgloader.v1;

import android.graphics.Bitmap;

public class BitmapDisplayer {

	Bitmap bitmap;
	PhotoToLoad photoToLoad;

	public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
		bitmap = b;
		photoToLoad = p;
	}

	public void run() {
		// if (imageViewReused(photoToLoad)) {
		// return;
		// }

		photoToLoad.setImageBitmap(bitmap);
	}

}
