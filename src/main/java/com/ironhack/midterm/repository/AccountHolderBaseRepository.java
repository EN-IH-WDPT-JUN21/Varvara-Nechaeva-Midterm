package com.ironhack.midterm.repository;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.AccountHolderBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHolderBaseRepository extends JpaRepository<AccountHolderBase, Long> {
  Optional<AccountHolderBase> findByLogin(String login);
}
