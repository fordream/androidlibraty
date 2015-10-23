package com.vnp.core.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class HttpsRestClient {
	public interface IHttpsRestClientLisner {
		// public void onError(Exception exception);
		public void onSucces(int responseCode, String message, String response, Exception exception, File file);

	}

	public static final int BUFFER = 1024 * 2;
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	private ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
	private Context context;

	public HttpsRestClient(Context context, String url) {
		this.url = url;
		this.context = context;
	}

	private String url;

	private int responseCode;
	private String message;

	private String response;

	public String getResponse() {
		return response;
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	private HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private void trustAllHosts() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		} };

		try {
			// SSLContext sc = SSLContext.getInstance("TLS");
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(final RequestMethod method, final IHttpsRestClientLisner lisner) {
		new AsyncTask<String, String, String>() {
			private Exception exception;

			@Override
			protected String doInBackground(String... zparams) {
				String methodStr = "GET";
				if (method == RequestMethod.GET) {
					methodStr = "GET";
					String s = getQuery(params);
					url = url + ("".equals(s) ? "" : ("?" + getQuery(params)));
				} else if (method == RequestMethod.POST) {
					methodStr = "POST";
				}

				try {
					URL mxurl = new URL(url);
					trustAllHosts();
					HttpsURLConnection https = (HttpsURLConnection) mxurl.openConnection();
					https.setRequestMethod(methodStr);
					https.setConnectTimeout(10 * 1000);
					https.setReadTimeout(10 * 1000);
					/**
					 * add header
					 */
					for (NameValuePair h : headers) {
						https.setRequestProperty(h.getName(), h.getValue());
					}

					https.setUseCaches(false);
					https.setDoInput(true);
					https.setDoOutput(true);
					https.setHostnameVerifier(DO_NOT_VERIFY);

					if (method == RequestMethod.POST) {
						OutputStream os = https.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
						writer.write(getQuery(params));
						writer.flush();
						writer.close();
						os.close();
					}

					https.connect();

					InputStream stream = https.getInputStream();
					copyInputStreamToOutputStream(stream, https);
					https.disconnect();
				} catch (Exception e) {
					exception = e;
				}
				return null;
			}

			protected void onPostExecute(String result) {
				lisner.onSucces(responseCode, message, response, exception, null);
			};
		}.execute("");
	}

	public void execute2(final RequestMethod method, final IHttpsRestClientLisner lisner) {

		new AsyncTask<String, String, String>() {
			private Exception exception;

			@Override
			protected String doInBackground(String... zparams) {
				String methodStr = "GET";
				if (method == RequestMethod.GET) {
					methodStr = "GET";
					String s = getQuery(params);
					url = url + ("".equals(s) ? "" : ("?" + getQuery(params)));
				} else if (method == RequestMethod.POST) {
					methodStr = "POST";
				}

				try {
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					InputStream caInput = context.getAssets().open("vdealer.crt");

					Certificate ca;
					try {
						ca = cf.generateCertificate(caInput);
					} finally {
						caInput.close();
					}

					String keyStoreType = KeyStore.getDefaultType();
					KeyStore keyStore = KeyStore.getInstance(keyStoreType);
					keyStore.load(null, null);
					keyStore.setCertificateEntry("ca", ca);

					String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
					TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
					tmf.init(keyStore);

					SSLContext context = SSLContext.getInstance("TLS");
					context.init(null, tmf.getTrustManagers(), null);

					URL url = new URL(HttpsRestClient.this.url);

					HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
					https.setRequestMethod(methodStr);

					/**
					 * add header
					 */
					for (NameValuePair h : headers) {
						https.setRequestProperty(h.getName(), h.getValue());
					}

					https.setUseCaches(false);
					https.setDoInput(true);
					https.setDoOutput(true);
					https.setHostnameVerifier(DO_NOT_VERIFY);

					if (method == RequestMethod.POST) {
						OutputStream os = https.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
						writer.write(getQuery(params));
						writer.flush();
						writer.close();
						os.close();
					}

					// HttpsURLConnection urlConnection = (HttpsURLConnection)
					// url.openConnection();
					https.setSSLSocketFactory(context.getSocketFactory());
					https.connect();
					InputStream in = https.getInputStream();
					copyInputStreamToOutputStream(in, https);
					https.disconnect();
				} catch (Exception e) {
					exception = e;
				}
				return null;
			}

			protected void onPostExecute(String result) {
				lisner.onSucces(responseCode, message, response, exception, null);
			};
		}.execute("");

	}

	/**
	 * -------------------------------------
	 * 
	 * @param stream
	 * @param https
	 */
	private void copyInputStreamToOutputStream(InputStream stream, HttpsURLConnection https) {
		try {
			InputStreamReader isReader = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(isReader);
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (line != null) {
					builder.append(line);
				}
			}
			br.close();
			isReader.close();
			response = builder.toString();
			responseCode = https.getResponseCode();
			message = https.getResponseMessage();
		} catch (Exception exception) {

		}

	}

	private String getQuery(List<NameValuePair> params) {
		try {
			StringBuilder result = new StringBuilder();
			boolean first = true;

			for (NameValuePair pair : params) {
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
			}

			return result.toString();
		} catch (Exception exception) {
			return "";
		}
	}

	public File executeDownloadFile(final RequestMethod method) {

		File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
		String methodStr = "GET";
		if (method == RequestMethod.GET) {
			methodStr = "GET";
			String s = getQuery(params);
			url = url + ("".equals(s) ? "" : ("?" + getQuery(params)));
		} else if (method == RequestMethod.POST) {
			methodStr = "POST";
		}

		try {
			URL mxurl = new URL(url);
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) mxurl.openConnection();
			https.setRequestMethod(methodStr);
			https.setConnectTimeout(10 * 1000);
			https.setReadTimeout(10 * 1000);
			/**
			 * add header
			 */
			for (NameValuePair h : headers) {
				https.setRequestProperty(h.getName(), h.getValue());
			}

			https.setUseCaches(false);
			https.setDoInput(true);
			https.setDoOutput(true);
			https.setHostnameVerifier(DO_NOT_VERIFY);

			if (method == RequestMethod.POST) {
				OutputStream os = https.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();
			}

			https.connect();
			FileOutputStream fileOutput = new FileOutputStream(file);
			InputStream inputStream = https.getInputStream();
			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
			}
			fileOutput.close();

			https.disconnect();
		} catch (Exception e) {
		}

		return file;
	}

	public File executeDownloadFile(final RequestMethod method, File file) {

		// File file = new File(Environment.getExternalStorageDirectory(),
		// System.currentTimeMillis() + ".jpg");
		String methodStr = "GET";
		if (method == RequestMethod.GET) {
			methodStr = "GET";
			String s = getQuery(params);
			url = url + ("".equals(s) ? "" : ("?" + getQuery(params)));
		} else if (method == RequestMethod.POST) {
			methodStr = "POST";
		}

		try {
			URL mxurl = new URL(url);
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) mxurl.openConnection();
			https.setRequestMethod(methodStr);
			https.setConnectTimeout(10 * 1000);
			https.setReadTimeout(10 * 1000);
			/**
			 * add header
			 */
			for (NameValuePair h : headers) {
				https.setRequestProperty(h.getName(), h.getValue());
			}

			https.setUseCaches(false);
			https.setDoInput(true);
			https.setDoOutput(true);
			https.setHostnameVerifier(DO_NOT_VERIFY);

			if (method == RequestMethod.POST) {
				OutputStream os = https.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();
			}

			https.connect();
			FileOutputStream fileOutput = new FileOutputStream(file);
			InputStream inputStream = https.getInputStream();
			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
			}
			fileOutput.close();

			https.disconnect();
		} catch (Exception e) {
		}

		return file;
	}
}