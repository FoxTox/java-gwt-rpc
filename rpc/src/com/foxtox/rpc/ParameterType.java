package com.foxtox.rpc;

import java.util.HashMap;
import java.util.Map;

public enum ParameterType {
	UNSUPPORTED(0),
	
	VOID(1),
	BOOLEAN(4),
	BYTE(5),
	CHAR(6),
	SHORT(7),
	INT(8),
	LONG(9),
	FLOAT(10),
	DOUBLE(11),
	STRING(16);
	
	private final byte code;

	private ParameterType(int code) {
		this.code = (byte)code;
	}

	public byte getCode() {
		return code;
	}
	
	public static ParameterType getFrom(Class<?> type) {
		if (supportedTypes == null)
			initializeSupportedTypes();
		return supportedTypes.getOrDefault(type, UNSUPPORTED);
	}
	
	private static Map<Class<?>, ParameterType> supportedTypes;
	
	private static void initializeSupportedTypes() {
		supportedTypes = new HashMap<Class<?>, ParameterType>();
		supportedTypes.put(void.class, VOID);
		supportedTypes.put(boolean.class, BOOLEAN);
		supportedTypes.put(byte.class, BYTE);
		supportedTypes.put(char.class, CHAR);
		supportedTypes.put(short.class, SHORT);
		supportedTypes.put(int.class, INT);
		supportedTypes.put(long.class, LONG);
		supportedTypes.put(float.class, FLOAT);
		supportedTypes.put(double.class, DOUBLE);
		supportedTypes.put(String.class, STRING);
	}
}
