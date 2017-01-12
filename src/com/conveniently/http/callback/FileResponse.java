package com.conveniently.http.callback;

import com.conveniently.http.exception.AppException;

public abstract class FileResponse extends AbstractResponse<String> {

	@Override
	protected String bindData(String path) throws AppException {
		return path;
	}
}
