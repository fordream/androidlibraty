package com.vnp.core.service.callapi;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.vnp.core.common.CommonAndroid;
import com.vnp.core.common.LogUtils;
import com.vnp.core.service.RequestMethod;
import com.vnp.core.service.RestClient;
import com.vnp.core.service.RestClientCallBack;

//application android:persistent="true" 
public class CallService extends Service {
	private void callBack(ICallApiParacelable paracel) {
		Intent callback = new Intent(ACTION_CALLBACK);
		Bundle bundle = new Bundle();
		bundle.putParcelable("data", paracel);
		bundle.putString("action", paracel.getActionCallBack());
		bundle.putString("url", paracel.getUrl());
		bundle.putString("header", paracel.getHeaders().toString());
		bundle.putString("params", paracel.getParams().toString());

		bundle.putInt("responsecode", paracel.getResponseCode());
		bundle.putString("errormessage", paracel.getErrorMessage());
		bundle.putString("response", paracel.getResponse());

		callback.putExtras(bundle);
		CallService.this.sendBroadcast(callback);
	};

	private void callBackAction(ICallApiParacelable paracel) {
		String action = paracel.getActionCallBack();
		Intent intent = new Intent(action);

		Bundle bundle = new Bundle();
		bundle.putParcelable("data", paracel);
		bundle.putString("action", paracel.getActionCallBack());
		bundle.putString("url", paracel.getUrl());
		bundle.putString("header", paracel.getHeaders().toString());
		bundle.putString("params", paracel.getParams().toString());

		bundle.putInt("responsecode", paracel.getResponseCode());
		bundle.putString("errormessage", paracel.getErrorMessage());
		bundle.putString("response", paracel.getResponse());

		intent.putExtras(bundle);

		CallService.this.sendBroadcast(intent);
	};

	public static final String ACTION_CALLBACK = "CallService.ACTION_CALLBACK";
	private final ICallApiService.Stub mBinder = new ICallApiService.Stub() {

		/**
		 * method get, post, put
		 */
		@Override
		public void callApi(final ICallApiParacelable paracel) throws RemoteException {
			RestClient restClient = new RestClient(paracel.getUrl());
			JSONArray array = paracel.getHeaders();
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					try {
						JSONObject object = array.getJSONObject(i);
						restClient.addHeader(CommonAndroid.getString(object, "key"), CommonAndroid.getString(object, "value"));
					} catch (Exception e) {
					}
				}
			}

			JSONArray array1 = paracel.getParams();
			if (array1 != null) {
				for (int i = 0; i < array1.length(); i++) {
					try {
						JSONObject object = array1.getJSONObject(i);
						restClient.addParam(CommonAndroid.getString(object, "key"), CommonAndroid.getString(object, "value"));
					} catch (Exception e) {
					}
				}
			}

			RequestMethod xmethod = RequestMethod.GET;
			String method = paracel.getMethod();
			if (!CommonAndroid.isBlank(method)) {
				method = method.toLowerCase();
			}
			if ("post".toLowerCase().equals(method)) {
				xmethod = RequestMethod.POST;
			} else if ("put".toLowerCase().equals(method)) {
				xmethod = RequestMethod.PUT;
			}
			restClient.execute(xmethod, new RestClientCallBack() {
				@Override
				public void onStart() {
					super.onStart();
				}

				public void onSucssesOnBackground(int responseCode, String responseMessage, String response) {
					paracel.onSucssesOnBackground(CallService.this, responseCode, responseMessage, response);
				};

				public void onSucsses(int responseCode, String responseMessage, String response) {
					paracel.onSucsses(CallService.this, responseCode, responseMessage, response);

					paracel.setResponseCode(responseCode);
					paracel.setResponse(response);
					paracel.setErrorMessage(responseMessage);

					/**
					 * callback action
					 */
					callBackAction(paracel);

					/**
					 * broadcast cacllback
					 */
					callBack(paracel);

				}

			});
		}

		@Override
		public void callApiThread(final ICallApiParacelable paracel) throws RemoteException {
			callApiByThread(paracel);
		}
	};

	public void onCreate() {
		super.onCreate();
		LogUtils.e("testx", "onCreate");
		timer.onCreate();
		init();
	};

	private void init() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// return super.onStartCommand(intent, flags, startId);

		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("data") && intent.getExtras().get("data") instanceof ICallApiParacelable) {
			ICallApiParacelable paracel = (ICallApiParacelable) intent.getExtras().get("data");
			callApiByThread(paracel);
		}
		return START_STICKY;
	}

	private void callApiByThread(final ICallApiParacelable paracel) {
		final Handler handler = new Handler() {
			public void dispatchMessage(Message msg) {
				RestClient restClient = (RestClient) msg.obj;
				paracel.onSucsses(CallService.this, restClient.getResponseCode(), restClient.getErrorMessage(), restClient.getResponse());
			};
		};
		new Thread() {
			public void run() {
				RestClient restClient = new RestClient(paracel.getUrl());
				JSONArray array = paracel.getHeaders();
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						try {
							JSONObject object = array.getJSONObject(i);
							restClient.addHeader(CommonAndroid.getString(object, "key"), CommonAndroid.getString(object, "value"));
						} catch (Exception e) {
						}
					}
				}

				JSONArray array1 = paracel.getParams();
				if (array1 != null) {
					for (int i = 0; i < array1.length(); i++) {
						try {
							JSONObject object = array1.getJSONObject(i);
							restClient.addParam(CommonAndroid.getString(object, "key"), CommonAndroid.getString(object, "value"));
						} catch (Exception e) {
						}
					}
				}

				RequestMethod xmethod = RequestMethod.GET;
				String method = paracel.getMethod();
				if (!CommonAndroid.isBlank(method)) {
					method = method.toLowerCase();
				}
				if ("post".toLowerCase().equals(method)) {
					xmethod = RequestMethod.POST;
				} else if ("put".toLowerCase().equals(method)) {
					xmethod = RequestMethod.PUT;
				}
				restClient.execute(xmethod);

				paracel.onSucssesOnBackground(CallService.this, restClient.getResponseCode(), restClient.getErrorMessage(), restClient.getResponse());
				Message message = new Message();
				message.obj = restClient;
				handler.sendMessage(message);

				paracel.setResponseCode(restClient.getResponseCode());
				paracel.setResponse(restClient.getResponse());
				paracel.setErrorMessage(restClient.getErrorMessage());

				/**
				 * callback action
				 */
				callBackAction(paracel);

				/**
				 * broadcast cacllback
				 */
				callBack(paracel);
			}

		}.start();
	}

	private final static class TimerAllwayForServiceLike extends Timer {
		private static long UPDATE_INTERVAL = 1 * 5 * 1000; // default

		public TimerAllwayForServiceLike() {
		}

		public void onCreate() {
			TimerTask timerTask = new TimerTask() {

				public void run() {
					// LogUtils.e("testx",
					// "FileScannerService Timer started....");
				}
			};
			scheduleAtFixedRate(timerTask, 0, UPDATE_INTERVAL);
		}

		public void onDestroy() {
			cancel();
		}
	}

	private static TimerAllwayForServiceLike timer = new TimerAllwayForServiceLike();
}