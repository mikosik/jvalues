package com.perunlabs.common.jval.oper;

public class IntOps {
  private IntOps() {}

  public static int add(int param1, int param2) {
    return param1 + param2;
  }

  public static int sub(int param1, int param2) {
    return param1 - param2;
  }

  public static int mul(int param1, int param2) {
    return param1 * param2;
  }

  public static int div(int param1, int param2) {
    return param1 / param2;
  }

  public static int neg(int param1) {
    return -param1;
  }

  public static int dif(int param1, int param2) {
    return Math.abs(param1 - param2);
  }

  public static int modulo(int param1, int param2) {
    Check.isPositive(param2);
    int result = param1 % param2;
    if (result < 0f) {
      return (result + param2) % param2;
    }
    return result;
  }

  public static int max(int param1, int param2) {
    return Math.max(param1, param2);
  }

  public static int min(int param1, int param2) {
    return Math.min(param1, param2);
  }

  public static int inc(int value) {
    return value + 1;
  }

  public static int dec(int value) {
    return value - 1;
  }

  /*
   * comparisons
   */

  public static boolean isEq(int value1, int value2) {
    return value1 == value2;
  }

  public static boolean isLt(int value1, int value2) {
    return value1 < value2;
  }

  public static boolean isLtEq(int value1, int value2) {
    return value1 <= value2;
  }

  public static boolean isGt(int value1, int value2) {
    return value1 > value2;
  }

  public static boolean isGtEq(int value1, int value2) {
    return value1 >= value2;
  }

  /*
   * conversions
   */

  public static float toFloat(int value) {
    return value;
  }
}
