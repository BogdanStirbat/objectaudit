package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.ObjectUtils.isCollection;
import static com.bstirbat.difftool.utils.ObjectUtils.isCollectionOfEndLevelObjects;
import static com.bstirbat.difftool.utils.ObjectUtils.isEndLevelObject;
import static com.bstirbat.difftool.utils.ObjectUtils.obtainKey;

import java.lang.reflect.Field;
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
        if (isEndLevelObject(previous)) {
          result.add(new PropertyUpdate(property, previous.toString(), null));
          continue;
        }

        if (isCollectionOfEndLevelObjects(previous)) {
          Collection<Object> previousListItems = (Collection<Object>) previous;
          List<String> removedValues = previousListItems.stream()
              .map(Object::toString)
              .toList();

          result.add(new ListUpdate(property, null, removedValues));
          continue;
        }

        if (isCollection(previous)) {
          Collection<Object> previousListItems = (Collection<Object>) previous;

          for (Object previousListItem: previousListItems) {

            String key = obtainKey(previousListItem);
            String prefix = String.format("%s[%s].", property, key);
            result.addAll(appendPrefix(prefix, diff(previousListItem, null)));
          }
          continue;
        }

        result.addAll(appendPrefix(property + ".", diff(previous, null)));
      }

      if (previous == null && current != null) {
        if (isEndLevelObject(current)) {
          result.add(new PropertyUpdate(property, null, current.toString()));
          continue;
        }

        if (isCollectionOfEndLevelObjects(current)) {
          Collection<Object> currentListItems = (Collection<Object>) current;
          List<String> addedValues = currentListItems.stream()
              .map(Object::toString)
              .toList();

          result.add(new ListUpdate(property, addedValues, null));
          continue;
        }

        if (isCollection(current)) {
          Collection<Object> currentListItems = (Collection<Object>) current;

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
        if (isEndLevelObject(previous)) {
          result.add(new PropertyUpdate(property, previous.toString(), current.toString()));
          continue;
        }

        if (isCollectionOfEndLevelObjects(previous) || isCollectionOfEndLevelObjects(current)) {
          Collection<Object> previousListItems = (Collection<Object>) previous;
          Collection<Object> currentListItems = (Collection<Object>) current;

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

        if (isCollection(current)) {
          Collection<Object> previousListItems = (Collection<Object>) previous;
          Collection<Object> currentListItems = (Collection<Object>) current;

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
}
