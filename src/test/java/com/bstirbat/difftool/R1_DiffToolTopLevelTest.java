package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class R1_DiffToolTopLevelTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);

    ClassA current = new ClassA();
    current.setFirstName("Jim");
    current.setLastName("Last");
    current.setAge(25);

    PropertyUpdate expectedChange = new PropertyUpdate("firstName", "James", "Jim");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenIntegerValue() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(24);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);

    PropertyUpdate expectedChange = new PropertyUpdate("age", "24", "25");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenValueAdded() throws Exception {
    ClassA previous = new ClassA();
    previous.setLastName("Last");
    previous.setAge(24);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(24);

    PropertyUpdate expectedChange = new PropertyUpdate("firstName", null, "James");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenValueRemoved() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(24);

    ClassA current = new ClassA();
    current.setLastName("Last");
    current.setAge(24);

    PropertyUpdate expectedChange = new PropertyUpdate("firstName","James", null);

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenNoChange() throws Exception {
    ClassA objectWithoutChanges = new ClassA();
    objectWithoutChanges.setFirstName("James");
    objectWithoutChanges.setLastName("Last");
    objectWithoutChanges.setAge(24);

    assertListsEquals(DiffTool.diff(objectWithoutChanges, objectWithoutChanges), List.of());
  }

  @Test
  void detectFieldChange_whenFieldsHaveNoValue() throws Exception {

    assertListsEquals(DiffTool.diff(new ClassA(), new ClassA()), List.of());
  }

  @Test
  void detectFieldChange_whenMultipleChanges() throws Exception {
    ClassA previous = new ClassA();

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(24);

    PropertyUpdate expectedChange1 = new PropertyUpdate("firstName",null, "James");
    PropertyUpdate expectedChange2 = new PropertyUpdate("lastName",null, "Last");
    PropertyUpdate expectedChange3 = new PropertyUpdate("age",null, "24");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenPreviousIsNull() throws Exception {
    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(24);

    PropertyUpdate expectedChange1 = new PropertyUpdate("firstName",null, "James");
    PropertyUpdate expectedChange2 = new PropertyUpdate("lastName",null, "Last");
    PropertyUpdate expectedChange3 = new PropertyUpdate("age",null, "24");

    assertListsEquals(DiffTool.diff(null, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenCurrentIsNull() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(24);

    PropertyUpdate expectedChange1 = new PropertyUpdate("firstName", "James", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("lastName", "Last", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("age", "24", null);

    assertListsEquals(DiffTool.diff(previous, null), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenBothValuesAreNull() throws Exception {

    assertListsEquals(DiffTool.diff(null, null), List.of());
  }

  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public Integer getAge() {
      return age;
    }

    public void setAge(Integer age) {
      this.age = age;
    }
  }
}
