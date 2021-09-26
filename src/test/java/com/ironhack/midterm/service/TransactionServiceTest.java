package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.DebitAccount;
import com.ironhack.midterm.dao.test_utils.Populator;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TransactionServiceTest {

  @Autowired TransactionService transactionService;

  @Autowired AccountRepository accountRepository;

  @BeforeEach
  void setup(@Autowired Populator populator) {
    populator.populate();
  }

  @Test
  void Moving_Moves_Changes_Balances() {
    transactionService.moveMoney(
        2L, 1L, new Money(new BigDecimal(100), Currency.getInstance("USD")));

    assertTrue(
        new BigDecimal("1000")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
    assertTrue(
        new BigDecimal("200")
                .compareTo(accountRepository.findById(1L).get().getBalance().getAmount())
            == 0);
  }

  @Test
  void Moving_Moves_From_Account_With_Insufficient_Balance() {
    assertThrows(
        ResponseStatusException.class,
        () -> {
          transactionService.moveMoney(
              1L, 2L, new Money(new BigDecimal(10000), Currency.getInstance("USD")));
        });

    assertTrue(
        new BigDecimal("1100")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
    assertTrue(
        new BigDecimal("100")
                .compareTo(accountRepository.findById(1L).get().getBalance().getAmount())
            == 0);
  }

  @Test
  void MovingMoneyTooFrequentlyIsProbablyFraud() {
    transactionService.moveMoney(
        1L, 2L, new Money(new BigDecimal(10), Currency.getInstance("USD")));
    assertThrows(
        ResponseStatusException.class,
        () -> {
          transactionService.moveMoney(
              1L, 2L, new Money(new BigDecimal(10), Currency.getInstance("USD")));
        });

    assertTrue(
        new BigDecimal("1110")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
    assertTrue(
        new BigDecimal("90")
                .compareTo(accountRepository.findById(1L).get().getBalance().getAmount())
            == 0);
    DebitAccount debitAccount = (DebitAccount) accountRepository.findById(1L).get();
    assertEquals(Status.FROZEN, debitAccount.getStatus());
  }

  @Test
  void MovingMoneyTooFrequentlyFromCreditCardsWorks() {
    transactionService.moveMoney(
        4L, 2L, new Money(new BigDecimal(10), Currency.getInstance("USD")));
    assertDoesNotThrow(
        () -> {
          transactionService.moveMoney(
              4L, 2L, new Money(new BigDecimal(10), Currency.getInstance("USD")));
        });

    assertTrue(
        new BigDecimal("1120")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
    assertTrue(
        new BigDecimal("980")
                .compareTo(accountRepository.findById(4L).get().getBalance().getAmount())
            == 0);
    DebitAccount debitAccount = (DebitAccount) accountRepository.findById(2L).get();
    assertEquals(Status.ACTIVE, debitAccount.getStatus());
  }

  @Test
  void MovingTooMuchMoneyIsProbablyFraud() {
    assertThrows(
        ResponseStatusException.class,
        () -> {
          transactionService.moveMoney(
              1L, 2L, new Money(new BigDecimal(20), Currency.getInstance("USD")));
        });

    assertTrue(
        new BigDecimal("1100")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
    assertTrue(
        new BigDecimal("100")
                .compareTo(accountRepository.findById(1L).get().getBalance().getAmount())
            == 0);
    DebitAccount debitAccount = (DebitAccount) accountRepository.findById(1L).get();
    assertEquals(Status.FROZEN, debitAccount.getStatus());
  }

  @Test
  void MovingMoneyFromFrozenAccountFails() {
    assertThrows(
        ResponseStatusException.class,
        () -> {
          transactionService.moveMoney(
              5L, 2L, new Money(new BigDecimal(10), Currency.getInstance("USD")));
        });

    assertTrue(
        new BigDecimal("1100")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
    assertTrue(
        new BigDecimal("100")
                .compareTo(accountRepository.findById(5L).get().getBalance().getAmount())
            == 0);
  }

  @Test
  void Moving_To_ThirdParty_Changes_Balance() {
    transactionService.moveMoney(
        2L, 6L, new Money(new BigDecimal(100), Currency.getInstance("USD")));

    assertTrue(
        new BigDecimal("1000")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
  }

  @Test
  void Moving_From_ThirdParty_Changes_Balance() {
    transactionService.moveMoney(
        6L, 2L, new Money(new BigDecimal(100), Currency.getInstance("USD")));

    assertTrue(
        new BigDecimal("1200")
                .compareTo(accountRepository.findById(2L).get().getBalance().getAmount())
            == 0);
  }
}
