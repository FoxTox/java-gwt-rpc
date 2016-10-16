package com.foxtox.rpc.core.client;

import java.lang.reflect.Proxy;

public class ClientTest {

	public static void main(String[] args) {

		SumServiceAsync f = (SumServiceAsync) Proxy.newProxyInstance(SumServiceAsync.class.getClassLoader(),
				new Class[] { SumServiceAsync.class }, new RPCInvocationHandler());
		f.getSum(10, 20, new AsyncCallback<Integer>() {
			public void onSuccess(Integer result) {
				System.out.println("Success: " + result);
			}
			public void onFailure(String reason) {
				System.out.println("Failure: " + reason);
			}
		});
	}

}