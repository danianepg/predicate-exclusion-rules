package com.danianepg.predicateexclusionrules.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.danianepg.predicateexclusionrules.enums.ComparatorEnum;
import com.danianepg.predicateexclusionrules.enums.OperatorEnum;

/**
 * Entity to represent the rules that will be saved.
 *
 * @author Daniane P. Gomes
 *
 */
@Entity
public class ExclusionRule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Attribute name of the class com.danianepg.predicateexclusionrules.data.PersonDTO to be validated. Example
   * "firstName" that is an attribute from the class first PersonDTO.
   */
  private String fieldName;

  /**
   * Operator AND or OR related to the attribute ruleValues
   */
  @Enumerated(EnumType.STRING)
  private OperatorEnum operator;

  /**
   * Comparator EQUALS or CONTAINS related to the attribute ruleValues
   */
  @Enumerated(EnumType.STRING)
  private ComparatorEnum comparator;

  /**
   * Set of strings that are not allowed on the attribute 'fieldName', separated by comma.
   */
  private String ruleValues;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  public void setFieldName(final String fieldName) {
    this.fieldName = fieldName;
  }

  public OperatorEnum getOperator() {
    return this.operator;
  }

  public void setOperator(final OperatorEnum operator) {
    this.operator = operator;
  }

  public ComparatorEnum getComparator() {
    return this.comparator;
  }

  public void setComparator(final ComparatorEnum comparator) {
    this.comparator = comparator;
  }

  public String getRuleValues() {
    return this.ruleValues;
  }

  public void setRuleValues(final String ruleValues) {
    this.ruleValues = ruleValues;
  }

}
