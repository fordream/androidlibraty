package com.vnp.core.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public abstract class GalleryCameraChooser {

	public static final int REQUESTCODEGALLERY = 107;
	public static final int REQUESTCODECAMERA = 106;

	public void startGalleryChooser(Activity activity) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		activity.startActivityForResult(photoPickerIntent, REQUESTCODEGALLERY);
	}

	public void startCameraChooser(Activity activity) {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//		if (hasImageCaptureBug()) {
//			cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));
//		} else {
//			cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		}
		activity.startActivityForResult(cameraIntent, REQUESTCODECAMERA);
	}

	public boolean hasImageCaptureBug() {

		// list of known devices that have the bug
		ArrayList<String> devices = new ArrayList<String>();
		devices.add("android-devphone1/dream_devphone/dream");
		devices.add("generic/sdk/generic");
		devices.add("vodafone/vfpioneer/sapphire");
		devices.add("tmobile/kila/dream");
		devices.add("verizon/voles/sholes");
		devices.add("google_ion/google_ion/sapphire");

		return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/" + android.os.Build.DEVICE);

	}

	public final void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (REQUESTCODEGALLERY == requestCode && resultCode == Activity.RESULT_OK) {
			Uri selectedImage = data.getData();

			try {
				onGallery(decodeUri(activity, selectedImage));
			} catch (FileNotFoundException e) {
				onGalleryError();
			}
		} else if (REQUESTCODECAMERA == requestCode && resultCode == Activity.RESULT_OK) {
			onCamera((Bitmap) data.getExtras().get("data"));
		}
	}

	private Bitmap decodeUri(Activity activity, Uri selectedImage) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(selectedImage), null, o2);

	}

	public abstract void onGalleryError();

	public abstract void onGallery(Bitmap bitmap);

	public abstract void onCamera(Bitmap bitmap);
}