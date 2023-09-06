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

  @Test
  void detectFieldChange_whenPropertyInListAdded() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);


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

    PropertyUpdate expectedChange1 = new PropertyUpdate("vehicles[v_1].displayName", null, "123 Ferrari");
    PropertyUpdate expectedChange2 = new PropertyUpdate("vehicles[v_1].key", null, "v_1");
    PropertyUpdate expectedChange3 = new PropertyUpdate("vehicles[v_2].displayName", null, "Ford");
    PropertyUpdate expectedChange4 = new PropertyUpdate("vehicles[v_2].key", null, "v_2");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenPropertyInListRemoved() throws Exception {
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


    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);

    PropertyUpdate expectedChange1 = new PropertyUpdate("vehicles[v_1].displayName", "My Car", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("vehicles[v_1].key", "v_1", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("vehicles[v_2].displayName", "Ford", null);
    PropertyUpdate expectedChange4 = new PropertyUpdate("vehicles[v_2].key", "v_2", null);
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3, expectedChange4));
  }

  @Test
  void detectFieldChange_whenDeepLevelChangeInList() throws Exception {
    ClassC previousKid1 = new ClassC();
    previousKid1.setId(1L);
    previousKid1.setName("Name 1");
    previousKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Sport")));

    ClassC previousKid2 = new ClassC();
    previousKid2.setId(2L);
    previousKid2.setName("Name 2");
    previousKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

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
    previous.setKids(List.of(previousKid1, previousKid2));


    ClassC currentKid1 = new ClassC();
    currentKid1.setId(1L);
    currentKid1.setName("Name 1");
    currentKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")));

    ClassC currentKid2 = new ClassC();
    currentKid2.setId(2L);
    currentKid2.setName("Name 2");
    currentKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

    ClassB currentV1 = new ClassB();
    currentV1.setKey("v_1");
    currentV1.setDisplayName("My Car");

    ClassB currentV2 = new ClassB();
    currentV2.setKey("v_2");
    currentV2.setDisplayName("Ford");

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setVehicles(List.of(currentV1, currentV2));
    current.setKids(List.of(currentKid1, currentKid2));

    PropertyUpdate expectedChange = new PropertyUpdate("kids[1].favoriteSubjects[s2].name", "Sport", "Soccer");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenItemAddedToDeepLevelList() throws Exception {
    ClassC previousKid1 = new ClassC();
    previousKid1.setId(1L);
    previousKid1.setName("Name 1");
    previousKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")));

    ClassC previousKid2 = new ClassC();
    previousKid2.setId(2L);
    previousKid2.setName("Name 2");
    previousKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

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
    previous.setKids(List.of(previousKid1, previousKid2));


    ClassC currentKid1 = new ClassC();
    currentKid1.setId(1L);
    currentKid1.setName("Name 1");
    currentKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer"), new ClassD("s3", "Programming")));

    ClassC currentKid2 = new ClassC();
    currentKid2.setId(2L);
    currentKid2.setName("Name 2");
    currentKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

    ClassB currentV1 = new ClassB();
    currentV1.setKey("v_1");
    currentV1.setDisplayName("My Car");

    ClassB currentV2 = new ClassB();
    currentV2.setKey("v_2");
    currentV2.setDisplayName("Ford");

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setVehicles(List.of(currentV1, currentV2));
    current.setKids(List.of(currentKid1, currentKid2));

    PropertyUpdate expectedChange1 = new PropertyUpdate("kids[1].favoriteSubjects[s3].name", null, "Programming");
    PropertyUpdate expectedChange2 = new PropertyUpdate("kids[1].favoriteSubjects[s3].id", null, "s3");
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenItemRemovedInDeepLevelList() throws Exception {
    ClassC previousKid1 = new ClassC();
    previousKid1.setId(1L);
    previousKid1.setName("Name 1");
    previousKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Sport")));

    ClassC previousKid2 = new ClassC();
    previousKid2.setId(2L);
    previousKid2.setName("Name 2");
    previousKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

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
    previous.setKids(List.of(previousKid1, previousKid2));


    ClassC currentKid1 = new ClassC();
    currentKid1.setId(1L);
    currentKid1.setName("Name 1");
    currentKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math")));

    ClassC currentKid2 = new ClassC();
    currentKid2.setId(2L);
    currentKid2.setName("Name 2");
    currentKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

    ClassB currentV1 = new ClassB();
    currentV1.setKey("v_1");
    currentV1.setDisplayName("My Car");

    ClassB currentV2 = new ClassB();
    currentV2.setKey("v_2");
    currentV2.setDisplayName("Ford");

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setVehicles(List.of(currentV1, currentV2));
    current.setKids(List.of(currentKid1, currentKid2));

    PropertyUpdate expectedChange1 = new PropertyUpdate("kids[1].favoriteSubjects[s2].id", "s2", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("kids[1].favoriteSubjects[s2].name", "Sport", null);
    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenNoObjectChange() throws Exception {
    ClassC previousKid1 = new ClassC();
    previousKid1.setId(1L);
    previousKid1.setName("Name 1");
    previousKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")));

    ClassC previousKid2 = new ClassC();
    previousKid2.setId(2L);
    previousKid2.setName("Name 2");
    previousKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

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
    previous.setKids(List.of(previousKid1, previousKid2));


    ClassC currentKid1 = new ClassC();
    currentKid1.setId(1L);
    currentKid1.setName("Name 1");
    currentKid1.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Soccer")));

    ClassC currentKid2 = new ClassC();
    currentKid2.setId(2L);
    currentKid2.setName("Name 2");
    currentKid2.setFavoriteSubjects(List.of(new ClassD("s1", "Math"), new ClassD("s2", "Biology")));

    ClassB currentV1 = new ClassB();
    currentV1.setKey("v_1");
    currentV1.setDisplayName("My Car");

    ClassB currentV2 = new ClassB();
    currentV2.setKey("v_2");
    currentV2.setDisplayName("Ford");

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setVehicles(List.of(currentV1, currentV2));
    current.setKids(List.of(currentKid1, currentKid2));

    assertListsEquals(DiffTool.diff(previous, current), List.of());
  }

  @Test
  void detectFieldChange_whenNoAuditInfoExists() {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setSpaceShips(List.of(new ClassE("k1", "Enterprise")));

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setSpaceShips(List.of(new ClassE("k1", "Borg")));

    assertThrows(MissingAuditInfoException.class, () -> DiffTool.diff(previous, current));
  }

  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;
    private List<ClassB> vehicles;
    private List<ClassC> kids;
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
