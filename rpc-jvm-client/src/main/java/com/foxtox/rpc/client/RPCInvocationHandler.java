package com.foxtox.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import com.foxtox.rpc.client.jvm.JsonRequestSerializer;
import com.foxtox.rpc.client.jvm.JsonResponseDeserializer;
import com.foxtox.rpc.common.RequestSerializer;
import com.foxtox.rpc.common.ResponseDeserializer;
import com.foxtox.rpc.common.RpcResponse;
import com.foxtox.rpc.common.SerializableType;

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
			URL url = new URL("http://127.0.0.1:8888/sum");
			// TODO: Async connection.
			HttpURLConnection servletConnection = (HttpURLConnection) url.openConnection();
			servletConnection.setRequestMethod("POST");
			servletConnection.setDoOutput(true);

			String ifaceName = method.getDeclaringClass().getCanonicalName();
			check(ifaceName.endsWith("Async"), "Interface should be Async");
			ifaceName = ifaceName.substring(0, ifaceName.length() - 5);

			RequestSerializer serializer = new JsonRequestSerializer();
			serializer.setServiceName(ifaceName);
			serializer.setServiceMethod(method.getName());
			for (int i = 0; i + 1 < args.length; ++i)
				serializer.addParameter(args[i]);
			servletConnection.getOutputStream().write(serializer.serialize());

			ResponseDeserializer deserializer = new JsonResponseDeserializer();
			byte[] requestData = readAll(servletConnection.getInputStream());
			SerializableType responseType = SerializableType.getFrom(actualReturnClass);
			RpcResponse response = deserializer.deserialize(responseType, requestData);

			assert (response.getType() != RpcResponse.Type.UNDEFINED);
			if (response.getType() == RpcResponse.Type.SUCCESS) {
				callback.onSuccess(response.getResult());
			} else {
				// response.getType() == RpcResponse.Type.ERROR
				callback.onFailure(response.getError());
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

	private static final int BUFFER_SIZE = 4096;

	protected byte[] readAll(InputStream input) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while (true) {
			int byteCount = input.read(buffer);
			if (byteCount == -1)
				break;
			output.write(buffer, 0, byteCount);
		}
		return output.toByteArray();
	}

}