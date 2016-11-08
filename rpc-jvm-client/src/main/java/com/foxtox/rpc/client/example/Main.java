package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.AsyncCallback;
import com.foxtox.rpc.client.RPC;

// import com.turbomanage.httpclient.*;

public class Main {
	// private static final String URL = "http://localhost:8080";

	/*
	public static void main(String[] args) {
		// System.out.println(Util.getValue());
		BasicHttpClient client = new BasicHttpClient(URL);
		HttpRequest req = new HttpGet("/rpc", null);
		HttpResponse resp = client.execute(req);
		System.out.println(resp.getBodyAsString());
	}
	*/

	public static void main(String[] args) {
		SumServiceAsync f = (SumServiceAsync) RPC.create(SumServiceAsync.class);
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