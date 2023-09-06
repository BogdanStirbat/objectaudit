package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import com.bstirbat.difftool.annotations.AuditKey;
import java.util.List;
import org.junit.jupiter.api.Test;

class DiffToolWithNestedLevelListsTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassB previousV1 = new ClassB();
    previousV1.setKey("v_1");
    previousV1.setDisplayName("My Car");

    ClassB previousV2 = new ClassB();
    previousV2.setKey("v_2");
    previousV2.setDisplayName("Ford");

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setVehicles(List.of(previousV1, previousV2));


    ClassB currentV1 = new ClassB();
    currentV1.setKey("v_1");
    currentV1.setDisplayName("123 Ferrari");

    ClassB currentV2 = new ClassB();
    currentV2.setKey("v_2");
    currentV2.setDisplayName("Ford");

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setVehicles(List.of(currentV1, currentV2));

    PropertyUpdate expectedChange = new PropertyUpdate("vehicles[v_1].displayName", "My Car", "123 Ferrari");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }


  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;
    private List<ClassB> vehicles;
    private List<ClassD> kids;
    private List<ClassE> spaceShips;

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

    public List<ClassB> getVehicles() {
      return vehicles;
    }

    public void setVehicles(List<ClassB> vehicles) {
      this.vehicles = vehicles;
    }

    public List<ClassD> getKids() {
      return kids;
    }

    public void setKids(List<ClassD> kids) {
      this.kids = kids;
    }

    public List<ClassE> getSpaceShips() {
      return spaceShips;
    }

    public void setSpaceShips(List<ClassE> spaceShips) {
      this.spaceShips = spaceShips;
    }
  }

  static class ClassB {
    @AuditKey
    private String key;
    private String displayName;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }
  }

  static class ClassC {
    private Long id;
    private Integer age;
    private String name;
    private List<ClassD> favoriteSubjects;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Integer getAge() {
      return age;
    }

    public void setAge(Integer age) {
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<ClassD> getFavoriteSubjects() {
      return favoriteSubjects;
    }

    public void setFavoriteSubjects(List<ClassD> favoriteSubjects) {
      this.favoriteSubjects = favoriteSubjects;
    }
  }

  static class ClassD {
    private String id;
    private String name;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  static class ClassE {
    private String key;
    private String displayName;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }
  }


}
