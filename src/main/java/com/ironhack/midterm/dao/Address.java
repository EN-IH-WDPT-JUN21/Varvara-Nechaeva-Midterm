package com.ironhack.midterm.dao;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
  private String street;
  private String city;
  private String postalCode;
}
