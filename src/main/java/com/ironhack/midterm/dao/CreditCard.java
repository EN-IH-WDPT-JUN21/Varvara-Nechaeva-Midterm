package com.ironhack.midterm.dao;

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

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@TypeDef(
    name = "persistentMoneyAmountAndCurrency",
    typeClass = PersistentMoneyAmountAndCurrency.class)
public class CreditCard extends Account {
  @Columns(
      columns = {
        @Column(name = "credit_limit_currency", length = 3),
        @Column(name = "credit_limit_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money creditLimit;

  @NotNull private BigDecimal interestRate;
}
