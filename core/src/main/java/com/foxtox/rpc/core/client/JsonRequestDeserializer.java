package com.foxtox.rpc.core.client;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonRequestDeserializer implements RequestDeserializer {

	public JsonRequestDeserializer(InputStream input) {
		JsonReader jsonReader = Json.createReader(input);
		JsonObject object = jsonReader.readObject();
		serviceName = object.getString("service");
		serviceMethod = object.getString("method");
		JsonObject paramsObject = object.getJsonObject("params");
		String types = paramsObject.getString("types");
		JsonArray valuesArray = paramsObject.getJsonArray("values");
		
		parameters = new Object[types.length()];
		assert(types.length() == valuesArray.size());
		for (int i = 0; i < types.length(); ++i) {
			SerializableType type = SerializableType.getFrom(types.charAt(i));
			assert(type != SerializableType.UNSUPPORTED);  // TODO: Exception.
			parameters[i] = type.parseFromString(valuesArray.getString(i));
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceMethod() {
		return serviceMethod;
	}

	public Object[] getParameters() {
		return parameters;
	}

	private String serviceName;
	private String serviceMethod;
	private Object[] parameters;
	
	/*
	public static void main(String[] args) {
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
		JsonRequestSerializer serializer = new JsonRequestSerializer(output);
		serializer.addServiceName("sumService");
		serializer.addServiceMethod("getSum");
		serializer.addParameters(new Object[]{10, 2.2});
		serializer.finish();
		
		System.out.println("Request: " + output.toString());
		byte[] data = output.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		JsonRequestDeserializer deserializer = new JsonRequestDeserializer(input);
		System.out.print(deserializer.getServiceName() + "." + deserializer.getServiceMethod() + "(");
		boolean begin = true;
		for (Object object : deserializer.getParameters()) {
			if (!begin)
				System.out.print(", ");
			begin = false;
			
			System.out.print(object);
		}
		System.out.println(")");
	}
	*/

}
