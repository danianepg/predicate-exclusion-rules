package com.danianepg.predicateexclusionrules.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.danianepg.predicateexclusionrules.data.PersonDTO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExclusionRulesServiceTests {

  @Autowired
  private ExclusionRuleService exclusionRuleService;

  @Test
  public void isInvalidPersonNameContainsOr_ok() {

    final PersonDTO person = new PersonDTO();
    person.setName("Daniane P. Gomes");
    person.setEmail("danianepg@gmail.com");
    person.setInternalCode("DPG001");
    person.setCompany("ACME");
    person.setLocation("BR");

    final Boolean isInvalid = this.exclusionRuleService.isInvalid(person);
    assertEquals(false, isInvalid);

  }

  @Test
  public void isInvalidPersonNameContainsOr_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot 1234");
    person.setEmail("robot@robot.com");
    person.setInternalCode("R001");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final Boolean isInvalid = this.exclusionRuleService.isInvalid(person);
    assertEquals(true, isInvalid);

  }

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

  @Test
  public void filterAllValidPersonLstNameContainsOr_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot 12345");
    person.setEmail("robot@robot.com");
    person.setInternalCode("R001");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final PersonDTO person2 = new PersonDTO();
    person2.setName("Robot 67890");
    person2.setEmail("robot@robot.com");
    person2.setInternalCode("R001");
    person2.setCompany("ACME");
    person2.setLocation("NZ");

    final List<PersonDTO> personLst = new ArrayList<>();
    personLst.add(person);
    personLst.add(person2);

    final List<PersonDTO> personValidLst = this.exclusionRuleService.filterAllValid(personLst);
    assertEquals(personValidLst.size(), 0);

  }

  @Test
  public void isInvalidPersonEmailContainsOr_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot");
    person.setEmail("robot1@robot.com");
    person.setInternalCode("R001");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final Boolean isInvalid = this.exclusionRuleService.isInvalid(person);
    assertEquals(true, isInvalid);

  }

  @Test
  public void filterAllValidPersonEmailContainsOr_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot");
    person.setEmail("robot1@robot.com");
    person.setInternalCode("R001");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final PersonDTO person2 = new PersonDTO();
    person2.setName("Robot");
    person2.setEmail("robot@exclude.me");
    person2.setInternalCode("R001");
    person2.setCompany("ACME");
    person2.setLocation("NZ");

    final List<PersonDTO> personLst = new ArrayList<>();
    personLst.add(person);
    personLst.add(person2);

    final List<PersonDTO> personValidLst = this.exclusionRuleService.filterAllValid(personLst);
    assertEquals(personValidLst.size(), 0);

  }

  // TODO
  @Test
  public void isInvalidPersonInternalCodeContainsAnd_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot");
    person.setEmail("robot@robot.com");
    person.setInternalCode("BOT1ab");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final Boolean isInvalid = this.exclusionRuleService.isInvalid(person);
    assertEquals(true, isInvalid);

  }

  @Test
  public void filterAllValidPersonInternalCodeContainsAnd_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot");
    person.setEmail("robot@robot.com");
    person.setInternalCode("R001a");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final PersonDTO person2 = new PersonDTO();
    person2.setName("Robot");
    person2.setEmail("robot@robot.com");
    person2.setInternalCode("BOT1ab");
    person2.setCompany("ACME");
    person2.setLocation("NZ");

    final List<PersonDTO> personLst = new ArrayList<>();
    personLst.add(person);
    personLst.add(person2);

    final List<PersonDTO> personValidLst = this.exclusionRuleService.filterAllValid(personLst);
    assertEquals(personValidLst.size(), 1);

  }

  @Test
  public void isInvalidPersonLocationEquals_invalid() {
    final PersonDTO person = new PersonDTO();
    person.setName("Robot");
    person.setEmail("robot@robot.com");
    person.setInternalCode("BOT1");
    person.setCompany("ACME");
    person.setLocation("jupiter");

    final Boolean isInvalid = this.exclusionRuleService.isInvalid(person);
    assertEquals(true, isInvalid);

  }

  @Test
  public void filterAllValidPersonLocationEquals_invalid() {

    final PersonDTO person = new PersonDTO();
    person.setName("Robot");
    person.setEmail("robot@robot.com");
    person.setInternalCode("R001");
    person.setCompany("ACME");
    person.setLocation("NZ");

    final PersonDTO person2 = new PersonDTO();
    person2.setName("Robot");
    person2.setEmail("robot@robot.com");
    person2.setInternalCode("BOT2");
    person2.setCompany("ACME");
    person2.setLocation("mars");

    final List<PersonDTO> personLst = new ArrayList<>();
    personLst.add(person);
    personLst.add(person2);

    final List<PersonDTO> personValidLst = this.exclusionRuleService.filterAllValid(personLst);
    assertEquals(personValidLst.size(), 1);

  }

}
