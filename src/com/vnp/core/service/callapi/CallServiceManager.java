package com.vnp.core.service.callapi;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.vnp.core.common.CommonAndroid;

public class CallServiceManager {
	public static final String TAG = "CallServiceManager";

	/**
	 * save action for class
	 * 
	 * @param action
	 */
	public void setAction(String action) {
		if (application != null && !CommonAndroid.isBlank(action)) {
			SharedPreferences preferences = application.getSharedPreferences(TAG, 0);
			preferences.edit().putString("action", action).commit();
		}
	}

	/**
	 * 
	 * @return action of service
	 */
	public String getAction() {
		if (application != null) {
			SharedPreferences preferences = application.getSharedPreferences(TAG, 0);
			return preferences.getString("action", "");
		}

		return "";
	}

	public Intent getIntentService() {
		if (application != null) {
			Intent intent = new Intent(application, CallService.class);

			if (!CommonAndroid.isBlank(getAction())) {
				intent = new Intent(getAction());
			}
			return intent;
		}

		return null;
	}

	public void callApiByStartService(ICallApiParacelable paracelable) {
		Intent intent = getIntentService();

		if (intent != null) {
			Bundle bundle = new Bundle();
			bundle.putParcelable("data", paracelable);
			intent.putExtras(bundle);

			if (application != null) {
				application.startService(intent);
			}
		}
	}

	public void connect() {
		if (application != null) {
			ServiceConnection mConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className, IBinder service) {
					mIRemoteService = ICallApiService.Stub.asInterface(service);
				}

				public void onServiceDisconnected(ComponentName className) {
					mIRemoteService = null;
				}
			};
			if (mIRemoteService != null) {
				application.bindService(getIntentService(), mConnection, Context.BIND_AUTO_CREATE);
			}
			application.startService(getIntentService());
		}
	}

	public void callApi(final ICallApiParacelable paracel) {
		if (mIRemoteService != null) {
			try {
				mIRemoteService.callApi(paracel);
			} catch (RemoteException e) {
				paracel.onFailRemoteException();
			}
		} else {
			if (application != null) {
				ServiceConnection mConnection = new ServiceConnection() {
					public void onServiceConnected(ComponentName className, IBinder service) {
						mIRemoteService = ICallApiService.Stub.asInterface(service);
						try {
							mIRemoteService.callApi(paracel);
						} catch (RemoteException e) {
							paracel.onFailRemoteException();
						}
					}

					public void onServiceDisconnected(ComponentName className) {
						mIRemoteService = null;
						paracel.onServiceDisconnected();
					}
				};
				application.bindService(getIntentService(), mConnection, Context.BIND_AUTO_CREATE);
			} else {
				paracel.onFailContextNull();
			}
		}
	}

	public void callApiThread(final ICallApiParacelable paracel) {
		if (mIRemoteService != null) {
			try {
				mIRemoteService.callApiThread(paracel);
			} catch (RemoteException e) {
				paracel.onFailRemoteException();
			}
		} else {
			if (application != null) {
				ServiceConnection mConnection = new ServiceConnection() {
					public void onServiceConnected(ComponentName className, IBinder service) {
						mIRemoteService = ICallApiService.Stub.asInterface(service);
						try {
							mIRemoteService.callApiThread(paracel);
						} catch (RemoteException e) {
							paracel.onFailRemoteException();
						}
					}

					public void onServiceDisconnected(ComponentName className) {
						mIRemoteService = null;
						paracel.onServiceDisconnected();
					}
				};
				application.bindService(getIntentService(), mConnection, Context.BIND_AUTO_CREATE);
			} else {
				paracel.onFailContextNull();
			}
		}
	}

	private ICallApiService mIRemoteService;

	/**
	 * init
	 */
	private final static CallServiceManager instance = new CallServiceManager();
	private Application application;

	private CallServiceManager() {

	}

	public static CallServiceManager getInstance(Context context) {
		instance.init(context);
		return instance;
	}

	private void init(Context context) {
		if (application == null) {
			if (context != null) {
				if (context instanceof Application) {
					application = (Application) context;
				} else {
					application = (Application) context.getApplicationContext();
				}
			}
		}

		if (application != null) {
			Intent intent = getIntentService();
			if (intent != null) {
				application.startService(intent);
			}
		}
	}
}
