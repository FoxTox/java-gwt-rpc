package com.foxtox.rpc.common;

import java.util.HashMap;
import java.util.Map;

public enum SerializableType {

  UNSUPPORTED(null, 'U') {
    @Override
    public Object parseFromString(String string) {
      assert false;
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

  private final Class<?> clazz;
  private final char symbol;

  private static Map<Class<?>, SerializableType> classToType;
  private static Map<Character, SerializableType> charToType;

  private SerializableType(Class<?> clazz, char symbol) {
    this.clazz = clazz;
    this.symbol = symbol;
  }

  public Class<?> getNativeClass() {
    return clazz;
  }

  public char getSymbol() {
    return symbol;
  }

  public abstract Object parseFromString(String string);

  public static SerializableType getFrom(Class<?> type) {
    assert classToType != null;
    return classToType.getOrDefault(type, UNSUPPORTED);
  }

  public static SerializableType getFrom(char symbol) {
    assert charToType != null;
    return charToType.getOrDefault(symbol, UNSUPPORTED);
  }

  static {
    classToType = new HashMap<Class<?>, SerializableType>();
    charToType = new HashMap<Character, SerializableType>();

    for (SerializableType type : SerializableType.values()) {
      if (type == SerializableType.UNSUPPORTED)
        continue;

      assert type.getNativeClass() != null;
      classToType.put(type.getNativeClass(), type);
      charToType.put(type.getSymbol(), type);
    }
  }

}
