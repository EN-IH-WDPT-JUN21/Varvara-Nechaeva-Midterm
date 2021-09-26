package com.ironhack.midterm.repository;

import com.ironhack.midterm.dao.user.AccountHolderBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderBaseRepository extends JpaRepository<AccountHolderBase, Long> {}
