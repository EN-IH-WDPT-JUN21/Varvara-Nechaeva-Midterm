package com.ironhack.midterm.controller;

import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.Transaction;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.service.HelperService;
import com.ironhack.midterm.service.TransactionService;
import com.ironhack.midterm.utils.Money;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired AccountRepository accountRepository;
  @Autowired TransactionService transactionService;

  @PostMapping("/{from_id}/transfer")
  @ResponseStatus(HttpStatus.OK)
  public Transaction transferMoney(
      @PathVariable(name = "from_id") long fromId,
      @RequestParam(name = "to_id") long toId,
      @RequestParam(name = "to_name") String toName,
      @RequestBody @Valid MoneyDTO moneyDTO) {
    //    AccountHolder accountHolder = new AccountHolder(); // currently authenticated user????
    //
    //    if (id != accountHolder.getId())
    //      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");

    Account accountTo =
        accountRepository
            .findById(toId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account with id " + toId + " doesn't exist"));

    if (!(Objects.equals(accountTo.getPrimaryOwner().getName(), toName)
        || Objects.equals(accountTo.getSecondaryOwner().getName(), toName))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Wrong owner name for account with id " + toId);
    }

    return transactionService.moveMoney(fromId, toId, moneyDTO.asMoney());
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

  @Autowired HelperService helperService;

  @PutMapping("/populate")
  public void populate() {
    helperService.populate();
  }
}
