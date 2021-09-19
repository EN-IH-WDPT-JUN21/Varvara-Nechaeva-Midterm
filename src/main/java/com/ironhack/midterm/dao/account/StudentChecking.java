package com.ironhack.midterm.dao.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@NoArgsConstructor
// @AllArgsConstructor
@Setter
@Getter
@Entity
public class StudentChecking extends DebitAccount {}
