package com.foxtox.rpc.client.gwt;

import com.foxtox.rpc.common.ResponseSerializer;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonResponseSerializer implements ResponseSerializer {
	
	public byte[] serializeResult(Object result) {
		jsonObject.put("result", new JSONString(result.toString()));
		return serialize();
	}

	public byte[] serializeError(String reason) {
		jsonObject.put("error", new JSONString(reason));
		return serialize();
	}
	
	private byte[] serialize() {
		return jsonObject.toString().getBytes();
	}
	
	private JSONObject jsonObject = new JSONObject();
}