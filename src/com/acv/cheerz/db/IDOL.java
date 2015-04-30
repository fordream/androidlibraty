package com.acv.cheerz.db;

import android.content.Context;

public class IDOL extends SkypeTable {
	public IDOL(Context context) {
		super(context);
		addColumns(AVATAR);
		addColumns(NAME);
		addColumns(ADDRESS);
		addColumns(NICKNAME);
		addColumns(FAVORITE_COLOR);
		addColumns(CHARACTERISTICS);
		addColumns(FACEBOOK);
		addColumns(NOTE);

	}

	public static final String ID = "id";
	public static final String AVATAR = "avatar";
	public static final String NAME = "name";
	public static final String ADDRESS = "address";
	public static final String NICKNAME = "nickname";
	public static final String FAVORITE_COLOR = "FAVORITE_COLOR";
	public static final String CHARACTERISTICS = "CHARACTERISTICS";
	public static final String FACEBOOK = "FACEBOOK";
	public static final String NOTE = "NOTE";

}