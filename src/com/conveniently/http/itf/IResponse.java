package com.conveniently.http.itf;

import java.net.HttpURLConnection;

import com.conveniently.http.exception.AppException;
import com.conveniently.http.util.RequestParams;

public interface IResponse<T> {

	void onSuccess(T result);

	void onFailure(AppException e);

	void onProgressUpdated(int state, int curLen, int totalLen);

	void cancel();

	RequestParams setParams();

	T postRequest(T t);

	T preRequest();

	T parse(HttpURLConnection connection, OnProgressUpdatedListener listener)
			throws AppException;

	T parse(HttpURLConnection connection) throws AppException;

}
