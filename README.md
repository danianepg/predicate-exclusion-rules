# Clean Validation in Java with Predicates

Imagine that you have to consume an API to retrieve data from people of your company. Now imagine that all these data don’t follow any pattern and the API can return not only people, but robots, “phantom” accounts and all the source of irrelevant information. There are no rules: no flag to identify if the data belongs to a person or to some other creature and from time to time you can discover another variation that would classify the data as invalid.

Well, that happened. The validation could be achieved with “regex”, but it would be hard coded and the costumer would always depend on the change in the code and new deploys.

### Aha!

The most efficient and clean way found to do that it in Java was to create a table to save the rules that would configure a record as invalid, read and convert them to Predicates and dynamically validate each part of the API’s return to classify an object as valid or invalid. 

## Show me the code

This behaviour was reproduced on the project available on my GitHub, using **Java 11** and Spring Boot.

### Object representation
The external API’s data is represented by the class `PersonDTO`.
The rules that define a `PersonDTO` as invalid are represented and persisted through entity ExclusionRule where:
* `fieldName` is the attribute on `PersonDTO` that will be checked.
* `operator` is an operator AND or OR.
* `comparator` is a comparator EQUALS or CONTAINS.
* `ruleValues` are the values separated by comma that would make the `fieldName` invalid.

### Interpret rules
The resource `data.sql` will initialize some rules for the purpose of this test:

```sql
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('name', 'CONTAINS', 'OR', '1,2,3,4,5,6,7,8,9,0');
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('email', 'CONTAINS', 'OR','@exclude.me,1');
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('internalCode', 'CONTAINS', 'AND','a,b');
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('location', 'EQUALS', 'OR','jupiter,mars');
```

The rules above can be interpreted as:
* If the attribute `name` on `PersonDTO` object contains 1, 2, 3, 4, 5, 6, 7, 8, 9 or 0, the object is invalid.
* If the attribute `email` on `PersonDTO` object contains “@exclude.me” or “1”, the object is invalid.
* If the attribute `internalCode` on `PersonDTO` object contains “a” and “b”, the object is invalid.
* If the attribute `location` on `PersonDTO` object is equals to “jupiter” or “mars”, the object is invalid.

### Using Predicates
For each possible combination of operators and compators a validation class was created (`RuleContainsAnd`, `RuleContainsOr` and `RuleEqualsOr`). By implementing the interface `Predicate<T>` those classes can be used to validate an object through the simple and elegant call of `test(myFieldValue)` . It is only necessary to overwrite `test` method and define a custom rule.

```java
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
```

Class `ExclusionRuleService` is the responsible to retrieve saved rules, transform them to its corresponding `Predicate` and keep them in a list. 

```java
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
```


### Where the magic lives

Now that all the validation “bed” is done, it is possible to use methods `filterAllValid` and `isInvalid` to receive an object or a list and pass them to `isInvalidTestPredicate`. On this last method we get the field of the class `PersonDTO` that matches the defined on `ExclusionRule` and its value using Reflections.  

It is important to be aware that the heavy use of Reflections can cause performance issues, but on this particular situation I’ve considered that some performance could be sacrificed to achieve the flexibility of the validation.

The magic happens when the method `test`is called. No additional test is required.  
```java
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
```


## Test me
On class `ExclusionRulesServiceTests` we can check if the rules are being properly applied to the fields of a PersonDTO object.

```java
@Test
  public void filterAllValidPersonLstNameContainsOr_ok() {

    final PersonDTO person = new PersonDTO();
    person.setName("Daniane P. Gomes");
    person.setEmail("danianepg@gmail.com");
    person.setInternalCode("DPG001");
    person.setCompany("ACME");
    person.setLocation("BR");

    final PersonDTO person2 = new PersonDTO();
    person2.setName("Dobberius Louis The Free Elf");
    person2.setEmail("dobby@free.com");
    person2.setInternalCode("DLTFE");
    person2.setCompany("Self Employed");
    person2.setLocation("HG");

    final List<PersonDTO> personLst = new ArrayList<>();
    personLst.add(person);
    personLst.add(person2);

    final List<PersonDTO> personValidLst = this.exclusionRuleService.filterAllValid(personLst);

    assertEquals(personValidLst.size(), 2);

  }
```

## Conclusion
While consuming an external API we can receive data that is not properly structured. To check its relevance in a clean way we can:
* Create a repository of rules and represent them  as `Predicate<T>` 
* Convert the API response data to a `PersonDTO` object
* Check if each attribute of `PersonDTO` is valid only by calling the method `test`

Originally posted on [my Medium page](https://medium.com/@danianepg/clean-validation-in-java-with-predicates-18bff4ba2888).
