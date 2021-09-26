package com.ironhack.midterm.dao.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ironhack.midterm.dao.user.AccountHolderBase;
import com.ironhack.midterm.dao.user.CustomAccountHolderBaseSerializer;
import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import com.ironhack.midterm.utils.PersistentMoneyAmountAndCurrency;
import lombok.*;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@TypeDef(
    name = "persistentMoneyAmountAndCurrency",
    typeClass = PersistentMoneyAmountAndCurrency.class)
public abstract class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  @Columns(
      columns = {
        @Column(name = "balance_currency", length = 3),
        @Column(name = "balance_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money balance;

  @Columns(
      columns = {
        @Column(name = "penalty_fee_currency", length = 3),
        @Column(name = "penalty_fee_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money penaltyFee = Constants.DEFAULT_PENALTY_FEE;

  @JsonSerialize(using = CustomAccountHolderBaseSerializer.class)
  @NotNull
  @ManyToOne
  private AccountHolderBase primaryOwner;

  @JsonSerialize(using = CustomAccountHolderBaseSerializer.class)
  @ManyToOne
  private AccountHolderBase secondaryOwner;

  @NotNull private Date creationDate = new Date();
}
