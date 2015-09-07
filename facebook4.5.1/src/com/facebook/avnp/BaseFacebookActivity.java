package com.facebook.avnp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.avnp.Facebook415Utils.IFacebookListener;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;

public abstract class BaseFacebookActivity extends Activity {
	private IFacebookListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listener = createIFacebookListener();
		Facebook415Utils.getInstance().onCreate(this, savedInstanceState, new IFacebookListener() {

			@Override
			public void onLoginSuccess(LoginResult loginResult) {
				listener.onLoginSuccess(loginResult);
			}

			@Override
			public void onLoginCancel() {
				listener.onLoginCancel();
			}

			@Override
			public void onLoginError(FacebookException exception) {
				listener.onLoginError(exception);
			}

			@Override
			public void onCurrentProfileChangedOfProfileTracker(Profile oldProfile, Profile currentProfile) {
				listener.onCurrentProfileChangedOfProfileTracker(oldProfile, currentProfile);
			}

			public void onShareCancel() {
				listener.onShareCancel();
			}

			public void onShareError(FacebookException error) {
				listener.onShareError(error);
			}

			public void onShareSuccess(Sharer.Result result) {
				listener.onShareSuccess(result);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Facebook415Utils.getInstance().onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Facebook415Utils.getInstance().onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Facebook415Utils.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		Facebook415Utils.getInstance().onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Facebook415Utils.getInstance().onDestroy();
	}

	public abstract IFacebookListener createIFacebookListener();
}
