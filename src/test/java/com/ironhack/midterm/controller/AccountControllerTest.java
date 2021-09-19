package com.ironhack.midterm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.account.Savings;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.utils.Address;
import com.ironhack.midterm.utils.Money;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountControllerTest {
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  @BeforeEach
  void setup(
      @Autowired WebApplicationContext webApplicationContext,
      @Autowired AccountRepository accountRepository,
      @Autowired AccountHolderRepository accountHolderRepository) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    Address primaryAddress = new Address("Main Street 1", "Main City", "9999");
    Address mailingAddress = new Address("Main Street 2", "Main City", "9991");

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    String dateInString = "7-Jun-2013";
    Date date = formatter.parse(dateInString);

    AccountHolder accountHolder1 =
        new AccountHolder(0L, "Vasja Pupkin", date, primaryAddress, mailingAddress, null, null);
    accountHolder1 = accountHolderRepository.save(accountHolder1);

    Savings savings =
        new Savings(
            0L,
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            accountHolder1,
            null,
            "abc",
            date,
            Status.ACTIVE,
            new BigDecimal("1.23"),
            new Money(new BigDecimal("10.00"), Currency.getInstance("USD")));

    accountRepository.save(savings);
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccount() {
    MvcResult mvcResult =
        mockMvc.perform(get("/account/1/balance")).andExpect(status().isOk()).andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains(("100.00")));
    assertTrue(mvcResult.getResponse().getContentAsString().contains(("USD")));
  }

  @SneakyThrows
  @Test
  void getBalanceNonExistingAccount() {
    MvcResult mvcResult =
        mockMvc.perform(get("/account/999/balance")).andExpect(status().isOk()).andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
  }

  @SneakyThrows
  @Test
  void updateExistingAccountBalance() {
    String body =
        objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("200.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch("/account/1/balance").content(body).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains(("200.00")));
    assertTrue(mvcResult.getResponse().getContentAsString().contains(("USD")));
  }

  @SneakyThrows
  @Test
  void updateNonExistingAccountBalance() {
    String body =
        objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("200.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch("/account/999/balance").content(body).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
  }
}
