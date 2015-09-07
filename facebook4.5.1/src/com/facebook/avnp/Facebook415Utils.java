package com.facebook.avnp;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.R;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class Facebook415Utils {

	private static final String PERMISSION = "publish_actions";
	private Activity activity;
	private boolean canPresentShareDialog;
	private boolean canPresentShareDialogWithPhotos;

	public interface IFacebookListener {
		public void onLoginSuccess(LoginResult loginResult);

		public void onLoginCancel();

		public void onLoginError(FacebookException exception);

		// /

		public void onCurrentProfileChangedOfProfileTracker(Profile oldProfile, Profile currentProfile);

		/**
		 * 
		 */

		public void onShareCancel();

		public void onShareError(FacebookException error);

		public void onShareSuccess(Sharer.Result result);
	}

	public void onCreate(Activity activity, Bundle savedInstanceState, final IFacebookListener callbaclLogin) {
		this.activity = activity;
		FacebookSdk.sdkInitialize(activity.getApplicationContext());
		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Profile profile = Profile.getCurrentProfile();
				Log.e("Facebook415Utils", "onSuccess " + profile.getFirstName());
				handlePendingAction();
				callbaclLogin.onLoginSuccess(loginResult);

			}

			@Override
			public void onCancel() {
				Log.e("Facebook415Utils", "onCancel");
				if (pendingAction != PendingAction.NONE) {
					pendingAction = PendingAction.NONE;
				}
				callbaclLogin.onLoginCancel();
			}

			@Override
			public void onError(FacebookException exception) {
				Log.e("Facebook415Utils", "onError");
				if (pendingAction != PendingAction.NONE && exception instanceof FacebookAuthorizationException) {
					pendingAction = PendingAction.NONE;
				}
				callbaclLogin.onLoginError(exception);
			}
		});

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
				handlePendingAction();
				callbaclLogin.onCurrentProfileChangedOfProfileTracker(oldProfile, currentProfile);
			}
		};

		/**
		 * share
		 */
		shareCallback = new FacebookCallback<Sharer.Result>() {
			@Override
			public void onCancel() {
				callbaclLogin.onShareCancel();
			}

			@Override
			public void onError(FacebookException error) {
				callbaclLogin.onShareError(error);
			}

			@Override
			public void onSuccess(Sharer.Result result) {
				callbaclLogin.onShareSuccess(result);
			}
		};

		shareDialog = new ShareDialog(activity);
		shareDialog.registerCallback(getCallbackManager(), shareCallback);

		// Can we present the share dialog for regular links?
		canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);

		// Can we present the share dialog for photos?
		canPresentShareDialogWithPhotos = ShareDialog.canShow(SharePhotoContent.class);
		if (savedInstanceState != null) {
			String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY());
			pendingAction = PendingAction.valueOf(name);
		}

	}

	public boolean isCanPresentShareDialog() {
		return canPresentShareDialog;
	}

	public boolean isCanPresentShareDialogWithPhotos() {
		return canPresentShareDialogWithPhotos;
	}

	public void login() {
		LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList(PERMISSION));
	}

	public boolean shareDialogShow() {
		ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentTitle("Hello Facebook").setContentDescription("The 'Hello Facebook' sample  showcases simple Facebook integration")
				.setContentUrl(Uri.parse("http://developers.facebook.com/docs/android")).build();

		boolean isCanShare = false;
		if (canPresentShareDialog) {
			shareDialog.show(linkContent);
			isCanShare = true;
		} else if (getProfile() != null && hasPublishPermission()) {
			ShareApi.share(linkContent, shareCallback);
			isCanShare = true;
		}

		return isCanShare;
	}

	public boolean shareDialogShow(Bitmap image, String title) {
		boolean isCanShare = false;
		SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).setCaption(title).build();
		
		ArrayList<SharePhoto> photos = new ArrayList<SharePhoto>();
		photos.add(sharePhoto);
		SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().setPhotos(photos).setRef(title).build();
		if (canPresentShareDialogWithPhotos) {
			shareDialog.show(sharePhotoContent);
			isCanShare = true;
		} else if (hasPublishPermission()) {
			ShareApi.share(sharePhotoContent, shareCallback);
			isCanShare = true;
		} else {
			LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList(PERMISSION));
		}

		return isCanShare;
	}

	public boolean isLogin() {
		return AccessToken.getCurrentAccessToken() != null;
	}

	public Profile getProfile() {
		return Profile.getCurrentProfile();
	}

	private CallbackManager callbackManager;

	public CallbackManager getCallbackManager() {
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}

		return callbackManager;
	}

	private FacebookCallback<Sharer.Result> shareCallback;
	private ShareDialog shareDialog;

	/**
	 * 
	 */
	private Facebook415Utils() {

	}

	public void onPause() {
		AppEventsLogger.deactivateApp(activity);
	}

	public void onResume() {
		AppEventsLogger.activateApp(activity);

	}

	private String PENDING_ACTION_BUNDLE_KEY() {
		return String.format("%s:PendingAction", activity.getPackageName());
	}

	public void onDestroy() {
		profileTracker.stopTracking();
	}

	private ProfileTracker profileTracker;
	private static Facebook415Utils facebook415Utils = new Facebook415Utils();

	public static Facebook415Utils getInstance() {
		return facebook415Utils;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		getCallbackManager().onActivityResult(requestCode, resultCode, data);
	}

	private boolean hasPublishPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains("publish_actions");
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putString(PENDING_ACTION_BUNDLE_KEY(), pendingAction.name());
	}

	private PendingAction pendingAction = PendingAction.NONE;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case NONE:
			break;
		case POST_PHOTO:
			postPhoto();
			break;
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		}
	}

	private void postPhoto() {
		Bitmap image = bitmapForPost;

		if (!shareDialogShow(image, title)) {
			pendingAction = PendingAction.POST_PHOTO;
		}
	}

	private void postStatusUpdate() {
		if (!shareDialogShow()) {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private void performPublish(PendingAction action, boolean allowNoToken) {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null || allowNoToken) {
			pendingAction = action;
			handlePendingAction();
		}
	}

	private Bitmap bitmapForPost;
	private String title;

	public void onClickPostPhoto(Bitmap bitmap, String title) {
		this.title = title;
		bitmapForPost = bitmap;
		performPublish(PendingAction.POST_PHOTO, isCanPresentShareDialogWithPhotos());
	}

	public void onClickPostStatusUpdate() {
		performPublish(PendingAction.POST_STATUS_UPDATE, Facebook415Utils.getInstance().isCanPresentShareDialog());
	}

	public void logout() {
		LoginManager.getInstance().logOut();
	}
}