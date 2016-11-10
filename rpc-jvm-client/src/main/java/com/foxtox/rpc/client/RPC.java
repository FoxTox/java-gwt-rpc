package com.foxtox.rpc.client;

import java.lang.reflect.Proxy;

public class RPC {

  public static Object create(Class<?> cls) {
    return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] { cls }, new RPCInvocationHandler());
  }

}
