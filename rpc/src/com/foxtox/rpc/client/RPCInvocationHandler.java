package com.foxtox.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.foxtox.rpc.AsyncCallback;
import com.foxtox.rpc.ParameterType;

public class RPCInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		check(!method.getDeclaringClass().equals(Object.class),
				"java.lang.Object methods are not supported.");

		Class<?>[] paramTypes = method.getParameterTypes();
		check(paramTypes.length >= 1, "Too few parameters.");
		check(paramTypes[paramTypes.length - 1].equals(AsyncCallback.class),
				"The last parameter should be AsyncCallback<ReturnType>.");
		check(method.getReturnType().equals(void.class), "The method should return void.");

		byte[] message = encodeAsyncInvocation(method, args);
		System.out.println("Encoded call message into " + message.length + " bytes.");

		return null;
	}

	byte[] encodeAsyncInvocation(Method method, Object[] args) throws Exception {
		Class<?>[] paramTypes = method.getParameterTypes();
		check(paramTypes.length == args.length, "Arrays lengths mismatch.");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(output);

		objectOutput.writeObject(method.getName());
		objectOutput.writeInt(paramTypes.length - 1);
		for (int i = 0; i + 1 < paramTypes.length; ++i) {
			check(paramTypes[i].isInstance(Serializable.class),
					"Type " + paramTypes[i].getCanonicalName() + " is not serializable.");
			encodeParameterType(objectOutput, paramTypes[i]);
			objectOutput.writeObject(args[i]);
		}
		objectOutput.flush();
		
		return output.toByteArray();
	}

	void encodeParameterType(ObjectOutputStream output, Class<?> type) throws Exception {
		ParameterType paramType = ParameterType.getFrom(type);
		check(paramType != ParameterType.UNSUPPORTED,
				"Type " + type.getCanonicalName() + "is not supported.");
		output.writeByte(paramType.getCode());
	}

	void check(boolean condition, String errorMessage) throws Exception {
		if (!condition)
			throw new Exception(errorMessage);
	}

}
