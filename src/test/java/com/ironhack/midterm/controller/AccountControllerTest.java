package com.ironhack.midterm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.dao.test_utils.Populator;
import com.ironhack.midterm.repository.AccountRepository;
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
      @Autowired WebApplicationContext webApplicationContext, @Autowired Populator populator) {

    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

    populator.populate();
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccountAnonymousFails() {
    MvcResult mvcResult =
        mockMvc.perform(get("/account/1/balance")).andExpect(status().isUnauthorized()).andReturn();
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccountWrongPasswordFails() {
    MvcResult mvcResult =
        mockMvc
            .perform(get("/account/1/balance").with(httpBasic("vasja", "wrong password")))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccountWrongLogin() {
    MvcResult mvcResult =
        mockMvc
            .perform(get("/account/1/balance").with(httpBasic("petja", "123")))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccount() {
    MvcResult mvcResult =
        mockMvc
            .perform(get("/account/1/balance").with(httpBasic("vasja", "123")))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("100.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));
  }

  @SneakyThrows
  @Test
  void getBalanceExistingAccountSecondOwnerLogin() {
    MvcResult mvcResult =
        mockMvc
            .perform(get("/account/2/balance").with(httpBasic("petja", "123")))
            .andExpect(status().isOk())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void moveMoneyCorrectPrimaryOwnerName() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .with(httpBasic("vasja", "123"))
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
                    .with(httpBasic("vasja", "123"))
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
  void moveMoneyLoginNotOwnerFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .with(httpBasic("petja", "123"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_name", "Vasja Pupkin"))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void moveMoneyIncorrectLoginFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .with(httpBasic("petja", "wrong password"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_name", "Vasja Pupkin"))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void moveMoneyIncorrectOwnerName() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/account/1/transfer")
                    .with(httpBasic("vasja", "123"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_name", "Not Vasja Pupkin"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void getBalanceNonExistingAccount() {
    MvcResult mvcResult =
        mockMvc
            .perform(get("/account/999/balance").with(httpBasic("vasja", "123")))
            .andExpect(status().isNotFound())
            .andReturn();
  }
}
