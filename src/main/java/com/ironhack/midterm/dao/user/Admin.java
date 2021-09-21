package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  private String name;
  private String password;
}
