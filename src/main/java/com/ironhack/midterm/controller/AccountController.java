package com.ironhack.midterm.controller;

import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.service.TransactionService;
import com.ironhack.midterm.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired AccountRepository accountRepository;
  @Autowired TransactionService transactionService;

  @PostMapping("/{from_id}/transfer/{to_id}")
  @ResponseStatus(HttpStatus.OK)
  public void transferMoney(
      @PathVariable(name = "from_id") long fromId,
      @PathVariable(name = "to_id") long toId,
      @RequestBody @Valid MoneyDTO moneyDTO) {
    Account fromAccount =
        accountRepository
            .findById(fromId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account " + fromId + " not found"));
    Account toAccount =
        accountRepository
            .findById(toId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account " + toId + " not found"));
    transactionService.moveMoney(fromId, toId, moneyDTO.asMoney());
  }

  @GetMapping("/{id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money getBalance(@PathVariable(name = "id") long id) {
    //    AccountHolder accountHolder = new AccountHolder(); // currently authenticated user????
    //
    //    if (id != accountHolder.getId())
    //      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");

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
    var dummy = account.getBalance(); // proc setting LastInterest
    account.setBalance(moneyDTO.asMoney());
    account = accountRepository.save(account);
    return account.getBalance();
  }
}
