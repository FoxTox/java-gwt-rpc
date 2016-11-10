package com.foxtox.rpc.common;

public interface RequestDeserializer {
  RpcRequest deserialize(byte[] data);
}
