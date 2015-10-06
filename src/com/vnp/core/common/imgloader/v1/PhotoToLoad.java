package com.vnp.core.common.imgloader.v1;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class PhotoToLoad {

	public String getName() {
		return url + imageLoaderTypeConvert + requimentSize;
	}

	public boolean setImageBitmap(final Bitmap bitmap) {
		if (imageView != null && bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return true;
		}

		return false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public ImageLoaderTypeConvert getImageLoaderTypeConvert() {
		return imageLoaderTypeConvert;
	}

	public void setImageLoaderTypeConvert(ImageLoaderTypeConvert imageLoaderTypeConvert) {
		this.imageLoaderTypeConvert = imageLoaderTypeConvert;
	}

	public int getRequimentSize() {
		return requimentSize;
	}

	public void setRequimentSize(int requimentSize) {
		this.requimentSize = requimentSize;
	}

	private String url;
	private ImageView imageView;
	private ImageLoaderTypeConvert imageLoaderTypeConvert;
	private int requimentSize;

	public PhotoToLoad(String u, ImageView i, ImageLoaderTypeConvert imageLoaderTypeConvert, int requimentSize) {
		url = u;
		imageView = i;
		this.imageLoaderTypeConvert = imageLoaderTypeConvert;
		this.requimentSize = requimentSize;

	}
}
