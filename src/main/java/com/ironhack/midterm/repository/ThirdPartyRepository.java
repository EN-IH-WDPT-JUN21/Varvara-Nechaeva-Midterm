package com.ironhack.midterm.repository;

import com.ironhack.midterm.dao.account.Transaction;
import com.ironhack.midterm.dao.user.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {
  Optional<ThirdParty> findByHashedKey(String hashedKey);
}
