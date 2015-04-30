package com.acv.cheerz.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.vnp.core.common.CommonAndroid;

public abstract class SkypeTable {
	public static final String _ID = "_id";
	private Context context;
	private String urlString;// = "content://" + DBProvider.PROVIDER_NAME + "/"
								// +
	// tableName;
	private Uri contentUri;// = Uri.parse(URL);

	// matcher
	private int USER_MATCHER = 1;
	private int USER_MATCHER_ID = 2;

	private void init(Context context, int index) {
		tableName = getClass().getSimpleName();
		this.context = context;
		USER_MATCHER = index * 10 + 1;
		USER_MATCHER_ID = index * 10 + 2;
		urlString = "content://" + DBProvider.PROVIDER_NAME + "/" + tableName;
		contentUri = Uri.parse(urlString);

		addColumns(_ID);
	}

	public SkypeTable(Context context) {
		init(context, getIndex());
	}

	public Context getContext() {
		return context;
	}

	public String getUrlString() {
		return urlString;
	}

	public Uri getContentUri() {
		return contentUri;
	}

	private String tableName = "";
	private Map<String, String> map = new HashMap<String, String>();

	public void addColumns(String columnName) {
		if (!CommonAndroid.isBlank(columnName) && !map.containsKey(columnName)) {
			if (columnName.equals("_id")) {
				map.put(columnName, " INTEGER PRIMARY KEY AUTOINCREMENT ");
			} else {
				map.put(columnName, " TEXT ");
			}
		}
	}

	public final String createDbTable() {
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(tableName);
		builder.append("(");

		Set<String> keys = map.keySet();
		boolean first = true;

		for (String key : keys) {
			if (first) {
				builder.append(key).append(map.get(key));
			} else {
				builder.append(",").append(key).append(map.get(key));
			}

			first = false;
		}
		builder.append(")");

		return builder.toString();

	}

	public void addUriMatcher(UriMatcher uriMatcher, String PROVIDER_NAME) {
		uriMatcher.addURI(PROVIDER_NAME, tableName, USER_MATCHER);
		uriMatcher.addURI(PROVIDER_NAME, tableName + "/#", USER_MATCHER_ID);
	}

	public void getType(Map<Integer, String> mMap) {
		mMap.put(USER_MATCHER, "vnd.android.cursor.dir/com.acv.cheerz");
		mMap.put(USER_MATCHER_ID, "vnd.android.cursor.item/com.acv.cheerz");
	}

	public int update(int match, SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (USER_MATCHER == match) {
			return db.update(tableName, values, selection, selectionArgs);
		} else if (USER_MATCHER_ID == match) {
			return db.update(tableName, values, "_id = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
		} else {
			return -2;
		}
	}

	public int delete(int match, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
		if (USER_MATCHER == match) {
			return db.delete(tableName, selection, selectionArgs);
		} else if (USER_MATCHER_ID == match) {
			String id = uri.getPathSegments().get(1);
			return db.delete(tableName, "_id = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
		} else {
			return -2;
		}
	}

	public Cursor query(int match, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName);
		if (USER_MATCHER == match) {
			return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		} else if (USER_MATCHER_ID == match) {
			qb.appendWhere("_id =" + uri.getPathSegments().get(1));
			return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		} else {
			return null;
		}
	}

	public Uri insert(int match, SQLiteDatabase db, Uri uri, ContentValues values) {
		if (USER_MATCHER == match) {
			long rowID = db.insert(tableName, "", values);
			if (rowID > 0) {
				Uri _uri = ContentUris.withAppendedId(getContentUri(), rowID);
				return _uri;
			}
		} else if (USER_MATCHER_ID == match) {
			long rowID = db.insert(tableName, "", values);
			if (rowID > 0) {
				Uri _uri = ContentUris.withAppendedId(getContentUri(), rowID);
				return _uri;
			}
		}

		return null;
	}

	public String getTableName() {
		return tableName;
	}

	public final int getIndex() {
		return CommonAndroid.StringToInt(getTableName());
	}

	public boolean has(String where) {
		boolean has = false;
		Cursor cursor = querry(where);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				has = true;
			}
			cursor.close();
		}
		return has;
	}

	public Cursor querry(String where) {
		return context.getContentResolver().query(getContentUri(), null, where, null, null);
	}
}
