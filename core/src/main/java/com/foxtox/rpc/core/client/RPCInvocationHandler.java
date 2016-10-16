package com.foxtox.rpc.core.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class RPCInvocationHandler implements InvocationHandler {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		check(!method.getDeclaringClass().equals(Object.class), "java.lang.Object methods should not be called.");

		// TODO: Cache method signature checks.
		Class<?>[] paramClasses = method.getParameterTypes();
		Type[] paramTypes = method.getGenericParameterTypes();
		check(paramClasses.length == paramTypes.length, "Lengths mismatch.");
		check(paramTypes.length >= 1, "Too few parameters.");

		Class<?> returnClass = paramClasses[paramClasses.length - 1];
		check(returnClass.equals(AsyncCallback.class), "The last parameter should be AsyncCallback<ReturnType>.");
		check(returnClass.getTypeParameters().length == 1, "Expected exactly 1 type parameter.");

		ParameterizedType returnType = (ParameterizedType) paramTypes[paramTypes.length - 1];
		check(returnType.getActualTypeArguments().length == 1, "Expected exactly 1 type parameter.");
		Type actualReturnType = returnType.getActualTypeArguments()[0];
		Class<?> actualReturnClass = Class.forName(actualReturnType.getTypeName());

		check(method.getReturnType().equals(void.class), "The method should return void.");

		AsyncCallback callback = (AsyncCallback) args[args.length - 1];
		try {
			// TODO: Adjust the server.
			URL url = new URL("http://127.0.0.1:8080");
			// TODO: Async connection.
			HttpURLConnection servletConnection = (HttpURLConnection) url.openConnection();
			servletConnection.setRequestMethod("POST");
			servletConnection.setDoOutput(true);
			
			String ifaceName = method.getDeclaringClass().getCanonicalName();
			check(ifaceName.endsWith("Async"), "Interface should be Async");
			ifaceName = ifaceName.substring(0, ifaceName.length() - 5);

			RequestSerializer serializer = new JsonRequestSerializer(servletConnection.getOutputStream());
			serializer.addServiceName(ifaceName);
			serializer.addServiceMethod(method.getName());
			serializer.addParameters(args, 0, args.length - 1);
			serializer.finish();

			JsonResponseDeserializer deserializer = new JsonResponseDeserializer(servletConnection.getInputStream(),
					SerializableType.getFrom(actualReturnClass));
			if (deserializer.isSuccess()) {
				callback.onSuccess(deserializer.getResult());
			} else {
				callback.onFailure(deserializer.getError());
			}
		} catch (Exception e) {
			callback.onFailure(e.toString());
		}

		return null;
	}

	private void check(boolean condition, String errorMessage) throws Exception {
		if (!condition)
			throw new Exception(errorMessage);
	}

}
