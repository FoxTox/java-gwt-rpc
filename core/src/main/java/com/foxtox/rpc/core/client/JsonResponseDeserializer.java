package com.foxtox.rpc.core.client;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

public class JsonResponseDeserializer implements ResponseDeserializer {
	
	public JsonResponseDeserializer(InputStream input, SerializableType type) {
		JsonReader jsonReader = Json.createReader(input);
		JsonObject object = jsonReader.readObject();
		
		JsonString errorString = object.getJsonString("error");
		if (errorString != null) {
			error = errorString.getString();
			return;
		}
		result = type.parseFromString(object.getString("result"));
	}

	public boolean isSuccess() { return error == null; }
	public Object getResult() { return result; }
	public String getError() { return error; }
	
	private Object result;
	private String error;

}
