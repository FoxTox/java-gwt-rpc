package com.foxtox.rpc.client.gwt;

import java.lang.StringBuilder;

import com.foxtox.rpc.common.RequestSerializer;
import com.foxtox.rpc.common.SerializableType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonRequestSerializer implements RequestSerializer {

	public void setServiceName(String service) {
		jsonObject.put("service", new JSONString(service));
	}

	public void setServiceMethod(String method) {
		jsonObject.put("method", new JSONString(method));
	}

	public void addParameter(Object parameter) {
		SerializableType type = SerializableType.getFrom(parameter.getClass());
		if (type == SerializableType.UNSUPPORTED) {
			return; // FIXME: throw
		}
		paramTypes.append(type.getSymbol());
		jsonParamsArray.set(jsonParamsArray.size(), new JSONString(parameter.toString()));
	}
	
	public byte[] serialize() {
		JSONObject params = new JSONObject();
		params.put("types", new JSONString(paramTypes.toString()));
		params.put("values", jsonParamsArray);
		jsonObject.put("params", params);
		return jsonObject.toString().getBytes();
	}
	
	private JSONObject jsonObject = new JSONObject();
	private JSONArray jsonParamsArray = new JSONArray();
	private StringBuilder paramTypes = new StringBuilder();
	
}