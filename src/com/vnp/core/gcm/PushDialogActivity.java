package com.vnp.core.gcm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

//import com.xing.joy.processdata.ApplicationData;

public class PushDialogActivity extends Activity {

	// ApplicationData data;
	String message;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

//		setContentView(R.layout.push_dialog);
//
//		TextView textView = (TextView) findViewById(R.id.message);
//		textView.setText(getIntent().getExtras().getString("message"));
//		findViewById(R.id.button_1).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

	}
}
