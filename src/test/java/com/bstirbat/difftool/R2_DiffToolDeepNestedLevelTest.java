package com.bstirbat.difftool;

import static com.bstirbat.difftool.utils.AssertUtils.assertListsEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class R2_DiffToolDeepNestedLevelTest {

  @Test
  void detectFieldChange() throws Exception {
    ClassB previousSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.INACTIVE)
        .build();

    ClassB currentSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.ACTIVE)
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(previousSubscription)
        .build();


    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(currentSubscription)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("subscription.status", "INACTIVE", "ACTIVE")));
  }

  @Test
  void detectFieldChange_whenNestedObjectAdded() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();


    ClassB currentSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.ACTIVE)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(currentSubscription)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("subscription.status", null, "ACTIVE")));
  }

  @Test
  void detectFieldChange_whenNestedObjectRemoved() throws Exception {
    ClassB previousSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.INACTIVE)
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(previousSubscription)
        .build();


    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of(new PropertyUpdate("subscription.status", "INACTIVE", null)));
  }

  @Test
  void detectFieldChange_whenDeepNestedObjectChanged() throws Exception {
    ClassD previousService = ClassD.builder()
        .withName("Netflix")
        .build();

    ClassC previousItem = ClassC.builder()
        .withDescription("Item description")
        .withService(previousService)
        .build();

    ClassB previousSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.INACTIVE)
        .withItem(previousItem)
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(previousSubscription)
        .build();


    ClassD currentService = ClassD.builder()
        .withName("Youtube")
        .build();

    ClassC currentItem = ClassC.builder()
        .withDescription("Item description")
        .withService(currentService)
        .build();

    ClassB currentSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.ACTIVE)
        .withItem(currentItem)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(currentSubscription)
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("subscription.status", "INACTIVE", "ACTIVE");
    PropertyUpdate expectedChange2 = new PropertyUpdate("subscription.item.service.name", "Netflix", "Youtube");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2));
  }

  @Test
  void detectFieldChange_whenDeepNestedObjectAdded() throws Exception {
    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();


    ClassD currentService = ClassD.builder()
        .withName("Youtube")
        .build();

    ClassC currentItem = ClassC.builder()
        .withDescription("Item description")
        .withService(currentService)
        .build();

    ClassB currentSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.ACTIVE)
        .withItem(currentItem)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(currentSubscription)
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("subscription.status", null, "ACTIVE");
    PropertyUpdate expectedChange2 = new PropertyUpdate("subscription.item.description", null, "Item description");
    PropertyUpdate expectedChange3 = new PropertyUpdate("subscription.item.service.name", null, "Youtube");

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenDeepNestedObjectRemoved() throws Exception {
    ClassD previousService = ClassD.builder()
        .withName("Netflix")
        .build();

    ClassC previousItem = ClassC.builder()
        .withDescription("Item description")
        .withService(previousService)
        .build();

    ClassB previousSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.INACTIVE)
        .withItem(previousItem)
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(previousSubscription)
        .build();


    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .build();

    PropertyUpdate expectedChange1 = new PropertyUpdate("subscription.status", "INACTIVE", null);
    PropertyUpdate expectedChange2 = new PropertyUpdate("subscription.item.description", "Item description", null);
    PropertyUpdate expectedChange3 = new PropertyUpdate("subscription.item.service.name", "Netflix", null);

    assertListsEquals(DiffTool.diff(previous, current), List.of(expectedChange1, expectedChange2, expectedChange3));
  }

  @Test
  void detectFieldChange_whenNoChanges() throws Exception {
    ClassD previousService = ClassD.builder()
        .withName("Netflix")
        .build();

    ClassC previousItem = ClassC.builder()
        .withDescription("Item description")
        .withService(previousService)
        .build();

    ClassB previousSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.ACTIVE)
        .withItem(previousItem)
        .build();

    ClassA previous = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(previousSubscription)
        .build();


    ClassD currentService = ClassD.builder()
        .withName("Netflix")
        .build();

    ClassC currentItem = ClassC.builder()
        .withDescription("Item description")
        .withService(currentService)
        .build();

    ClassB currentSubscription = ClassB.builder()
        .withStatus(SubscriptionStatus.ACTIVE)
        .withItem(currentItem)
        .build();

    ClassA current = ClassA.builder()
        .withFirstName("James")
        .withLastName("Last")
        .withAge(25)
        .withSubscription(currentSubscription)
        .build();

    assertListsEquals(DiffTool.diff(previous, current), List.of());
  }

  static class ClassA {
    private String firstName;
    private String lastName;
    private Integer age;
    private ClassB subscription;

    public ClassA() {

    }

    private ClassA(ClassABuilder builder) {
      this.firstName = builder.firstName;
      this.lastName = builder.lastName;
      this.age = builder.age;
      this.subscription = builder.subscription;
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

    public ClassB getSubscription() {
      return subscription;
    }

    public void setSubscription(ClassB subscription) {
      this.subscription = subscription;
    }

    public static ClassABuilder builder() {
      return new ClassABuilder();
    }

    public static class ClassABuilder {
      private String firstName;
      private String lastName;
      private Integer age;
      private ClassB subscription;

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
      public ClassABuilder withSubscription(ClassB subscription) {
        this.subscription = subscription;
        return this;
      }

      public ClassA build() {
        return new ClassA(this);
      }
    }
  }

  static class ClassB {
    private SubscriptionStatus status;
    private Boolean valid;
    private LocalDate startDate;
    private ClassC item;

    public ClassB() {

    }

    private ClassB(ClassBBuilder builder) {
      this.status = builder.status;
      this.valid = builder.valid;
      this.startDate = builder.startDate;
      this.item = builder.item;
    }

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

    public static ClassBBuilder builder() {
      return new ClassBBuilder();
    }

    public static class ClassBBuilder {
      private SubscriptionStatus status;
      private Boolean valid;
      private LocalDate startDate;
      private ClassC item;

      private ClassBBuilder() {

      }

      public ClassBBuilder withStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
      }
      public ClassBBuilder withValid(Boolean valid) {
        this.valid = valid;
        return this;
      }
      public ClassBBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
      }
      public ClassBBuilder withItem(ClassC item) {
        this.item = item;
        return this;
      }

      public ClassB build() {
        return new ClassB(this);
      }
    }
  }

  static class ClassC {
    private String description;
    private ClassD service;

    public ClassC() {

    }

    public ClassC(ClassCBuilder builder) {
      this.description = builder.description;
      this.service = builder.service;
    }

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

    public static ClassCBuilder builder() {
      return new ClassCBuilder();
    }

    public static class ClassCBuilder {
      private String description;
      private ClassD service;

      private ClassCBuilder() {

      }

      public ClassCBuilder withDescription(String description) {
        this.description = description;
        return this;
      }
      public ClassCBuilder withService(ClassD service) {
        this.service = service;
        return this;
      }

      public ClassC build() {
        return new ClassC(this);
      }
    }
  }

  static class ClassD {
    private String name;

    public ClassD() {

    }

    private ClassD(ClassDBuilder builder) {
      this.name = builder.name;
    }


    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public static ClassDBuilder builder() {
      return new ClassDBuilder();
    }

    public static class ClassDBuilder {
      private String name;

      private ClassDBuilder() {

      }

      public ClassDBuilder withName(String name) {
        this.name = name;
        return this;
      }

      public ClassD build() {
        return new ClassD(this);
      }
    }
  }

  enum SubscriptionStatus {
    ACTIVE,
    INACTIVE;
  }
}
