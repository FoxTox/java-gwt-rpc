package com.foxtox.rpc.common;

import java.util.HashMap;
import java.util.Map;

public enum SerializableType {

	UNSUPPORTED(null, 'U') {
		@Override
		public Object parseFromString(String string) {
			return null;
		}
	},

	STRING(String.class, 'T') {
		@Override
		public Object parseFromString(String string) {
			return string;
		}
	},

	VOID(Void.class, 'V') {
		@Override
		public Object parseFromString(String string) {
			return null;
		}
	},
	BOOLEAN(Boolean.class, 'Z') {
		@Override
		public Object parseFromString(String string) {
			return Boolean.parseBoolean(string);
		}
	},
	BYTE(Byte.class, 'B') {
		@Override
		public Object parseFromString(String string) {
			return Byte.parseByte(string);
		}
	},
	CHARACTER(Character.class, 'C') {
		@Override
		public Object parseFromString(String string) {
			return string.charAt(0);
		}
	},
	SHORT(Short.class, 'S') {
		@Override
		public Object parseFromString(String string) {
			return Short.parseShort(string);
		}
	},
	INTEGER(Integer.class, 'I') {
		@Override
		public Object parseFromString(String string) {
			return Integer.parseInt(string);
		}
	},
	LONG(Long.class, 'J') {
		@Override
		public Object parseFromString(String string) {
			return Long.parseLong(string);
		}
	},
	FLOAT(Float.class, 'F') {
		@Override
		public Object parseFromString(String string) {
			return Float.parseFloat(string);
		}
	},
	DOUBLE(Double.class, 'D') {
		@Override
		public Object parseFromString(String string) {
			return Double.parseDouble(string);
		}
	};

	private final Class<?> cls;
	private final char symbol;

	private SerializableType(Class<?> cls, char symbol) {
		this.cls = cls;
		this.symbol = symbol;
	}

	public Class<?> getNativeClass() {
		return cls;
	}

	public char getSymbol() {
		return symbol;
	}

	public abstract Object parseFromString(String string);

	public static SerializableType getFrom(Class<?> type) {
		if (classToType == null)
			initializeSupportedTypes();
		return classToType.getOrDefault(type, UNSUPPORTED);
	}

	public static SerializableType getFrom(char symbol) {
		if (charToType == null)
			initializeSupportedTypes();
		return charToType.getOrDefault(symbol, UNSUPPORTED);
	}

	private static Map<Class<?>, SerializableType> classToType;
	private static Map<Character, SerializableType> charToType;

	private static void addType(Class<?> cls, SerializableType type) {
		classToType.put(cls, type);
		charToType.put(type.getSymbol(), type);
	}

	private static void initializeSupportedTypes() {
		classToType = new HashMap<Class<?>, SerializableType>();
		charToType = new HashMap<Character, SerializableType>();

		addType(Void.class, VOID);
		addType(Boolean.class, BOOLEAN);
		addType(Byte.class, BYTE);
		addType(Character.class, CHARACTER);
		addType(Short.class, SHORT);
		addType(Integer.class, INTEGER);
		addType(Long.class, LONG);
		addType(Float.class, FLOAT);
		addType(Double.class, DOUBLE);
		addType(String.class, STRING);
	}
}