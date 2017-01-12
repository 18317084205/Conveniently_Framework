package com.conveniently.http.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

import com.conveniently.http.exception.AppException;
import com.conveniently.http.itf.OnProgressUpdatedListener;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

public class HttpUrlConnectionUtil {
	public static HttpURLConnection execute(Request request,
			OnProgressUpdatedListener listener) throws AppException {
		if (!URLUtil.isNetworkUrl(request.url)) {
			throw new AppException(AppException.ErrorType.MANUAL, "the url :"
					+ request.url + " is not valid");
		}
		switch (request.method) {
		case GET:
		case DELETE:
			return get(request);
		case POST:
		case PUT:
			return post(request, listener);
		}

		return null;
	}

	private static HttpURLConnection get(Request request) throws AppException {
		try {

			request.checkIfCancelled();

			String params = addParams(request.requestParams);

			HttpURLConnection connection = (HttpURLConnection) new URL(
					TextUtils.isEmpty(params) ? request.url : request.url + "?"
							+ params).openConnection();
			connection.setRequestMethod(request.method.name());
			connection.setConnectTimeout(15 * 3000);
			connection.setReadTimeout(15 * 3000);

			// addHeader(connection, request.requestParams);

			Log.d("RequestParams : ", connection.getURL().toString());

			request.checkIfCancelled();
			return connection;
		} catch (InterruptedIOException e) {
			throw new AppException(AppException.ErrorType.TIMEOUT,
					e.getMessage());
		} catch (IOException e) {
			throw new AppException(AppException.ErrorType.SERVER,
					e.getMessage());
		}
	}

	private static HttpURLConnection post(Request request,
			OnProgressUpdatedListener listener) throws AppException {
		HttpURLConnection connection = null;
		OutputStream os = null;
		try {
			request.checkIfCancelled();

			connection = (HttpURLConnection) new URL(request.url)
					.openConnection();
			connection.setRequestMethod(request.method.name());
			connection.setConnectTimeout(15 * 3000);
			connection.setReadTimeout(15 * 3000);
			connection.setDoOutput(true);

			// addHeader(connection, request.requestParams);
			request.checkIfCancelled();

			os = connection.getOutputStream();
			os.write(addParams(request.requestParams).getBytes());

			Log.d("RequestParams : ", request.requestParams.toString());

			// if (request.filePath != null){
			// UploadUtil.upload(os, request.filePath);
			// }else if(request.fileEntities != null){
			// UploadUtil.upload(os,request.content,request.fileEntities,listener);
			// }else if(request.content != null){
			// os.write(request.content.getBytes());
			// }else {
			// throw new
			// AppException(AppException.ErrorType.MANUAL,"the post request has no post content");
			// }

			request.checkIfCancelled();
		} catch (InterruptedIOException e) {
			throw new AppException(AppException.ErrorType.TIMEOUT,
					e.getMessage());
		} catch (IOException e) {
			throw new AppException(AppException.ErrorType.SERVER,
					e.getMessage());
		} finally {
			try {
				os.flush();
				os.close();
			} catch (IOException e) {
				throw new AppException(AppException.ErrorType.IO,
						"the post outputstream can't be closed");
			}
		}

		return connection;
	}

	private static String addParams(RequestParams requestParams) {
		if (requestParams == null || requestParams.size() == 0) {
			return "";
		}
		StringBuilder params = new StringBuilder();
		for (ConcurrentHashMap.Entry<String, String> entry : requestParams
				.getParams().entrySet()) {

			try {
				if (params.length() > 0) {
					params.append("&");
				}
				params.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				params.append("=");
				params.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return params.toString();
	}
}
