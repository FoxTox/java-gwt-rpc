package com.foxtox.rpc.example.client;

import java.lang.reflect.Proxy;

import com.foxtox.rpc.AsyncCallback;
import com.foxtox.rpc.client.RPCInvocationHandler;

public class Main {
	
    public static void main(String[] args) {
    	
    	SumServiceAsync f = (SumServiceAsync)Proxy.newProxyInstance(SumServiceAsync.class.getClassLoader(),
                new Class[] { SumServiceAsync.class },
                new RPCInvocationHandler());
    	f.getSum(10, 20, new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				System.out.println("Int result: " + result);
			}
			@Override
			public void ofFailure(String reason) {
				System.out.println("Int fail: " + reason);
			}
		});
    	
    	f.getSum(10.0, 20.1, new AsyncCallback<Double>() {
			@Override
			public void onSuccess(Double result) {
				System.out.println("Double result: " + result);
			}
			@Override
			public void ofFailure(String reason) {
				System.out.println("Double fail: " + reason);
			}
		});
    }
    
}