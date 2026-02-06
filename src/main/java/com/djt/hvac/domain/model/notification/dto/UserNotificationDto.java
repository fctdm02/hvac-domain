//@formatter:off
package com.djt.hvac.domain.model.notification.dto;

public class UserNotificationDto {

  private Integer userId;
  private Integer notificationEventId;
  private boolean hasBeenRead = false;
  private boolean hasBeenEmailed = false;
  
  public UserNotificationDto() {
  }
  
  public Integer getUserId() {
    return userId;
  }
  public void setUserId(Integer userId) {
    this.userId = userId;
  }
  public Integer getNotificationEventId() {
    return notificationEventId;
  }
  public void setNotificationEventId(Integer notificationEventId) {
    this.notificationEventId = notificationEventId;
  }
  public boolean getHasBeenRead() {
    return hasBeenRead;
  }
  public void setHasBeenRead(boolean hasBeenRead) {
    this.hasBeenRead = hasBeenRead;
  }
  public boolean getHasBeenEmailed() {
    return hasBeenEmailed;
  }
  public void setHasBeenEmailed(boolean hasBeenEmailed) {
    this.hasBeenEmailed = hasBeenEmailed;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((notificationEventId == null) ? 0 : notificationEventId.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UserNotificationDto other = (UserNotificationDto) obj;
    if (notificationEventId == null) {
      if (other.notificationEventId != null)
        return false;
    } else if (!notificationEventId.equals(other.notificationEventId))
      return false;
    if (userId == null) {
      if (other.userId != null)
        return false;
    } else if (!userId.equals(other.userId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserNotificationDto [userId=").append(userId).append(", notificationEventId=")
        .append(notificationEventId).append(", hasBeenRead=").append(hasBeenRead)
        .append(", hasBeenEmailed=").append(hasBeenEmailed).append("]");
    return builder.toString();
  }
}
//@formatter:on