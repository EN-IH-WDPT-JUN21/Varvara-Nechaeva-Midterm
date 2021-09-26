package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.Savings;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.AccountHolderBase;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.repository.AccountHolderBaseRepository;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Address;
import com.ironhack.midterm.utils.Money;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

@Service
public class HelperService {

  @Autowired private AccountRepository accountRepository;

  @Autowired private AccountHolderBaseRepository accountHolderBaseRepository;

  @SneakyThrows
  public void populate() {
    Address primaryAddress = new Address("Main Street 1", "Main City", "9999");
    Address mailingAddress = new Address("Main Street 2", "Main City", "9991");

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    String dateInString = "7-Jun-2013";
    Date date = formatter.parse(dateInString);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    AccountHolderBase accountHolder1 =
        new AccountHolder(
            0L,
            "vasja",
            passwordEncoder.encode("123"),
            "Vasja Pupkin",
            null,
            null,
            date,
            primaryAddress,
            mailingAddress);
    accountHolder1 = accountHolderBaseRepository.save(accountHolder1);

    Savings savings =
        new Savings(
            0L,
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            accountHolder1,
            null,
            "abc",
            date,
            Status.ACTIVE,
            new BigDecimal("1.23"),
            new Money(new BigDecimal("10.00"), Currency.getInstance("USD")));

    accountRepository.save(savings);
  }
}
