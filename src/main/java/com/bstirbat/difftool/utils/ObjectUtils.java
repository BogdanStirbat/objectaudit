package com.bstirbat.difftool.utils;

import com.bstirbat.difftool.annotations.AuditKey;
import com.bstirbat.difftool.exception.MissingAuditInfoException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public class ObjectUtils {

  private ObjectUtils() {

  }

  public static boolean isEndLevelType(Object obj) {
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

  public static boolean isCollection(Object obj) {
    if (obj == null) {
      return false;
    }

    return obj instanceof Collection<?>;
  }

  public static boolean isCollectionOfEndLevelType(Object obj) {
    if (!isCollection(obj)) {
      return false;
    }

    Collection<Object> listItems = (Collection<Object>) obj;
    if (!listItems.isEmpty()) {
      Object testObject = listItems.toArray()[0];
      if (isEndLevelType(testObject)) {
        return true;
      }
    }

    return false;
  }

  public static String obtainKey(Object obj) throws IllegalAccessException {
    Class<?> clazz = obj.getClass();
    for (Field field: clazz.getDeclaredFields()) {
      field.setAccessible(true);

      String fieldName = field.getName();
      if ("id".equals(fieldName)) {
        Object value = field.get(obj);
        if (value != null) {
          return value.toString();
        }
      }

      AuditKey annotatedValue = field.getAnnotation(AuditKey.class);
      if (annotatedValue != null) {
        Object value = field.get(obj);
        if (value != null) {
          return value.toString();
        }
      }
    }

    throw new MissingAuditInfoException("Could not determine audit key, id property or @AuditKey field missing or null for class: " + obj.getClass().getName());
  }
}
