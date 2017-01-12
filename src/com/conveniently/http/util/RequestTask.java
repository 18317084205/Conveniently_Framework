package com.conveniently.http.util;

import java.net.HttpURLConnection;

import android.os.AsyncTask;

import com.conveniently.http.exception.AppException;
import com.conveniently.http.itf.OnProgressUpdatedListener;

public class RequestTask extends AsyncTask<Void, Integer, Object> {

	private Request request;

	public void checkIfCancelled() throws AppException {
		if (isCancelled()) {
			throw new AppException(AppException.ErrorType.CANCEL,
					"the request has been cancelled");
		}
	}

	public RequestTask(Request request) {
		// TODO Auto-generated constructor stub
		this.request = request;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(Void... params) {
		if (request.iResponse != null) {
			Object o = request.iResponse.preRequest();
			request.requestParams = request.iResponse.setParams();
			if (o != null) {
				return o;
			}
		}
		return request(0);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	public Object request(int retry) {
		try {
			// FIXME: for HttpUrlConnection
			HttpURLConnection connection = null;
			connection = HttpUrlConnectionUtil.execute(request,
					!request.enableProgressUpdated ? null
							: new OnProgressUpdatedListener() {
								@Override
								public void onProgressUpdated(int curLen,
										int totalLen) {
									publishProgress(Request.STATE_UPLOAD,
											curLen, totalLen);
								}
							});
			if (request.enableProgressUpdated) {
				return request.iResponse.parse(connection,
						new OnProgressUpdatedListener() {
							@Override
							public void onProgressUpdated(int curLen,
									int totalLen) {
								publishProgress(Request.STATE_DOWNLOAD, curLen,
										totalLen);
							}
						});
			} else {
				return request.iResponse.parse(connection);
			}
		} catch (AppException e) {
			if (e.type == AppException.ErrorType.TIMEOUT) {
				if (retry < Request.MAXRETRYCOUNT) {
					retry++;
					return request(retry);
				}
			}
			return e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		request.isCompleted = true;
		if (o instanceof AppException) {
			if (request.onGlobalExceptionListener != null) {
				if (!request.onGlobalExceptionListener
						.handleException((AppException) o)) {
					request.iResponse.onFailure((AppException) o);
				}
			} else {
				request.iResponse.onFailure((AppException) o);
			}
		} else {
			request.iResponse.onSuccess(o);
		}

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		request.iResponse.onProgressUpdated(values[0], values[1], values[2]);

	}
}
