package com.foxtox.rpc.example.client;

import com.foxtox.rpc.AsyncCallback;

public interface SumServiceAsync {
	void getSum(int a, int b, AsyncCallback<Integer> resultCallback);
	void getSum(double a, double b, AsyncCallback<Double> resultCallback);
}
