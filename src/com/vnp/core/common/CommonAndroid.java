package com.vnp.core.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vnp.core.service.RequestMethod;
import com.vnp.core.service.RestClient;
import com.vnp.core.service.RestClientCallBack;

@SuppressLint("NewApi")
public class CommonAndroid {

	public static void hideKeyboard(View view) {
		if (view != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

	}

	private static void append(JSONArray sb, String str1, String str2, boolean hasN) {

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", str1);
			jsonObject.put("value", str2);
			sb.put(jsonObject);
		} catch (Exception e) {
		}
	}

	private static JSONArray sb = null;

	public static final String decodeUnicodeEncodingToAStringOfLetters(final String in) {
		String working = in;
		int index;
		index = working.indexOf("\\u");
		while (index > -1) {
			int length = working.length();
			if (index > (length - 6))
				break;
			int numStart = index + 2;
			int numFinish = numStart + 4;
			String substring = working.substring(numStart, numFinish);
			int number = Integer.parseInt(substring, 16);
			String stringStart = working.substring(0, index);
			String stringEnd = working.substring(numFinish);
			working = stringStart + ((char) number) + stringEnd;
			index = working.indexOf("\\u");
		}

		if (sb == null) {
			sb = new JSONArray();
			append(sb, "040", " ", true);
			append(sb, "041", "!", true);
			append(sb, "042", "\"", true);
			append(sb, "043", "#", true);
			append(sb, "044", "$", true);
			append(sb, "045", "%", true);
			append(sb, "046", "&", true);
			append(sb, "047", "'", true);
			append(sb, "050", "(", true);
			append(sb, "051", ")", true);
			append(sb, "052", "*", true);
			append(sb, "053", "+", true);
			append(sb, "054", ",", true);
			append(sb, "055", "-", true);
			append(sb, "056", ".", true);
			append(sb, "057", "/", true);
			append(sb, "060", "0", true);
			append(sb, "071", "9", true);
			append(sb, "072", ":", true);
			append(sb, "073", ";", true);
			append(sb, "074", "<", true);
			append(sb, "075", "=", true);
			append(sb, "076", ">", true);
			append(sb, "077", "?", true);
			append(sb, "100", "@", true);
			append(sb, "101", "A", true);
			append(sb, "132", "Z", true);
			append(sb, "133", "[", true);
			append(sb, "134", "\\", true);
			append(sb, "135", "]", true);
			append(sb, "136", "^", true);
			append(sb, "137", "_", true);
			append(sb, "140", "`", true);
			append(sb, "141", "a", true);
			append(sb, "172", "z", true);
			append(sb, "173", "{", true);
			append(sb, "174", "	", true);
			append(sb, "175", "}", true);
			append(sb, "176", "~", true);
			append(sb, "240", " ", true);
			append(sb, "241", "¡", true);
			append(sb, "242", "¢", true);
			append(sb, "243", "£", true);
			append(sb, "244", "¤", true);
			append(sb, "245", "¥", true);
			append(sb, "246", "¦", true);
			append(sb, "247", "§", true);
			append(sb, "250", "¨", true);
			append(sb, "251", "©", true);
			append(sb, "252", "ª", true);
			append(sb, "253", "«", true);
			append(sb, "254", "¬", true);
			append(sb, "255", "­", true);
			append(sb, "256", "®", true);
			append(sb, "257", "¯", true);
			append(sb, "260", "°", true);
			append(sb, "261", "±", true);
			append(sb, "262", "²", true);
			append(sb, "263", "³", true);
			append(sb, "264", "´", true);
			append(sb, "265", "µ", true);
			append(sb, "266", "¶", true);
			append(sb, "267", "·", true);
			append(sb, "270", "¸", true);
			append(sb, "271", "¹", true);
			append(sb, "272", "º", true);
			append(sb, "273", "»", true);
			append(sb, "274", "¼", true);
			append(sb, "275", "½", true);
			append(sb, "276", "¾", true);
			append(sb, "277", "¿", true);
			append(sb, "300", "À", true);
			append(sb, "301", "Á", true);
			append(sb, "302", "Â", true);
			append(sb, "303", "Ã", true);
			append(sb, "304", "Ä", true);
			append(sb, "305", "Å", true);
			append(sb, "306", "Æ", true);
			append(sb, "307", "Ç", true);
			append(sb, "310", "È", true);
			append(sb, "311", "É", true);
			append(sb, "312", "Ê", true);
			append(sb, "313", "Ë", true);
			append(sb, "314", "Ì", true);
			append(sb, "315", "Í", true);
			append(sb, "316", "Î", true);
			append(sb, "317", "Ï", true);
			append(sb, "320", "Ð", true);
			append(sb, "321", "Ñ", true);
			append(sb, "322", "Ò", true);
			append(sb, "323", "Ó", true);
			append(sb, "324", "Ô", true);
			append(sb, "325", "Õ", true);
			append(sb, "326", "Ö", true);
			append(sb, "327", "×", true);
			append(sb, "330", "Ø", true);
			append(sb, "331", "Ù", true);
			append(sb, "332", "Ú", true);
			append(sb, "333", "Û", true);
			append(sb, "334", "Ü", true);
			append(sb, "335", "Ý", true);
			append(sb, "336", "Þ", true);
			append(sb, "337", "ß", true);
			append(sb, "340", "à", true);
			append(sb, "341", "á", true);
			append(sb, "342", "â", true);
			append(sb, "343", "ã", true);
			append(sb, "344", "ä", true);
			append(sb, "345", "å", true);
			append(sb, "346", "æ", true);
			append(sb, "347", "ç", true);
			append(sb, "350", "è", true);
			append(sb, "351", "é", true);
			append(sb, "352", "ê", true);
			append(sb, "353", "ë", true);
			append(sb, "354", "ì", true);
			append(sb, "355", "í", true);
			append(sb, "356", "î", true);
			append(sb, "357", "ï", true);
			append(sb, "360", "ð", true);
			append(sb, "361", "ñ", true);
			append(sb, "362", "ò", true);
			append(sb, "363", "ó", true);
			append(sb, "364", "ô", true);
			append(sb, "365", "õ", true);
			append(sb, "366", "ö", true);
			append(sb, "367", "÷", true);
			append(sb, "370", "ø", true);
			append(sb, "371", "ù", true);
			append(sb, "372", "ú", true);
			append(sb, "373", "û", true);
			append(sb, "374", "ü", true);
			append(sb, "375", "ý", true);
			append(sb, "376", "þ", true);
			append(sb, "377", "ÿ", false);
		}
		try {
			JSONArray array = sb;
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String key = "\\" + jsonObject.getString("name");
				String value = jsonObject.getString("value");
				working = working.replace(key, value);
			}
		} catch (Exception e) {
		}
		return working;
	}

	public static Bitmap getBitmapFromAsset(String assetFile, Context context) {
		AssetManager assetManager = context.getAssets();

		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(assetFile);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (Exception e) {
		}

		return bitmap;
	}

	/**
	 * 
	 * @param extras
	 * @param fileName
	 */
	public static void saveToFile(Bundle extras, String fileName) {
		StringBuilder report = new StringBuilder();
		Set<String> keys = extras.keySet();
		for (String key : keys) {
			report.append(key).append(":\n");
			report.append(extras.getString(key));
			report.append("\n");
		}
		saveToFile(report.toString(), fileName);
	}

	/**
	 * 
	 * @param extras
	 * @param fileName
	 */
	public static void saveToFile(String extras, String fileName) {
		try {
			FileOutputStream trace = new FileOutputStream(new File(fileName));
			trace.write(extras.toString().getBytes());
			trace.close();
		} catch (IOException ioe) {
		}
	}

	public static Bitmap base64ToBitmap(String myImageData) {
		byte[] imageAsBytes = Base64.decode(myImageData, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
	}

	public static void saveBitmapTofile(Bitmap bmp, File f) {

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 
	 * @param text
	 * @param str
	 */
	public static void setText(TextView text, String str) {
		if (text != null)
			text.setText(str);
	}

	/**
	 * 
	 * @param text
	 * @param cursor
	 * @param key
	 */
	public static void setText(TextView text, Cursor cursor, String key) {
		if (text != null)
			text.setText(getString(cursor, key));
	}

	/**
	 * 
	 * @param text
	 * @param cursor
	 * @param key
	 */
	public static void setText(TextView text, JSONObject cursor, String key) {
		if (text != null)
			text.setText(getString(cursor, key));
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param text
	 */
	public static void setText(Activity activity, int res, String text) {
		TextView textView = getView(activity, res);
		setText(textView, text);
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param text
	 */
	public static void setText(View activity, int res, String text) {
		TextView textView = getView(activity, res);
		setText(textView, text);
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param text
	 */
	public static void setText(Dialog activity, int res, String text) {
		TextView textView = getView(activity, res);
		setText(textView, text);
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param cursor
	 * @param key
	 */

	public static void setText(Activity activity, int res, Cursor cursor, String key) {
		TextView textView = getView(activity, res);
		setText(textView, getString(cursor, key));
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param cursor
	 * @param key
	 */
	public static void setText(View activity, int res, Cursor cursor, String key) {
		TextView textView = getView(activity, res);
		setText(textView, getString(cursor, key));
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param cursor
	 * @param key
	 */
	public static void setText(Dialog activity, int res, Cursor cursor, String key) {
		TextView textView = getView(activity, res);
		setText(textView, getString(cursor, key));
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param cursor
	 * @param key
	 */
	public static void setText(Activity activity, int res, JSONObject cursor, String key) {
		TextView textView = getView(activity, res);
		setText(textView, getString(cursor, key));
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param cursor
	 * @param key
	 */
	public static void setText(View activity, int res, JSONObject cursor, String key) {
		TextView textView = getView(activity, res);
		setText(textView, getString(cursor, key));
	}

	/**
	 * 
	 * @param activity
	 * @param res
	 * @param cursor
	 * @param key
	 */
	public static void setText(Dialog activity, int res, JSONObject cursor, String key) {
		TextView textView = getView(activity, res);
		setText(textView, getString(cursor, key));
	}

	/**
	 * Load infor of app from server
	 */
	public static void loadAppInfor(String packageName, RestClientCallBack restClientCallBack) {
		String url = "http://api.playstoreapi.com/v1.1/apps/%s?key=0f9723628e28a2863004b44f2440aeed";
		url = String.format(url, packageName);

		RestClient client = new RestClient(url);
		client.execute(RequestMethod.GET, restClientCallBack);
	}

	/**
	 * 
	 * 
	 * @author teemo
	 * 
	 */
	public interface LoadThread {
		public Object execute();

		public void onSucess(Object object);
	};

	public void loadThread(final long time, final LoadThread loadThread) {

		Runnable runnable = new Runnable() {
			final Handler handler = new Handler() {
				@Override
				public void dispatchMessage(Message msg) {
					super.dispatchMessage(msg);

					loadThread.onSucess(object);
				}
			};
			Object object;

			@Override
			public void run() {
				try {
					if (time >= 0) {
						Thread.sleep(time);
					} else {
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
				}

				object = loadThread.execute();
				handler.sendEmptyMessage(0);
			}
		};

		new Thread(runnable).start();
	}

	public static int StringToInt(String str) {
		if (isBlank(str)) {
			str = "";
		}

		return str.hashCode();
	}

	public static Bitmap getBitmapFromContactId(Context context, String contactId, File yourFile) {
		Bitmap bitmap = null;
		try {
			Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(contactId));
			Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
			Cursor cursor = context.getContentResolver().query(photoUri, new String[] { Contacts.Photo.PHOTO }, null, null, null);
			// File yourFile = new
			// File(Environment.getExternalStorageDirectory(),
			// System.currentTimeMillis() + "");
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(yourFile));
					bos.write(data);
					bos.flush();
					bos.close();
				}
			}

			bitmap = decodeFile(yourFile);
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception exception) {

		}
		return bitmap;
	}

	public static final void viewPagerOrListViewSetSlowChangePage(ViewGroup pager, final int mDuration) {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");

			if (pager instanceof ListView) {
				mScroller = ListView.class.getDeclaredField("mScroller");
			} else {

			}
			mScroller.setAccessible(true);
			Scroller scroller = new Scroller(pager.getContext()) {

				@Override
				public void startScroll(int startX, int startY, int dx, int dy, int duration) {
					super.startScroll(startX, startY, dx, dy, mDuration);
				}

				@Override
				public void startScroll(int startX, int startY, int dx, int dy) {
					super.startScroll(startX, startY, dx, dy, mDuration);
				}
			};

			mScroller.set(pager, scroller);
		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}

	public static final void setAnimationOnClick(View view) {
		OnTouchListener onTouchListener = new OnTouchListener() {
			private Animation a_normal, a_selected, a_current;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				/**
				 */
				// a_normal = new AlphaAnimation(1, 1f);
				if (a_normal == null) {
					a_normal = new ScaleAnimation(1, 1, 1, 1, v.getWidth() / 2, v.getHeight() / 2);
					a_normal.setDuration(0);
					a_normal.setFillAfter(true);
				}
				// a_selected = new AlphaAnimation(0.5f, 0.5f);
				if (a_selected == null) {
					a_selected = new ScaleAnimation(0.9f, 0.9f, 0.9f, 0.9f, v.getWidth() / 2, v.getHeight() / 2);
					a_selected.setDuration(0);
					a_selected.setFillAfter(true);
				}
				if (a_current == null) {
					a_current = a_normal;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					a_current = a_selected;
					v.startAnimation(a_current);
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					a_current = a_normal;
					v.startAnimation(a_current);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					float x = event.getX();
					float y = event.getY();
					float w = v.getWidth();
					float h = v.getHeight();
					if (x < 0 || x > w) {
						if (a_current.equals(a_selected)) {
							a_current = a_normal;
							v.startAnimation(a_current);
						}
					} else if (y < 0 || y > h) {
						if (a_current.equals(a_selected)) {
							a_current = a_normal;
							v.startAnimation(a_current);
						}
					} else {
						// if (a_current.equals(a_normal)) {
						// a_current = a_selected;
						// v.startAnimation(a_current);
						// }
					}
				}
				return false;
			}

		};
		if (view != null) {
			view.setOnTouchListener(onTouchListener);
		}
	}

	public static boolean isBlank(String columnName) {
		return columnName == null || columnName != null && columnName.trim().equals("");
	}

	public static void showKeyBoard(EditText keEditText) {
		keEditText.requestFocus();
		InputMethodManager imm = (InputMethodManager) keEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(keEditText.getWindowToken(), 0);
		imm.showSoftInput(keEditText, InputMethodManager.SHOW_IMPLICIT);

	}

	/**
	 * 
	 * @param listView
	 */
	public static void disableListTypeIphone(ListView listView) {

	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 480;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE | height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	public static String fileImageToBase64(Context context, String url) {
		try {
			Bitmap bitmap = null;
			if (url != null && url.startsWith("content://")) {
				try {
					bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(url));
				} catch (Exception e) {

				}
			} else if (url != null && url.startsWith("file://")) {
				url = url.substring(url.indexOf("file://") + 7, url.length());
				bitmap = decodeFile(new File(url));
			} else {
				bitmap = decodeFile(new File(url));
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
																	// bitmap
																	// object
			byte[] b = baos.toByteArray();
			return Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			return "";
			// TODO: handle exception
		}
	}

	public static String bitmapToBase64(Bitmap bitmap) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			return Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param cursor
	 * @param key
	 * @return
	 */
	public static String getString(Cursor cursor, String key) {
		try {
			String str = cursor.getString(cursor.getColumnIndex(key));

			if ("null".equals(str)) {
				str = "";
			}

			if (str == null)
				str = "";

			return decodeUnicodeEncodingToAStringOfLetters(str);
		} catch (Exception exception) {
			return "";
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static String getString(JSONObject jsonObject, String key) {
		try {
			String str = jsonObject.getString(key);

			if ("null".equals(str)) {
				str = "";
			}

			if (str == null)
				str = "";

			return decodeUnicodeEncodingToAStringOfLetters(str);
			// return str;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 
	 * @param v
	 * @param res
	 * @return
	 */
	public static <T extends View> T getView(View v, int res) {
		T t = (T) v.findViewById(res);
		return t;
	}

	public static <T extends View> T getView(Dialog v, int res) {
		T t = (T) v.findViewById(res);
		return t;
	}

	/**
	 * 
	 * @param v
	 * @param res
	 * @return
	 */
	public static <T extends View> T getView(Activity v, int res) {
		T t = (T) v.findViewById(res);
		return t;
	}

	/**
	 * 
	 * @param context
	 * @param res
	 * @param viewGroup
	 * @return
	 */
	public static View getView(Context context, int res, ViewGroup viewGroup) {
		return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(res, viewGroup);
	}

	public interface ScreenCallBack {
		// when screen off
		public void screenOff();

		// when screen on but haven't lock
		public void screenOn();

		// when screen on but have lock
		public void screenOnHaveLock();

		// screen unlock
		public void screenUnlock();
	}

	/**
	 * 
	 * @param context
	 * @param screenCallBack
	 */
	public static void registerScreenAction(final Context context, final ScreenCallBack screenCallBack) {
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					screenCallBack.screenOff();
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					if (!haveLockScreen(context)) {
						screenCallBack.screenOn();
					} else {
						screenCallBack.screenOnHaveLock();
					}
				} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
					screenCallBack.screenUnlock();
				}
			}

		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		context.registerReceiver(broadcastReceiver, filter);
	}

	/**
	 * 
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog(Context context, String message, OnClickListener listener) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", listener);
		builder.show();
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean haveLockScreen(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		return km.inKeyguardRestrictedInputMode();
	}

	/**
	 * 
	 * @param context
	 * @param publish
	 */
	public static void showMarketPublish(Context context, String publish) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://search?q=pub:" + publish));
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param pack
	 */
	public static void showMarketProductBuyPackage(Context context, String pack) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + pack));
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param phone
	 * @return
	 */
	public static boolean callPhone(Context context, String phone) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phone));
			context.startActivity(callIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	public static boolean callWeb(Context context, String url) {
		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(myIntent);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param activity
	 */
	public static void hiddenKeyBoard(Activity activity) {
		try {
			String service = Context.INPUT_METHOD_SERVICE;
			InputMethodManager imm = null;
			imm = (InputMethodManager) activity.getSystemService(service);
			IBinder binder = activity.getCurrentFocus().getWindowToken();
			imm.hideSoftInputFromWindow(binder, 0);
		} catch (Exception e) {
		}
	}

	public static void setOrientation(Activity activity, boolean islandscape) {
		if (islandscape) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public static void hiddenTitleBarAndFullScreen(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		activity.getWindow().setFlags(flag, flag);
	}

	public static void showKeyBoard(Activity activity, EditText editText) {
		String service = Context.INPUT_METHOD_SERVICE;
		InputMethodManager imm = null;
		imm = (InputMethodManager) activity.getSystemService(service);
		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}

	public static void showKeyBoard(Activity activity) {
		String service = Context.INPUT_METHOD_SERVICE;
		InputMethodManager imm = null;
		imm = (InputMethodManager) activity.getSystemService(service);
		imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_FORCED);
	}

	public static int getVersionApp(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static boolean checkApplicationRunning(Context context) {
		// <uses-permission android:name="android.permission.GET_TASKS" />
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		if (packageName.equalsIgnoreCase(context.getPackageName())) {
			return true;
		}
		return false;
	}

	public static boolean getAllPackage(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		for (int i = 0; i < procInfos.size(); i++) {

			LogUtils.e("getAllPackage", procInfos.get(i).processName);
		}

		return false;
	}

	public static Intent getLaucher(String otherPackage, Context context) {
		PackageManager packageManager = (PackageManager) context.getPackageManager();
		return packageManager.getLaunchIntentForPackage(otherPackage);
	}

	/**
	 * @param packageName
	 * @return
	 */
	public static boolean isAppRuning(String packageName, Context mContext) {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		for (int i = 0; i < procInfos.size(); i++) {
			if (procInfos.get(i).processName.equals(packageName)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAppRunningOnTop(Context mContext) {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		if (packageName.equalsIgnoreCase(mContext.getPackageName())) {
			return true;
		}
		return false;
	}

	public static boolean checkPermission(String permission, Context mContext) {
		PackageManager packageManager = mContext.getPackageManager();
		return (packageManager.checkPermission(permission, mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED);
	}

	// ============================================================================
	// GPS
	// ============================================================================
	public static class GPS {
		public static boolean isSupportGPS(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> lAllProviders = manager.getAllProviders();
			for (int i = 0; i < lAllProviders.size(); i++) {
				if (LocationManager.GPS_PROVIDER.equals(lAllProviders.get(i))) {
					return true;
				}
			}
			return false;
		}

		public static void showGPSSetting(Context context, int request_code) {
			String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
			Intent intent = new Intent(action);
			((Activity) context).startActivityForResult(intent, request_code);
		}

		public static void onpenGPS(Context context) {
			final Intent poke = new Intent();
			String packageName = "com.android.settings";
			String className = "com.android.settings.widget.SettingsAppWidgetProvider";
			poke.setClassName(packageName, className);
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);
		}

		public static void turnOffGPS(Context context) {
			Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);

		}

		public static boolean isOpenGPS(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
	}

	// ============================================================================
	// NetWork
	// ============================================================================
	public static class NETWORK {
		public static boolean haveConnected(Context context) {
			Object service = context.getSystemService(Context.CONNECTIVITY_SERVICE);
			ConnectivityManager connectivityManager = (ConnectivityManager) service;
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				if (networkInfos[i].isConnected()) {
					return true;
				}
			}

			return false;
		}

		public static void opennetworkSim(Context context, int requestCode) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
			((Activity) context).startActivityForResult(intent, requestCode);
		}

		public static void openWIFISetting(Context context, int requestCode) {
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			((Activity) context).startActivityForResult(intent, requestCode);
		}

		public static void openNetWorkSetting(Context context, int requestCode) {
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			((Activity) context).startActivityForResult(intent, requestCode);
		}
	}

	// ============================================================================
	// Account
	// ============================================================================
	public static class ACCOUNT {
		/**
		 * Check account google
		 * 
		 * @param context
		 * @return
		 */
		public static boolean haveMailAccountGoogleOnDevice(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account[] accounts = accountManager.getAccountsByType("com.google");
			Account account;
			if (accounts.length > 0) {
				account = accounts[0];
			} else {
				account = null;
			}

			return account != null;
		}

	}

	// ============================================================================
	// ROOT CHECKER
	// ============================================================================
	public static class ROOTCHECKER {

		public static boolean checkRoot() {

			Process p;
			try {
				// Perform SU to get root privledges
				p = Runtime.getRuntime().exec("su");

				DataOutputStream os = new DataOutputStream(p.getOutputStream());
				os.writeBytes("remount rw");
				os.writeBytes("echo Root Test > /system/rootcheck.txt");
				os.writeBytes("exit\n");
				os.flush();

				p.waitFor();
				if (p.exitValue() != 255) {
					return true;
					// Phone is rooted
				} else {
					// Phone is not rooted
				}
			} catch (Exception e) {

			}
			return false;

		}
	}

	// ============================================================================
	// FONT
	// ============================================================================
	public static class FONT {
		private static FONT instance = new FONT();
		private Application application;

		public static FONT getInstance() {
			return instance;
		}

		public void init(Application application) {
			this.application = application;
		}

		private FONT() {
		}

		private Map<String, Typeface> typeFaces = new HashMap<String, Typeface>();

		public boolean addTypeFaces(String assetPath) {
			boolean success = false;
			if (!typeFaces.containsKey(typeFaces)) {
				AssetManager assertManager = application.getAssets();
				Typeface tf = Typeface.createFromAsset(assertManager, assetPath);
				if (tf != null) {
					typeFaces.put(assetPath, tf);
					success = true;
				}
			} else {
				success = true;
			}

			return success;
		}

		public void setTypeFace(TextView textView, String assetPath) {
			try {
				addTypeFaces(assetPath);
				Typeface tf = typeFaces.get(assetPath);
				if (tf != null)
					textView.setTypeface(tf);
			} catch (Exception ex) {
			}
		}

		// public public static final String PATH =
		// "src/org/com/cnc/common/android/font/";

		// public static final String BRADHITC = "BRADHITC.TTF";
		// public static final String AGENCYB = "AGENCYB.TTF";
		// public static final String BROADW = "BROADW.TTF";
		// public static final String ALGER = "ALGER.TTF";

		// public void setTypeface(TextView tv, String fileAsset) {
		// try {
		// File file = new File(PATH + fileAsset);
		// Log.i("file", file.exists() + "");
		// Typeface tf = Typeface.createFromFile(file);
		// tv.setTypeface(tf);
		// } catch (Exception e) {
		// }
		// }

		// public void setTypefaceFromAsset(TextView tv, String fileAsset) {
		// try {
		// AssetManager assertManager = tv.getContext().getAssets();
		// Typeface tf = Typeface.createFromAsset(assertManager, fileAsset);
		// tv.setTypeface(tf);
		// } catch (Exception e) {
		// }
		// }

	}

	// ============================================================================
	// CommonDeviceId
	// ============================================================================

	public static class DEVICEID {
		private static final int SIZE_10 = 10;
		public static final String TYPE_ID_IMEI = "IMEI";
		public static final String TYPE_ID_IPSEUDO_UNIQUE_ID = "Pseudo_Unique_Id";
		public static final String TYPE_ID_IANDROIDID = "AndroidId";
		public static final String TYPE_ID_IWLAN_MAC_ADDRESS = "WLAN_MAC_Address";
		public static final String TYPE_ID_IBT_MAC_ADDRESS = "BT_MAC_Address";
		public static final String TYPE_ID_ICOMBINED_DEVICE_ID = "Combined_Device_ID";

		public static final int SIZE_WIDTH_Y = 240;// Galaxy Y
		public static final int SIZE_HEIGHT_Y = 320;// Galaxy Y

		public static final int SIZE_WIDTH_EMULATOR_16 = 320;// EMULATOR
		public static final int SIZE_HEIGHT_EMULATOR_16 = 480;// EMULATOR

		public static final int SIZE_WIDTH_S = 480;// Galaxy S
		public static final int SIZE_HEIGHT_S = 800;// Galaxy S

		public static final int SIZE_WIDTH_TAB = 600;// Tab 7'
		public static final int SIZE_HEIGHT_TAB = 1024;// Tab 7'

		public static final int SIZE_WIDTH_VIEWSONIC = 600;// View Sonic
		public static final int SIZE_HEIGHT_VIEWSONIC = 1024;// View Sonic\

		public static boolean isTablet(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			int min = width < height ? width : height;
			if (min > SIZE_WIDTH_S) {
				return true;
			}

			return false;
		}

		public static int getWidth(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			return display.getWidth();
		}

		public static int getHeight(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			return display.getHeight();
		}

		// Device ID
		public static String deviceId(Context context, String type) {
			if (TYPE_ID_IANDROIDID.equals(type)) {
				return deviceIdFromAndroidId(context);
			} else if (TYPE_ID_IBT_MAC_ADDRESS.equals(type)) {
				return deviceIdFromBT_MAC_Address(context);
			} else if (TYPE_ID_ICOMBINED_DEVICE_ID.equals(type)) {
				return deviceIdFromCombined_Device_ID(context);
			} else if (TYPE_ID_IMEI.equals(type)) {
				return deviceIdFromIMEI(context);
			} else if (TYPE_ID_IPSEUDO_UNIQUE_ID.equals(type)) {
				return deviceIdFromIMEI(context);
			} else if (TYPE_ID_IWLAN_MAC_ADDRESS.equals(type)) {
				return deviceIdFromWLAN_MAC_Address(context);
			}

			return null;
		}

		private static String deviceIdFromIMEI(Context context) {
			try {
				TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				return TelephonyMgr.getDeviceId();
			} catch (Exception e) {
				return null;
			}

		}

		private static String deviceIdFromPseudo_Unique_Id() {
			StringBuilder builder = new StringBuilder();
			builder.append("35");
			builder.append(Build.BOARD.length() % SIZE_10);
			builder.append(Build.BRAND.length() % SIZE_10);
			builder.append(Build.CPU_ABI.length() % SIZE_10);
			builder.append(Build.DEVICE.length() % SIZE_10);
			builder.append(Build.DISPLAY.length() % SIZE_10);
			builder.append(Build.HOST.length() % SIZE_10);
			builder.append(Build.ID.length() % SIZE_10);
			builder.append(Build.MANUFACTURER.length() % SIZE_10);
			builder.append(Build.MODEL.length() % SIZE_10);
			builder.append(Build.PRODUCT.length() % SIZE_10);
			builder.append(Build.TAGS.length() % SIZE_10);
			builder.append(Build.TYPE.length() % SIZE_10);
			builder.append(Build.USER.length() % SIZE_10);
			return builder.toString();
		}

		private static String deviceIdFromAndroidId(Context context) {
			try {
				ContentResolver cr = context.getContentResolver();
				return Secure.getString(cr, Secure.ANDROID_ID);
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromWLAN_MAC_Address(Context context) {
			try {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				return wm.getConnectionInfo().getMacAddress();
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromBT_MAC_Address(Context context) {
			try {
				BluetoothAdapter m_BluetoothAdapter = null;
				m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				return m_BluetoothAdapter.getAddress();
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromCombined_Device_ID(Context context) {
			StringBuilder builder = new StringBuilder();
			builder.append(deviceIdFromIMEI(context));
			builder.append(deviceIdFromPseudo_Unique_Id());
			builder.append(deviceIdFromAndroidId(context));
			builder.append(deviceIdFromWLAN_MAC_Address(context));
			builder.append(deviceIdFromBT_MAC_Address(context));

			String m_szLongID = builder.toString();
			MessageDigest m = null;
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
			byte p_md5Data[] = m.digest();
			String m_szUniqueID = new String();
			for (int i = 0; i < p_md5Data.length; i++) {
				int b = (0xFF & p_md5Data[i]);
				if (b <= 0xF)
					m_szUniqueID += "0";
				m_szUniqueID += Integer.toHexString(b);
			}

			return m_szUniqueID;
		}

		public static boolean canCallPhone(Context context) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager.getSimSerialNumber() != null) {
				if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
					return true;
				}
			}

			return false;
		}

		public static void rescanSdcard(Context context) {
			new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
			intentFilter.addDataScheme("file");
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		}

		public static String findDeviceID(Context context) {
			String deviceID = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			return deviceID;
		}
	}

	// ============================================================================
	// RESIZE
	// ============================================================================
	public static class RESIZE {

		public static int getWidthScreen(Context context) {
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			return display.getWidth();
		}

		public static int getHeightScreen(Context context) {
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			return display.getHeight();
		}

		// -------------------------------------------------------
		// 20130408 fix
		// -------------------------------------------------------
		public static float _20130408_ScaleLandW960H640(Context context) {
			try {
				float SCREEN_HIGHT = 640;
				float SCREEN_WIDTH = 960;

				float res_width = getWidthScreen(context);
				float res_height = getHeightScreen(context);

				float scale = res_height / SCREEN_HIGHT;

				if (SCREEN_HIGHT / res_height < SCREEN_WIDTH / res_width) {
					scale = res_width / SCREEN_WIDTH;
				}

				return scale;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeLandW960H640(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleLandW960H640(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenLandW960H640(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleLandW960H640(context));
		}

		public static void _20130408_sendViewToPositionLandW960H640(View view, int left, int top) {
			float scale = _20130408_ScaleLandW960H640(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}

		// Scale Width
		public static float _20130408_ScaleW960(Context context) {
			try {
				float SCREEN_WIDTH = 960;
				float res_width = getWidthScreen(context);
				return res_width / SCREEN_WIDTH;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeW960(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleW960(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			if (width == LayoutParams.WRAP_CONTENT) {
				layoutParams.width = LayoutParams.WRAP_CONTENT;
			} else if (width == LayoutParams.MATCH_PARENT) {
				layoutParams.width = LayoutParams.MATCH_PARENT;
			} else if (width == LayoutParams.FILL_PARENT) {
				layoutParams.width = LayoutParams.FILL_PARENT;
			}

			if (height == LayoutParams.WRAP_CONTENT) {
				layoutParams.height = LayoutParams.WRAP_CONTENT;
			} else if (height == LayoutParams.MATCH_PARENT) {
				layoutParams.height = LayoutParams.MATCH_PARENT;
			} else if (height == LayoutParams.FILL_PARENT) {
				layoutParams.height = LayoutParams.FILL_PARENT;
			}

			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenW960(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleW960(context));
		}

		public static void _20130408_sendViewToPositionW960(View view, int left, int top) {
			float scale = _20130408_ScaleW960(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}

		// Height
		// Scale Width
		public static float _20130408_ScaleH960(Context context) {
			try {
				float SCREEN_WIDTH = 960;
				float res_width = getHeightScreen(context);
				return res_width / SCREEN_WIDTH;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeH960(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleH960(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenH960(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleH960(context));
		}

		public static void _20130408_sendViewToPositionH960(View view, int left, int top) {
			float scale = _20130408_ScaleH960(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}
	}

	// ============================================================================
	// SHORTCUT
	// ============================================================================
	/**
	 * <uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	 * 
	 * @author truongvv
	 * 
	 */
	public static class SHORTCUT {
		private Context context;

		public SHORTCUT(Context context) {
			this.context = context;
		}

		public void deleteShortCut(Class<?> cls, int resstrName, int resIcon) {
			Intent removeIntent = createIntent(cls, "com.android.launcher.action.UNINSTALL_SHORTCUT", resstrName, resIcon);
			context.sendBroadcast(removeIntent);
		}

		public void autoCreateShortCut(Class<?> clss, int resstrName, int resIcon) {
			Intent intentShortcut = createIntent(clss, null, resstrName, resIcon);
			intentShortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(intentShortcut);
		}

		private Intent createIntent(Class<?> cls, String action, int resstrName, int resIcon) {

			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// -------------------------------------------------------------
			// sam sung 2x
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.BRAND.equals("samsung")) {
				// sam sung 2.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.BRAND.equals("samsung")) {
				// sam sung 4.x
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setPackage(context.getPackageName());
				shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else {
				// orther
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setPackage(context.getPackageName());
			}
			// -------------------------------------------------------

			shortcutIntent.setClass(context, cls);

			Intent intentShortcut = new Intent();
			if (action != null) {
				intentShortcut = new Intent(action);
			}
			intentShortcut.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
			String title = context.getResources().getString(resstrName);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			// intentShortcut
			// .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intentShortcut.putExtra("duplicate", false);

			final int icon = resIcon;

			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, icon));
			return intentShortcut;
		}

		public void removieShortCutLauncher() {
			Intent intent = CommonAndroid.getLaucher(context.getPackageName(), context);
			intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
			context.sendBroadcast(intent);
		}

		public void createShortCutLauncher(int resstrName, int resIcon) {
			Intent intent = CommonAndroid.getLaucher(context.getPackageName(), context);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //

			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// -------------------------------------------------------------
			// sam sung 2x
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.BRAND.equals("samsung")) {
				// sam sung 2.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.BRAND.equals("samsung")) {
				// sam sung 4.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				// shortcutIntent.setPackage(context.getPackageName());
				intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else {
				// orther
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				// shortcutIntent.setPackage(context.getPackageName());
			}
			// -------------------------------------------------------

			Intent intentShortcut = new Intent();
			intentShortcut.putExtra("android.intent.extra.shortcut.INTENT", intent);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(resstrName));
			intentShortcut.putExtra("duplicate", false);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, resIcon));
			intentShortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(intent);
		}

	}

	// ============================================================================
	// STORE AVAILABLE
	// ============================================================================
	public static class STOREAVAIABLE {
		public static long avaiableInternalStoreMemory() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
			return (bytesAvailable / 1048576);
		}

		public static long totalInternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
			return bytesAvailable / 1048576;
		}

		public static long availableExternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
			return bytesAvailable / 1048576;
		}

		public static long totalExternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
			return bytesAvailable / 1048576;
		}
	}

	/**
	 * 
	 */

	public static final class StringConnvert {
		public static final String convertVNToAlpha(String str) {
			if (isBlank(str)) {
				return str;
			}
			String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			// D D d d
			return pattern.matcher(temp).replaceAll("").replaceAll("Ã„ï¿½", "D").replaceAll("Ã„â€˜", "d");
		}

		private static boolean isBlank(String str) {
			return str == null || (str != null && str.trim().equals(""));
		}
	}

	public static class VNPPartternChecked {

		/**
		 * 
		 * @param email
		 */
		public static final boolean isEmail(String email) {
			final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			return pattern.matcher(email).matches();
		}

	}

	public static class Md5Checker {

		/**
		 * getMd5 of file
		 * 
		 * @param filePath
		 * @return blank if false
		 */
		public static String getMd5(String filePath) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				FileInputStream fis = new FileInputStream(filePath);

				byte[] dataBytes = new byte[1024];

				int nread = 0;
				while ((nread = fis.read(dataBytes)) != -1) {
					md.update(dataBytes, 0, nread);
				}
				byte[] mdbytes = md.digest();

				// convert the byte to hex format method 1
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < mdbytes.length; i++) {
					sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
				}

				// convert the byte to hex format method 2
				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < mdbytes.length; i++) {
					String hex = Integer.toHexString(0xff & mdbytes[i]);
					if (hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}

				return hexString.toString();
			} catch (Exception exception) {
				return "";
			}
		}
	}

	public static class UnZipExecute {
		public static final String TAG = UnZipExecute.class.getName();
		private static final int BUFFER = 8 * 1024;

		public boolean execute(String pathFileZip, String pathFolderUnZip, boolean isDeleteFileZip) {

			// create folder unzip
			LogUtils.e(TAG, "path zip file : " + pathFileZip);
			LogUtils.e(TAG, "path unzip folder : " + pathFolderUnZip);

			File mDirectory = new File(pathFolderUnZip);

			if (!mDirectory.exists()) {
				mDirectory.mkdirs();
			}

			// recheck path
			if (!mDirectory.exists()) {
				LogUtils.e(TAG, "start unzip file fail check file path or permistion : " + pathFileZip);
				return false;
			}

			LogUtils.e(TAG, "start unzip file : " + pathFileZip);
			LogUtils.e(TAG, "path unzip folder : don't created");

			File file = new File(pathFileZip);
			try {
				ZipFile zip = new ZipFile(file);
				Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
				while (zipFileEntries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
					String currentEntry = entry.getName();
					File destFile = new File(pathFolderUnZip, currentEntry);
					File destinationParent = destFile.getParentFile();

					destinationParent.mkdirs();

					if (!entry.isDirectory()) {
						BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));

						LogUtils.e(TAG, "file name of zip : " + currentEntry);

						int currentByte;
						FileOutputStream fos = new FileOutputStream(destFile);

						byte data[] = new byte[BUFFER];
						BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

						while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, currentByte);
						}

						dest.flush();
						dest.close();
						is.close();
					}
				}

				LogUtils.e(TAG, "unzip sucess  : " + pathFileZip);
				if (isDeleteFileZip) {
					file.delete();
				}
			} catch (Exception e) {
				LogUtils.e(TAG, "unzip fail  : " + pathFileZip);
				return false;
			}
			return true;
		}
	}

	public static class VnpLoaderClassUtils {
		/**
		 * 
		 * @param pakageClassName
		 *            example: org.com.MClass
		 * @return
		 */
		public static Object getObject(String pakageClassName) {
			Object object = null;

			try {
				Class<?> c = Class.forName(pakageClassName);
				object = c.getConstructor().newInstance();
			} catch (ClassNotFoundException e) {
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			}

			return object;
		}

		/**
		 * 
		 * @param pakageClassName
		 *            example: org.com.MClass
		 * @return
		 */
		public static Object getObject(String pakageClassName, Context context) {
			Object object = null;

			try {
				Class<?> c = Class.forName(pakageClassName);
				object = c.getConstructor().newInstance(context);
			} catch (ClassNotFoundException e) {
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			}

			return object;
		}
	}

	/**
	 * Fragment save state
	 */

	public final static class FragmentState {

		public enum FragmentStateEnum {//
			none, //
			clear, //
			onActivityCreated, //
			onActivityDestroyed, //
			onActivityPaused, //
			onActivityResumed, //
			onActivitySaveInstanceState, //
			onActivityStarted, //
			onActivityStopped//
		}//

		public FragmentStateEnum getFragmentStateEnum(Fragment f, Context context) {
			FragmentStateEnum stateEnum = FragmentStateEnum.none;
			SharedPreferences preferences = getSharedPreferences(context);
			String value = preferences.getString(f.getClass().getName(), "");
			if (preferences != null && f != null) {
				stateEnum = FragmentStateEnum.clear;

				FragmentStateEnum[] states = new FragmentStateEnum[] {//
				FragmentStateEnum.none, //
						FragmentStateEnum.clear, //
						FragmentStateEnum.onActivityCreated, //
						FragmentStateEnum.onActivityDestroyed, //
						FragmentStateEnum.onActivityPaused, //
						FragmentStateEnum.onActivityResumed, //
						FragmentStateEnum.onActivitySaveInstanceState, //
						FragmentStateEnum.onActivityStarted, //
						FragmentStateEnum.onActivityStopped //
				};

				for (FragmentStateEnum state : states) {
					if (state.toString().equals(value)) {
						stateEnum = state;
						break;
					}
				}
			}
			return stateEnum;
		}

		private static FragmentState instance = new FragmentState();
		private Context context;

		public static FragmentState getInstance() {
			return instance;
		}

		private FragmentState() {
		}

		private SharedPreferences getSharedPreferences(Context context) {
			if (this.context == null) {
				this.context = context;
			}

			if (this.context != null) {
				return this.context.getSharedPreferences(FragmentState.class.getName(), 0);
			}

			return null;
		}

		public void saveSharedPreferences(Fragment f, Context context, Bundle extras, FragmentStateEnum stateEnum) {
			SharedPreferences preferences = getSharedPreferences(context);
			if (preferences != null) {
				if (stateEnum == FragmentStateEnum.clear) {
					preferences.edit().clear().commit();
				} else {
					if (f != null) {
						preferences.edit().putString(f.getClass().getName(), stateEnum.toString()).commit();
					}
				}
			}
		}

		public void clear(Context context) {
			saveSharedPreferences(null, context, null, FragmentStateEnum.clear);
		}

		public void onActivityCreated(Fragment f, Context context, Bundle extras) {
			saveSharedPreferences(f, context, extras, FragmentStateEnum.onActivityCreated);
		}

		public void onActivityDestroyed(Fragment f, Context context) {
			saveSharedPreferences(f, context, null, FragmentStateEnum.onActivityDestroyed);
		}

		public void onActivityPaused(Fragment f, Context context) {
			saveSharedPreferences(f, context, null, FragmentStateEnum.onActivityPaused);
		}

		public void onActivityResumed(Fragment f, Context context) {
			saveSharedPreferences(f, context, null, FragmentStateEnum.onActivityResumed);
		}

		public void onActivitySaveInstanceState(Fragment f, Context context, Bundle extras) {
			saveSharedPreferences(f, context, extras, FragmentStateEnum.onActivitySaveInstanceState);
		}

		public void onActivityStarted(Fragment f, Context context) {
			saveSharedPreferences(f, context, null, FragmentStateEnum.onActivityStarted);
		}

		public void onActivityStopped(Fragment f, Context context) {
			saveSharedPreferences(f, context, null, FragmentStateEnum.onActivityStopped);
		}
	}

	public static Bitmap getScaledBitmap(Bitmap b, int reqWidth, int reqHeight) {

		// crop image
		int width = 0;
		if (b.getHeight() <= b.getWidth()) {
			width = b.getHeight();
		} else {
			width = b.getWidth();
		}

		int startX = (b.getWidth() - width) / 2;
		int startY = (b.getHeight() - width) / 2;
		b = Bitmap.createBitmap(b, startX, startY, width, width);

		// // scale bitmap
		int bWidth = b.getWidth();
		int bHeight = b.getHeight();

		int nWidth = reqWidth;
		int nHeight = reqHeight;

		float parentRatio = (float) reqHeight / reqWidth;

		nHeight = bHeight;
		nWidth = (int) (reqWidth * parentRatio);
		nHeight = nWidth;
		return Bitmap.createScaledBitmap(b, nWidth, nHeight, true);
		// return b;
	}
}