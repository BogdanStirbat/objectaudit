package com.bstirbat.difftool.utils;

import java.util.Collection;
import java.util.List;

public class CollectionUtils {

  private CollectionUtils() {
  }

  public static List<String> toListOfStrings(Collection<Object> items) {

    return items.stream()
        .map(Object::toString)
        .toList();
  }
}
