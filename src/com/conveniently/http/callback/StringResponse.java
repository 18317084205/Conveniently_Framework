package com.conveniently.http.callback;

import com.conveniently.http.exception.AppException;

public abstract class StringResponse extends AbstractResponse<String> {

	@Override
	protected String bindData(String result) throws AppException {
		return result;
	}

}
