package com.djt.hvac.domain.model.common.dsl.tagquery;

import java.util.Set;

interface Expression {
  String toSql();

  boolean match(Set<String> tags);

  void getTags(Set<String> tags);
}
