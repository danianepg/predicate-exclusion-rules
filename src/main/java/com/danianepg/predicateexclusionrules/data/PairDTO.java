package com.danianepg.predicateexclusionrules.data;

import java.util.function.Predicate;

/**
 * Combination of a string rule and predicate
 *
 * @author Daniane P. Gomes
 *
 */
public class PairDTO {

  private String rule;

  private Predicate<String> predicate;

  public PairDTO(final String rule, final Predicate<String> predicate) {
    this.rule = rule;
    this.predicate = predicate;
  }

  public String getRule() {
    return this.rule;
  }

  public void setRule(final String rule) {
    this.rule = rule;
  }

  public Predicate<String> getPredicate() {
    return this.predicate;
  }

  public void setPredicate(final Predicate<String> predicate) {
    this.predicate = predicate;
  }

}
