package com.conveniently.http.entity;

import com.conveniently.http.exception.AppException;

public interface JsonReaderable {
	void readFromJson(com.google.gson.stream.JsonReader reader)
			throws AppException;

}
