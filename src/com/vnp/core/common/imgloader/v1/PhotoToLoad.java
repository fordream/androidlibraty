package com.vnp.core.common.imgloader.v1;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class PhotoToLoad {

	public String getName() {
		return url + isRound + requimentSize;
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

	public boolean isRound() {
		return isRound;
	}

	public void setRound(boolean isRound) {
		this.isRound = isRound;
	}

	public int getRequimentSize() {
		return requimentSize;
	}

	public void setRequimentSize(int requimentSize) {
		this.requimentSize = requimentSize;
	}

	private String url;
	private ImageView imageView;
	private boolean isRound;
	private int requimentSize;

	public PhotoToLoad(String u, ImageView i, boolean isRound, int requimentSize) {
		url = u;
		imageView = i;
		this.isRound = isRound;
		this.requimentSize = requimentSize;

	}
}
