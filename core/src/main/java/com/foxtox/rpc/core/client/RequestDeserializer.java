package com.foxtox.rpc.core.client;

public interface RequestDeserializer {
	String getServiceName();
	String getServiceMethod();
	Object[] getParameters();
}
