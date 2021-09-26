package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolderBase;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.utils.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Date;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class StudentChecking extends DebitAccount {
  public StudentChecking(
      Long id,
      Money balance,
      Money penaltyFee,
      AccountHolderBase primaryOwner,
      AccountHolderBase secondaryOwner,
      String secretKey,
      Date creationDate,
      Status status) {
    super(id, balance, penaltyFee, primaryOwner, secondaryOwner, secretKey, creationDate, status);
  }
}
