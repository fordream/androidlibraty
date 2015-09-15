package com.vnp.core.service.asyn;

import java.util.Set;

import org.json.JSONObject;

import com.vnp.core.service.RequestMethod;
import com.vnp.core.service.RestClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class VNPServiceAsynTask extends AsyncTask<String, String, String> {
	private ProgressDialog progressDialog;
	private boolean isRunning = false;

	public boolean isRunning() {
		return isRunning;
	}

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
		progressDialog = createProgressDialog();
	}

	public ProgressDialog createProgressDialog() {
		return null;
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
		ExecuteAPI executeAPI = new ExecuteAPI(getServiceUrl());
		executeAPI.execute(extras, api);
		return executeAPI;
	}

	@Override
	final public String doInBackground(String... params) {
		isRunning = true;
		run();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		isRunning = false;
	}
}