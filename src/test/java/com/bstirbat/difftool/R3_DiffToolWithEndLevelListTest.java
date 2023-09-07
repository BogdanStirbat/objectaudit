package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class R3_DiffToolWithEndLevelListTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Interior/Exterior wash"))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Oil change"))
        .build();

    ListUpdate expectedChange = new ListUpdate("services", List.of("Oil change"), List.of("Interior/Exterior wash"));
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenFieldWasAdded() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of())
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Oil change"))
        .build();

    ListUpdate expectedChange = new ListUpdate("services", List.of("Oil change"), List.of());
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenFieldWasRemoved() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Interior/Exterior wash"))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of())
        .build();

    ListUpdate expectedChange = new ListUpdate("services", List.of(), List.of("Interior/Exterior wash"));
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenNoChanges() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Interior/Exterior wash"))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Interior/Exterior wash"))
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of());
  }

  @Test
  void detectFieldChange_whenMultipleElementsChanged() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Wash", "Clean", "Protect"))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Wash", "Clean", "Drive"))
        .build();

    ListUpdate expectedChange = new ListUpdate("services", List.of("Drive"), List.of("Protect"));
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenCurrentObjectIsNull() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Wash", "Clean", "Protect"))
        .build();

    ListUpdate expectedChange1 = new ListUpdate("services", null, List.of("Wash", "Clean", "Protect"));
    PropertyUpdate expectedChange2 = new PropertyUpdate("firstName", "James", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("lastName", "Last", null);
    PropertyUpdate expectedChange4 = new PropertyUpdate("age", "25", null);

    assertListsEquals(DiffTool.diff(previous, null), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenPreviousObjectIsNull() throws Exception {
    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Wash", "Clean", "Protect"))
        .build();

    ListUpdate expectedChange1 = new ListUpdate("services", List.of("Wash", "Clean", "Protect"), null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("firstName", null, "James");
    PropertyUpdate expectedChange3 = new PropertyUpdate("lastName", null, "Last");
    PropertyUpdate expectedChange4 = new PropertyUpdate("age", null, "25");

    assertListsEquals(DiffTool.diff(null, current), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenDeepLevelChange() throws Exception {
    ClassB previousCar = ClassB.builder()
        .withName("Ford")
        .withAccessories(List.of("wheel", "seat"))
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Oil change"))
        .withCar(previousCar)
        .build();


    ClassB currentCar = ClassB.builder()
        .withName("Mercedes")
        .withAccessories(List.of("joystick", "seat", "tv"))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Oil change"))
        .withCar(currentCar)
        .build();

    ListUpdate expectedChange1 = new ListUpdate("car.accessories", List.of("joystick", "tv"), List.of("wheel"));
    PropertyUpdate expectedChange2 = new PropertyUpdate("car.name", "Ford", "Mercedes");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenDeepLevelChange_andCurrentObjectIsNull() throws Exception {
    ClassB previousCar = ClassB.builder()
        .withName("Ford")
        .withAccessories(List.of("wheel", "seat"))
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Oil change"))
        .withCar(previousCar)
        .build();

    ListUpdate expectedChange1 = new ListUpdate("car.accessories", null, List.of("wheel", "seat"));
    ListUpdate expectedChange2 = new ListUpdate("services", null, List.of("Oil change"));
    PropertyUpdate expectedChange3 = new PropertyUpdate("car.name", "Ford", null);
    PropertyUpdate expectedChange4 = new PropertyUpdate("firstName", "James", null);
    PropertyUpdate expectedChange5 = new PropertyUpdate("lastName", "Last", null);
    PropertyUpdate expectedChange6 = new PropertyUpdate("age", "25", null);

    List<ChangeType> expected = List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4,
        expectedChange5, expectedChange6);

    assertListsEquals(DiffTool.diff(previous, null), expected);
  }

  @Test
  void detectFieldChange_whenDeepLevelChange_andPreviousObjectIsNull() throws Exception {
    ClassB currentCar = ClassB.builder()
        .withName("Mercedes")
        .withAccessories(List.of("joystick", "seat", "tv"))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withServices(List.of("Oil change"))
        .withCar(currentCar)
        .build();

    ListUpdate expectedChange1 = new ListUpdate("car.accessories", List.of("joystick", "seat", "tv"), null);
    ListUpdate expectedChange2 = new ListUpdate("services", List.of("Oil change"), null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("car.name", null, "Mercedes");
    PropertyUpdate expectedChange4 = new PropertyUpdate("firstName", null, "James");
    PropertyUpdate expectedChange5 = new PropertyUpdate("lastName", null, "Last");
    PropertyUpdate expectedChange6 = new PropertyUpdate("age", null, "25");

    List<ChangeType> expected = List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4,
        expectedChange5, expectedChange6);

    assertListsEquals(DiffTool.diff(null, current), expected);
  }

  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;
    private List<String> services;
    private ClassB car;

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

    public List<String> getServices() {
      return services;
    }

    public void setServices(List<String> services) {
      this.services = services;
    }

    public ClassB getCar() {
      return car;
    }

    public void setCar(ClassB car) {
      this.car = car;
    }

    public ClassA() {

    }

    private ClassA(ClassABuilder builder) {
      this.firstName = builder.firstName;
      this.lastName = builder.lastName;
      this.age = builder.age;
      this.services = builder.services;
      this.car = builder.car;
    }

    public static ClassABuilder builder() {
      return new ClassABuilder();
    }

    public static class ClassABuilder {
      private String firstName;
      private String lastName;
      private Integer age;
      private List<String> services;
      private ClassB car;

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
      public ClassABuilder withServices(List<String> services) {
        this.services = services;
        return this;
      }
      public ClassABuilder withCar(ClassB car) {
        this.car = car;
        return this;
      }

      public ClassA build() {
        return new ClassA(this);
      }
    }
  }

  static class ClassB {
    private String name;
    private List<String> accessories;

    public ClassB() {

    }

    private ClassB(ClassBBuilder builder) {
      this.name = builder.name;
      this.accessories = builder.accessories;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<String> getAccessories() {
      return accessories;
    }

    public void setAccessories(List<String> accessories) {
      this.accessories = accessories;
    }

    public static ClassBBuilder builder() {
      return new ClassBBuilder();
    }

    public static class ClassBBuilder {
      private String name;
      private List<String> accessories;

      private ClassBBuilder() {

      }

      public ClassBBuilder withName(String name) {
        this.name = name;
        return this;
      }
      public ClassBBuilder withAccessories(List<String> accessories) {
        this.accessories = accessories;
        return this;
      }

      public ClassB build() {
        return new ClassB(this);
      }
    }
  }
}
