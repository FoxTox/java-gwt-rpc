package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.gwt.JsonRequestSerializer;
import com.foxtox.rpc.common.RequestSerializer;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;

public class GwtEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		System.out.println("Hey, I'm running!");
		Window.alert("Hello, AJAX");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/sum");
		
		// TODO: Generate this with a generator.
		RequestSerializer serializer = new JsonRequestSerializer();
		serializer.setServiceName("com.foxtox.rpc.client.example.SumService");
		serializer.setServiceMethod("getSum");
		serializer.addParameter(10);
		serializer.addParameter(20);
		builder.setRequestData(new String(serializer.serialize()));
		
		builder.setCallback(new RequestCallback() {
			public void onResponseReceived(Request request, Response response) {
				Window.alert(response.getText());
			}

			public void onError(Request request, Throwable throwable) {
			}
		});
		try {
			builder.send();
		} catch (RequestException e) {
			// ignore
		}
	}
}