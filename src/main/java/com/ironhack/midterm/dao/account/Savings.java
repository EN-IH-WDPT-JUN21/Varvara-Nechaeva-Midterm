package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;
import com.ironhack.midterm.utils.PersistentMoneyAmountAndCurrency;
import com.ironhack.midterm.enums.Status;

import static com.ironhack.midterm.utils.Utils.getDiffYears;
import static com.ironhack.midterm.utils.Utils.oneYearBack;
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
  @NotNull private BigDecimal interestRate = Constants.SAVINGS_DEFAULT_INTEREST_RATE;

  @Columns(
      columns = {
        @Column(name = "minimum_balance_currency", length = 3),
        @Column(name = "minimum_balance_amount", precision = 19, scale = 5)
      })
  @Type(type = "persistentMoneyAmountAndCurrency")
  @NotNull
  private Money minimumBalance = Constants.SAVINGS_DEFAULT_MINIMUM_BALANCE;

  @Override
  public void setBalance(Money balance) {
    if (balance.getAmount().compareTo(this.minimumBalance.getAmount()) < 0) {
      balance.decreaseAmount(this.getPenaltyFee());
    }
    super.setBalance(balance);
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
    if (getLastInterest().before(oneYearBack())) {
      for (int i = 0; i < getDiffYears(getLastInterest(), new Date()); i++) {
        super.getBalance().increaseAmount(super.getBalance().getAmount().multiply(interestRate));
      }
      setLastInterest(new Date());
    }
    return super.getBalance();
  }

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
    // Savings accounts may be instantiated with an interest rate other than the default, with a
    // maximum interest rate of 0.5
    this.interestRate = Constants.SAVINGS_MAXIMUM_INTEREST_RATE.min(interestRate);
    // Savings accounts may be instantiated with a minimum balance of less than 1000 but no lower
    // than 100
    if (-1
        == minimumBalance
            .getAmount()
            .compareTo(Constants.SAVINGS_MINIMUM_MINIMUM_BALANCE.getAmount())) {
      minimumBalance =
          new Money(
              Constants.SAVINGS_MINIMUM_MINIMUM_BALANCE.getAmount(), minimumBalance.getCurrency());
    }
    this.minimumBalance = minimumBalance;
  }
}
