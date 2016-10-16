package com.foxtox.rpc.core.client;

import java.io.OutputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public class JsonResponseSerializer implements ResponseSerializer {
	
	public JsonResponseSerializer(OutputStream output) {
		this.output = output;
		jsonWriter = Json.createWriter(this.output);
		requestObjectBuilder = Json.createObjectBuilder();
	}

	public void setResult(Object result) {
		requestObjectBuilder.add("result", result.toString());
		finish();
	}

	public void setError(String reason) {
		requestObjectBuilder.add("error", reason);
		finish();
	}
	
	private void finish() {
		JsonObject obj = requestObjectBuilder.build();
		jsonWriter.writeObject(obj);
		jsonWriter.close();
	}
	
	private OutputStream output;
	private JsonWriter jsonWriter;
	private JsonObjectBuilder requestObjectBuilder;

}
