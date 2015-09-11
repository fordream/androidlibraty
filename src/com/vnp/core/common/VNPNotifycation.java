package com.vnp.core.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.ict.library.R;

public class VNPNotifycation {
	public void createNotificationSerice(Context context, int NOTIFY_ME_ID, String title, int app_name, Class<Service> service) {

		final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(context, service.getClass());
		intent.putExtra("api", "notification");
		intent.putExtra("id", "callscreen");
		intent.putExtra("title", title);
		intent.putExtra("title", title);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			// API 16 onwards
			Notification.Builder builder = new Notification.Builder(context);
			builder.setAutoCancel(false);
			builder.setContentIntent(pendingIntent);
			builder.setContentText(title);
			builder.setContentTitle(context.getString(app_name));
			builder.setOngoing(true);
			builder.setSmallIcon(R.drawable.ic_rotate_left);
			builder.setWhen(System.currentTimeMillis());
			Notification notification = builder.build();
			notification.flags = Notification.DEFAULT_LIGHTS & Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(NOTIFY_ME_ID, notification);
		} else {
			// API 15 and earlier
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(false);
			builder.setContentIntent(pendingIntent);
			builder.setContentText(title);
			builder.setContentTitle(context.getString(app_name));
			builder.setOngoing(true);
			builder.setSmallIcon(R.drawable.ic_rotate_left);
			builder.setWhen(System.currentTimeMillis());
			Notification notification = builder.getNotification();
			notification.flags = Notification.DEFAULT_LIGHTS & Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(NOTIFY_ME_ID, notification);
		}

		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {
		}
	}
}
