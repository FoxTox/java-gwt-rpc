package com.foxtox.rpc.client.example;

import java.util.concurrent.CountDownLatch;

import com.foxtox.rpc.client.AsyncCallback;
import com.foxtox.rpc.client.RPC;

public class Main {
  private static final String serverAddress = "http://localhost:8888";
  private static final int iFirst = 10, iSecond = 20;
  private static final double dFirst = 10.1, dSecond = 30.6;

  public static void main(String[] args) {
    SumServiceAsync sum = (SumServiceAsync) RPC.create(SumServiceAsync.class, serverAddress);
    ConcatenationServiceAsync concat = (ConcatenationServiceAsync) RPC
        .create(ConcatenationServiceAsync.class, serverAddress);

    final CountDownLatch countDown = new CountDownLatch(3);

    sum.getSum(iFirst, iSecond, new AsyncCallback<Integer>() {
      public void onSuccess(Integer result) {
        System.out.println("Success: " + iFirst + " + " + iSecond + " == " + result);
        countDown.countDown();
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
        countDown.countDown();
      }
    });

    sum.getSum(dFirst, dSecond, new AsyncCallback<Double>() {
      public void onSuccess(Double result) {
        System.out.println("Success: " + dFirst + " + " + dSecond + " == " + result);
        countDown.countDown();
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
        countDown.countDown();
      }
    });

    concat.concatenate("begin_", "end", new AsyncCallback<String>() {
      public void onSuccess(String result) {
        System.out.println("Concatenation: " + result);
        countDown.countDown();
      }

      public void onFailure(String reason) {
        System.out.println("Failure: " + reason);
        countDown.countDown();
      }
    });

    try {
      countDown.await();
      RPC.terminate();
      System.out.println("RPC client terminated.");
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

}
