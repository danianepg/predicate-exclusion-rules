INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('name', 'CONTAINS', 'OR', '1,2,3,4,5,6,7,8,9,0');
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('email', 'CONTAINS', 'OR','@exclude.me,1');
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('internalCode', 'CONTAINS', 'AND','a,b');
INSERT INTO exclusion_rule(field_name, comparator, operator, rule_values) VALUES('location', 'EQUALS', 'OR','jupiter,mars');