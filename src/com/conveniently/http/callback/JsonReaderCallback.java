package com.conveniently.http.callback;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.conveniently.http.entity.JsonReaderable;
import com.conveniently.http.exception.AppException;
import com.google.gson.stream.JsonReader;

public abstract class JsonReaderCallback<T extends JsonReaderable> extends
		AbstractResponse<T> {
	@SuppressWarnings("unchecked")
	@Override
	protected T bindData(String path) throws AppException {
		try {
			Type type = ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
			T t = ((Class<T>) type).newInstance();

			FileReader in = new FileReader(path);
			JsonReader reader = new JsonReader(in);
			String node;
			reader.beginObject();
			while (reader.hasNext()) {
				node = reader.nextName();
				if ("data".equalsIgnoreCase(node)) {
					t.readFromJson(reader);
					;
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return t;

		} catch (Exception e) {
			throw new AppException(AppException.ErrorType.JSON, e.getMessage());
		}
	}
}
