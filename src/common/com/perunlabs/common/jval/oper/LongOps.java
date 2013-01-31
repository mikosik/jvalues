package com.perunlabs.common.jval.oper;

public class LongOps {
  private LongOps() {}

  public static long add(long param1, long param2) {
    return param1 + param2;
  }

  public static long sub(long param1, long param2) {
    return param1 - param2;
  }

  public static long mul(long param1, long param2) {
    return param1 * param2;
  }

  public static long div(long param1, long param2) {
    return param1 / param2;
  }

  public static long neg(long param1) {
    return -param1;
  }

  public static long dif(long param1, long param2) {
    return Math.abs(param1 - param2);
  }

  public static long modulo(long param1, long param2) {
    Check.isPositive(param2);
    long result = param1 % param2;
    if (result < 0f) {
      return (result + param2) % param2;
    }
    return result;
  }

  public static long max(long param1, long param2) {
    return Math.max(param1, param2);
  }

  public static long min(long param1, long param2) {
    return Math.min(param1, param2);
  }

  public static long inc(long value) {
    return value + 1;
  }

  public static long dec(long value) {
    return value - 1;
  }

  /*
   * comparisons
   */

  public static boolean isEq(long value1, long value2) {
    return value1 == value2;
  }

  public static boolean isLt(long value1, long value2) {
    return value1 < value2;
  }

  public static boolean isLtEq(long value1, long value2) {
    return value1 <= value2;
  }

  public static boolean isGt(long value1, long value2) {
    return value1 > value2;
  }

  public static boolean isGtEq(long value1, long value2) {
    return value1 >= value2;
  }
}
