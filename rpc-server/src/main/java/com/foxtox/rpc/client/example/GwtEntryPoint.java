package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class GwtEntryPoint implements EntryPoint {

  public void onModuleLoad() {
    final Integer iFirst = 10, iSecond = 20;
    final Double dFirst = 10.1, dSecond = 30.6;

    SumServiceAsync sumService = (SumServiceAsync) GWT.create(SumService.class);
    ConcatenationServiceAsync concatService = (ConcatenationServiceAsync) GWT
        .create(ConcatenationService.class);

    sumService.getSum(iFirst, iSecond, new AsyncCallback<Integer>() {
      public void onSuccess(Integer returnValue) {
        Window.alert(iFirst + " + " + iSecond + " == " + returnValue);
      }

      public void onFailure(String reason) {
        Window.alert("Failed to compute " + iFirst + " + " + iSecond + ". Reason:\n" + reason);
      }
    });

    sumService.getSum(dFirst, dSecond, new AsyncCallback<Double>() {
      public void onSuccess(Double returnValue) {
        Window.alert(dFirst + " + " + dSecond + " == " + returnValue);
      }

      public void onFailure(String reason) {
        Window.alert("Failed to compute " + dFirst + " + " + dSecond + ". Reason:\n" + reason);
      }
    });

    concatService.concatenate("begin_", "end", new AsyncCallback<String>() {
      public void onSuccess(String returnValue) {
        Window.alert("Concatenated: " + returnValue);
      }

      public void onFailure(String reason) {
        Window.alert("Failed to concatenate. Reason:\n" + reason);
      }
    });
  }
}
