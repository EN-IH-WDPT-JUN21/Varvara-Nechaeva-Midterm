package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.utils.Money;
import com.ironhack.midterm.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@Setter
@Getter
@Entity
public abstract class DebitAccount extends Account {

  @NotNull private String secretKey;

  @Enumerated(EnumType.STRING)
  @NotNull
  private Status status;

  public DebitAccount(
      Long id,
      @NotNull Money balance,
      @NotNull Money penaltyFee,
      @NotNull AccountHolder primaryOwner,
      AccountHolder secondaryOwner,
      String secretKey,
      Date creationDate,
      Status status) {
    super(id, balance, penaltyFee, primaryOwner, secondaryOwner, creationDate);
    this.secretKey = secretKey;
    this.status = status;
  }
}
