package com.ironhack.midterm.dao.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@TypeDef(
    name = "persistentMoneyAmountAndCurrency",
    typeClass = PersistentMoneyAmountAndCurrency.class)
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  @NotNull private Date transactionDate;

  @JsonSerialize(using = CustomAccountSerializer.class)
  @ManyToOne
  @NotNull
  private Account fromAccount;

  @JsonSerialize(using = CustomAccountSerializer.class)
  @ManyToOne
  @NotNull
  private Account toAccount;

  @Columns(
      columns = {
        @Column(name = "currency", length = 3),
        @Column(name = "amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  Money money;
}
