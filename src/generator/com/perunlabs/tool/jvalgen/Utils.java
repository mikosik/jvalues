package com.perunlabs.tool.jvalgen;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Utils {
  private Utils() {}

  @SuppressWarnings("unchecked")
  public static <T> ImmutableList<T> list(T... elements) {
    return ImmutableList.<T> copyOf(elements);
  }

  public static <T> ImmutableList<T> empty() {
    return ImmutableList.of();
  }

  public static <F, T> ImmutableList<T> transform(Iterable<? extends F> iterable,
      Function<F, T> function) {
    return ImmutableList.copyOf(Iterables.transform(iterable, function));
  }

  public static <T> ImmutableList<T> skipOne(Iterable<T> iterable) {
    return ImmutableList.copyOf(Iterables.skip(iterable, 1));
  }

  public static <T> boolean anyMatches(Iterable<T> iterable, Predicate<? super T> predicate) {
    return Iterables.any(iterable, predicate);
  }

  public static <T, S extends T, R extends T> ImmutableList<T> append(S head, Iterable<R> tail) {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    builder.add(head);
    builder.addAll(tail);
    return builder.build();
  }
}
