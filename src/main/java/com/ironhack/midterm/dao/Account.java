package com.ironhack.midterm.dao;

import lombok.*;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private int id;

  @NotNull private Money balance;
  @NotNull private Money penaltyFee;

  @NotNull private AccountHolder primaryOwner;
  private AccountHolder secondaryOwner;
}
