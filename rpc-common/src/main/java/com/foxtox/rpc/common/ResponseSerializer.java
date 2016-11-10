package com.foxtox.rpc.common;

public interface ResponseSerializer {
  byte[] serializeResult(Object result);
  byte[] serializeError(String reason);
}
