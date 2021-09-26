package com.ironhack.midterm.dao.user;

import com.ironhack.midterm.dao.account.Account;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AccountHolderBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "primaryOwner")
  private List<Account> primaryAccounts;

  @OneToMany(mappedBy = "secondaryOwner")
  private List<Account> secondaryAccounts;
}
