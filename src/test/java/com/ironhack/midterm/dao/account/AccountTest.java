package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static com.ironhack.midterm.utils.Utils.twoYearsBack;
import static com.ironhack.midterm.utils.Utils.oneYearBack;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

  @Test
  void setBalanceSavings() {
    Account account = new Savings();

    account.setBalance(new Money(new BigDecimal("0"), Currency.getInstance("USD")));

    assertEquals(
        BigDecimal.ZERO.subtract(Constants.DEFAULT_PENALTY_FEE.getAmount()),
        account.getBalance().getAmount());
  }

  @Test
  void setBalanceChecking() {
    Account account = new Checking();

    account.setBalance(new Money(new BigDecimal("0"), Currency.getInstance("USD")));

    assertEquals(
        BigDecimal.ZERO.subtract(Constants.DEFAULT_PENALTY_FEE.getAmount()),
        account.getBalance().getAmount());
  }

  @Test
  void checkBalanceWithInterestRate() {
    Savings account = new Savings();
    account.setBalance(new Money(new BigDecimal("1000000"), Currency.getInstance("USD")));
    account.setInterestRate(new BigDecimal("0.01"));
    account.setCreationDate(oneYearBack());

    assertTrue(new BigDecimal("1010000").compareTo(account.getBalance().getAmount()) == 0);
    // two times in a row same result
    assertTrue(new BigDecimal("1010000").compareTo(account.getBalance().getAmount()) == 0);
  }

  @Test
  void checkBalanceWithInterestRateAfterTwoYears() {
    Savings account = new Savings();
    account.setBalance(new Money(new BigDecimal("1000000"), Currency.getInstance("USD")));
    account.setInterestRate(new BigDecimal("0.01"));
    account.setCreationDate(twoYearsBack());

    assertTrue(new BigDecimal("1020100").compareTo(account.getBalance().getAmount()) == 0);
    // two times in a row same result
    assertTrue(new BigDecimal("1020100").compareTo(account.getBalance().getAmount()) == 0);
  }
}
