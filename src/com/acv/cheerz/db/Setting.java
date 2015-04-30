package com.acv.cheerz.db;

import android.content.Context;

public class Setting extends SkypeTable {
	public Setting(Context context) {
		super(context);

		addColumns(lang);
	}

	public static final String lang = "lang";

}
