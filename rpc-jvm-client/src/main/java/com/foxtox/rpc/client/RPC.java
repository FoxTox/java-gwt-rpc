package com.foxtox.rpc.client;

import java.io.IOException;
import java.lang.reflect.Proxy;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

public class RPC {

  // TODO: Double check that this class is thread safe. If not, synchronize
  // access.
  private static AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

  public static Object create(Class<?> cls, String serverAddress) {
    String asyncServiceName = cls.getCanonicalName();
    assert asyncServiceName.substring(asyncServiceName.length() - 5).equals("Async");

    Class<?> syncServiceClass = null;
    try {
      syncServiceClass = Class
          .forName(asyncServiceName.substring(0, asyncServiceName.length() - 5));
    } catch (Exception e) {
      assert false;
    }

    RemoteServiceRelativePath annotation = syncServiceClass
        .getAnnotation(RemoteServiceRelativePath.class);
    assert annotation != null;

    String serviceURL = serverAddress + annotation.value();
    try {
      return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] { cls },
          new RPCInvocationHandler(serviceURL));
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

}
