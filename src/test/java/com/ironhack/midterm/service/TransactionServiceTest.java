package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.Savings;
import com.ironhack.midterm.dao.test_utils.Populator;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
