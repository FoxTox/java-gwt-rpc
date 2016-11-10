package com.foxtox.rpc.client;

import java.lang.reflect.Proxy;
import java.net.URL;

public class RPC {

  public static Object create(Class<?> cls, String serverAddress) {
    // TODO: Get path from the class annotations.
    String serviceURL = serverAddress + "/sum";
    try {
      return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] { cls },
          new RPCInvocationHandler(new URL(serviceURL)));
    } catch (Exception e) {
      return null;
    }
  }

}
