package com.bstirbat.difftool;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        if (isEndLevelType(previous)) {
          result.add(new PropertyUpdate(property, previous.toString(), null));
        } else {
          result.addAll(appendPrefix(property + ".", diff(previous, null)));
        }

        continue;
      }

      if (previous == null && current != null) {

        if (isEndLevelType(current)) {
          result.add(new PropertyUpdate(property, null, current.toString()));
        } else {
          result.addAll(appendPrefix(property + ".", diff(null, current)));
        }

        continue;
      }

      if (previous != null && current != null && !previous.equals(current)) {
        if (isEndLevelType(previous)) {
          result.add(new PropertyUpdate(property, previous.toString(), current.toString()));
        } else {
          result.addAll(appendPrefix(property + ".", diff(previous, current)));
        }
      }
    }

    return result;
  }

  private static List<ChangeType> appendPrefix(String prefix, List<ChangeType> changeTypes) {
    return changeTypes.stream()
        .map(changeType -> appendPrefix(prefix, changeType))
        .toList();
  }

  private static ChangeType appendPrefix(String prefix, ChangeType changeType) {
    if (changeType instanceof PropertyUpdate propertyUpdate) {
      return new PropertyUpdate(prefix + propertyUpdate.getProperty(), propertyUpdate.getPrevious(), propertyUpdate.getCurrent());
    }
    if (changeType instanceof ListUpdate listUpdate) {
      return new ListUpdate(prefix + listUpdate.getProperty(), listUpdate.getPrevious(), listUpdate.getCurrent());
    }

    // Sealed classes, typed based pattern matching for switch are preview features in Java 17
    throw new RuntimeException("Unimplemented case");
  }

  private static boolean isEndLevelType(Object obj) {
    if (obj == null) {
      return true;
    }

    Class<?> clazz = obj.getClass();
    if (clazz.isPrimitive() || clazz.isEnum()) {
      return true;
    }

    if (obj instanceof Number ||
        obj instanceof Character ||
        obj instanceof Boolean ||
        obj instanceof String ||
        obj instanceof LocalDate ||
        obj instanceof LocalDateTime) {
      return true;
    }

    return false;
  }
}
