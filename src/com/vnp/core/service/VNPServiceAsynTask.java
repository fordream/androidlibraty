package com.vnp.core.service;

import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class VNPServiceAsynTask extends AsyncTask<String, String, String> {
	public void start() {
		execute("");
	}

	private Context context;
	private String api;

	public Context getContext() {
		return context;
	}

	public VNPServiceAsynTask(Context context, String api) {
		this.context = context;
		this.api = api;
	}

	public String getServiceUrl() {
		return "";
	}

	public Bundle createBundle() {
		Bundle extras = new Bundle();
		return extras;
	}

	public void run() {
	}

	public void finisḥ(ExecuteAPI execute) {

	}

	public void run(ExecuteAPI execute) {

	}

	// =============================================================================
	public ExecuteAPI execute(Bundle extras, String api) {
		ExecuteAPI executeAPI = new ExecuteAPI();
		executeAPI.execute(extras, api);
		return executeAPI;
	}

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
			if (client == null) {
				client = new RestClient(getServiceUrl() + api);
			}
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
		};

		public void execute(Bundle extras, String api) {
			this.api = api;
			this.extras = extras;
			client = new RestClient(getServiceUrl() + api);
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

	@Override
	final public String doInBackground(String... params) {
		run();
		return null;
	}
}