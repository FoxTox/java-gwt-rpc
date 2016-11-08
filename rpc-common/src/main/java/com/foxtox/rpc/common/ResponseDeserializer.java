package com.foxtox.rpc.common;

public interface ResponseDeserializer {
	RpcResponse deserialize(SerializableType type, byte[] data);
}