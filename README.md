# Clean Validation in Java with Predicates

Imagine that you have to consume an API to retrieve data from people of your company. Now imagine that all these data don’t follow any pattern and the API can return not only people, but robots, “phantom” accounts and all the source of irrelevant information. There are no rules: no flag to identify if the data belongs to a person or to some other creature and from time to time you can discover another variation that would classify the data as invalid.

Well, that happened. The validation could be achieved with “regex”, but it would be hard coded and the costumer would always depend on the change in the code and new deploys.

## Aha!

The most efficient and clean way found to do that it in Java was to create a table to save the rules that would configure a record as invalid, read and convert them to Predicates and dynamically validate each part of the API’s return to classify an object as valid or invalid. 

## Show me the code

This behaviour was reproduced on the project available on my GitHub, using Java 11 and Spring Boot.

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
<script src="https://gist.github.com/danianepg/a21953db57099444f268413ac992e609.js"></script>

Class `ExclusionRuleService` is the responsible to retrieve saved rules, transform them to its corresponding `Predicate` and keep them in a list. 
<script src="https://gist.github.com/danianepg/c4f2d8e44ba68ed2e4954efc64f6742c.js"></script>

### Where the magic lives

Now that all the validation “bed” is done, it is possible to use methods `filterAllValid` and `isInvalid` to receive an object or a list and pass them to `isInvalidTestPredicate`. On this last method we get the field of the class `PersonDTO` that matches the defined on `ExclusionRule` and its value using Reflections.  

It is important to be aware that the heavy use of Reflections can cause performance issues, but on this particular situation I’ve considered that some performance could be sacrificed to achieve the flexibility of the validation.

The magic happens on line 13, when the method `test`is called. No additional test is required.  
<script src="https://gist.github.com/danianepg/f8bfa5fd81406214ddb21a6726fd062f.js"></script>

## Test me
On class `ExclusionRulesServiceTests` we can check if the rules are being properly applied to the fields of a PersonDTO object.
<script src="https://gist.github.com/danianepg/6f20d10074187085a3880c31eae8299e.js"></script>

## Conclusion
While consuming an external API we can receive data that is not properly structured. To check its relevance in a clean way we can:
create a repository of rules and represent them  as `Predicate<T>` 
convert the API response data to a `PersonDTO` object
check if each attribute of `PersonDTO` is valid only by calling the method `test`
