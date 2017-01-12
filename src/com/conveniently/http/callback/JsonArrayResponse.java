package com.conveniently.http.callback;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.conveniently.http.entity.JsonReaderable;
import com.conveniently.http.exception.AppException;
import com.google.gson.stream.JsonReader;

public abstract class JsonArrayResponse<T extends JsonReaderable> extends
		AbstractResponse<ArrayList<T>> {
	@SuppressWarnings("unchecked")
	@Override
	protected ArrayList<T> bindData(String path) throws AppException {
		try {

			Type type = ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
			ArrayList<T> ts = new ArrayList<T>();
			T t;

			FileReader in = new FileReader(path);
			JsonReader reader = new JsonReader(in);
			String node;
			reader.beginObject();
			while (reader.hasNext()) {
				node = reader.nextName();
				if ("data".equalsIgnoreCase(node)) {
					reader.beginArray();
					while (reader.hasNext()) {
						t = ((Class<T>) type).newInstance();
						t.readFromJson(reader);
						ts.add(t);
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return ts;

		} catch (Exception e) {
			throw new AppException(AppException.ErrorType.JSON, e.getMessage());
		}
	}
}
