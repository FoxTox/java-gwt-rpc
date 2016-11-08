package com.foxtox.rpc.client.jvm;

import java.io.ByteArrayInputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.foxtox.rpc.common.RequestDeserializer;
import com.foxtox.rpc.common.RpcRequest;
import com.foxtox.rpc.common.SerializableType;

public class JsonRequestDeserializer implements RequestDeserializer {

	public RpcRequest deserialize(byte[] data) {
		RpcRequest request = new RpcRequest();

		JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(data));
		JsonObject object = jsonReader.readObject();
		request.setServiceName(object.getString("service"));
		request.setServiceMethod(object.getString("method"));

		JsonObject paramsObject = object.getJsonObject("params");
		String types = paramsObject.getString("types");
		JsonArray valuesArray = paramsObject.getJsonArray("values");

		assert (types.length() == valuesArray.size());
		Object[] parameters = new Object[types.length()];
		for (int i = 0; i < types.length(); ++i) {
			SerializableType type = SerializableType.getFrom(types.charAt(i));
			assert (type != SerializableType.UNSUPPORTED); // TODO: Exception.
			parameters[i] = type.parseFromString(valuesArray.getString(i));
		}
		request.setParameters(parameters);

		return request;
	}

}
