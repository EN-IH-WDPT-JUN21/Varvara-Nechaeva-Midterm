package com.ironhack.midterm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.controller.dto.SavingsCreationDTO;
import com.ironhack.midterm.dao.account.Savings;
import com.ironhack.midterm.dao.account.StudentChecking;
import com.ironhack.midterm.dao.account.ThirdPartyAccount;
import com.ironhack.midterm.dao.test_utils.Populator;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.repository.AccountRepository;
import com.ironhack.midterm.repository.AdminRepository;
import com.ironhack.midterm.utils.Money;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountControllerTest {
  @Autowired private AccountRepository accountRepository;

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  @BeforeEach
  void setup(
      @Autowired WebApplicationContext webApplicationContext,
      @Autowired Populator populator,
      @Autowired AdminRepository adminRepository) {

    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

    populator.populate();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    adminRepository.save(new Admin(0L, "admin", passwordEncoder.encode("123456")));
  }

  @SneakyThrows
  @Test
  void moveMoneyCorrectPrimaryOwnerName() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_name", "Vasja Pupkin"))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("10.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));
  }

  @SneakyThrows
  @Test
  void moveMoneyCorrectSecondaryOwnerName() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_name", "Petja Pupkin"))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("10.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));
  }

  @SneakyThrows
  @Test
  void moveMoneyIncorrectOwnerName() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_name", "Not Vasja Pupkin"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccount() {
    MvcResult mvcResult =
        mockMvc.perform(get("/account/1/balance")).andExpect(status().isOk()).andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("100.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));
  }

  @SneakyThrows
  @Test
  void getBalanceNonExistingAccount() {
    MvcResult mvcResult =
        mockMvc.perform(get("/account/999/balance")).andExpect(status().isNotFound()).andReturn();
  }
}
