package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;
import com.foxtox.rpc.client.gwt.JsonRequestSerializer;
import com.foxtox.rpc.client.gwt.JsonResponseDeserializer;
import com.foxtox.rpc.common.RequestSerializer;
import com.foxtox.rpc.common.ResponseDeserializer;
import com.foxtox.rpc.common.RpcResponse;
import com.foxtox.rpc.common.SerializableType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

// TODO: Generate this with a generator.
public class SumServiceAsyncImplTemplate implements SumServiceAsync {

  public void getSum(Integer first, Integer second, final AsyncCallback<Integer> callback) {
    // TODO: Make the URL configurable.
    RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/sum");

    RequestSerializer serializer = new JsonRequestSerializer();
    serializer.setServiceName("com.foxtox.rpc.client.example.SumService");
    serializer.setServiceMethod("getSum");
    serializer.addParameter(first);
    serializer.addParameter(second);
    builder.setRequestData(new String(serializer.serialize()));

    builder.setCallback(new RequestCallback() {
      public void onResponseReceived(Request request, Response response) {
        ResponseDeserializer deserializer = new JsonResponseDeserializer();
        RpcResponse rpcResponse = deserializer.deserialize(SerializableType.INTEGER, response.getText().getBytes());

        if (rpcResponse.getType() == RpcResponse.Type.SUCCESS)
          callback.onSuccess((Integer) rpcResponse.getResult());
        else if (rpcResponse.getType() == RpcResponse.Type.ERROR)
          callback.onFailure(rpcResponse.getError());
        else
          callback.onFailure("Received wrong response type: " + rpcResponse.getType());
      }

      public void onError(Request request, Throwable throwable) {
        callback.onFailure("RPC Error: " + throwable);
      }
    });
    try {
      builder.send();
    } catch (RequestException e) {
      // Ignore.
    }
  }

  public void getSum(Double first, Double second, final AsyncCallback<Double> callback) {
    // TODO: Make the URL configurable.
    RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/sum");

    RequestSerializer serializer = new JsonRequestSerializer();
    serializer.setServiceName("com.foxtox.rpc.client.example.SumService");
    serializer.setServiceMethod("getSum");
    serializer.addParameter(first);
    serializer.addParameter(second);
    builder.setRequestData(new String(serializer.serialize()));

    builder.setCallback(new RequestCallback() {
      public void onResponseReceived(Request request, Response response) {
        ResponseDeserializer deserializer = new JsonResponseDeserializer();
        RpcResponse rpcResponse = deserializer.deserialize(SerializableType.DOUBLE, response.getText().getBytes());

        if (rpcResponse.getType() == RpcResponse.Type.SUCCESS)
          callback.onSuccess((Double) rpcResponse.getResult());
        else if (rpcResponse.getType() == RpcResponse.Type.ERROR)
          callback.onFailure(rpcResponse.getError());
        else
          callback.onFailure("Received wrong response type: " + rpcResponse.getType());
      }

      public void onError(Request request, Throwable throwable) {
        callback.onFailure("RPC Error: " + throwable);
      }
    });
    try {
      builder.send();
    } catch (RequestException e) {
      // Ignore.
    }
  }

}
