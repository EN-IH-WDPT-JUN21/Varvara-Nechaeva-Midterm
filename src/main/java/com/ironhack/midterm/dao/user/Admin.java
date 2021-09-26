package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Admin extends User {
  public Admin(Long id, String login, String password) {
    super(id, login, password);
  }
}
