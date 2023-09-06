package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class DiffToolWithTopLevelListsTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Interior/Exterior wash"));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Oil change"));

    ListUpdate expectedChange = new ListUpdate("services", List.of("Oil change"), List.of("Interior/Exterior wash"));
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenFieldWasAdded() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of());

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Oil change"));

    ListUpdate expectedChange = new ListUpdate("services", List.of("Oil change"), List.of());
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenFieldWasRemoved() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Interior/Exterior wash"));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of());

    ListUpdate expectedChange = new ListUpdate("services", List.of(), List.of("Interior/Exterior wash"));
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenNoChanges() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Interior/Exterior wash"));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Interior/Exterior wash"));

    assertListsEquals(DiffTool.diff(previous, current), List.of());
  }

  @Test
  void detectFieldChange_whenMultipleElementsChanged() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Wash", "Clean", "Protect"));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Wash", "Clean", "Drive"));

    ListUpdate expectedChange = new ListUpdate("services", List.of("Drive"), List.of("Protect"));
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenCurrentObjectIsNull() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Wash", "Clean", "Protect"));

    ListUpdate expectedChange1 = new ListUpdate("services", null, List.of("Wash", "Clean", "Protect"));
    PropertyUpdate expectedChange2 = new PropertyUpdate("firstName", "James", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("lastName", "Last", null);
    PropertyUpdate expectedChange4 = new PropertyUpdate("age", "25", null);

    assertListsEquals(DiffTool.diff(previous, null), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenPreviousObjectIsNull() throws Exception {
    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Wash", "Clean", "Protect"));

    ListUpdate expectedChange1 = new ListUpdate("services", List.of("Wash", "Clean", "Protect"), null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("firstName", null, "James");
    PropertyUpdate expectedChange3 = new PropertyUpdate("lastName", null, "Last");
    PropertyUpdate expectedChange4 = new PropertyUpdate("age", null, "25");

    assertListsEquals(DiffTool.diff(null, current), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenDeepLevelChange() throws Exception {
    ClassB previousCar = new ClassB();
    previousCar.setName("Ford");
    previousCar.setAccessories(List.of("wheel", "seat"));

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Oil change"));
    previous.setCar(previousCar);

    ClassB currentCar = new ClassB();
    currentCar.setName("Mercedes");
    currentCar.setAccessories(List.of("joystick", "seat", "tv"));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Oil change"));
    current.setCar(currentCar);

    ListUpdate expectedChange1 = new ListUpdate("car.accessories", List.of("joystick", "tv"), List.of("wheel"));
    PropertyUpdate expectedChange2 = new PropertyUpdate("car.name", "Ford", "Mercedes");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenDeepLevelChange_andCurrentObjectIsNull() throws Exception {
    ClassB previousCar = new ClassB();
    previousCar.setName("Ford");
    previousCar.setAccessories(List.of("wheel", "seat"));

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setServices(List.of("Oil change"));
    previous.setCar(previousCar);

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
    ClassB currentCar = new ClassB();
    currentCar.setName("Mercedes");
    currentCar.setAccessories(List.of("joystick", "seat", "tv"));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setServices(List.of("Oil change"));
    current.setCar(currentCar);

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
  }

  static class ClassB {
    private String name;
    private List<String> accessories;

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
  }
}
