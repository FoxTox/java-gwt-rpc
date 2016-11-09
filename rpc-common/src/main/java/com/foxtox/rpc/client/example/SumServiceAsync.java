package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;

public interface SumServiceAsync {
	void getSum(Integer first, Integer second, AsyncCallback<Integer> callback);
	void getSum(Double first, Double second, AsyncCallback<Double> callback);
}