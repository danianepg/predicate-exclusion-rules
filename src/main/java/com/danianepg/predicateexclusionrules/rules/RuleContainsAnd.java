package com.danianepg.predicateexclusionrules.rules;

import java.util.List;
import java.util.function.Predicate;

/**
 * Definition of a rule that combines comparator 'CONTAINS' and operator 'AND'.
 * Test if a string contains all the values determined by the rule of exclusion.
 *
 * @author Daniane P. Gomes
 *
 */
public class RuleContainsAnd implements Predicate<String> {

  private List<String> exclusionRulesLst;

  public RuleContainsAnd(final List<String> exclusionRulesLst) {
    this.exclusionRulesLst = exclusionRulesLst;
  }

  @Override
  public boolean test(final String fieldValue) {
    return this.exclusionRulesLst.stream().allMatch(fieldValue::contains);
  }

}
