package com.ironhack.midterm.controller;

import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired AccountRepository accountRepository;

  @GetMapping("/{id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money getBalance(@PathVariable(name = "id") long id) {
    Optional<Account> optionalAccount = accountRepository.findById(id);
    return optionalAccount.isPresent() ? optionalAccount.get().getBalance() : null;
  }

  @PatchMapping("/{id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money updateBalance(
      @PathVariable(name = "id") long id, @RequestBody @Valid MoneyDTO moneyDTO) {
    Optional<Account> optionalAccount = accountRepository.findById(id);
    if (optionalAccount.isEmpty()) {
      return null;
    }
    Account account = optionalAccount.get();
    account.setBalance(moneyDTO.asMoney());
    account = accountRepository.save(account);
    return account.getBalance();
  }
}
