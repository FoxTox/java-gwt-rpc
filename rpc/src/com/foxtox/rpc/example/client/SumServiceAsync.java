package com.foxtox.rpc.example.client;

import com.foxtox.rpc.AsyncCallback;

public interface SumServiceAsync {
	void getSum(Integer a, Integer b, AsyncCallback<Integer> resultCallback);
	void getSum(Double a, Double b, AsyncCallback<Double> resultCallback);
}
