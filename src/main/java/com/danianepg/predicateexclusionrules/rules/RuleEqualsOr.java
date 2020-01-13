package com.danianepg.predicateexclusionrules.rules;

import java.util.List;
import java.util.function.Predicate;

/**
 * Definition of a rule that combines comparator 'EQUALS' and operator 'OR'.
 * Test if a string is equals to any of the values determined by the rule of exclusion.
 *
 * @author Daniane P. Gomes
 *
 */
public class RuleEqualsOr implements Predicate<String> {

  private List<String> exclusionRulesLst;

  public RuleEqualsOr(final List<String> exclusionRulesLst) {
    this.exclusionRulesLst = exclusionRulesLst;
  }

  @Override
  public boolean test(final String fieldValue) {
    return this.exclusionRulesLst.stream().anyMatch(fieldValue::equals);
  }

}
