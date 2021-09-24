package com.ironhack.midterm.dao.test_utils;

import com.ironhack.midterm.dao.account.Savings;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Address;
import com.ironhack.midterm.utils.Money;
import com.ironhack.midterm.utils.Utils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

@Component
public class Populator {

  @Autowired AccountRepository accountRepository;
  @Autowired AccountHolderRepository accountHolderRepository;

  @SneakyThrows
  public void populate() {
    Address primaryAddress = new Address("Main Street 1", "Main City", "9999");
    Address mailingAddress = new Address("Main Street 2", "Main City", "9991");

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    String dateInString = "7-Jun-2013";
    Date date = formatter.parse(dateInString);

    AccountHolder accountHolder1 =
        new AccountHolder(0L, "Vasja Pupkin", date, primaryAddress, mailingAddress, null, null);
    accountHolder1 = accountHolderRepository.save(accountHolder1);

    Savings savings =
        new Savings(
            0L,
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            accountHolder1,
            null,
            "abc",
            new Date(),
            Status.ACTIVE,
            new BigDecimal("1.23"),
            new Money(new BigDecimal("10.00"), Currency.getInstance("USD")));

    Savings savings1 =
        new Savings(
            0L,
            new Money(new BigDecimal("1100.00"), Currency.getInstance("USD")),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            accountHolder1,
            null,
            "abc",
            new Date(),
            Status.ACTIVE,
            new BigDecimal("1.23"),
            new Money(new BigDecimal("10.00"), Currency.getInstance("USD")));

    accountRepository.save(savings);
    accountRepository.save(savings1);

    accountRepository.save(
        new Savings(
            0L,
            new Money(new BigDecimal("1000.00"), Currency.getInstance("USD")),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            accountHolder1,
            null,
            "abc",
            Utils.oneYearBack(),
            Status.ACTIVE,
            new BigDecimal("0.01"),
            new Money(new BigDecimal("10.00"), Currency.getInstance("USD"))));
  }
}
