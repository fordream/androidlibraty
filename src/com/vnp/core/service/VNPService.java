package com.vnp.core.service;

import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import com.vnp.core.service.RequestInfo.TypeServer;

@SuppressWarnings("deprecation")
public class VNPService extends Service implements LocationListener, SensorListener {

	@Override
	public void onCreate() {
		super.onCreate();
		if (locationM == null) {
			locationM = new LocationM(this);
		}

		if (sensorM == null) {
			sensorM = new SensorM();
		}

		iBinder = new VNPServiceBinder(this);
	}

	private IBinder iBinder;

	@Override
	public IBinder onBind(Intent arg0) {
		return iBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String typeService = intent.getStringExtra(TYPESERVICE.KEY_TYPESERVICE);
			if (TYPESERVICE.SENSSOR.equals(typeService)) {
				sensorM.onStartCommand(this, this);
			} else if (TYPESERVICE.LOCATION.equals(typeService)) {
				locationM.onStartCommand(this);
			} else if (TYPESERVICE.LOADCONTENT.equals(typeService)) {
				callToServer(intent);
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	// ===========================================================
	// Location
	// ===========================================================
	@Override
	public void onLocationChanged(Location location) {
		locationM.onLocationChanged(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	private LocationM locationM;

	private class LocationM {
		private Context context;

		public LocationM(Context mContext) {
			context = mContext;
		}

		public static final String LOCATION_ACTION = "com.example.senssortest.LOCATION_ACTION";
		public static final String LOCATION_KEY = "LOCATION_KEY";
		public static final String LOCATION_VALUE = "LOCATION_VALUE";
		public CommonLocationParacelable curentLocation;
		private String provider = LocationManager.NETWORK_PROVIDER;
		private LocationManager locationManager;

		public void onStartCommand(LocationListener listener) {

			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			if (provider == null) {
				provider = LocationManager.NETWORK_PROVIDER;
			}

			final Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.removeUpdates(listener);
			locationManager.requestLocationUpdates(provider, 400, 1, listener);
		}

		public void onLocationChanged(Location location) {
			if (location != null) {
				// Location Change
				curentLocation = new CommonLocationParacelable(location.getProvider());
				curentLocation.set(location);
				Bundle extras = new Bundle();
				extras.putParcelable(LOCATION_VALUE, curentLocation);
				Intent intent = new Intent(LOCATION_ACTION);
				intent.putExtras(extras);
				sendBroadcast(intent);
			}
		}

	}

	public static class CommonLocationParacelable extends Location implements Parcelable {

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(Parcel out, int flags) {
			out.writeString(getProvider());
			out.writeDouble(getLatitude());
			out.writeDouble(getLongitude());
			out.writeFloat(getAccuracy());
			out.writeDouble(getAltitude());
			out.writeFloat(getBearing());

			out.writeFloat(getSpeed());
			out.writeLong(getTime());
			out.writeBundle(getExtras());
		}

		public static final Parcelable.Creator<CommonLocationParacelable> CREATOR = new Parcelable.Creator<CommonLocationParacelable>() {
			public CommonLocationParacelable createFromParcel(Parcel in) {
				return new CommonLocationParacelable(in);
			}

			public CommonLocationParacelable[] newArray(int size) {
				return new CommonLocationParacelable[size];
			}
		};

		private CommonLocationParacelable(Parcel in) {
			super(in.readString());
			// mData = in.readInt();
			setLatitude(in.readDouble());
			setLongitude(in.readDouble());
			setAccuracy(in.readFloat());
			setAltitude(in.readDouble());
			setBearing(in.readFloat());
			setSpeed(in.readFloat());
			setTime(in.readLong());
			setExtras(in.readBundle());
		}

		public CommonLocationParacelable(String provider) {
			super(provider);
		}
	}

	// ===========================================================
	// Sensor
	// ===========================================================

	@Override
	public void onDestroy() {
		sensorM.onDestroy(this);
		super.onDestroy();
	}

	public void onAccuracyChanged(int sensor, int accuracy) {

	}

	public void onSensorChanged(int sensor, float[] values) {
		sensorM.onSensorChanged(sensor, values);

	}

	SensorM sensorM;

	private class SensorM {
		public static final String SENSSOR_ACTION = "com.example.senssortest.SENSSOR_ACTION";
		public static final String SENSSOR_VALUE = "SENSSOR_VALUE";
		private SensorManager sensorManager;

		public void onDestroy(SensorListener listener) {
			sensorManager.unregisterListener(listener);
		}

		private void onSensorChanged(int sensor, float[] values) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {

				// orientation
				// int orientation = (int) values[0];
				// Log.w(SENSSOR_KEY, values[0] + "");
				Bundle extras = new Bundle();

				// values[0] is z(0<=azimuth<360). 0 = North, 90 = East, 180 =
				// South, 270 = West
				// values[1] is x(-180<=pitch<=180), with positive values when
				// the
				// z-axis moves toward the y-axis.
				// values[2] is y(-90<=roll<=90), with positive values when the
				// z-axis moves toward the x-axis.
				extras.putFloatArray(SENSSOR_VALUE, values);

				Intent intent = new Intent(SENSSOR_ACTION);
				intent.putExtras(extras);

				// send broadcast
				sendBroadcast(intent);
			}
		}

		public void onStartCommand(Context context, SensorListener listener) {
			sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
			sensorManager.registerListener(listener, SensorManager.SENSOR_ORIENTATION);
		}
	}

	// ===========================================================
	// Call To server
	// ===========================================================
	private void callToServer(Intent intent) {
		RequestInfo requestInfo = intent.getParcelableExtra("RequestInfo");
		if (TypeServer.GET_CONTENT_STRING == requestInfo.getTypeServer()) {
			// load content
		}
	}

	private class TYPESERVICE {
		public static final String KEY_TYPESERVICE = "KEY_TYPESERVICE";
		public static final String SENSSOR = "SENSSOR";
		public static final String LOCATION = "LOCATION";
		public static final String LOADCONTENT = "LOADCONTENT";
	}

	public static VNPServicemanager getManager() {
		return new VNPServicemanager();
	}

	public static class VNPServicemanager {
		public void enableSennsorService(Context context) {
			Intent intent = new Intent(context, VNPService.class);
			intent.putExtra(TYPESERVICE.KEY_TYPESERVICE, TYPESERVICE.SENSSOR);
			context.startService(intent);
		}

		public void enableLocationService(Context context) {
			Intent intent = new Intent(context, VNPService.class);
			intent.putExtra(TYPESERVICE.KEY_TYPESERVICE, TYPESERVICE.LOCATION);
			context.startService(intent);
		}

		public void useLoadContentFromServer(Context context, String url, String header, HashMap<String, String> parameters, RequestMethod method) {
			Intent intent = new Intent(context, VNPService.class);
			intent.putExtra(TYPESERVICE.KEY_TYPESERVICE, TYPESERVICE.LOADCONTENT);

			RequestInfo requestInfo = new RequestInfo();
			requestInfo.setTypeServer(TypeServer.GET_CONTENT_STRING);
			requestInfo.setMethod(method);
			requestInfo.setHeader(header);
			requestInfo.putParam(parameters);
			requestInfo.setUrl(url);
			intent.putExtra("RequestInfo", requestInfo);

			context.startService(intent);
		}
	}

	public void execute() {

	}
}