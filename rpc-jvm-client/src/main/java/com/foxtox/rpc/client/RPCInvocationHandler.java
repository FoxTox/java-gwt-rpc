package com.foxtox.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;

import com.foxtox.rpc.client.jvm.JsonRequestSerializer;
import com.foxtox.rpc.client.jvm.JsonResponseDeserializer;
import com.foxtox.rpc.common.RequestSerializer;
import com.foxtox.rpc.common.ResponseDeserializer;
import com.foxtox.rpc.common.RpcResponse;
import com.foxtox.rpc.common.SerializableType;

public class RPCInvocationHandler implements InvocationHandler {

  private String serverAddress;

  public RPCInvocationHandler(String serverAddress) {
    this.serverAddress = serverAddress;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    check(!method.getDeclaringClass().equals(Object.class),
        "java.lang.Object methods should not be called.");

    // TODO: Cache method signature checks.
    Class<?>[] paramClasses = method.getParameterTypes();
    Type[] paramTypes = method.getGenericParameterTypes();
    check(paramClasses.length == paramTypes.length, "Lengths mismatch.");
    check(paramTypes.length >= 1, "Too few parameters.");

    Class<?> returnClass = paramClasses[paramClasses.length - 1];
    check(returnClass.equals(AsyncCallback.class),
        "The last parameter should be AsyncCallback<ReturnType>.");
    check(returnClass.getTypeParameters().length == 1, "Expected exactly 1 type parameter.");

    ParameterizedType returnType = (ParameterizedType) paramTypes[paramTypes.length - 1];
    check(returnType.getActualTypeArguments().length == 1, "Expected exactly 1 type parameter.");
    Type actualReturnType = returnType.getActualTypeArguments()[0];
    final Class<?> actualReturnClass = Class.forName(actualReturnType.getTypeName());

    check(method.getReturnType().equals(void.class), "The method should return void.");

    final AsyncCallback callback = (AsyncCallback) args[args.length - 1];

    String ifaceName = method.getDeclaringClass().getCanonicalName();
    check(ifaceName.endsWith("Async"), "Interface should be Async");
    ifaceName = ifaceName.substring(0, ifaceName.length() - 5);

    RequestSerializer serializer = new JsonRequestSerializer();
    serializer.setServiceName(ifaceName);
    serializer.setServiceMethod(method.getName());
    for (int i = 0; i + 1 < args.length; ++i)
      serializer.addParameter(args[i]);

    try {
      RPC.getAsyncHttpClient().preparePost(serverAddress).setBody(serializer.serialize())
          .execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
              ResponseDeserializer deserializer = new JsonResponseDeserializer();
              byte[] requestData = response.getResponseBodyAsBytes();
              SerializableType responseType = SerializableType.getFrom(actualReturnClass);
              RpcResponse rpcResponse = deserializer.deserialize(responseType, requestData);

              assert (rpcResponse.getType() != RpcResponse.Type.UNDEFINED);
              if (rpcResponse.getType() == RpcResponse.Type.SUCCESS) {
                callback.onSuccess(rpcResponse.getResult());
              } else {
                // response.getType() == RpcResponse.Type.ERROR
                callback.onFailure(rpcResponse.getError());
              }

              return response;
            }

            @Override
            public void onThrowable(Throwable t) {
              callback.onFailure("RPC failed with Throwable: " + t);
            }
          });

    } catch (Exception e) {
      callback.onFailure("RPC failed with Exception: " + e);
    }

    return null;
  }

  private void check(boolean condition, String errorMessage) throws Exception {
    if (!condition)
      throw new Exception(errorMessage);
  }

}
