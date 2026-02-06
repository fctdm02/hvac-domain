package com.djt.hvac.domain.model.notification.dto;

import org.junit.Test;

public class GetUserNotificationsResponseTest {

/*  
  private static final String JSON;
  private static final String COMPACT_JSON;

  static {
    try (InputStream in = GetUserNotificationsResponseTest.class.getResourceAsStream("GetUserNotificationsResponse.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      JSON = s.hasNext() ? s.next() : "";

     JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(JSON, JsonNode.class);
     COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
*/  
  
  @Test
  public void test() {
    
  }
  
/*
  @Test
  public void serialize() throws JsonProcessingException {
    
    // STEP 1: ARRANGE
    List<GetUserNotificationsResponse> dtoList = buildGetUserNotificationsResponse();
    
    
    // STEP 2: ACT
    String actualJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(dtoList);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    List<GetUserNotificationsResponse> expected = buildGetUserNotificationsResponse();
    
    
    // STEP 2: ACT
    List<GetUserNotificationsResponse> actual = AbstractEntity.OBJECT_MAPPER.get().readValue(COMPACT_JSON, new TypeReference<List<GetUserNotificationsResponse>>() {});
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("dto is null", actual);
    Assert.assertEquals("dtos are not equal", expected.toString(), actual.toString());
  }
  
  private List<GetUserNotificationsResponse> buildGetUserNotificationsResponse() {
    
    List<String> presentationTypes = new ArrayList<>();
    for (NotificationPresentationType e: NotificationEventType.PLANNED_SITE_MAINTENANCE.getPresentationTypes()) {
      presentationTypes.add(e.toString());
    }

    List<String> applicationTypes = new ArrayList<>();
    for (NotificationApplicationType e: NotificationEventType.PLANNED_SITE_MAINTENANCE.getApplicationTypes()) {
      applicationTypes.add(e.toString());
    }

    List<String> emailTypes = new ArrayList<>();
    for (EmailType e: NotificationEventType.PLANNED_SITE_MAINTENANCE.getEmailTypes()) {
      emailTypes.add(e.toString());
    }
    
    SortedMap<String, String> substitutionTokenValues = new TreeMap<>();
    substitutionTokenValues.put("DATE_AND_TIME_OF_MAINTENANCE", "July 15th, 2022 at 3:00AM");
    
    List<GetUserNotificationsResponse> dtoList = new ArrayList<>();
    dtoList.add(GetUserNotificationsResponse
        .builder()
        .withUserId(100)
        .withAudienceScopeId(4)
        .withCategory(NotificationEventType.PLANNED_SITE_MAINTENANCE.getCategory().toString())
        .withProducer(NotificationEventType.PLANNED_SITE_MAINTENANCE.getProducer().toString())
        .withAttentionLevel(NotificationEventType.PLANNED_SITE_MAINTENANCE.getAttentionLevel().toString())
        .withPresentationTypes(presentationTypes)
        .withApplicationTypes(applicationTypes)
        .withEmailTypes(emailTypes)
        .withName(NotificationEventType.PLANNED_SITE_MAINTENANCE.getName())
        .withDisplayName(NotificationEventType.PLANNED_SITE_MAINTENANCE.getDisplayName())
        .withEmailCannotBeTurnedOff(NotificationEventType.PLANNED_SITE_MAINTENANCE.getEmailCannotBeTurnedOff())
        .withEventId(1000)
        .withEventUuid("5afde5b6-e802-11ec-8fea-0242ac120002")
        .withOccurredOnDate("2022-09-31 00:00:00")
        .withExpirationDate("2022-12-31 00:00:00")
        .withIsDownStatus(null)
        .withSubstitutionTokenValues(substitutionTokenValues)
        .withDetails(null)
        .withHasBeenPublished(Boolean.TRUE)
        .withHasBeenRead(Boolean.FALSE)
        .withHasBeenEmailed(Boolean.TRUE)
        .withPublishedBy("tmyers@resolutebi.com")
        .build());
    return dtoList;
  }
*/  
}