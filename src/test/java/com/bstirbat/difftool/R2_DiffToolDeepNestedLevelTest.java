package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class R2_DiffToolDeepNestedLevelTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassB previousSubscription = new ClassB();
    previousSubscription.setStatus(SubscriptionStatus.INACTIVE);

    ClassB currentSubscription = new ClassB();
    currentSubscription.setStatus(SubscriptionStatus.ACTIVE);

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setSubscription(previousSubscription);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setSubscription(currentSubscription);

    PropertyUpdate expectedChange = new PropertyUpdate("subscription.status", "INACTIVE", "ACTIVE");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenNestedObjectAdded() throws Exception {
    ClassB currentSubscription = new ClassB();
    currentSubscription.setStatus(SubscriptionStatus.ACTIVE);

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setSubscription(currentSubscription);

    PropertyUpdate expectedChange = new PropertyUpdate("subscription.status", null, "ACTIVE");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenNestedObjectRemoved() throws Exception {
    ClassB previousSubscription = new ClassB();
    previousSubscription.setStatus(SubscriptionStatus.INACTIVE);

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setSubscription(previousSubscription);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);

    PropertyUpdate expectedChange = new PropertyUpdate("subscription.status", "INACTIVE", null);

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange));
  }

  @Test
  void detectFieldChange_whenDeepNestedObjectChanged() throws Exception {
    ClassD previousService = new ClassD();
    previousService.setName("Netflix");

    ClassC previousItem = new ClassC();
    previousItem.setDescription("Item description");
    previousItem.setService(previousService);

    ClassB previousSubscription = new ClassB();
    previousSubscription.setStatus(SubscriptionStatus.INACTIVE);
    previousSubscription.setItem(previousItem);

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setSubscription(previousSubscription);


    ClassD currentService = new ClassD();
    currentService.setName("Youtube");

    ClassC currentItem = new ClassC();
    currentItem.setDescription("Item description");
    currentItem.setService(currentService);

    ClassB currentSubscription = new ClassB();
    currentSubscription.setStatus(SubscriptionStatus.ACTIVE);
    currentSubscription.setItem(currentItem);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setSubscription(currentSubscription);

    PropertyUpdate expectedChange1 = new PropertyUpdate("subscription.status", "INACTIVE", "ACTIVE");
    PropertyUpdate expectedChange2 = new PropertyUpdate("subscription.item.service.name", "Netflix", "Youtube");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenDeepNestedObjectAdded() throws Exception {
    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);


    ClassD currentService = new ClassD();
    currentService.setName("Youtube");

    ClassC currentItem = new ClassC();
    currentItem.setDescription("Item description");
    currentItem.setService(currentService);

    ClassB currentSubscription = new ClassB();
    currentSubscription.setStatus(SubscriptionStatus.ACTIVE);
    currentSubscription.setItem(currentItem);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setSubscription(currentSubscription);

    PropertyUpdate expectedChange1 = new PropertyUpdate("subscription.status", null, "ACTIVE");
    PropertyUpdate expectedChange2 = new PropertyUpdate("subscription.item.description", null, "Item description");
    PropertyUpdate expectedChange3 = new PropertyUpdate("subscription.item.service.name", null, "Youtube");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenDeepNestedObjectRemoved() throws Exception {
    ClassD previousService = new ClassD();
    previousService.setName("Netflix");

    ClassC previousItem = new ClassC();
    previousItem.setDescription("Item description");
    previousItem.setService(previousService);

    ClassB previousSubscription = new ClassB();
    previousSubscription.setStatus(SubscriptionStatus.INACTIVE);
    previousSubscription.setItem(previousItem);

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setSubscription(previousSubscription);


    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);

    PropertyUpdate expectedChange1 = new PropertyUpdate("subscription.status", "INACTIVE", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("subscription.item.description", "Item description", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("subscription.item.service.name", "Netflix", null);

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenNoChanges() throws Exception {
    ClassD previousService = new ClassD();
    previousService.setName("Netflix");

    ClassC previousItem = new ClassC();
    previousItem.setDescription("Item description");
    previousItem.setService(previousService);

    ClassB previousSubscription = new ClassB();
    previousSubscription.setStatus(SubscriptionStatus.ACTIVE);
    previousSubscription.setItem(previousItem);

    ClassA previous = new ClassA();
    previous.setFirstName("James");
    previous.setLastName("Last");
    previous.setAge(25);
    previous.setSubscription(previousSubscription);


    ClassD currentService = new ClassD();
    currentService.setName("Netflix");

    ClassC currentItem = new ClassC();
    currentItem.setDescription("Item description");
    currentItem.setService(currentService);

    ClassB currentSubscription = new ClassB();
    currentSubscription.setStatus(SubscriptionStatus.ACTIVE);
    currentSubscription.setItem(currentItem);

    ClassA current = new ClassA();
    current.setFirstName("James");
    current.setLastName("Last");
    current.setAge(25);
    current.setSubscription(currentSubscription);

    assertListsEquals(DiffTool.diff(previous, current), List.of());
  }

  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;
    private ClassB subscription;

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

    public ClassB getSubscription() {
      return subscription;
    }

    public void setSubscription(ClassB subscription) {
      this.subscription = subscription;
    }
  }

  static class ClassB {
    private SubscriptionStatus status;
    private Boolean valid;
    private LocalDate startDate;
    private ClassC item;

    public SubscriptionStatus getStatus() {
      return status;
    }

    public void setStatus(SubscriptionStatus status) {
      this.status = status;
    }

    public Boolean getValid() {
      return valid;
    }

    public void setValid(Boolean valid) {
      this.valid = valid;
    }

    public LocalDate getStartDate() {
      return startDate;
    }

    public void setStartDate(LocalDate startDate) {
      this.startDate = startDate;
    }

    public ClassC getItem() {
      return item;
    }

    public void setItem(ClassC item) {
      this.item = item;
    }
  }

  static class ClassC {
    private String description;
    private ClassD service;

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public ClassD getService() {
      return service;
    }

    public void setService(ClassD service) {
      this.service = service;
    }
  }

  static class ClassD {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  enum SubscriptionStatus {
    ACTIVE,
    INACTIVE;
  }
}
