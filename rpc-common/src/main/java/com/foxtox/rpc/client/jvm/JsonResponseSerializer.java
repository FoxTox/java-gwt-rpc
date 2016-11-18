package com.foxtox.rpc.client.jvm;

import java.io.ByteArrayOutputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.foxtox.rpc.common.ResponseSerializer;

public class JsonResponseSerializer implements ResponseSerializer {
  
  private JsonObjectBuilder requestObjectBuilder = Json.createObjectBuilder();

  public byte[] serializeResult(Object result) {
    requestObjectBuilder.add("result", result.toString());
    return finish();
  }

  public byte[] serializeError(String reason) {
    requestObjectBuilder.add("error", reason);
    return finish();
  }

  private byte[] finish() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JsonWriter jsonWriter = Json.createWriter(output);
    JsonObject obj = requestObjectBuilder.build();
    jsonWriter.writeObject(obj);
    jsonWriter.close();
    return output.toByteArray();
  }

}
