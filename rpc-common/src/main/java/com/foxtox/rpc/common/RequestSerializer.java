package com.foxtox.rpc.common;

public interface RequestSerializer {
  void setServiceName(String serviceName);
  void setServiceMethod(String serviceMethod);
  void addParameter(Object parameter);

  byte[] serialize();
}
