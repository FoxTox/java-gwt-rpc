package com.foxtox.rpc.common;

public class RpcResponse {

  public enum Type {
    UNDEFINED, SUCCESS, ERROR,
  }

  public Type getType() {
    return type;
  }

  // Returns the resulting object if getType() == SUCCESS, otherwise null.
  public Object getResult() {
    return result;
  }

  // Returns error string if getType() == ERROR, otherwise null.
  public String getError() {
    return error;
  }

  public void setResult(Object result) {
    this.type = Type.SUCCESS;
    this.result = result;
  }

  public void setError(String error) {
    this.type = Type.ERROR;
    this.error = error;
  }

  private Type type;
  private Object result;
  private String error;
}
