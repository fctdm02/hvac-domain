package com.djt.hvac.domain.model.common.dsl.tagquery;

import java.util.Set;

import static java.util.Objects.requireNonNull;

class TagExpression implements Expression {
  private final String name;

  TagExpression(String name) {
    this.name = requireNonNull(name, "name cannot be null");
  }

  @Override
  public String toSql() {
    return "SELECT node_id FROM node_tags WHERE tag_id IN (SELECT id FROM tags WHERE name = '"
        + name + "')";
  }

  @Override
  public boolean match(Set<String> tags) {
    requireNonNull(tags, "tags cannot be null");
    return tags.contains(name);
  }

  @Override
  public void getTags(Set<String> tags) {
    tags.add(name);
  }

}
