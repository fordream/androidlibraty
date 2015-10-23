package com.acv.cheerz.db;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareReferentDB {
	private Context context;
	private SharedPreferences preferences;

	public ShareReferentDB(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences(getNameDB(), 0);
	}

	public final String getNameDB() {
		return getClass().getName();
	}

}