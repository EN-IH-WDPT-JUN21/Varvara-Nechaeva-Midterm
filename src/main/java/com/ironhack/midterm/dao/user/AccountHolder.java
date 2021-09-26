package com.ironhack.midterm.dao.user;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.utils.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class AccountHolder extends AccountHolderBase {

  private Date dateOfBirth;

  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "primary_street")),
    @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
    @AttributeOverride(name = "postalCode", column = @Column(name = "primary_postalCode"))
  })
  @Embedded
  private Address primaryAddress;

  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "mailing_street")),
    @AttributeOverride(name = "city", column = @Column(name = "mailing_city")),
    @AttributeOverride(name = "postalCode", column = @Column(name = "mailing_postalCode"))
  })
  @Embedded
  private Address mailingAddress;

  public AccountHolder(
      Long id,
      String name,
      List<Account> primaryAccounts,
      List<Account> secondaryAccounts,
      Date dateOfBirth,
      Address primaryAddress,
      Address mailingAddress) {
    super(id, name, primaryAccounts, secondaryAccounts);
    this.dateOfBirth = dateOfBirth;
    this.primaryAddress = primaryAddress;
    this.mailingAddress = mailingAddress;
  }
}
