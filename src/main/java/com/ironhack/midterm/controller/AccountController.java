package com.ironhack.midterm.controller;

import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired AccountRepository accountRepository;

  @GetMapping("/{id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money getBalance(@PathVariable(name = "id") long id) {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account " + id + " not found"));
    return account.getBalance();
  }

  @PatchMapping("/{id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money updateBalance(
      @PathVariable(name = "id") long id, @RequestBody @Valid MoneyDTO moneyDTO) {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account " + id + " not found"));

    account.setBalance(moneyDTO.asMoney());
    account = accountRepository.save(account);
    return account.getBalance();
  }
}
