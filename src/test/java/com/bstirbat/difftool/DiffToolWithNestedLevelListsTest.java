package com.bstirbat.difftool;

import com.bstirbat.difftool.annotations.AuditKey;
import java.util.List;

class DiffToolWithNestedLevelListsTest {


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
