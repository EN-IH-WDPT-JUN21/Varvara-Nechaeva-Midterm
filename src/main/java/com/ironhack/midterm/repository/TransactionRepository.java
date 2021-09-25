package com.ironhack.midterm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ironhack.midterm.dao.account.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
