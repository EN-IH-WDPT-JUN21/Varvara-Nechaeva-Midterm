package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static com.ironhack.midterm.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class CreditCardTest {

  @Test
  void checkLimitingValues() {
    CreditCard creditCard =
        new CreditCard(
            0L,
            null,
            null,
            null,
            null,
            null,
            new Money(new BigDecimal(9999999), Currency.getInstance("USD")),
            new BigDecimal("0.00000002"));
    assertEquals(Constants.CREDIT_MAXIMUM_CREDIT_LIMIT, creditCard.getCreditLimit());
    assertEquals(Constants.CREDIT_MINIMUM_INTEREST_RATE, creditCard.getInterestRate());
  }

  @Test
  void checkDefaultValues() {
    CreditCard creditCard = new CreditCard();
    assertEquals(Constants.CREDIT_DEFAULT_CREDIT_LIMIT, creditCard.getCreditLimit());
    assertEquals(Constants.CREDIT_DEFAULT_INTEREST_RATE, creditCard.getInterestRate());
    assertEquals(Constants.DEFAULT_PENALTY_FEE, creditCard.getPenaltyFee());
  }

  @Test
  void checkBalanceWithInterestRate() {
    CreditCard creditCard = new CreditCard();
    creditCard.setBalance(new Money(new BigDecimal("1000000"), Currency.getInstance("USD")));
    creditCard.setInterestRate(new BigDecimal("0.12"));
    creditCard.setCreationDate(oneMonthBack());

    assertTrue(new BigDecimal("1010000").compareTo(creditCard.getBalance().getAmount()) == 0);
    // two times in a row same result
    assertTrue(new BigDecimal("1010000").compareTo(creditCard.getBalance().getAmount()) == 0);
  }

  @Test
  void checkBalanceWithInterestRateAfterTwoMonths() {
    CreditCard creditCard = new CreditCard();
    creditCard.setBalance(new Money(new BigDecimal("1000000"), Currency.getInstance("USD")));
    creditCard.setInterestRate(new BigDecimal("0.12"));
    creditCard.setCreationDate(twoMonthsBack());

    assertTrue(new BigDecimal("1020100").compareTo(creditCard.getBalance().getAmount()) == 0);
    // two times in a row same result
    assertTrue(new BigDecimal("1020100").compareTo(creditCard.getBalance().getAmount()) == 0);
  }
}
