package com.danianepg.predicateexclusionrules.data;

/**
 * Representatio of Person's data received from external API.
 *
 * @author Daniane P. Gomes
 *
 */
public class PersonDTO {

  private String name;

  private String internalCode;

  private String email;

  private String company;

  private String location;

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getInternalCode() {
    return this.internalCode;
  }

  public void setInternalCode(final String internalCode) {
    this.internalCode = internalCode;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getCompany() {
    return this.company;
  }

  public void setCompany(final String company) {
    this.company = company;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(final String location) {
    this.location = location;
  }

}
