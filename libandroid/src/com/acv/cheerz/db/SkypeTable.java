package com.acv.cheerz.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.vnp.core.common.LogUtils;

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

	private void init(Context context, int index, String providerName) {
		this.context = context;
		USER_MATCHER = Math.abs(index + 1);
		USER_MATCHER_ID = Math.abs(index + 2);
		urlString = "content://" + providerName + "/" + getTableName();
		contentUri = Uri.parse(urlString);

		addColumns(_ID);
	}

	/**
	 * 
	 * @param context
	 */
	public SkypeTable(Context context, Class provider) {
		init(context, getIndex(), provider.getName());
	}

	public Set<String> getColumns() {
		return map.keySet();
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
		builder.append("CREATE TABLE ").append(getTableName());
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
		uriMatcher.addURI(PROVIDER_NAME, getTableName(), USER_MATCHER);
		uriMatcher.addURI(PROVIDER_NAME, getTableName() + "/#", USER_MATCHER_ID);
	}

	public void getType(Map<Integer, String> mMap) {
		mMap.put(USER_MATCHER, "vnd.android.cursor.dir/com.acv.cheerz");
		mMap.put(USER_MATCHER_ID, "vnd.android.cursor.item/com.acv.cheerz");
	}

	public int update(int match, SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (USER_MATCHER == match) {
			return db.update(getTableName(), values, selection, selectionArgs);
		} else if (USER_MATCHER_ID == match) {
			return db.update(getTableName(), values, "_id = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
		} else {
			return -2;
		}
	}

	public int delete(int match, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
		if (USER_MATCHER == match) {
			return db.delete(getTableName(), selection, selectionArgs);
		} else if (USER_MATCHER_ID == match) {
			String id = uri.getPathSegments().get(1);
			return db.delete(getTableName(), "_id = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
		} else {
			return -2;
		}
	}

	public Cursor query(int match, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(getTableName());
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
			long rowID = db.insert(getTableName(), "", values);
			if (rowID > 0) {
				Uri _uri = ContentUris.withAppendedId(getContentUri(), rowID);
				return _uri;
			}
		} else if (USER_MATCHER_ID == match) {
			long rowID = db.insert(getTableName(), "", values);
			if (rowID > 0) {
				Uri _uri = ContentUris.withAppendedId(getContentUri(), rowID);
				return _uri;
			}
		}

		return null;
	}

	public String getTableName() {
		return getClass().getSimpleName();
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

	public Cursor querry(String where, String order) {
		return context.getContentResolver().query(getContentUri(), null, where, null, order);
	}

	public void insert(ContentValues values) {
		if (getContext() != null) {
			getContext().getContentResolver().insert(getContentUri(), values);
		}
	}

	public void update(ContentValues values, String where) {
		if (getContext() != null) {
			getContext().getContentResolver().update(getContentUri(), values, where, null);
		}
	}

	public void delete(String where) {
		if (getContext() != null) {
			getContext().getContentResolver().delete(getContentUri(), where, null);
		}
	}

	/**
	 * 
	 * @param json
	 */
	public void add(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			add(jsonObject);
		} catch (Exception e) {
		}

		try {
			JSONArray jsonObject = new JSONArray(json);
			add(jsonObject);
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param object
	 */
	public void add(JSONObject object) {

		try {
			ContentValues values = createContentValuesFormJsonObject(object);
			// Set<String> colums = getColumns();
			// for (String column : colums) {
			// if (object.has(column)) {
			// values.put(column, object.getString(column));
			// }
			// }

			insert(values);

		} catch (Exception ex) {
		}
	}

	/**
	 * 
	 * @param object
	 * @param where
	 */
	public void add(JSONObject object, String where) {
		try {

			ContentValues values = createContentValuesFormJsonObject(object);
			if (has(where)) {
				update(values, where);
			} else {
				insert(values);
			}

		} catch (Exception ex) {
		}
	}

	/**
	 * 
	 * @param object
	 */
	public void add(JSONArray object) {
		try {
			for (int i = 0; i < object.length(); i++) {
				add(object.getJSONObject(i));
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 
	 * @param where
	 * @param columName
	 * @return
	 */
	public String getValueByName(String where, String columName) {
		String SchoolName = null;
		Cursor cursor = querry(where);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				SchoolName = CommonAndroid.getString(cursor, columName);
			}
			cursor.close();
		}
		return SchoolName;
	}

	public ContentValues createContentValuesFormJsonString(String json) {

		
		try {
			JSONObject jsonObject = new JSONObject(json);
			ContentValues values = new ContentValues();
			Set<String> colums = getColumns();
			for (String column : colums) {
				if (jsonObject.has(column)) {
					String data = CommonAndroid.decodeOctal(jsonObject.getString(column));
					values.put(column, data);
				}

			}
			return values;
		} catch (Exception ex) {
		}

		return null;

	}

	public ContentValues createContentValuesFormJsonObject(JSONObject jsonObject) {

		try {
			ContentValues values = new ContentValues();
			Set<String> colums = getColumns();
			for (String column : colums) {
				if (jsonObject.has(column)) {
					String data = CommonAndroid.getString(jsonObject, column);
					data = CommonAndroid.decodeOctal(data);
					values.put(column, data);
				}

			}
			return values;
		} catch (Exception ex) {
		}

		return null;
	}
}
