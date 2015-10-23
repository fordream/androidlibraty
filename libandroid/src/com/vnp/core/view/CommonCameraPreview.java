package com.vnp.core.view;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

//<uses-permission android:name="android.permission.CAMERA"/> 
//<uses-feature android:name="android.hardware.camera" />
//<uses-feature android:name="android.hardware.camera.autofocus" />
//com.vnp.core.view.CommonCameraPreview
public class CommonCameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	public interface TakePictureListener {
		public void takeFail();

		public void takeSucess(Bitmap bitmap);
	}

	public CommonCameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		open();
	}

	public CommonCameraPreview(Context context) {
		super(context);
		open();
	}

	public static Camera isCameraAvailiable() {
		Camera object = null;
		try {
			object = Camera.open();
		} catch (Exception e) {
		}
		return object;
	}

	private SurfaceHolder holdMe;
	private Camera theCamera;

	private void open() {
		theCamera = isCameraAvailiable();

		if (theCamera != null) {
			Parameters parameters = theCamera.getParameters();
			parameters.setRotation(90);

			theCamera.setParameters(parameters);
		}
		holdMe = getHolder();
		holdMe.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
			int width, int height) {
		try {
			theCamera.setPreviewDisplay(surfaceHolder);
			theCamera.startPreview();
		} catch (Exception e) {
			// intentionally left blank for a test
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			theCamera.setPreviewDisplay(holder);
			theCamera.startPreview();
		} catch (IOException e) {
		}
	}

	public void takePicture(final TakePictureListener takePictureListener) {
		PictureCallback capturedIt = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				if (bitmap == null) {
					takePictureListener.takeFail();
					// Toast.makeText(getContext(), "not taken",
					// Toast.LENGTH_SHORT).show();
				} else {
					takePictureListener.takeSucess(bitmap);
					// Toast.makeText(getContext(), "taken", Toast.LENGTH_SHORT)
					// .show();
				}
				theCamera.startPreview();
				// theCamera.release();

				// open();
			}
		};

		if (theCamera != null) {
			theCamera.takePicture(null, null, capturedIt);
		} else {
			takePictureListener.takeFail();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (theCamera != null) {
			theCamera.stopPreview();
			theCamera.release();
			theCamera = null;
		}
	}

}