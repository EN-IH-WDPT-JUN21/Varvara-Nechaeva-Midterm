package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import com.ironhack.midterm.utils.PersistentMoneyAmountAndCurrency;
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
import java.math.RoundingMode;
import java.util.Date;

import static com.ironhack.midterm.utils.Utils.*;

@NoArgsConstructor
// @AllArgsConstructor
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
  private Money creditLimit = Constants.CREDIT_DEFAULT_CREDIT_LIMIT;

  @NotNull private BigDecimal interestRate = Constants.CREDIT_DEFAULT_INTEREST_RATE;

  public BigDecimal getInterestRate() {
    // we need "big enough" scale in order for the division in getBalance() to work correctly
    if (interestRate.scale() < 10)
      interestRate = interestRate.setScale(10, RoundingMode.UNNECESSARY);
    return interestRate;
  }

  private Date lastInterest;

  public Date getLastInterest() {
    if (null == lastInterest) {
      lastInterest = getCreationDate();
    }
    return lastInterest;
  }

  @Override
  public Money getBalance() {
    if (getLastInterest().before(oneMonthBack())) {
      for (int i = 0; i < getDiffMonths(getLastInterest(), new Date()); i++) {
        super.getBalance()
            .increaseAmount(
                super.getBalance()
                    .getAmount()
                    .multiply(interestRate.divide(new BigDecimal("12"), RoundingMode.HALF_EVEN)));
      }
      setLastInterest(new Date());
    }
    return super.getBalance();
  }

  public CreditCard(
      Long id,
      @NotNull Money balance,
      @NotNull Money penaltyFee,
      @NotNull AccountHolder primaryOwner,
      AccountHolder secondaryOwner,
      @NotNull Date creationDate,
      Money creditLimit,
      BigDecimal interestRate) {
    super(id, balance, penaltyFee, primaryOwner, secondaryOwner, creationDate);
    if (1 == creditLimit.getAmount().compareTo(Constants.CREDIT_MAXIMUM_CREDIT_LIMIT.getAmount())) {
      creditLimit =
          new Money(Constants.CREDIT_MAXIMUM_CREDIT_LIMIT.getAmount(), creditLimit.getCurrency());
    }

    this.creditLimit = creditLimit;
    this.interestRate = Constants.CREDIT_MINIMUM_INTEREST_RATE.max(interestRate);
  }
}
