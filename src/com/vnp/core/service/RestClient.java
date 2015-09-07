package com.vnp.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.vnp.core.common.LogUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public class RestClient {

	public static final int TIME_OUT = 10 * 1000;
	public static final int BUFFER = 1024 * 2;
	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;

	private String url;

	private int responseCode;
	private String message;

	private String response;

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void execute(final RequestMethod method, final RestClientCallBack restClientCallBack) {
		if (restClientCallBack != null) {
			restClientCallBack.onStart();
		}

		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... xparams) {
				RestClient.this.execute(method);
				if (restClientCallBack != null) {
					restClientCallBack.onSucssesOnBackground(responseCode, message, response);
				}
				return null;
			}

			protected void onPostExecute(String result) {
				if (restClientCallBack != null) {
					restClientCallBack.onSucsses(responseCode, message, response);
				}
			};
		}.execute("");

	}

	public void execute(final RequestMethod method) {

		try {
			switch (method) {
			case GET: {
				String combinedParams = "";
				if (!params.isEmpty()) {
					combinedParams += "?";
					for (NameValuePair p : params) {
						String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
						if (combinedParams.length() > 1) {
							combinedParams += "&" + paramString;
						} else {
							combinedParams += paramString;
						}
					}
				}

				HttpGet request = new HttpGet(url + combinedParams);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				executeRequest(request, url);
				break;
			}
			case POST: {
				HttpPost request = new HttpPost(url);

				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				if (!params.isEmpty()) {
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}

				executeRequest(request, url);
				break;
			}
			case PUT: {
				HttpPut request = new HttpPut(url);
				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				if (!params.isEmpty()) {
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
				executeRequest(request, url);
				break;

			}
			case DELETE: {
				HttpDelete request = new HttpDelete(url);
				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				executeRequest(request, url);
				break;
			}
			}
		} catch (Exception exception) {

		}

	}

	private void executeRequest(HttpUriRequest request, String url) {

		int timeout = TIME_OUT;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout);

		HttpClient client = new DefaultHttpClient(httpParameters);

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				response = EntityUtils.toString(entity);
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
		}
	}

	// public void executeUploadFile(final RestClientCallBack
	// restClientCallBack) {
	//
	// if (restClientCallBack != null)
	// restClientCallBack.onStart();
	// new AsyncTask<String, String, String>() {
	// @Override
	// protected String doInBackground(String... xparams) {
	// HttpClient client = new DefaultHttpClient();
	// try {
	//
	// HttpPut request = new HttpPut(url);
	// MultipartEntity partEntity = new MultipartEntity();
	//
	// HttpResponse httpResponse;
	// for (NameValuePair h : headers) {
	// request.addHeader(h.getName(), h.getValue());
	// }
	//
	// for (NameValuePair p : params) {
	// if (p.getName().equals("user[avatar]")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("file")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("user[cover]")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("post[url]")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else {
	// partEntity.addPart(p.getName(), new StringBody(p.getValue()));
	// }
	// }
	//
	// request.setEntity(partEntity);
	//
	// httpResponse = client.execute(request);
	// responseCode = httpResponse.getStatusLine().getStatusCode();
	// message = httpResponse.getStatusLine().getReasonPhrase();
	//
	// HttpEntity entity = httpResponse.getEntity();
	//
	// if (entity != null) {
	// response = EntityUtils.toString(entity);
	// }
	//
	// } catch (ClientProtocolException e) {
	// client.getConnectionManager().shutdown();
	// } catch (IOException e) {
	// client.getConnectionManager().shutdown();
	// }
	//
	// if (restClientCallBack != null)
	// restClientCallBack.onSucssesOnBackground(responseCode, message,
	// response);
	// ;
	// return null;
	// }
	//
	// protected void onPostExecute(String result) {
	// if (restClientCallBack != null)
	// restClientCallBack.onSucsses(responseCode, message, response);
	// };
	// }.execute("");
	//
	// }

	// public void executeUploadFile(final boolean isPost, final
	// RestClientCallBack restClientCallBack) {
	// if (restClientCallBack != null)
	// restClientCallBack.onStart();
	// new AsyncTask<String, String, String>() {
	// @Override
	// protected String doInBackground(String... xparams) {
	// HttpClient client = new DefaultHttpClient();
	// try {
	// HttpEntityEnclosingRequestBase request = new HttpPut(url);
	//
	// if (isPost) {
	// request = new HttpPost(url);
	// }
	// MultipartEntity partEntity = new MultipartEntity();
	//
	// HttpResponse httpResponse;
	// for (NameValuePair h : headers) {
	// request.addHeader(h.getName(), h.getValue());
	// }
	//
	// for (NameValuePair p : params) {
	// if (p.getName().equals("user[avatar]")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("file")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("image")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("user[cover]")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else if (p.getName().equals("post[url]")) {
	// partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()),
	// "image/jpeg"));
	// } else {
	// partEntity.addPart(p.getName(), new StringBody(p.getValue()));
	// }
	// }
	//
	// request.setEntity(partEntity);
	//
	// httpResponse = client.execute(request);
	// responseCode = httpResponse.getStatusLine().getStatusCode();
	// message = httpResponse.getStatusLine().getReasonPhrase();
	//
	// HttpEntity entity = httpResponse.getEntity();
	//
	// if (entity != null) {
	// response = EntityUtils.toString(entity);
	// }
	//
	// } catch (ClientProtocolException e) {
	// client.getConnectionManager().shutdown();
	// e.printStackTrace();
	// } catch (IOException e) {
	// client.getConnectionManager().shutdown();
	// e.printStackTrace();
	// }
	// if (restClientCallBack != null)
	// restClientCallBack.onSucssesOnBackground(responseCode, message,
	// response);
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// if (restClientCallBack != null)
	// restClientCallBack.onSucsses(responseCode, message, response);
	// }
	// }.execute("");
	//
	// }

	public void exeDownloadFile(RequestInfo requestInfo, final IDownloadUploadFileCallBack downloadUploadFileCallBack) {

		if (downloadUploadFileCallBack != null) {
			downloadUploadFileCallBack.start();
		}

		File mDirectory = new File(requestInfo.getFileFolderSaveFile());

		if (!mDirectory.exists()) {
			mDirectory.mkdirs();
		}

		if (!mDirectory.exists()) {
			if (downloadUploadFileCallBack != null) {
				downloadUploadFileCallBack.error(IDownloadUploadFileCallBack.STATUS_CREATE_FILE_FOLDER_FAIL);
			}

			return;
		}

		// create file
		File mFile = new File(mDirectory, requestInfo.getFileNameSave());

		HttpURLConnection urlConnection = null;
		FileOutputStream fileOutput = null;
		long total = 0;
		long fileSize = 0;
		try {
			URL tmp = new URL(requestInfo.getUrl());
			urlConnection = (HttpURLConnection) tmp.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setReadTimeout(TIME_OUT);
			urlConnection.setConnectTimeout(TIME_OUT);
			urlConnection.connect();

			String typeData = urlConnection.getHeaderField("content-type");
			if (typeData.contains("text/plain")) {

			} else {
				fileSize = Long.parseLong(urlConnection.getHeaderField("content-length"));
				String getDateModifier = urlConnection.getHeaderField("last-modified");

				if (downloadUploadFileCallBack != null) {

					downloadUploadFileCallBack.onProcess(fileSize, total);
				}

				fileOutput = new FileOutputStream(mFile);

				InputStream inputStream = urlConnection.getInputStream();

				byte[] buffer = new byte[BUFFER];
				int bufferLength = 0;

				while ((bufferLength = inputStream.read(buffer)) > 0) {
					total += bufferLength;
					fileOutput.write(buffer, 0, bufferLength);

					if (downloadUploadFileCallBack != null) {
						downloadUploadFileCallBack.onProcess(fileSize, total);
					}
				}

				fileOutput.close();
			}
		} catch (Exception e) {
		} finally {
			try {
				urlConnection.disconnect();
			} catch (Exception e2) {
			}
		}

		if (fileSize == 0 || mFile.length() < fileSize && fileSize != 0) {
			mFile.delete();
			if (downloadUploadFileCallBack != null) {
				downloadUploadFileCallBack.error(IDownloadUploadFileCallBack.STATUS_DOWNLOAD_UPLOAD_FAIL);
			}
		} else {
			if (downloadUploadFileCallBack != null) {
				downloadUploadFileCallBack.sucess();
			}
		}
	}

	public interface IDownloadUploadFileCallBack {
		public static final int STATUS_CREATE_FILE_FOLDER_FAIL = 1;
		public static final int STATUS_SUCESS = 2;
		public static final int STATUS_DOWNLOAD_UPLOAD_FAIL = 3;

		/**
		 * 
		 */
		public void start();

		/**
		 * @param code
		 */
		public void error(int code);

		public void sucess();

		/**
		 * 
		 * @param total
		 * @param curent
		 */
		public void onProcess(long total, long curent);

	}

	/**
	 * 
	 * @param userId
	 */
	public interface IFacebookAvatarCallBack {

		/**
		 * 
		 */
		public void onStart();

		public void onSsucess(Bitmap bitmap);

		public void onSsucessInBackground(Bitmap bitmap);
	}

	/**
	 * 
	 * @param userId
	 * @param facebookAvatarCallBack
	 */
	public void executeLoadAvatarFacebook(final String userId, final IFacebookAvatarCallBack facebookAvatarCallBack) {
		facebookAvatarCallBack.onStart();
		new AsyncTask<String, String, String>() {
			Bitmap mIcon1;

			@Override
			protected String doInBackground(String... params) {
				String image = "https://graph.facebook.com/" + userId + "/picture?type=large";
				mIcon1 = executeLoadAvatarFacebook(userId);
//				try {
//					mIcon1 = BitmapFactory.decodeStream(new URL(image).openConnection().getInputStream());
//				} catch (MalformedURLException e) {
//				} catch (IOException e) {
//				}
				facebookAvatarCallBack.onSsucessInBackground(mIcon1);
				return null;
			}

			protected void onPostExecute(String result) {
				facebookAvatarCallBack.onSsucess(mIcon1);
			};
		}.execute("");
	}

	public Bitmap executeLoadAvatarFacebook(final String facebookId) {
		Bitmap mIcon1 = null;
		String image = "https://graph.facebook.com/" + facebookId + "/picture?type=large";
		try {
			mIcon1 = BitmapFactory.decodeStream(new URL(image).openConnection().getInputStream());
		} catch (Exception e) {
		}

		return mIcon1;
	}
}

// ============================================================
// RequestInfo
