package com.ironhack.midterm.controller;

import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.controller.dto.SavingsCreationDTO;
import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.AccountHolderBase;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.repository.AccountHolderBaseRepository;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.repository.ThirdPartyRepository;
import com.ironhack.midterm.service.HelperService;
import com.ironhack.midterm.service.TransactionService;
import com.ironhack.midterm.utils.Constants;
import com.ironhack.midterm.utils.Money;

import com.ironhack.midterm.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired AccountRepository accountRepository;
  @Autowired AccountHolderBaseRepository accountHolderBaseRepository;
  @Autowired TransactionService transactionService;
  @Autowired ThirdPartyRepository thirdPartyRepository;

  @PostMapping("/{from_id}/transfer")
  @ResponseStatus(HttpStatus.OK)
  public Transaction transferMoney(
      @PathVariable(name = "from_id") long fromId,
      @RequestParam(name = "to_id") long toId,
      @RequestParam(name = "to_name") String toName,
      @RequestBody @Valid MoneyDTO moneyDTO) {
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

    checkUserIsOwnerOrThrow(fromId);

    return transactionService.moveMoney(fromId, toId, moneyDTO.asMoney());
  }

  @GetMapping("/{id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money getBalance(@PathVariable(name = "id") long id) {
    var account = checkUserIsOwnerOrThrow(id);
    return account.getBalance();
  }

  private Account checkUserIsOwnerOrThrow(Long id) {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account " + id + " not found"));

    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var optionalAccountHolderBase =
        accountHolderBaseRepository.findByLogin(userDetails.getUsername());
    if (optionalAccountHolderBase.isEmpty()
        || !(optionalAccountHolderBase.get() instanceof AccountHolder)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Account holder with login " + userDetails.getUsername() + " not found");
    }
    var userAccountHolder = (AccountHolder) optionalAccountHolderBase.get();
    if (account.getPrimaryOwner().getId() != userAccountHolder.getId()
        && (account.getSecondaryOwner() == null
            || account.getSecondaryOwner().getId() != userAccountHolder.getId()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");

    return account;
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

  @PutMapping("/new/savings")
  @ResponseStatus(HttpStatus.OK)
  public DebitAccount newSavings(@RequestBody @Valid SavingsCreationDTO savingsCreationDTO) {
    var primaryOwner =
        accountHolderBaseRepository
            .findById(savingsCreationDTO.getPrimaryOwnerId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Primary Owner " + savingsCreationDTO.getPrimaryOwnerId() + " not found"));

    AccountHolderBase secondaryOwner = null;
    if (savingsCreationDTO.getSecondaryOwnerId() != null) {
      secondaryOwner =
          accountHolderBaseRepository
              .findById(savingsCreationDTO.getSecondaryOwnerId())
              .orElseThrow(
                  () ->
                      new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Primary Owner "
                              + savingsCreationDTO.getSecondaryOwnerId()
                              + " not found"));
    }
    if (primaryOwner instanceof AccountHolder) {
      var accountHolder = (AccountHolder) primaryOwner;
      if (Utils.getDiffYears(accountHolder.getDateOfBirth(), new Date())
          < Constants.CHECKING_STUDENT_AGE) {
        return accountRepository.save(
            new StudentChecking(
                0L,
                new Money(new BigDecimal("0.00"), Currency.getInstance("USD")),
                savingsCreationDTO.getPenaltyFee().asMoney(),
                primaryOwner,
                secondaryOwner,
                savingsCreationDTO.getSecretKey(),
                new Date(),
                Status.ACTIVE));
      }
    }
    return accountRepository.save(
        new Savings(
            0L,
            new Money(new BigDecimal("0.00"), Currency.getInstance("USD")),
            savingsCreationDTO.getPenaltyFee().asMoney(),
            primaryOwner,
            secondaryOwner,
            savingsCreationDTO.getSecretKey(),
            new Date(),
            Status.ACTIVE,
            savingsCreationDTO.getInterestRate(),
            savingsCreationDTO.getMinimumBalance().asMoney()));
  }

  @PutMapping("/new/thirdparty")
  @ResponseStatus(HttpStatus.OK)
  public ThirdPartyAccount newThirdParty(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "hashedKey") String hashedKey) {
    if (thirdPartyRepository.findByHashedKey(hashedKey).isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE,
          "Third Party with hashed Key  " + hashedKey + " already exists");
    }

    var thirdParty = thirdPartyRepository.save(new ThirdParty(0L, name, null, null, hashedKey));

    return accountRepository.save(new ThirdPartyAccount(0L, thirdParty, new Date()));
  }

  @Autowired HelperService helperService;

  @PutMapping("/populate")
  public void populate() {
    helperService.populate();
  }
}
