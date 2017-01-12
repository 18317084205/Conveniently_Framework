package com.conveniently.http.util;

import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

	private ConcurrentHashMap<String, String> urlParams;

	public RequestParams() {
		urlParams = new ConcurrentHashMap<String, String>();
	}

	public void put(String key, String value) {
		if (key != null && value != null) {
			urlParams.put(key, value);
		}
	}

	public ConcurrentHashMap<String, String> getParams() {
		return urlParams;
	}

	public int size() {
		// TODO Auto-generated method stub
		return urlParams.size();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return urlParams.toString();
	}
}
