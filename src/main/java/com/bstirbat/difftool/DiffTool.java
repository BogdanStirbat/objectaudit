package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.CollectionUtils.toListOfStrings;
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
        result.addAll(detectDeleteObjectChanges(previous, property));
      }

      if (previous == null && current != null) {
        result.addAll(detectAddObjectChanges(current, property));
      }

      if (previous != null && current != null && !previous.equals(current)) {
        result.addAll(detectUpdateObjectChanges(previous, current, property));
      }
    }

    return result;
  }

  private static List<ChangeType> detectDeleteObjectChanges(Object previous, String property) throws IllegalAccessException {

    if (isEndLevelObject(previous)) {
      return List.of(new PropertyUpdate(property, previous.toString(), null));
    }

    if (isCollectionOfEndLevelObjects(previous)) {
      return List.of(new ListUpdate(property, null, toListOfStrings((Collection<Object>) previous)));
    }

    if (isCollection(previous)) {
      return detectDeleteCollectionChanges((Collection<Object>) previous, property);
    }

    return appendPrefix(property + ".", diff(previous, null));
  }

  private static List<ChangeType> detectDeleteCollectionChanges(Collection<Object> previous, String property)
      throws IllegalAccessException {
    List<ChangeType> result = new ArrayList<>();

    for (Object previousListItem: previous) {
      String key = obtainKey(previousListItem);
      String prefix = String.format("%s[%s].", property, key);
      result.addAll(appendPrefix(prefix, diff(previousListItem, null)));
    }

    return result;
  }

  private static List<ChangeType> detectAddObjectChanges(Object current, String property) throws IllegalAccessException {
    if (isEndLevelObject(current)) {
      return List.of(new PropertyUpdate(property, null, current.toString()));
    }

    if (isCollectionOfEndLevelObjects(current)) {
      return List.of(new ListUpdate(property, toListOfStrings((Collection<Object>) current), null));
    }

    if (isCollection(current)) {
      return detectAddCollectionChanges((Collection<Object>) current, property);
    }

    return appendPrefix(property + ".", diff(null, current));
  }

  private static List<ChangeType> detectAddCollectionChanges(Collection<Object> current, String property)
      throws IllegalAccessException {
    List<ChangeType> result = new ArrayList<>();

    for (Object currentListItem: current) {
      String key = obtainKey(currentListItem);
      String prefix = String.format("%s[%s].", property, key);
      result.addAll(appendPrefix(prefix, diff(null, currentListItem)));
    }

    return result;
  }

  private static List<ChangeType> detectUpdateObjectChanges(Object previous, Object current, String property) throws IllegalAccessException {
    if (isEndLevelObject(previous)) {
      return List.of(new PropertyUpdate(property, previous.toString(), current.toString()));
    }

    if (isCollectionOfEndLevelObjects(previous) || isCollectionOfEndLevelObjects(current)) {
      return collectionOfEndLevelObjectsChanges((Collection<Object>) previous, (Collection<Object>) current, property);
    }

    if (isCollection(current)) {
      List<ChangeType> result = new ArrayList<>();
      addAllObjectChanges(result, property, (Collection<Object>) previous, (Collection<Object>) current);
      return result;
    }

    return appendPrefix(property + ".", diff(previous, current));
  }

  private static List<ChangeType> collectionOfEndLevelObjectsChanges(Collection<Object> previous, Collection<Object> current, String property) {
    List<String> previousStrings = toListOfStrings(previous);
    List<String> currentStrings = toListOfStrings(current);

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

    return List.of(new ListUpdate(property, added, removed));
  }

  private static void addAllObjectChanges(List<ChangeType> result, String property, Collection<Object> previous,
      Collection<Object> current) throws IllegalAccessException {
    Collection<Object> previousListItems = previous;
    Collection<Object> currentListItems = current;

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
