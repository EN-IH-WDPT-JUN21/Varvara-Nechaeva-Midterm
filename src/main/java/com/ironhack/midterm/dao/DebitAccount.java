package com.ironhack.midterm.dao;

import com.ironhack.midterm.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class DebitAccount extends Account {

  @NotNull private String secretKey;
  @NotNull private Date creationDate;
  @NotNull private Status status;
}
