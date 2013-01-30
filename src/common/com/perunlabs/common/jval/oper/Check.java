package com.perunlabs.common.jval.oper;

public class Check {
  private Check() {}

  public static <T> T notNull(T value) {
    if (value == null) {
      throw new NullPointerException();
    }
    return value;
  }

  public static void isPositive(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Value " + value + " should be positive");
    }
  }

  public static void isPositive(long value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Value " + value + " should be positive");
    }
  }
}
