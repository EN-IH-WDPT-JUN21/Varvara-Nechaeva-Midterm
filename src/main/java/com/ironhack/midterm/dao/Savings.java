package com.ironhack.midterm.dao;

import com.ironhack.midterm.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Setter
@Getter
@Entity
@TypeDef(
    name = "persistentMoneyAmountAndCurrency",
    typeClass = PersistentMoneyAmountAndCurrency.class)
public class Savings extends DebitAccount {
  @NotNull private BigDecimal interestRate;

  @Columns(
      columns = {
        @Column(name = "minimum_balance_currency", length = 3),
        @Column(name = "minimum_balance_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money minimumBalance;

  public Savings(
      Long id,
      Money balance,
      Money penaltyFee,
      AccountHolder primaryOwner,
      AccountHolder secondaryOwner,
      String secretKey,
      Date creationDate,
      Status status,
      BigDecimal interestRate,
      Money minimumBalance) {
    super(id, balance, penaltyFee, primaryOwner, secondaryOwner, secretKey, creationDate, status);
    this.interestRate = interestRate;
    this.minimumBalance = minimumBalance;
  }
}
