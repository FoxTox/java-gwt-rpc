package com.foxtox.rpc.core.client;

public interface SumServiceAsync  {
	void getSum(Integer first, Integer second, AsyncCallback<Integer> callback);
	void getSum(Double first, Double second, AsyncCallback<Double> callback);
}
