package com.ironhack.midterm.controller;

import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.DebitAccount;
import com.ironhack.midterm.dao.account.Transaction;
import com.ironhack.midterm.repository.AccountHolderBaseRepository;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.repository.ThirdPartyRepository;
import com.ironhack.midterm.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("/thirdparty")
public class ThirdPartyController {

  @Autowired AccountRepository accountRepository;
  @Autowired TransactionService transactionService;
  @Autowired ThirdPartyRepository thirdPartyRepository;

  @PostMapping("/deposit")
  @ResponseStatus(HttpStatus.OK)
  public Transaction depositMoney(
      @RequestHeader(name = "hashedKey") String hashedKey,
      @RequestParam(name = "to_id") long toId,
      @RequestParam(name = "to_secret_key") String toSecretKey,
      @RequestBody @Valid MoneyDTO moneyDTO) {

    var thirdParty =
        thirdPartyRepository
            .findByHashedKey(hashedKey)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Third party with hashed key " + hashedKey + " not found"));
    var fromAccount = thirdParty.getPrimaryAccounts().get(0);

    Account accountTo =
        accountRepository
            .findById(toId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account with id " + toId + " doesn't exist"));

    if (!(accountTo instanceof DebitAccount))
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Account with id " + toId + " is not a debit account.");

    var debitAccount = (DebitAccount) accountTo;

    if (!Objects.equals(debitAccount.getSecretKey(), toSecretKey)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Wrong secret key for account with id " + toId);
    }

    return transactionService.moveMoney(fromAccount.getId(), toId, moneyDTO.asMoney());
  }

  @PostMapping("/withdraw")
  @ResponseStatus(HttpStatus.OK)
  public Transaction withdrawMoney(
      @RequestHeader(name = "hashedKey") String hashedKey,
      @RequestParam(name = "from_id") long fromId,
      @RequestParam(name = "from_secret_key") String fromSecretKey,
      @RequestBody @Valid MoneyDTO moneyDTO) {

    var thirdParty =
        thirdPartyRepository
            .findByHashedKey(hashedKey)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Third party with hashed key " + hashedKey + " not found"));
    var accountTo = thirdParty.getPrimaryAccounts().get(0);

    Account accountFrom =
        accountRepository
            .findById(fromId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account with id " + fromId + " doesn't exist"));

    if (!(accountFrom instanceof DebitAccount))
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Account with id " + fromId + " is not a debit account.");

    var debitAccount = (DebitAccount) accountFrom;

    if (!Objects.equals(debitAccount.getSecretKey(), fromSecretKey)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Wrong secret key for account with id " + fromId);
    }

    return transactionService.moveMoney(fromId, accountTo.getId(), moneyDTO.asMoney());
  }
}
