package com.ironhack.midterm.dao.user;

import com.ironhack.midterm.dao.account.Account;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class ThirdParty extends AccountHolderBase {

  private String hashedKey;

  public ThirdParty(
      Long id,
      String name,
      List<Account> primaryAccounts,
      List<Account> secondaryAccounts,
      String hashedKey) {
    super(id, null, null, name, primaryAccounts, secondaryAccounts);
    this.hashedKey = hashedKey;
  }
}
