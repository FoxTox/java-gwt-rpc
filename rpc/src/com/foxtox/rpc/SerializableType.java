package com.foxtox.rpc;

import java.util.HashMap;
import java.util.Map;

public enum SerializableType {
	UNSUPPORTED(0),
	
	VOID(1),
	BOOLEAN(4),
	BYTE(5),
	CHARACTER(6),
	SHORT(7),
	INTEGER(8),
	LONG(9),
	FLOAT(10),
	DOUBLE(11),
	STRING(16);
	
	private final byte code;

	private SerializableType(int code) {
		this.code = (byte)code;
	}

	public byte getCode() {
		return code;
	}
	
	public static boolean isSupported(Class<?> type) {
		return getFrom(type) != UNSUPPORTED;
	}
	
	public static SerializableType getFrom(Class<?> type) {
		if (supportedTypes == null)
			initializeSupportedTypes();
		return supportedTypes.getOrDefault(type, UNSUPPORTED);
	}
	
	private static Map<Class<?>, SerializableType> supportedTypes;
	
	private static void initializeSupportedTypes() {
		supportedTypes = new HashMap<Class<?>, SerializableType>();
		supportedTypes.put(Void.class, VOID);
		supportedTypes.put(Boolean.class, BOOLEAN);
		supportedTypes.put(Byte.class, BYTE);
		supportedTypes.put(Character.class, CHARACTER);
		supportedTypes.put(Short.class, SHORT);
		supportedTypes.put(Integer.class, INTEGER);
		supportedTypes.put(Long.class, LONG);
		supportedTypes.put(Float.class, FLOAT);
		supportedTypes.put(Double.class, DOUBLE);
		supportedTypes.put(String.class, STRING);
	}
}
