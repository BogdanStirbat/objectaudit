package com.bstirbat.difftool.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class AssertUtils {

  private AssertUtils() {

  }

  public static <T> void assertListsEquals(List<T> expected, List<T> actual) {

    assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
  }
}
