package com.vnp.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestInfo implements Parcelable {

	public enum TypeServer {
		NONE, DOWNLOAD_FILE, SEND_FILE, GET_CONTENT_STRING, GET_LIST_DOWNLOADDING
	}

	private String header;

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}

	private String url;
	private TypeServer typeServer;
	private String fileFolderSaveFile;
	private String fileNameSave;

	public RequestInfo() {
		super();
	}

	public String getActionForCallBack() {
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append(typeServer);
		builder.append(fileFolderSaveFile);
		builder.append(fileNameSave);

		Set<String> set = hashMap.keySet();
		List<String> lKey = new ArrayList<String>();
		lKey.addAll(set);

		Collections.sort(lKey, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});

		for (String key : lKey) {
			builder.append(key).append(hashMap.get(key));
		}

		return builder.toString();

	}

	private RequestMethod method = RequestMethod.GET;

	private HashMap<String, String> hashMap = new HashMap<String, String>();

	public void putParam(HashMap<String, String> parameters) {
		hashMap = parameters;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TypeServer getTypeServer() {
		return typeServer;
	}

	public void setTypeServer(TypeServer typeServer) {
		this.typeServer = typeServer;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public void setFileFolderSaveFile(String fileFolderSaveFile) {
		this.fileFolderSaveFile = fileFolderSaveFile;
	}

	public void setFileNameSave(String fileNameSave) {
		this.fileNameSave = fileNameSave;
	}

	public void putParam(String key, String value) {
		hashMap.put(key, value);
	}

	public Set<String> keyList() {
		return hashMap.keySet();
	}

	public String getParam(String key) {
		return hashMap.get(key);
	}

	public String getFileFolderSaveFile() {
		return fileFolderSaveFile;
	}

	public String getFileNameSave() {
		return fileNameSave;
	}

	// -----------------------------------------------
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(url);
		out.writeString(fileFolderSaveFile);
		out.writeString(fileNameSave);
		out.writeSerializable(typeServer);
		out.writeSerializable(method);

		Set<String> keys = hashMap.keySet();
		out.writeLong((long) keys.size());
		for (String key : keys) {
			out.writeString(key);
			out.writeString(hashMap.get(key));
		}
	}

	private RequestInfo(Parcel in) {
		super();
		url = in.readString();
		fileFolderSaveFile = in.readString();
		fileNameSave = in.readString();
		typeServer = (TypeServer) in.readSerializable();
		method = (RequestMethod) in.readSerializable();
		long size = in.readLong();
		for (int i = 0; i < size; i++) {
			hashMap.put(in.readString(), in.readString());
		}
	}

	public static final Parcelable.Creator<RequestInfo> CREATOR = new Parcelable.Creator<RequestInfo>() {
		public RequestInfo createFromParcel(Parcel in) {
			return new RequestInfo(in);
		}

		public RequestInfo[] newArray(int size) {
			return new RequestInfo[size];
		}
	};
}