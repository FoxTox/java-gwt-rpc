package com.foxtox.rpc.client.example;

import java.io.IOException;

import com.foxtox.rpc.client.AsyncCallback;
import com.foxtox.rpc.client.RPC;

class RequestCounter {

  private Integer requestsInFly = 0;

  public void emitRequest() {
    editRequestsInFly(1);
  }

  public void completeRequest() {
    editRequestsInFly(-1);
  }

  public void waitUntilAllCompleted() {
    try {
      synchronized (this) {
        while (requestsInFly != 0)
          this.wait();
      }
    } catch (InterruptedException exc) {
      assert false;
    }
  }

  private void editRequestsInFly(int delta) {
    synchronized (this) {
      requestsInFly += delta;
      if (requestsInFly == 0)
        this.notifyAll();
    }
  }

}

public class Main {
  private static final String serverAddress = "http://localhost:8888";
  private static final int iFirst = 10, iSecond = 20;
  private static final double dFirst = 10.1, dSecond = 30.6;

  public static void main(String[] args) {
    SumServiceAsync sum = (SumServiceAsync) RPC.create(SumServiceAsync.class, serverAddress);
    ConcatenationServiceAsync concat = (ConcatenationServiceAsync) RPC
        .create(ConcatenationServiceAsync.class, serverAddress);

    final RequestCounter requests = new RequestCounter();

    requests.emitRequest();
    sum.getSum(iFirst, iSecond, new AsyncCallback<Integer>() {
      public void onSuccess(Integer result) {
        System.out.println("Success: " + iFirst + " + " + iSecond + " == " + result);
        requests.completeRequest();
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
        requests.completeRequest();
      }
    });

    requests.emitRequest();
    sum.getSum(dFirst, dSecond, new AsyncCallback<Double>() {
      public void onSuccess(Double result) {
        System.out.println("Success: " + dFirst + " + " + dSecond + " == " + result);
        requests.completeRequest();
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
        requests.completeRequest();
      }
    });

    requests.emitRequest();
    concat.concatenate("begin_", "end", new AsyncCallback<String>() {
      public void onSuccess(String result) {
        System.out.println("Concatenation: " + result);
        requests.completeRequest();
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
        requests.completeRequest();
      }
    });

    requests.waitUntilAllCompleted();
    try {
      RPC.terminate();
      System.out.println("RPC client terminated.");
    } catch (IOException exc) {
      exc.printStackTrace();
    }
  }

}
