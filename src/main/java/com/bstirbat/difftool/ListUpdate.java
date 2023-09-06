package com.bstirbat.difftool;

import java.util.List;
import java.util.Objects;

public final class ListUpdate implements ChangeType {

  private final String property;
  private final List<String> added;
  private final List<String> removed;

  public ListUpdate(String property, List<String> added, List<String> removed) {
    this.property = property;
    this.added = added;
    this.removed = removed;
  }

  public String getProperty() {
    return property;
  }

  public List<String> getAdded() {
    return added;
  }

  public List<String> getRemoved() {
    return removed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListUpdate that = (ListUpdate) o;
    return Objects.equals(property, that.property) && Objects.equals(added, that.added)
        && Objects.equals(removed, that.removed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(property, added, removed);
  }
}
