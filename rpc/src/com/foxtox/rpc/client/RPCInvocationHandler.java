package com.foxtox.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.foxtox.rpc.AsyncCallback;

public class RPCInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		check(!method.getDeclaringClass().equals(Object.class),
				"java.lang.Object methods are not supported.");

		Class<?>[] paramClasses = method.getParameterTypes();
		Type[] paramTypes = method.getGenericParameterTypes();
		check(paramClasses.length == paramTypes.length, "Lengths mismatch.");
		check(paramTypes.length >= 1, "Too few parameters.");

		Class<?> returnClass = paramClasses[paramClasses.length - 1];
		check(returnClass.equals(AsyncCallback.class),
				"The last parameter should be AsyncCallback<ReturnType>.");
		check(returnClass.getTypeParameters().length == 1, "Expected exactly 1 type parameter.");
		ParameterizedType returnType = (ParameterizedType) paramTypes[paramTypes.length - 1];
		check(returnType.getActualTypeArguments().length == 1,
				"Expected exactly 1 type parameter.");
		// Type actualReturnType = returnType.getActualTypeArguments()[0];

		check(method.getReturnType().equals(void.class), "The method should return void.");

		byte[] message = encodeAsyncInvocation(method, args);
		System.out.println("Encoded call message into " + message.length + " bytes.");
		for (byte c : message)
			System.out.print((char) c);
		System.out.println();

		try {
			URL url = new URL("http://127.0.0.1:8888"); // FIXME
			HttpURLConnection servletConnection = (HttpURLConnection) url.openConnection();
			servletConnection.setRequestMethod("POST");
			servletConnection.setDoOutput(true);

			ObjectOutputStream objOut = new ObjectOutputStream(servletConnection.getOutputStream());
			objOut.writeObject(message);
			objOut.flush();
			objOut.close();

			ObjectInputStream objIn = new ObjectInputStream(servletConnection.getInputStream());
			return objIn.readObject(); // TODO: Exceptions.
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private byte[] encodeAsyncInvocation(Method method, Object[] args) throws Exception {
		Class<?>[] paramTypes = method.getParameterTypes();
		check(paramTypes.length == args.length, "Arrays lengths mismatch.");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(output);

		objectOutput.writeObject(method.getName());
		objectOutput.writeInt(paramTypes.length - 1);
		for (int i = 0; i + 1 < paramTypes.length; ++i) {
			// encodeParameterType(objectOutput, paramTypes[i]);
			objectOutput.writeObject(args[i]);
		}
		objectOutput.flush();

		return output.toByteArray();
	}

	/*
	private void encodeParameterType(ObjectOutputStream output, Class<?> type) throws Exception {
		SerializableType paramType = SerializableType.getFrom(type);
		check(paramType != SerializableType.UNSUPPORTED,
				"Type " + type.getCanonicalName() + "is not supported.");
		output.writeByte(paramType.getCode());
	}
	*/

	private void check(boolean condition, String errorMessage) throws Exception {
		if (!condition)
			throw new Exception(errorMessage);
	}

}
