/**
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * http://www.londatiga.net
 */

package com.vnp.core.social;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwitterApp {
	private Twitter mTwitter;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private String mConsumerKey;
	private String mSecretKey;
	private ProgressDialog mProgressDlg;
	private TwDialogListener mListener;
	private Context context;
	private boolean mInit = true;

	// public static final String CALLBACK_URL = "twitterapp://connect";
	public static String CALLBACK_URL = "http://google.com";
	private static final String TAG = "TwitterApp";

	public TwitterApp(Activity context, String consumerKey, String secretKey,
			String callback) {
		this.context = context;
		CALLBACK_URL = callback;
		mTwitter = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context);
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey,
				mSecretKey);
		mHttpOauthprovider = new DefaultOAuthProvider(
				"https://api.twitter.com/oauth/request_token",
				"https://api.twitter.com/oauth/access_token",
				"https://api.twitter.com/oauth/authorize");

		mAccessToken = mSession.getAccessToken();

		configureToken();
	}

	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	private void configureToken() {
		if (mAccessToken != null) {
			if (mInit) {
				mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);
				mInit = false;
			}

			mTwitter.setOAuthAccessToken(mAccessToken);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();

			mAccessToken = null;
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public void updateStatus(String status) throws Exception {
		try {
			mTwitter.updateStatus(status);
		} catch (TwitterException e) {
			throw e;
		}
	}

	public void authorize() {
		mProgressDlg.setMessage("Initializing ...");
		mProgressDlg.show();

		new Thread() {

			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL);

					what = 0;

					Log.d(TAG, "Request token url " + authUrl);
				} catch (Exception e) {
					Log.d(TAG, "Failed to get request token");

					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler
						.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void processToken(String callbackUrl) {
		mProgressDlg.setMessage("Finalizing ...");
		mProgressDlg.show();

		final String verifier = getVerifier(callbackUrl);

		new Thread() {

			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer,
							verifier);

					mAccessToken = new AccessToken(
							mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret());

					configureToken();

					User user = mTwitter.verifyCredentials();

					mSession.storeAccessToken(mAccessToken, user.getName());

					what = 0;
				} catch (Exception e) {
					Log.d(TAG, "Error getting access token");

					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {

			public void onComplete(String value) {
				processToken(value);
			}

			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		new TwitterDialog(context, url, listener).show();
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};

	public interface TwDialogListener {
		public void onComplete(String value);

		public void onError(String value);
	}

	public void logout() {
		// CookieManager cookieManager = CookieManager.getInstance();
		// cookieManager.removeSessionCookie();

		mTwitter.setOAuthAccessToken(null);
		mSession.resetAccessToken();
		mAccessToken = null;
	}

	// ============================================================================
	// TwitterDialog
	// ============================================================================

	public static class TwitterDialog extends Dialog {
		static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		static final int MARGIN = 4;
		static final int PADDING = 2;

		private String mUrl;
		private TwDialogListener mListener;
		private ProgressDialog mSpinner;
		private WebView mWebView;
		private LinearLayout mContent;
		private TextView mTitle;

		private static final String TAG = "Twitter-WebView";

		public TwitterDialog(Context context, String url,
				TwDialogListener listener) {
			super(context);

			mUrl = url;
			mListener = listener;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mSpinner = new ProgressDialog(getContext());

			mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mSpinner.setMessage("Loading...");

			mContent = new LinearLayout(getContext());

			mContent.setOrientation(LinearLayout.VERTICAL);

			setUpTitle();
			setUpWebView();

			Display display = getWindow().getWindowManager()
					.getDefaultDisplay();
			double[] dimensions = new double[2];

			if (display.getWidth() < display.getHeight()) {
				dimensions[0] = 0.87 * display.getWidth();
				dimensions[1] = 0.82 * display.getHeight();
			} else {
				dimensions[0] = 0.75 * display.getWidth();
				dimensions[1] = 0.75 * display.getHeight();
			}

			addContentView(mContent, new FrameLayout.LayoutParams(
					(int) dimensions[0], (int) dimensions[1]));
		}

		private void setUpTitle() {
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			Drawable icon = getContext().getResources().getDrawable(
					android.R.drawable.btn_star);

			mTitle = new TextView(getContext());

			mTitle.setText("Twitter");
			mTitle.setTextColor(Color.WHITE);
			mTitle.setTypeface(Typeface.DEFAULT_BOLD);
			mTitle.setBackgroundColor(0xFFbbd7e9);
			mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
			mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
			mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null,
					null);

			mContent.addView(mTitle);
		}

		private void setUpWebView() {
			CookieSyncManager.createInstance(getContext());
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();

			mWebView = new WebView(getContext());

			mWebView.setVerticalScrollBarEnabled(false);
			mWebView.setHorizontalScrollBarEnabled(false);
			mWebView.setWebViewClient(new TwitterWebViewClient());
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.loadUrl(mUrl);
			mWebView.setLayoutParams(FILL);

			mContent.addView(mWebView);
		}

		private class TwitterWebViewClient extends WebViewClient {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(TAG, "Redirecting URL " + url);

				if (url.startsWith(CALLBACK_URL)) {
					mListener.onComplete(url);
					TwitterDialog.this.dismiss();
					return true;
				} else if (url.startsWith("authorize")) {
					// return false;
				}

				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.d(TAG, "Page error: " + description);

				super.onReceivedError(view, errorCode, description, failingUrl);

				mListener.onError(description);

				TwitterDialog.this.dismiss();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d(TAG, "Loading URL: " + url);
				super.onPageStarted(view, url, favicon);
				mSpinner.show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				String title = mWebView.getTitle();
				if (title != null && title.length() > 0) {
					mTitle.setText(title);
				}
				mSpinner.dismiss();
			}

		}
	}

	// ============================================================================
	// TwitterSession
	// ============================================================================
	public static class TwitterSession {
		private SharedPreferences sharedPref;
		private Editor editor;

		private static final String TWEET_AUTH_KEY = "auth_key";
		private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
		private static final String TWEET_USER_NAME = "user_name";
		private static final String SHARED = "Twitter_Preferences";

		public TwitterSession(Context context) {
			sharedPref = context.getSharedPreferences(SHARED,
					Context.MODE_PRIVATE);

			editor = sharedPref.edit();
		}

		public void storeAccessToken(AccessToken accessToken, String username) {
			editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
			editor.putString(TWEET_AUTH_SECRET_KEY,
					accessToken.getTokenSecret());
			editor.putString(TWEET_USER_NAME, username);

			editor.commit();
		}

		public void resetAccessToken() {
			editor.putString(TWEET_AUTH_KEY, null);
			editor.putString(TWEET_AUTH_SECRET_KEY, null);
			editor.putString(TWEET_USER_NAME, null);

			editor.commit();
		}

		public String getUsername() {
			return sharedPref.getString(TWEET_USER_NAME, "");
		}

		public AccessToken getAccessToken() {
			String token = sharedPref.getString(TWEET_AUTH_KEY, null);
			String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY,
					null);

			if (token != null && tokenSecret != null)
				return new AccessToken(token, tokenSecret);
			else
				return null;
		}
	}
}