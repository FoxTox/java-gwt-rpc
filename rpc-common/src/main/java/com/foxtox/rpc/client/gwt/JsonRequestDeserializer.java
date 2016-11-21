package com.foxtox.rpc.client.gwt;

import com.foxtox.rpc.common.RequestDeserializer;
import com.foxtox.rpc.common.RpcRequest;
import com.foxtox.rpc.common.SerializableType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

public class JsonRequestDeserializer implements RequestDeserializer {

  public RpcRequest deserialize(byte[] data) {
    RpcRequest request = new RpcRequest();

    JSONObject jsonObject = JSONParser.parseStrict(new String(data)).isObject();

    JSONString serviceName = jsonObject.get("service").isString();
    assert serviceName != null;
    request.setServiceName(serviceName.stringValue());

    JSONString serviceMethod = jsonObject.get("method").isString();
    assert serviceMethod != null;
    request.setServiceMethod(serviceMethod.stringValue());

    JSONObject params = jsonObject.get("params").isObject();
    assert params != null;
    JSONString paramTypes = params.get("types").isString();
    assert paramTypes != null;
    String typesString = paramTypes.stringValue();
    Object[] parameters = new Object[typesString.length()];

    JSONArray paramValues = params.get("values").isArray();
    assert paramValues != null;
    assert paramValues.size() == typesString.length();
    for (int i = 0, size = paramValues.size(); i < size; ++i) {
      JSONString param = paramValues.get(i).isString();
      assert param != null;
      SerializableType type = SerializableType.getFrom(typesString.charAt(i));
      assert type != SerializableType.UNSUPPORTED; // TODO: Exception.
      parameters[i] = type.parseFromString(param.stringValue());
    }
    request.setParameters(parameters);

    return request;
  }

}
