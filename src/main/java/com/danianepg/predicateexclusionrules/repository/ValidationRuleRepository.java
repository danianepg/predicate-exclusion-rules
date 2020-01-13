package com.danianepg.predicateexclusionrules.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.danianepg.predicateexclusionrules.entity.ExclusionRule;

@RepositoryRestResource
public interface ValidationRuleRepository extends JpaRepository<ExclusionRule, Long> {
}
