package com.foxtox.rpc.client;

public interface AsyncCallback<ReturnType> {
  public void onSuccess(ReturnType returnValue);
  public void onFailure(String reason);
}
