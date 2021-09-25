package com.ironhack.midterm.repository;

import com.ironhack.midterm.dao.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ironhack.midterm.dao.account.Transaction;

import java.util.Date;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Set<Transaction> findAllByFromAccountAndTransactionDateGreaterThan(
      Account fromAccount, Date fromDate);
}
