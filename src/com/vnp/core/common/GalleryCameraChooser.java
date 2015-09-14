package com.vnp.core.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

public abstract class GalleryCameraChooser {

	public static final int REQUESTCODEGALLERY = 107;
	public static final int REQUESTCODECAMERA = 106;

	public void startGalleryChooser(Activity activity) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		activity.startActivityForResult(photoPickerIntent, REQUESTCODEGALLERY);
		releaseCamera();
	}

	// private String outfileName;
	// private Uri uriOutfileName;

	public void startCameraChooser(Activity activity) {
		VnpFileCache memoryUtils = new VnpFileCache(activity);
		new File(memoryUtils.getPathCacheExternalMemory()).mkdirs();
		// outfileName = memoryUtils.getPathCacheExternalMemory() +
		// System.currentTimeMillis() + "xouttemp.png";
		// uriOutfileName = Uri.fromFile(new File(outfileName));
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriOutfileName);
		activity.startActivityForResult(cameraIntent, REQUESTCODECAMERA);
	}

	private static int exifToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	public final void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (REQUESTCODEGALLERY == requestCode && resultCode == Activity.RESULT_OK) {
			Uri selectedImage = data.getData();
			Bitmap bitmap = null;
			try {
				bitmap = decodeUri(activity, selectedImage);
				int orientation = getOrientation(activity, selectedImage);

				ExifInterface exif = new ExifInterface(selectedImage.getPath());
				int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

				int rotationInDegrees = exifToDegrees(rotation);
				bitmap = Bitmap90(bitmap, orientation);
			} catch (Exception e1) {
			}
			onGallery(bitmap);
		} else if (REQUESTCODECAMERA == requestCode && resultCode == Activity.RESULT_OK) {

			// Bitmap bitmap = null;
			// try {
			// bitmap = decodeUri(activity, uriOutfileName);
			// ExifInterface exif = new ExifInterface(uriOutfileName.getPath());
			// int rotation =
			// exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
			// ExifInterface.ORIENTATION_NORMAL);
			// int rotationInDegrees = exifToDegrees(rotation);
			// bitmap = Bitmap90(bitmap, rotationInDegrees);
			// } catch (IOException e1) {
			// }
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");

			onCamera(bitmap);
			releaseCamera();
			// new File(outfileName).delete();
		}
	}

	public static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private Bitmap Bitmap90(Bitmap bitmap, int r) {
		LogUtils.e("rrrr", r + "");
		Matrix matrix = new Matrix();
		matrix.postRotate(r);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
		Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		return rotatedBitmap;
	}

	private void releaseCamera() {
		try {
			Camera camera = Camera.open();
			if (camera != null) {
				camera.stopPreview();
				camera.setPreviewCallback(null);
				camera.release();
				camera = null;
			}
		} catch (Exception ex) {

		} catch (Error er) {

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