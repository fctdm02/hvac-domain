package com.djt.hvac.domain.model.email.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EmailDto.Builder.class)
public class EmailDto {
  private final Integer id;
  private final String sender;
  private final String recipient;
  private final String subject;
  private final String body;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (EmailDto emailDto) {
    return new Builder(emailDto);
  }

  private EmailDto (Builder builder) {
    this.id = builder.id;
    this.sender = builder.sender;
    this.recipient = builder.recipient;
    this.subject = builder.subject;
    this.body = builder.body;
  }

  public Integer getId() {
    return id;
  }

  public String getSender() {
    return sender;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;

    private Builder() {}

    private Builder(EmailDto emailDto) {
      requireNonNull(emailDto, "emailDto cannot be null");
      this.id = emailDto.id;
      this.sender = emailDto.sender;
      this.recipient = emailDto.recipient;
      this.subject = emailDto.subject;
      this.body = emailDto.body;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }

    public Builder withSender(String sender) {
      requireNonNull(sender, "sender cannot be null");
      this.sender = sender;
      return this;
    }

    public Builder withRecipient(String recipient) {
      requireNonNull(recipient, "recipient cannot be null");
      this.recipient = recipient;
      return this;
    }

    public Builder withSubject(String subject) {
      requireNonNull(subject, "subject cannot be null");
      this.subject = subject;
      return this;
    }

    public Builder withBody(String body) {
      requireNonNull(body, "body cannot be null");
      this.body = body;
      return this;
    }

    public EmailDto build() {
      requireNonNull(sender, "sender cannot be null");
      requireNonNull(recipient, "recipient cannot be null");
      requireNonNull(subject, "subject cannot be null");
      requireNonNull(body, "body cannot be null");
      return new EmailDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((body == null) ? 0 : body.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
    result = prime * result + ((sender == null) ? 0 : sender.hashCode());
    result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
    EmailDto other = (EmailDto) obj;
    if (body == null) {
      if (other.body != null)
        return false;
    } else if (!body.equals(other.body))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (recipient == null) {
      if (other.recipient != null)
        return false;
    } else if (!recipient.equals(other.recipient))
      return false;
    if (sender == null) {
      if (other.sender != null)
        return false;
    } else if (!sender.equals(other.sender))
      return false;
    if (subject == null) {
      if (other.subject != null)
        return false;
    } else if (!subject.equals(other.subject))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("EmailDto [id=").append(id).append(", sender=").append(sender)
        .append(", recipient=").append(recipient).append(", subject=").append(subject)
        .append(", body=").append(body).append("]");
    return builder2.toString();
  }
}