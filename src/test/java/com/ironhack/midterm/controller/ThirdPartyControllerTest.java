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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ThirdPartyControllerTest {

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
  void depositMoney() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/deposit")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_secret_key", "abc")
                    .header("hashedKey", "abc"))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("10.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));

    var account = accountRepository.findById(2L).get();
    assertTrue(account.getBalance().getAmount().compareTo(new BigDecimal("1110")) == 0);
  }

  @SneakyThrows
  @Test
  void depositMoneyWrongIdFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/deposit")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "999")
                    .param("to_secret_key", "abc")
                    .header("hashedKey", "abc"))
            .andExpect(status().isNotFound())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void depositMoneyWrongSecretKeyFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/deposit")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_secret_key", "wrong key")
                    .header("hashedKey", "abc"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void depositMoneyWrongHashedKeyFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/deposit")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "2")
                    .param("to_secret_key", "abc")
                    .header("hashedKey", "wrong key"))
            .andExpect(status().isNotFound())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void withdrawMoney() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/withdraw")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("from_id", "2")
                    .param("from_secret_key", "abc")
                    .header("hashedKey", "abc"))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("10.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));

    var account = accountRepository.findById(2L).get();
    assertTrue(account.getBalance().getAmount().compareTo(new BigDecimal("1090")) == 0);
  }

  @SneakyThrows
  @Test
  void withdrawMoneyWrongIdFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/withdraw")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("from_id", "999")
                    .param("from_secret_key", "abc")
                    .header("hashedKey", "abc"))
            .andExpect(status().isNotFound())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void withdrawMoneyWrongSecretKeyFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/withdraw")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("from_id", "2")
                    .param("from_secret_key", "wrong key")
                    .header("hashedKey", "abc"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void withdrawMoneyWrongHashedKeyFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/withdraw")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("from_id", "2")
                    .param("from_secret_key", "abc")
                    .header("hashedKey", "wrong key"))
            .andExpect(status().isNotFound())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void depositMoneyCreditAccountFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/deposit")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("to_id", "4")
                    .param("to_secret_key", "abc")
                    .header("hashedKey", "abc"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void withdrawMoneyCreditAccountFails() {
    String body = objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("10.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/thirdparty/withdraw")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("from_id", "4")
                    .param("from_secret_key", "abc")
                    .header("hashedKey", "abc"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }
}
