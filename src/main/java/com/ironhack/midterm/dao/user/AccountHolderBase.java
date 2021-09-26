package com.ironhack.midterm.dao.user;

import com.ironhack.midterm.dao.account.Account;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public abstract class AccountHolderBase extends User {
  private String name;

  @OneToMany(mappedBy = "primaryOwner")
  private List<Account> primaryAccounts;

  @OneToMany(mappedBy = "secondaryOwner")
  private List<Account> secondaryAccounts;

  public AccountHolderBase(
      Long id,
      String login,
      String password,
      String name,
      List<Account> primaryAccounts,
      List<Account> secondaryAccounts) {
    super(id, login, password);
    this.name = name;
    this.primaryAccounts = primaryAccounts;
    this.secondaryAccounts = secondaryAccounts;
  }
}
