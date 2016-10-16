package com.foxtox.rpc.core.client;

import java.io.OutputStream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public class JsonRequestSerializer implements RequestSerializer {

	public JsonRequestSerializer(OutputStream output) {
		this.output = output;
		jsonWriter = Json.createWriter(this.output);
		requestObjectBuilder = Json.createObjectBuilder();
	}

	public void addServiceName(String service) {
		requestObjectBuilder.add("service", service);
	}

	public void addServiceMethod(String method) {
		requestObjectBuilder.add("method", method);
	}

	public void addParameters(Object[] params, int begin, int end) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		StringBuilder typesStringBuilder = new StringBuilder(end - begin);
		for (int i = begin; i < end; ++i) {
			SerializableType type = SerializableType.getFrom(params[i].getClass());
			if (type == SerializableType.UNSUPPORTED) {
				return; // FIXME: throw
			}
			typesStringBuilder.append(type.getSymbol());
			arrayBuilder.add(params[i].toString()); // TODO: Call specific add.
		}
		requestObjectBuilder.add("params",
				Json.createObjectBuilder().add("types", typesStringBuilder.toString()).add("values", arrayBuilder));
	}
	
	public void finish() {
		jsonWriter.writeObject(requestObjectBuilder.build());
		jsonWriter.close();
	}

	private OutputStream output;
	private JsonWriter jsonWriter;
	private JsonObjectBuilder requestObjectBuilder;
	
}
