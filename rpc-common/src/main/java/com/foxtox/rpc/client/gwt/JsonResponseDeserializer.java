package com.foxtox.rpc.client.gwt;

import com.foxtox.rpc.common.ResponseDeserializer;
import com.foxtox.rpc.common.RpcResponse;
import com.foxtox.rpc.common.SerializableType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JsonResponseDeserializer implements ResponseDeserializer {

  public RpcResponse deserialize(SerializableType type, byte[] data) {
    RpcResponse response = new RpcResponse();

    JSONObject jsonObject = JSONParser.parseStrict(new String(data)).isObject();
    assert jsonObject != null;

    JSONValue errorValue = jsonObject.get("error");
    if (errorValue != null) {
      JSONString errorString = errorValue.isString();
      assert errorString != null;
      response.setError(errorString.stringValue());
    } else {
      JSONString result = jsonObject.get("result").isString();
      assert result != null;
      response.setResult(type.parseFromString(result.stringValue()));
    }
    return response;
  }

}
