package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class SavingsTest {

  @Test
  void checkLimitingValues() {
    Savings savings =
        new Savings(
            0L,
            null,
            null,
            null,
            null,
            null,
            null,
            Status.ACTIVE,
            new BigDecimal("1"),
            new Money(new BigDecimal(0), Currency.getInstance("USD")));
    assertEquals(Constants.SAVINGS_MAXIMUM_INTEREST_RATE, savings.getInterestRate());
    assertEquals(Constants.SAVINGS_MINIMUM_MINIMUM_BALANCE, savings.getMinimumBalance());
  }

  @Test
  void checkDefaultValues() {
    Savings savings = new Savings();
    assertEquals(Constants.SAVINGS_DEFAULT_INTEREST_RATE, savings.getInterestRate());
    assertEquals(Constants.SAVINGS_DEFAULT_MINIMUM_BALANCE, savings.getMinimumBalance());
    assertEquals(Constants.DEFAULT_PENALTY_FEE, savings.getPenaltyFee());
  }
}
