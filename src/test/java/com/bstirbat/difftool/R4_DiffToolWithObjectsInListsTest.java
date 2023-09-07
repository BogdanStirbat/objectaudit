package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bstirbat.difftool.annotations.AuditKey;
import com.bstirbat.difftool.exception.MissingAuditInfoException;
import java.util.List;
import org.junit.jupiter.api.Test;

class R4_DiffToolWithObjectsInListsTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassB previousV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB previousV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(previousV1, previousV2))
        .build();


    ClassB currentV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("123 Ferrari")
        .build();

    ClassB currentV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(currentV1, currentV2))
        .build();

    PropertyUpdate expectedChange = new PropertyUpdate("vehicles[v_1].displayName", "My Car", "123 Ferrari");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenPropertyInListAdded() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();


    ClassB currentV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("123 Ferrari")
        .build();

    ClassB currentV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(currentV1, currentV2))
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("vehicles[v_1].displayName", null, "123 Ferrari");
    PropertyUpdate expectedChange2 = new PropertyUpdate("vehicles[v_1].key", null, "v_1");
    PropertyUpdate expectedChange3 = new PropertyUpdate("vehicles[v_2].displayName", null, "Ford");
    PropertyUpdate expectedChange4 = new PropertyUpdate("vehicles[v_2].key", null, "v_2");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenPropertyInListRemoved() throws Exception {
    ClassB previousV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB previousV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(previousV1, previousV2))
        .build();


    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("vehicles[v_1].displayName", "My Car", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("vehicles[v_1].key", "v_1", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("vehicles[v_2].displayName", "Ford", null);
    PropertyUpdate expectedChange4 = new PropertyUpdate("vehicles[v_2].key", "v_2", null);
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenDeepLevelChangeInList() throws Exception {
    ClassC previousKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Sport")))
        .build();

    ClassC previousKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB previousV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB previousV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(previousV1, previousV2))
        .withKids(List.of(previousKid1, previousKid2))
        .build();


    ClassC currentKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")))
        .build();

    ClassC currentKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB currentV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB currentV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();
    
    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(currentV1, currentV2))
        .withKids(List.of(currentKid1, currentKid2))
        .build();

    PropertyUpdate expectedChange = new PropertyUpdate("kids[1].favoriteSubjects[s2].name", "Sport", "Soccer");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenItemAddedToDeepLevelList() throws Exception {
    ClassC previousKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")))
        .build();

    ClassC previousKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB previousV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB previousV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(previousV1, previousV2))
        .withKids(List.of(previousKid1, previousKid2))
        .build();


    ClassC currentKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer"), new ClassD("s3", "Programming")))
        .build();

    ClassC currentKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB currentV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB currentV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(currentV1, currentV2))
        .withKids(List.of(currentKid1, currentKid2))
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("kids[1].favoriteSubjects[s3].name", null, "Programming");
    PropertyUpdate expectedChange2 = new PropertyUpdate("kids[1].favoriteSubjects[s3].id", null, "s3");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenItemRemovedInDeepLevelList() throws Exception {
    ClassC previousKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Sport")))
        .build();

    ClassC previousKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB previousV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB previousV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(previousV1, previousV2))
        .withKids(List.of(previousKid1, previousKid2))
        .build();


    ClassC currentKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math")))
        .build();

    ClassC currentKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB currentV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB currentV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(currentV1, currentV2))
        .withKids(List.of(currentKid1, currentKid2))
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("kids[1].favoriteSubjects[s2].id", "s2", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("kids[1].favoriteSubjects[s2].name", "Sport", null);
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenNoObjectChange() throws Exception {
    ClassC previousKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")))
        .build();

    ClassC previousKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB previousV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB previousV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(previousV1, previousV2))
        .withKids(List.of(previousKid1, previousKid2))
        .build();


    ClassC currentKid1 = ClassC.builder()
        .withId(1L)
        .withName("Name 1")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")))
        .build();

    ClassC currentKid2 = ClassC.builder()
        .withId(2L)
        .withName("Name 2")
        .withFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")))
        .build();

    ClassB currentV1 = ClassB.builder()
        .withKey("v_1")
        .withDisplayName("My Car")
        .build();

    ClassB currentV2 = ClassB.builder()
        .withKey("v_2")
        .withDisplayName("Ford")
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withVehicles(List.of(currentV1, currentV2))
        .withKids(List.of(currentKid1, currentKid2))
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of());
  }

  @Test
  void detectFieldChange_whenNoAuditInfoExists() {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSpaceShips(List.of(new ClassE("k1", "Enterprise")))
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSpaceShips(List.of(new ClassE("k1", "Borg")))
        .build();

    assertThrows(MissingAuditInfoException.class, () -> DiffTool.diff(previous, current));
  }

  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;
    private List<ClassB> vehicles;
    private List<ClassC> kids;
    private List<ClassE> spaceShips;

    public ClassA() {

    }

    private ClassA(ClassABuilder builder) {
      this.firstName = builder.firstName;
      this.lastName = builder.lastName;
      this.age = builder.age;
      this.vehicles = builder.vehicles;
      this.kids = builder.kids;
      this.spaceShips = builder.spaceShips;
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

    public List<ClassB> getVehicles() {
      return vehicles;
    }

    public void setVehicles(List<ClassB> vehicles) {
      this.vehicles = vehicles;
    }

    public List<ClassC> getKids() {
      return kids;
    }

    public void setKids(List<ClassC> kids) {
      this.kids = kids;
    }

    public List<ClassE> getSpaceShips() {
      return spaceShips;
    }

    public void setSpaceShips(List<ClassE> spaceShips) {
      this.spaceShips = spaceShips;
    }

    public static ClassABuilder builder() {
      return new ClassABuilder();
    }

    public static class ClassABuilder {
      private String firstName;
      private String lastName;
      private Integer age;
      private List<ClassB> vehicles;
      private List<ClassC> kids;
      private List<ClassE> spaceShips;

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
      public ClassABuilder withVehicles(List<ClassB> vehicles) {
        this.vehicles = vehicles;
        return this;
      }
      public ClassABuilder withKids(List<ClassC> kids) {
        this.kids = kids;
        return this;
      }
      public ClassABuilder withSpaceShips(List<ClassE> spaceShips) {
        this.spaceShips = spaceShips;
        return this;
      }

      public ClassA build() {
        return new ClassA(this);
      }
    }
  }

  static class ClassB {
    @AuditKey
    private String key;
    private String displayName;

    public ClassB() {

    }

    private ClassB(ClassBBuilder builder) {
      this.key = builder.key;
      this.displayName = builder.displayName;
    }

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

    public static ClassBBuilder builder() {
      return new ClassBBuilder();
    }

    public static class ClassBBuilder {
      private String key;
      private String displayName;

      private ClassBBuilder() {

      }

      public ClassBBuilder withKey(String key) {
        this.key = key;
        return this;
      }
      public ClassBBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
      }

      public ClassB build() {
        return new ClassB(this);
      }
    }
  }

  static class ClassC {
    private Long id;
    private Integer age;
    private String name;
    private List<ClassD> favoriteSubjects;

    public ClassC() {

    }
    private ClassC(ClassCBuilder builder) {
      this.id = builder.id;
      this.age = builder.age;
      this.name = builder.name;
      this.favoriteSubjects = builder.favoriteSubjects;
    }

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

    public static ClassCBuilder builder() {
      return new ClassCBuilder();
    }

    public static class ClassCBuilder {
      private Long id;
      private Integer age;
      private String name;
      private List<ClassD> favoriteSubjects;

      private ClassCBuilder() {

      }

      public ClassCBuilder withId(Long id) {
        this.id = id;
        return this;
      }
      public ClassCBuilder withAge(Integer age) {
        this.age = age;
        return this;
      }
      public ClassCBuilder withName(String name) {
        this.name = name;
        return this;
      }
      public ClassCBuilder withFavoriteSubjects(List<ClassD> favoriteSubjects) {
        this.favoriteSubjects = favoriteSubjects;
        return this;
      }

      public ClassC build() {
        return new ClassC(this);
      }
    }
  }

  static class ClassD {
    private String id;
    private String name;

    public ClassD(String id, String name) {
      this.id = id;
      this.name = name;
    }

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

    public ClassE(String key, String displayName) {
      this.key = key;
      this.displayName = displayName;
    }

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
