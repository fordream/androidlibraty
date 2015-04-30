package com.vnp.core.service;

import android.os.Binder;

public class VNPServiceBinder extends Binder {
	private VNPService vnpService;

	public VNPServiceBinder(VNPService vnpService) {
		this.vnpService = vnpService;
	}

	public VNPService getVnpService() {
		return vnpService;
	}
}