package com.vnp.viewshowonhomescreen;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ict.library.R;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		create();
		return super.onStartCommand(intent, flags, startId);
	}

	private void create() {
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		int delta_change = (int) getApplicationContext().getResources()
				.getDimension(R.dimen.dimen_20dp);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.main_notif_mail, null);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(MyService.this, "aaaa", Toast.LENGTH_LONG)
				// .show();
				startHomeLuncher();
			}
		});

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = windowManager.getDefaultDisplay().getWidth()
				- layout.getWidth();
		params.y = 100;
		windowManager.addView(layout, params);
	}

	protected void startHomeLuncher() {
		//com.sec.android.app.launcher
		
		Intent mIntent = new Intent();
		mIntent.setPackage("com.sec.android.app.launcher");
		mIntent.addCategory(Intent.CATEGORY_HOME);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(mIntent);
		List<Intent> intents = homes();
		Intent intent = null;
		if (intents.size() > 0) {
			intent = intents.get(1);
		}
		if (intent != null)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//if (intent != null)
		//	startActivity(intent);

		//Log.e("TAGSSS", (intent == null) + "");
	}

	public List<Intent> homes() {
		ArrayList<Intent> intentList = new ArrayList<Intent>();
		Intent intent = null;
		final PackageManager packageManager = getPackageManager();
		for (final ResolveInfo resolveInfo : packageManager
				.queryIntentActivities(new Intent(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME),
						PackageManager.MATCH_DEFAULT_ONLY)) {
			intent = packageManager
					.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName);
			Log.e("TAGSSS", resolveInfo.activityInfo.packageName + " " + (intent == null));
			intentList.add(intent);
		}

		return intentList;
	}

}
