package com.ironhack.midterm.controller.dto;

import com.ironhack.midterm.utils.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SavingsCreationDTO {
  @NotNull private Long primaryOwnerId;
  private Long secondaryOwnerId;

  @NotNull private String secretKey;

  private MoneyDTO penaltyFee;

  private MoneyDTO minimumBalance;

  private BigDecimal interestRate;
}
