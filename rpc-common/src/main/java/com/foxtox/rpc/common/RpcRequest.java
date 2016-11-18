package com.foxtox.rpc.common;

public class RpcRequest {
  
  private String serviceName;
  private String serviceMethod;
  private Object[] parameters;

  public String getServiceName() {
    return serviceName;
  }
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceMethod() {
    return serviceMethod;
  }
  public void setServiceMethod(String serviceMethod) {
    this.serviceMethod = serviceMethod;
  }

  public Object[] getParameters() {
    return parameters;
  }
  public void setParameters(Object[] parameters) {
    this.parameters = parameters;
  }

}
