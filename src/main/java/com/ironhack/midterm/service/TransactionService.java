package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.DebitAccount;
import com.ironhack.midterm.dao.account.Transaction;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.repository.TransactionRepository;

import com.ironhack.midterm.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;

import static com.ironhack.midterm.utils.Utils.oneSecondBack;

@Service
public class TransactionService {

  @Autowired private TransactionRepository transactionRepository;
  @Autowired AccountRepository accountRepository;

  public Transaction moveMoney(Long fromId, Long toId, Money money) {
    Account accountFrom =
        accountRepository
            .findById(fromId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account with id " + fromId + " doesn't exist"));
    Account accountTo =
        accountRepository
            .findById(toId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account with id " + toId + " doesn't exist"));

    // only DebitAccount has status, so only debit account gets fraud prevention logic
    if (accountFrom instanceof DebitAccount) {
      // fraud detection: More than 2 transactions occurring on a single account within a 1 second
      // period.
      var lastTransactions =
          transactionRepository.findAllByFromAccountAndTransactionDateGreaterThan(
              accountFrom, oneSecondBack());
      if (!lastTransactions.isEmpty()) {

        DebitAccount debitAccount = (DebitAccount) accountFrom;
        debitAccount.setStatus(Status.FROZEN);
        accountRepository.save(debitAccount);

        throw new ResponseStatusException(
            HttpStatus.NOT_ACCEPTABLE,
            "Too frequent transactions from account id " + toId + ": possibly a fraud.");
      }
    }

    accountFrom.getBalance().decreaseAmount(money);
    if (accountFrom.getBalance().getAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE, "Account with id " + fromId + " has insufficient balance.");
    }

    accountTo.getBalance().increaseAmount(money);

    accountRepository.save(accountTo);
    accountRepository.save(accountFrom);

    Transaction transaction = new Transaction(null, new Date(), accountFrom, accountTo, money);
    return transactionRepository.save(transaction);
  }
}
