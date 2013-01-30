package com.perunlabs.common.jval.oper;

import static java.lang.Float.NaN;

public class FloatOps {
  public static final float PI = (float) Math.PI;
  public static final float PIx2 = PI * 2;

  private FloatOps() {}

  public static float add(float param1, float param2) {
    return param1 + param2;
  }

  public static float sub(float param1, float param2) {
    return param1 - param2;
  }

  public static float mul(float param1, float param2) {
    return param1 * param2;
  }

  public static float div(float param1, float param2) {
    return param1 / param2;
  }

  public static float neg(float param1) {
    return -param1;
  }

  public static float dif(float param1, float param2) {
    return Math.abs(param1 - param2);
  }

  public static float modulo(float param1, float param2) {
    if (param2 <= 0f) {
      return NaN;
    }
    float result = param1 % param2;
    if (result < 0f) {
      return (result + param2) % param2;
    }
    return result;
  }

  public static float max(float param1, float param2) {
    return Math.max(param1, param2);
  }

  public static float min(float param1, float param2) {
    return Math.min(param1, param2);
  }

  public static float clamp(float value, float clampLow, float clampHigh) {
    if (value < clampLow) {
      return clampLow;
    } else if (value > clampHigh) {
      return clampHigh;
    } else {
      return value;
    }
  }

  public static float floor(float value) {
    return (float) Math.floor(value);
  }

  public static float ceil(float value) {
    return (float) Math.ceil(value);
  }

  public static float round(float value) {
    return Math.round(value);
  }

  public static float sin(float value) {
    return (float) Math.sin(value);
  }

  public static float cos(float value) {
    return (float) Math.cos(value);
  }

  public static float atan2(float y, float x) {
    return (float) Math.atan2(y, x);
  }

  public static float average(float value1, float value2) {
    return 0.5f * (value1 + value2);
  }

  public static float sqr(float value1) {
    return value1 * value1;
  }

  public static float sqrt(float value1) {
    return (float) Math.sqrt(value1);
  }

  /*
   * comparisons
   */

  public static boolean isEq(float value1, float value2) {
    return value1 == value2;
  }

  public static boolean isLt(float value1, float value2) {
    return value1 < value2;
  }

  public static boolean isLte(float value1, float value2) {
    return value1 <= value2;
  }

  public static boolean isGt(float value1, float value2) {
    return value1 > value2;
  }

  public static boolean isGte(float value1, float value2) {
    return value1 >= value2;
  }

  /*
   * conversions
   */

  public static int toInt(float value) {
    return (int) value;
  }
}
