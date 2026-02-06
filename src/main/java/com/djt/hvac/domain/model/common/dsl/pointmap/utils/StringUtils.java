package com.djt.hvac.domain.model.common.dsl.pointmap.utils;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;
import java.util.stream.Collector;

public class StringUtils {

  public static <T> T processStringAsReader(String string, Function<Reader, T> function) {
    requireNonNull(string, "string cannot be null");
    requireNonNull(function, "function cannot be null");
    try (StringReader reader = new StringReader(string)) {
      return function.apply(reader);
    }
  }

  public static Collector<CharSequence, ?, String> prettyJoin(String finalDelimiter) {
    return collectingAndThen(joining(", "), s -> insertFinalJoinString(s, finalDelimiter));
  }

  private static String insertFinalJoinString(String s, String finalDelimiter) {
    StringBuilder buf = new StringBuilder(s);
    int lastCommaIdx = buf.toString().lastIndexOf(", ");
    if (lastCommaIdx != -1) {
      buf.insert(lastCommaIdx + 2, finalDelimiter);
    }
    return buf.toString();
  }

  private StringUtils() {}

}
