package com.vnp.core.common;

import com.vnp.core.common.imgloader.v1.ScalingLogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class VnpBitmapUtils {
	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int roundDip, boolean roundTL, boolean roundTR, boolean roundBL, boolean roundBR) {
		try {
			int w_default = bitmap.getWidth();
			int h_default = bitmap.getHeight();
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

	public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
		Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
		Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
		return scaledBitmap;
	}

	private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		return new Rect(0, 0, srcWidth, srcHeight);
	}

	private static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		if (scalingLogic == ScalingLogic.FIT) {
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;
			return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
		} else {
			return new Rect(0, 0, dstWidth, dstHeight);
		}
	}

}
