package com.foxtox.rpc.client;

import java.io.IOException;
import java.lang.reflect.Proxy;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

public class RPC {

  public static Object create(Class<?> cls, String serverAddress) {
    // TODO: Get path from the class annotations.
    String serviceURL = serverAddress + "/sum";
    try {
      return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] { cls }, new RPCInvocationHandler(serviceURL));
    } catch (Exception e) {
      return null;
    }
  }

  public static AsyncHttpClient getAsyncHttpClient() {
    return asyncHttpClient;
  }
  
  public static void terminate() throws IOException {
    asyncHttpClient.close();
  }

  // TODO: Double check that this class is thread safe. If not, synchronize
  // access.
  private static AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

}
