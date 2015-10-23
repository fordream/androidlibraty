package com.vnp.core.service.asyn;

import java.util.Set;

import org.json.JSONObject;

import android.os.Bundle;

import com.vnp.core.service.RequestMethod;
import com.vnp.core.service.RestClient;

public class ExecuteAPI {
	public String getParseCode() {
		return parseCode;
	}

	public String getParseData() {
		return parseData;
	}

	public String getParseDescription() {
		return parseDescription;
	}

	public String getApi() {
		return api;
	}

	public RestClient getClient() {
		return client;
	}

	private String parseCode = "";
	private String parseDescription = "";
	private String parseData = "";
	private String api;
	private RestClient client;
	private Bundle extras;

	public Bundle getExtras() {
		return extras;
	}

	String url;

	public ExecuteAPI(String url) {
		this.url = url;
	}

	public void execute(Bundle extras, String api) {
		this.api = api;
		this.extras = extras;
		client = new RestClient(url + api);
		Set<String> keys = extras.keySet();
		for (String key : keys) {
			client.addParam(key, extras.getString(key));
		}

		client.execute(RequestMethod.POST);

		try {
			JSONObject json = new JSONObject(client.getResponse());
			parseCode = json.getString("code");
			parseDescription = json.getString("description");
			parseData = json.getString("data");
		} catch (Exception e) {
		}
	}
}