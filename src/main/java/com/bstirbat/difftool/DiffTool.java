package com.bstirbat.difftool;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DiffTool {

  private DiffTool() {

  }

  public static <T> List<ChangeType> diff(T previousValue, T currentValue) throws IllegalAccessException {
    List<ChangeType> result = new ArrayList<>();

    if (previousValue == null && currentValue == null) {
      return result;
    }

    Class<?> clazz = previousValue != null? previousValue.getClass(): currentValue.getClass();
    for (Field field: clazz.getDeclaredFields()) {
      field.setAccessible(true);

      String property = field.getName();
      Object previous = previousValue != null? field.get(previousValue): null;
      Object current = currentValue != null? field.get(currentValue): null;

      if (previous == null && current == null) {
        continue;
      }

      if (previous != null && current == null) {
        result.add(new PropertyUpdate(property, previous.toString(), null));
        continue;
      }

      if (previous == null && current != null) {
        result.add(new PropertyUpdate(property, null, current.toString()));
        continue;
      }

      if (previous != null && current != null) {
        if (!previous.equals(current)) {
          result.add(new PropertyUpdate(property, previous.toString(), current.toString()));
        }
      }
    }

    return result;
  }
}
