package com.conveniently.http.itf;

import com.conveniently.http.exception.AppException;

public interface OnGlobalExceptionListener {

	boolean handleException(AppException exception);
}
