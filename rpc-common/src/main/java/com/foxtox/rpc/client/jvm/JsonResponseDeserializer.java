package com.foxtox.rpc.client.jvm;

import java.io.ByteArrayInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import com.foxtox.rpc.common.ResponseDeserializer;
import com.foxtox.rpc.common.RpcResponse;
import com.foxtox.rpc.common.SerializableType;

public class JsonResponseDeserializer implements ResponseDeserializer {

  public RpcResponse deserialize(SerializableType type, byte[] data) {
    RpcResponse response = new RpcResponse();

    JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(data));
    JsonObject object = jsonReader.readObject();

    JsonString errorString = object.getJsonString("error");
    if (errorString != null) {
      response.setError(errorString.getString());
      return response;
    }
    response.setResult(type.parseFromString(object.getString("result")));
    return response;
  }

}
