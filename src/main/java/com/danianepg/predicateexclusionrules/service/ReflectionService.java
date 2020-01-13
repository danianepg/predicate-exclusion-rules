package com.danianepg.predicateexclusionrules.service;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

/**
 * Utility class to retrieve class member by reflection
 * 
 * @author Daniane P. Gomes
 *
 */
@Service
public class ReflectionService {

  /**
   * Get a class field by name
   *
   * @param obj
   * @param fieldName
   * @return
   */
  public Field getFieldByName(final Object obj, final String fieldName) {

    // @formatter:off
    return Stream.of(obj.getClass().getDeclaredFields())
        .filter(field -> field.getName().equalsIgnoreCase(fieldName))
        .findFirst().orElseGet(null);
    // @formatter:on
  }

  /**
   * Get a field value
   *
   * @param target
   * @param field
   * @return
   */
  public Object getFieldValue(final Object target, final Field field) {
    ReflectionUtils.makeAccessible(field);
    return ReflectionUtils.getField(field, target);
  }
}
