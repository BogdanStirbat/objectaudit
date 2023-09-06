package com.bstirbat.difftool;

import com.bstirbat.difftool.annotations.AuditKey;
import com.bstirbat.difftool.exception.MissingAuditInfoException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
          continue;
        }

        if (isCollection(previous)) {
          Collection<Object> previousListItems = (Collection<Object>) previous;

          if(!previousListItems.isEmpty()) {
            Object testObject = previousListItems.toArray()[0];
            if (isEndLevelType(testObject)) {
              List<String> removedValues = previousListItems.stream()
                  .map(Object::toString)
                  .toList();

              result.add(new ListUpdate(property, null, removedValues));
              continue;
            }
          }

          for (Object previousListItem: previousListItems) {

            String key = obtainKey(previousListItem);
            String prefix = String.format("%s[%s]", property, key);
            result.addAll(appendPrefix(prefix, diff(previousListItem, null)));
          }
          continue;
        }

        result.addAll(appendPrefix(property + ".", diff(previous, null)));
      }

      if (previous == null && current != null) {
        if (isEndLevelType(current)) {
          result.add(new PropertyUpdate(property, null, current.toString()));
          continue;
        }

        if (isCollection(current)) {
          Collection<Object> currentListItems = (Collection<Object>) current;

          if (!currentListItems.isEmpty()) {
            Object testObject = currentListItems.toArray()[0];
            if (isEndLevelType(testObject)) {
              List<String> addedValues = currentListItems.stream()
                  .map(Object::toString)
                  .toList();

              result.add(new ListUpdate(property, addedValues, null));
              continue;
            }
          }

          for (Object currentListItem: currentListItems) {
            String key = obtainKey(currentListItem);
            String prefix = String.format("%s[%s].", property, key);
            result.addAll(appendPrefix(prefix, diff(null, currentListItem)));
          }
          continue;
        }

        result.addAll(appendPrefix(property + ".", diff(null, current)));
      }

      if (previous != null && current != null && !previous.equals(current)) {
        if (isEndLevelType(previous)) {
          result.add(new PropertyUpdate(property, previous.toString(), current.toString()));
          continue;
        }

        if (isCollection(current)) {
          Collection<Object> previousListItems = (Collection<Object>) previous;
          Collection<Object> currentListItems = (Collection<Object>) current;

          if (!previousListItems.isEmpty() || !currentListItems.isEmpty()) {
            Object testObject = previousListItems.isEmpty()? currentListItems.toArray()[0]: previousListItems.toArray()[0];

            if (isEndLevelType(testObject)) {
              List<String> previousStrings = previousListItems.stream()
                  .map(Object::toString)
                  .toList();

              List<String> currentStrings = currentListItems.stream()
                  .map(Object::toString)
                  .toList();

              List<String> added = new ArrayList<>();
              for (String currentString: currentStrings) {
                if (!previousStrings.contains(currentString)) {
                  added.add(currentString);
                }
              }

              List<String> removed = new ArrayList<>();
              for (String previousString: previousStrings) {
                if (!currentStrings.contains(previousString)) {
                  removed.add(previousString);
                }
              }

              result.add(new ListUpdate(property, added, removed));
              continue;
            }
          }

          Map<String, Object> previousObjects = new HashMap<>();
          for (Object previousListItem: previousListItems) {
            String key = obtainKey(previousListItem);
            previousObjects.put(key, previousListItem);
          }

          Map<String, Object> currentObjects = new HashMap<>();
          for (Object currentListItem: currentListItems) {
            String key = obtainKey(currentListItem);
            currentObjects.put(key, currentListItem);
          }

          for (String previousKey : previousObjects.keySet()) {
            if (!currentObjects.containsKey(previousKey)) {
              Object previousListItem = previousObjects.get(previousKey);
              String prefix = String.format("%s[%s].", property, previousKey);
              result.addAll(appendPrefix(prefix, diff(previousListItem, null)));
            }
          }

          for (String currentKey: currentObjects.keySet()) {
            if (!previousObjects.containsKey(currentKey)) {
              Object currentListItem = currentObjects.get(currentKey);
              String prefix = String.format("%s[%s].", property, currentKey);
              result.addAll(appendPrefix(prefix, diff(null, currentListItem)));
            }
          }

          for (String key : previousObjects.keySet()) {
            if (currentObjects.containsKey(key)) {
              Object previousListItem = previousObjects.get(key);
              Object currentListItem = currentObjects.get(key);
              if (!previousListItem.equals(currentListItem)) {
                String prefix = String.format("%s[%s].", property, key);
                result.addAll(appendPrefix(prefix, diff(previousListItem, currentListItem)));
              }
            }
          }

          continue;
        }

        result.addAll(appendPrefix(property + ".", diff(previous, current)));
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
      return new ListUpdate(prefix + listUpdate.getProperty(), listUpdate.getAdded(), listUpdate.getRemoved());
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

  private static boolean isCollection(Object obj) {
    if (obj == null) {
      return false;
    }

    return obj instanceof Collection<?>;
  }

  private static String obtainKey(Object obj) throws IllegalAccessException {
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
