package com.vnp.core.googleservice;

public class InAppV3InforPurchaseItem {
	private String id;
	private String price;
	private boolean isSubs;

	public boolean isSubs() {
		return isSubs;
	}

	public void setSubs(boolean isSubs) {
		this.isSubs = isSubs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getprice() {
		return price;
	}

	public void setPrice(String value) {
		this.price = value;
	}
}
