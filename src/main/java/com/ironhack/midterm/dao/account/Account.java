package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.utils.Money;
import com.ironhack.midterm.utils.PersistentMoneyAmountAndCurrency;
import lombok.*;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
  private Money penaltyFee;

  @NotNull @ManyToOne private AccountHolder primaryOwner;

  @ManyToOne private AccountHolder secondaryOwner;
}
