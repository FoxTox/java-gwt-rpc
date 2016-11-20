package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;

public interface ConcatenationServiceAsync {
  void concatenate(String first, String second, AsyncCallback<String> callback);
}
