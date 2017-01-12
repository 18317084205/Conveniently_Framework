package com.conveniently.http.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import com.conveniently.http.exception.AppException;
import com.google.gson.Gson;

public abstract class JsonResponse<T> extends AbstractResponse<T> {
	@Override
	protected T bindData(String result) throws AppException {
		try {
			JSONObject json = new JSONObject(result);
			Object data = json.opt("data");
			Gson gson = new Gson();
			Type type = ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
			return gson.fromJson(data.toString(), type);
		} catch (JSONException e) {
			throw new AppException(AppException.ErrorType.JSON, e.getMessage());
		}
	}
}
