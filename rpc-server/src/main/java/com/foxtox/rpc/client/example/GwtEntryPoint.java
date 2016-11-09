package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

public class GwtEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		final int first = 10, second = 20;

		SumServiceAsync sumService = new SumServiceAsyncImpl();
		sumService.getSum(first, second, new AsyncCallback<Integer>() {
			public void onSuccess(Integer returnValue) {
				Window.alert(first + " + " + second + " == " + returnValue);
			}
			public void onFailure(String reason) {
				Window.alert("Failed to compute " + first + " + " + second + ". Reason:\n" + reason);
			}
		});
	}
}