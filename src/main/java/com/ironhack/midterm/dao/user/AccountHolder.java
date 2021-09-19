package com.ironhack.midterm.dao.user;

import com.ironhack.midterm.utils.Address;
import com.ironhack.midterm.dao.account.Account;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class AccountHolder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  private String name;
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

  @OneToMany(mappedBy = "primaryOwner")
  private List<Account> primaryAccounts;

  @OneToMany(mappedBy = "secondaryOwner")
  private List<Account> secondaryAccounts;
}
