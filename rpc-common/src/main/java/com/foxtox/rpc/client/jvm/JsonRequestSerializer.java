package com.foxtox.rpc.client.jvm;

import java.io.ByteArrayOutputStream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.foxtox.rpc.common.RequestSerializer;
import com.foxtox.rpc.common.SerializableType;

public class JsonRequestSerializer implements RequestSerializer {

	public void setServiceName(String serviceName) {
		requestObjectBuilder.add("service", serviceName);
	}

	public void setServiceMethod(String serviceMethod) {
		requestObjectBuilder.add("method", serviceMethod);
	}

	public void addParameter(Object parameter) {
		SerializableType type = SerializableType.getFrom(parameter.getClass());
		if (type == SerializableType.UNSUPPORTED) {
			return; // FIXME: throw
		}
		paramTypesStringBuilder.append(type.getSymbol());
		// TODO: Call specific add.
		paramsArrayBuilder.add(parameter.toString());
	}

	public byte[] serialize() {
		requestObjectBuilder.add("params", Json.createObjectBuilder().add("types", paramTypesStringBuilder.toString())
				.add("values", paramsArrayBuilder));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		JsonWriter jsonWriter = Json.createWriter(output);
		jsonWriter.writeObject(requestObjectBuilder.build());
		jsonWriter.close();
		return output.toByteArray();
	}

	private JsonObjectBuilder requestObjectBuilder = Json.createObjectBuilder();
	private JsonArrayBuilder paramsArrayBuilder = Json.createArrayBuilder();
	private StringBuilder paramTypesStringBuilder = new StringBuilder();

}
