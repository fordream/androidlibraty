package com.vnp.core.service.callapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vnp.core.common.LogUtils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ICallApiParacelable implements Parcelable {

	private JSONArray params = new JSONArray();
	private JSONArray headers = new JSONArray();

	public void addParam(String key, String value) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(key, value);
			params.put(jsonObject);
		} catch (JSONException e) {
		}
	}

	public void addHeader(String key, String value) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(key, value);
			headers.put(jsonObject);
		} catch (JSONException e) {
		}
	}

	private String method;

	private String url;
	private int responseCode;
	private String response;
	private String errorMessage;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static final Parcelable.Creator<ICallApiParacelable> CREATOR = new Parcelable.Creator<ICallApiParacelable>() {
		public ICallApiParacelable createFromParcel(Parcel in) {
			return new ICallApiParacelable(in);
		}

		public ICallApiParacelable[] newArray(int size) {
			return new ICallApiParacelable[size];
		}
	};

	public ICallApiParacelable() {
	}

	private ICallApiParacelable(Parcel in) {
		url = in.readString();
		responseCode = in.readInt();
		errorMessage = in.readString();
		response = in.readString();
		method = in.readString();
		try {
			headers = new JSONArray(in.readString());
			params = new JSONArray(in.readString());
		} catch (Exception exception) {

		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(url);
		out.writeInt(responseCode);
		out.writeString(errorMessage);
		out.writeString(response);
		out.writeString(method);
		out.writeString(headers.toString());
		out.writeString(params.toString());
	}

	public final String getActionCallBack() {
		return url + headers.toString() + params.toString();
	}

	public final String getUrl() {
		return url;
	}

	public final JSONArray getHeaders() {
		return headers;
	}

	public final JSONArray getParams() {
		return params;
	}

	public void update(Context context, String response) {

	}

	public void onSucssesOnBackground(Context context, int responseCode, String responseMessage, String response) {
	}

	public void onSucsses(Context context, int responseCode, String responseMessage, String response) {
	}

	public void onFailRemoteException() {

	}

	public void onServiceDisconnected() {

	}

	public void onFailContextNull() {

	}
}
