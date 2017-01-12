package com.conveniently.http.util;

import java.util.concurrent.Executor;

import com.conveniently.http.exception.AppException;
import com.conveniently.http.itf.IResponse;
import com.conveniently.http.itf.OnGlobalExceptionListener;

import android.os.Build;

public class Request {

	public enum RequestMethod {
		GET, POST, PUT, DELETE
	}

	public static final int STATE_UPLOAD = 1;
	public static final int STATE_DOWNLOAD = 2;
	public static final int MAXRETRYCOUNT = 3;
	public volatile boolean isCancelled;
	public boolean isCompleted;
	public RequestMethod method;
	public String url;
	public String parentTag;
	public String selfTag;
	private RequestTask task;
	@SuppressWarnings("rawtypes")
	public IResponse iResponse;
	public boolean enableProgressUpdated = false;
	public OnGlobalExceptionListener onGlobalExceptionListener;
	public RequestParams requestParams;

	public void enableProgressUpdated(boolean enable) {
		this.enableProgressUpdated = enable;
	}

	public void setGlobalExceptionListener(
			OnGlobalExceptionListener onGlobalExceptionListener) {
		this.onGlobalExceptionListener = onGlobalExceptionListener;
	}

	public void checkIfCancelled() throws AppException {
		if (isCancelled) {
			throw new AppException(AppException.ErrorType.CANCEL,
					"the request has been cancelled");
		}
	}

	public Request(String parentTag, String selfTag, String url,
			RequestMethod method,
			@SuppressWarnings("rawtypes") IResponse response) {
		this.url = url;
		this.method = method;
		this.parentTag = parentTag;
		this.selfTag = selfTag;
		this.iResponse = response;
	}

	public void execute(Executor mExecutors) {
		task = new RequestTask(this);
		if (Build.VERSION.SDK_INT > 11) {
			task.executeOnExecutor(mExecutors);
		} else {
			task.execute();
		}
	}

	public void cancel(boolean force) {
		isCancelled = true;
		iResponse.cancel();
		if (force && task != null) {
			task.cancel(force);
		}
	}

}
