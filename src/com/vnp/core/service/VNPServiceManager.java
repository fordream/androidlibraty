package com.vnp.core.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class VNPServiceManager {
	private static VNPServiceManager serviceManager = new VNPServiceManager();
	private VNPService vnpService;

	public void execute(Context context) {

		if (context == null) {
			return;
		}

		if (vnpService != null) {
			vnpService.execute();
		} else {
			ServiceConnection conn = new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					vnpService = null;
				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					vnpService = ((VNPServiceBinder) service).getVnpService();
					vnpService.execute();
				}
			};

			context.bindService(new Intent(context, VNPService.class), conn, Context.BIND_AUTO_CREATE);
		}
	}

	public static VNPServiceManager getServiceManager() {
		if (serviceManager == null) {
			serviceManager = new VNPServiceManager();
		}
		return serviceManager;
	}

	private VNPServiceManager() {

	}
}