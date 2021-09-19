package com.ironhack.midterm.controller.dto;

import com.ironhack.midterm.utils.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MoneyDTO {
  private Currency currency;
  private BigDecimal amount;

  public MoneyDTO(Money money) {
    currency = money.getCurrency();
    amount = money.getAmount();
  }

  public Money asMoney() {
    return new Money(amount, currency);
  }
}
