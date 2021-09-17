package com.ironhack.midterm.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@TypeDef(
    name = "persistentMoneyAmountAndCurrency",
    typeClass = PersistentMoneyAmountAndCurrency.class)
public class Checking extends DebitAccount {
  @Columns(
      columns = {
        @Column(name = "minimum_balance_currency", length = 3),
        @Column(name = "minimum_balance_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money minimumBalance;

  @Columns(
      columns = {
        @Column(name = "monthly_maintenance_fee_currency", length = 3),
        @Column(name = "monthly_maintenance_fee_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money monthlyMaintenanceFee;
}
