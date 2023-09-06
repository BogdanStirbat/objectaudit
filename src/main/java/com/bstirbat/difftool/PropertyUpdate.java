package com.bstirbat.difftool;

import java.util.Objects;

public final class PropertyUpdate implements ChangeType {

  private final String property;
  private final String previous;
  private final String current;

  public PropertyUpdate(String property, String previous, String current) {
    this.property = property;
    this.previous = previous;
    this.current = current;
  }

  public String getProperty() {
    return property;
  }

  public String getPrevious() {
    return previous;
  }

  public String getCurrent() {
    return current;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyUpdate that = (PropertyUpdate) o;
    return Objects.equals(property, that.property) && Objects.equals(previous, that.previous)
        && Objects.equals(current, that.current);
  }

  @Override
  public int hashCode() {
    return Objects.hash(property, previous, current);
  }
}
