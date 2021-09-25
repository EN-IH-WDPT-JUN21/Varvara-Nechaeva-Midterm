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

import static com.ironhack.midterm.utils.Utils.*;

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

    Transaction transaction = new Transaction(null, new Date(), accountFrom, accountTo, money);

    // only DebitAccount has status, so only debit account gets fraud prevention logic
    if (accountFrom instanceof DebitAccount) {
      DebitAccount debitAccount = (DebitAccount) accountFrom;

      if (Status.FROZEN == debitAccount.getStatus()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Account with id " + fromId + " is frozen");
      }

      checkForFrequentTransactions(debitAccount);
      checkForOverlyLargeTransaction(debitAccount, transaction);
    }

    accountFrom.getBalance().decreaseAmount(money);
    if (accountFrom.getBalance().getAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE, "Account with id " + fromId + " has insufficient balance.");
    }

    accountTo.getBalance().increaseAmount(money);

    accountRepository.save(accountTo);
    accountRepository.save(accountFrom);

    return transactionRepository.save(transaction);
  }

  private void checkForFrequentTransactions(DebitAccount account) {
    // fraud detection: More than 2 transactions occurring on a single account within a 1 second
    // period.
    var lastTransactions =
        transactionRepository.findAllByFromAccountAndTransactionDateGreaterThan(
            account, oneSecondBack());
    if (!lastTransactions.isEmpty()) {
      account.setStatus(Status.FROZEN);
      accountRepository.save(account);

      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE,
          "Too frequent transactions from account id " + account.getId() + ": possibly a fraud.");
    }
  }

  private void checkForOverlyLargeTransaction(DebitAccount account, Transaction transaction) {
    /*
    MySQL query:

    with d as (
      SELECT t1.from_account_id
      , t1.id as starting_id
      , t1.transaction_date as starting_date
      , t2.id, t2.transaction_date, t2.currency, t2.amount
      FROM transaction t1
      join transaction t2 on (
        t1.from_account_id = t2.from_account_id
        and t2.transaction_date between t1.transaction_date and addtime(t1.transaction_date, "24:00:00")
      )
      where t1.from_account_id = 1
      -- order by t1.from_account_id, t1.id
    )
    , d_max as (
      select from_account_id, starting_id, sum(amount) as sum_amount from d
      group by from_account_id, starting_id
    )
    select max(sum_amount) as max_sum_amount from d_max
    ;
    */
    var transactions = transactionRepository.findAllByFromAccount(account);
    if (transactions.isEmpty()) return; // very first transaction is always allowed
    var max = new Money(new BigDecimal(0)); // because there is no negative money transfers
    for (var t : transactions) {
      var sum = new Money(new BigDecimal(0));
      var d24 = addTwentyFourHours(t.getTransactionDate());
      for (var tt : transactions) {
        if (tt.getTransactionDate().before(d24)
            && tt.getTransactionDate().after(t.getTransactionDate())) {
          sum.increaseAmount(tt.getMoney());
        }
      }
      if (sum.getAmount().compareTo(max.getAmount()) > 0) {
        max = sum;
      }
    }
    // max = highest daily total transactions in any other 24 hour period
    // INCLUDING the current pending transactions
    var sum = new Money(transaction.getMoney().getAmount());
    var d24 = subtractTwentyFourHours(transaction.getTransactionDate());
    for (var tt : transactions) {
      if (tt.getTransactionDate().before(d24)
          && tt.getTransactionDate().after(transaction.getTransactionDate())) {
        sum.increaseAmount(tt.getMoney());
      }
    }
    // sum = Transactions made in last 24 hours
    var max150 = new BigDecimal("1.5").multiply(max.getAmount());
    if (sum.getAmount().compareTo(max150) > 0) {
      account.setStatus(Status.FROZEN);
      accountRepository.save(account);

      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE,
          "Too big transactions from account id "
              + account.getId()
              + " in last 24 hours: possibly a fraud.");
    }
  }
}
