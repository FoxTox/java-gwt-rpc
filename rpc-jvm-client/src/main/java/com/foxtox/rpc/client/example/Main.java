package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;
import com.foxtox.rpc.client.RPC;

// import com.turbomanage.httpclient.*;

public class Main {
  private static final String serverAddress = "http://localhost:8888";
  private static final int first = 10, second = 20;
  
  /*
  public static void main(String[] args) {
    // System.out.println(Util.getValue());
    BasicHttpClient client = new BasicHttpClient(URL);
    HttpRequest req = new HttpGet("/rpc", null);
    HttpResponse resp = client.execute(req);
    System.out.println(resp.getBodyAsString());
  }
  */

  public static void main(String[] args) {
    SumServiceAsync f = (SumServiceAsync) RPC.create(SumServiceAsync.class, serverAddress);
    f.getSum(first, second, new AsyncCallback<Integer>() {
      public void onSuccess(Integer result) {
        System.out.println("Success: " + first + " + " + second + " == " + result);
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
      }
    });
    // FIXME: Wait here until the request is done.
  }
}
