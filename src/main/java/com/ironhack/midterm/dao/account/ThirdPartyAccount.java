package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolderBase;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.utils.Money;
import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class ThirdPartyAccount extends Account {
  public ThirdPartyAccount(Long id, @NotNull ThirdParty primaryOwner, @NotNull Date creationDate) {
    super(
        id,
        new Money(BigDecimal.ZERO),
        new Money(BigDecimal.ZERO),
        primaryOwner,
        null,
        creationDate);
  }

  @Override
  public Money getBalance() {
    return new Money(new BigDecimal("100000000000000"));
  }

  @Override
  public Money getPenaltyFee() {
    return null;
  }

  @Override
  public AccountHolderBase getSecondaryOwner() {
    return null;
  }

  @Override
  public void setBalance(Money balance) {}

  @Override
  public void setPenaltyFee(Money penaltyFee) {}

  @Override
  public void setSecondaryOwner(AccountHolderBase secondaryOwner) {}
}
