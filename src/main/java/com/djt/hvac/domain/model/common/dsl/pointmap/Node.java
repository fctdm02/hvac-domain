package com.djt.hvac.domain.model.common.dsl.pointmap;

import static java.util.Objects.requireNonNull;

public class Node {
  private final NodeType type;
  private final String name;
  private final String displayName;

  public static Builder builder() {
    return new Builder();
  }

  private Node(Builder builder) {
    this.type = builder.type;
    this.name = builder.name;
    this.displayName = builder.displayName;
  }

  public NodeType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return "Node [type=" + type + ", name=" + name + ", displayName=" + displayName + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Node other = (Node) obj;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  public static class Builder {
    private NodeType type;
    private String name;
    private String displayName;

    private Builder() {}

    public Builder withType(NodeType type) {
      this.type = requireNonNull(type, "type cannot be null");
      return this;
    }

    public Builder withName(String name) {
      this.name = requireNonNull(name, "name cannot be null");
      return this;
    }

    public Builder withDisplayName(String displayName) {
      this.displayName = requireNonNull(displayName, "displayName cannot be null");
      return this;
    }

    public Node build() {
      requireNonNull(type, "type cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(displayName, "displayName cannot be null");
      return new Node(this);
    }

  }

}
