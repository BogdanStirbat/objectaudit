package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class R1_DiffToolTopLevelTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("Jim")
        .withLastName("Last")
        .withAge(25)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("firstName", "James", "Jim")));
  }

  @Test
  void detectFieldChange_whenIntegerValue() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("age", "24", "25")));
  }

  @Test
  void detectFieldChange_whenValueAdded() throws Exception {
    ClassA previous = ClassA.builder()
        .withLastName("Last")
        .withAge(24)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("firstName", null, "James")));
  }

  @Test
  void detectFieldChange_whenValueRemoved() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

    ClassA current = ClassA.builder()
        .withLastName("Last")
        .withAge(24)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("firstName","James", null)));
  }

  @Test
  void detectFieldChange_whenNoChange() throws Exception {
    ClassA objectWithoutChanges = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

    assertListsEquals(DiffTool.diff(objectWithoutChanges, objectWithoutChanges), List.of());
  }

  @Test
  void detectFieldChange_whenFieldsHaveNoValue() throws Exception {

    assertListsEquals(DiffTool.diff(new ClassA(), new ClassA()), List.of());
  }

  @Test
  void detectFieldChange_whenMultipleChanges() throws Exception {
    ClassA previous = new ClassA();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("firstName",null, "James");
    PropertyUpdate expectedChange2 = new PropertyUpdate("lastName",null, "Last");
    PropertyUpdate expectedChange3 = new PropertyUpdate("age",null, "24");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenPreviousIsNull() throws Exception {
    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("firstName",null, "James");
    PropertyUpdate expectedChange2 = new PropertyUpdate("lastName",null, "Last");
    PropertyUpdate expectedChange3 = new PropertyUpdate("age",null, "24");

    assertListsEquals(DiffTool.diff(null, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenCurrentIsNull() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(24)
        .build();

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

    public ClassA() {

    }

    private ClassA(ClassABuilder builder) {
      this.firstName = builder.firstName;
      this.lastName = builder.lastName;
      this.age = builder.age;
    }

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

    public static ClassABuilder builder() {
      return new ClassABuilder();
    }

    public static class ClassABuilder {
      private String firstName;
      private String lastName;
      private Integer age;

      private ClassABuilder() {

      }

      public ClassABuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
      }
      public ClassABuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
      }
      public ClassABuilder withAge(Integer age) {
        this.age = age;
        return this;
      }

      public ClassA build() {
        return new ClassA(this);
      }
    }
  }
}
