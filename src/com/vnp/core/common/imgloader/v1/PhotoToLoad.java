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
		} else if (resDefault > 0) {
			imageView.setImageResource(resDefault);
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

	public int getResDefault() {
		return resDefault;
	}

	public void setResDefault(int resDefault) {
		this.resDefault = resDefault;
	}

	private String url;
	private ImageView imageView;
	private ImageLoaderTypeConvert imageLoaderTypeConvert;
	private int requimentSize;
	private int resDefault = 0;

	public PhotoToLoad(String u, ImageView i, ImageLoaderTypeConvert imageLoaderTypeConvert, int requimentSize, int resDefault) {
		url = u;
		imageView = i;
		this.imageLoaderTypeConvert = imageLoaderTypeConvert;
		this.requimentSize = requimentSize;
		this.resDefault = resDefault;
	}
}
