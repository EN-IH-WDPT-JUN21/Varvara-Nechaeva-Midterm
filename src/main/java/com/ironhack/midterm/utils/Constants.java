package com.ironhack.midterm.utils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public abstract class Constants {
  public static final BigDecimal SAVINGS_DEFAULT_INTEREST_RATE = new BigDecimal("0.0025");
  public static final BigDecimal SAVINGS_MAXIMUM_INTEREST_RATE = new BigDecimal("0.5");
  public static final Money SAVINGS_DEFAULT_MINIMUM_BALANCE =
      new Money(new BigDecimal("1000"), Currency.getInstance("USD"));
  public static final Money SAVINGS_MINIMUM_MINIMUM_BALANCE =
      new Money(new BigDecimal("100"), Currency.getInstance("USD"));
}
