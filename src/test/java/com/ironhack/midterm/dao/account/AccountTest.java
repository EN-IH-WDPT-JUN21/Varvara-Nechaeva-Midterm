package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

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
}
