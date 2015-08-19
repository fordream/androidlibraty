/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vnp.core.gcm;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * 
 * @author tvuong1pc
 * @see
 * 
 *      <uses-permission android:name="android.permission.GET_ACCOUNTS" />
 *      <uses-permission android:name="android.permission.WAKE_LOCK" />
 * 
 *      <permission android:name="vnp.mr.mintmedical.permission.C2D_MESSAGE"
 *      android:protectionLevel="signature" />
 * 
 *      <uses-permission
 *      android:name="vnp.mr.mintmedical.permission.C2D_MESSAGE" />
 *      <uses-permission
 *      android:name="com.google.android.c2dm.permission.RECEIVE" />
 * 
 * 
 * 
 *      <receiver android:name=".GcmBroadcastReceiver"
 *      android:permission="com.google.android.c2dm.permission.SEND" >
 *      <intent-filter> <action
 *      android:name="com.google.android.c2dm.intent.RECEIVE" /> <category
 *      android:name="app.bynal.woman" /> </intent-filter> </receiver>
 * 
 *      <activity android:name=".PushDialogActivity"
 *      android:theme="@android:style/Theme.Dialog" > </activity>
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
	static final String TAG = "GCMDemo";
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);
		Intent trIntent = new Intent(context, PushDialogActivity.class);
		trIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		trIntent.putExtra("message", intent.getExtras().getString("message"));
		context.startActivity(trIntent);
	}

	public interface RegisterCallBackRegisterId {
		/**
		 * 
		 * @param registerId
		 * @param deviceId
		 */
		public void onCallBack(String registerId, String deviceId);

		public void onStart();
	}

	public static final void registerCallBackRegisterId(final Context context, final RegisterCallBackRegisterId registerCallBackRegisterId) {
		registerCallBackRegisterId.onStart();
		final String SENDER_ID = "498720258430";// "27284071298";//609478506422

		new AsyncTask<Void, Void, String>() {
			String registerId;
			String deviceId;

			@Override
			protected String doInBackground(Void... _params) {
				try {
					GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
					registerId = gcm.register(SENDER_ID);
					deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
				} catch (Exception ex) {

				}
				return null;
			}

			@Override
			protected void onPostExecute(String msg) {
				registerCallBackRegisterId.onCallBack(registerId, deviceId);
			}
		}.execute(null, null, null);
	}

	public static void register(final Context context) {
		final String TAG = "register-push";
		final SharedPreferences preferences = context.getSharedPreferences(TAG, 0);
		final String SENDER_ID = "498720258430";// "27284071298";//609478506422
		final String SERVER = "http://vnpmanager.esy.es/gcm/register.php";

		if (!preferences.getBoolean(TAG, false))
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... _params) {
					try {
						GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
						String regid = gcm.register(SENDER_ID);
						String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
						RestClient client = new RestClient(SERVER);
						client.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
						client.addParam("name", deviceId);
						client.addParam("regId", regid);
						try {
							client.execute(RequestMethod.GET);
							if (client.getResponse().equals("200")) {
								preferences.edit().putBoolean(TAG, true).commit();
							}
							return client.getResponse();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (IOException ex) {

					}
					return null;
				}

				@Override
				protected void onPostExecute(String msg) {
				}
			}.execute(null, null, null);
	}

	static public enum RequestMethod {
		GET, POST, PUT, DELETE
	}

	static class RestClient {

		public static final int TIME_OUT = 10 * 1000;
		public static final int BUFFER = 1024 * 2;
		private ArrayList<NameValuePair> params;
		private ArrayList<NameValuePair> headers;

		private String url;

		private int responseCode;
		private String message;

		private String response;

		public String getResponse() {
			return response;
		}

		public String getErrorMessage() {
			return message;
		}

		public int getResponseCode() {
			return responseCode;
		}

		public RestClient(String url) {
			this.url = url;
			params = new ArrayList<NameValuePair>();
			headers = new ArrayList<NameValuePair>();
		}

		public void addParam(String name, String value) {
			params.add(new BasicNameValuePair(name, value));
		}

		public void addHeader(String name, String value) {
			headers.add(new BasicNameValuePair(name, value));
		}

		public void execute(RequestMethod method) throws Exception {
			switch (method) {
			case GET: {
				// add parameters
				String combinedParams = "";
				if (!params.isEmpty()) {
					combinedParams += "?";
					for (NameValuePair p : params) {
						String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
						if (combinedParams.length() > 1) {
							combinedParams += "&" + paramString;
						} else {
							combinedParams += paramString;
						}
					}
				}

				HttpGet request = new HttpGet(url + combinedParams);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				this.executeRequest(request, url);
				break;
			}
			case POST: {
				HttpPost request = new HttpPost(url);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				if (!params.isEmpty()) {
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}

				this.executeRequest(request, url);
				break;
			}
			case PUT: {
				HttpPut request = new HttpPut(url);
				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				if (!params.isEmpty()) {
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
				this.executeRequest(request, url);
				break;

			}
			case DELETE: {
				HttpDelete request = new HttpDelete(url);
				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				this.executeRequest(request, url);
				break;

			}
			}
		}

		private void executeRequest(HttpUriRequest request, String url) {

			int timeout = TIME_OUT;
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
			HttpConnectionParams.setSoTimeout(httpParameters, timeout);

			HttpClient client = new DefaultHttpClient(httpParameters);

			HttpResponse httpResponse;

			try {
				httpResponse = client.execute(request);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				message = httpResponse.getStatusLine().getReasonPhrase();

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {
					response = EntityUtils.toString(entity);
				}

			} catch (ClientProtocolException e) {
				client.getConnectionManager().shutdown();
			} catch (IOException e) {
				client.getConnectionManager().shutdown();
			}
		}

	}
}
