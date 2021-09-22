package com.ironhack.midterm.utils;

import java.math.BigDecimal;
import java.util.Currency;

public abstract class Constants {
  public static final BigDecimal SAVINGS_DEFAULT_INTEREST_RATE = new BigDecimal("0.0025");
  public static final BigDecimal SAVINGS_MAXIMUM_INTEREST_RATE = new BigDecimal("0.5");
  public static final Money SAVINGS_DEFAULT_MINIMUM_BALANCE =
      new Money(new BigDecimal("1000"), Currency.getInstance("USD"));
  public static final Money SAVINGS_MINIMUM_MINIMUM_BALANCE =
      new Money(new BigDecimal("100"), Currency.getInstance("USD"));

  public static final Money CREDIT_DEFAULT_CREDIT_LIMIT =
      new Money(new BigDecimal("100"), Currency.getInstance("USD"));
  public static final Money CREDIT_MAXIMUM_CREDIT_LIMIT =
      new Money(new BigDecimal("100000"), Currency.getInstance("USD"));
  public static final BigDecimal CREDIT_DEFAULT_INTEREST_RATE = new BigDecimal("0.2");
  public static final BigDecimal CREDIT_MINIMUM_INTEREST_RATE = new BigDecimal("0.1");

  public static final Integer CHECKING_STUDENT_AGE = 24;
  public static final Money CHECKING_MINUM_BALANCE =
      new Money(new BigDecimal("250.00"), Currency.getInstance("USD"));
  public static final Money CHECKING_MONTHLY_MAINTENANCE_FEE =
      new Money(new BigDecimal("12"), Currency.getInstance("USD"));

  public static final Money DEFAULT_PENALTY_FEE =
      new Money(new BigDecimal("40.00"), Currency.getInstance("USD"));
}
