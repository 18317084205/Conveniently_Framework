package com.conveniently.http.callback;

import com.conveniently.http.exception.AppException;

public abstract class XMLResponse<T> extends AbstractResponse<T> {

	@Override
	protected T bindData(String result) throws AppException {

		return null;
	}
}
