package com.danianepg.predicateexclusionrules.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.danianepg.predicateexclusionrules.data.PairDTO;
import com.danianepg.predicateexclusionrules.data.PersonDTO;
import com.danianepg.predicateexclusionrules.entity.ExclusionRule;
import com.danianepg.predicateexclusionrules.enums.ComparatorEnum;
import com.danianepg.predicateexclusionrules.enums.OperatorEnum;
import com.danianepg.predicateexclusionrules.repository.ValidationRuleRepository;
import com.danianepg.predicateexclusionrules.rules.RuleContainsAnd;
import com.danianepg.predicateexclusionrules.rules.RuleContainsOr;
import com.danianepg.predicateexclusionrules.rules.RuleEqualsOr;

/**
 * Implementation of rules of exclusion. From a set of rules saved on the database, create validations that use
 * Predicates to verify if a string is valid or not.
 *
 * All rules should be saved using the com.danianepg.predicateexclusionrules.entity.ExclusionRule class following the
 * format below.
 *
 * fieldName: attribute name of the class com.danianepg.predicateexclusionrules.data.PersonDTO to be validated for
 * example "firstName" that is an attribute from the class first PersonDTO.
 * operator: AND or OR related to the ruleValues
 * comparator: EQUALS or CONTAINS related to the ruleValues
 * ruleValues: strings that are not allowed on the attribute, separated by comma.
 *
 * For example, the saved rules
 *
 * INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('name', 'CONTAINS', 'OR',
 * '1,2,3,4,5,6,7,8,9,0')
 * can be interpreted: "all the PersonDTO objects where the attribute firstName contains 1,2,3,4,5,6,7,8,9 or 0 are
 * invalid".
 *
 * INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('email', 'CONTAINS', 'OR',
 * '@exclude.me')
 * can be interpreted: "all the PersonDTO objects where the attribute email contains '@exclude.me' are invalid".
 *
 * INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('internalCode', 'CONTAINS', 'AND',
 * 'a,b')
 * can be interpreted: "all the PersonDTO objects where the attribute internalCode contains 'a' and 'b' are invalid".
 *
 * INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('location', 'EQUALS', 'OR',
 * 'jupiter,mars')
 * can be interpreted: "all the PersonDTO objects where the attribute location equals 'jupiter' or 'mars' are invalid".
 *
 *
 * @author Daniane P. Gomes
 *
 */
@Service
public class ExclusionRuleService {

  @Autowired
  private ValidationRuleRepository validationRuleRepository;

  @Autowired
  private ReflectionService reflectionService;

  private static Map<String, Predicate<String>> exclusionRulesLst;

  @PostConstruct
  public void init() {
    exclusionRulesLst = this.decodeAllRules();
  }

  /**
   * Retrieve all rules from the database and process it.
   *
   * @return
   */
  private Map<String, Predicate<String>> decodeAllRules() {
    // @formatter:off
    return this.validationRuleRepository.findAll()
        .stream()
        .map(this::deconeOneRule)
        .collect(Collectors.toMap(PairDTO::getRule, PairDTO::getPredicate));
    // @formatter:on

  }

  /**
   * According to the rule configuration, create a Predicate.
   *
   * @param validationRule
   * @return
   */
  private PairDTO deconeOneRule(final ExclusionRule validationRule) {

    PairDTO pairDTO = null;
    List<String> values = new ArrayList<>();

    if (validationRule.getRuleValues().contains(",")) {
      values = Arrays.asList(validationRule.getRuleValues().split(","));
    } else {
      values.add(validationRule.getRuleValues());
    }

    if (validationRule.getComparator() == ComparatorEnum.EQUALS && validationRule.getOperator() == OperatorEnum.OR) {
      pairDTO = new PairDTO(validationRule.getFieldName(), new RuleEqualsOr(values));

    } else {

      if (validationRule.getOperator() == OperatorEnum.OR) {
        pairDTO = new PairDTO(validationRule.getFieldName(), new RuleContainsOr(values));
      } else {
        pairDTO = new PairDTO(validationRule.getFieldName(), new RuleContainsAnd(values));
      }

    }

    return pairDTO;

  }

  /**
   * Retrieve the person's object fields by reflection and test its validity.
   *
   * @param person
   * @param entry
   * @return
   */
  private Boolean isInvalidTestPredicate(final PersonDTO person, final Entry<String, Predicate<String>> entry) {

    final Field field = this.reflectionService.getFieldByName(person, entry.getKey());
    final String fieldValue = String.valueOf(this.reflectionService.getFieldValue(person, field));

    return entry.getValue().test(fieldValue);

  }

  /**
   * Verify if a person is invalid if it fails on any determined rule.
   *
   * @param person
   * @return
   */
  public Boolean isInvalid(final PersonDTO person) {
    return exclusionRulesLst.entrySet().stream().anyMatch(e -> this.isInvalidTestPredicate(person, e));
  }

  /**
   * Get only valid objects from a list
   *
   * @param personDTOLst
   * @return
   */
  public List<PersonDTO> filterAllValid(final List<PersonDTO> personDTOLst) {
    // @formatter:off
    return personDTOLst.stream()
              .filter(person -> !this.isInvalid(person))
              .collect(Collectors.toList());
    // @formatter:on
  }

}
