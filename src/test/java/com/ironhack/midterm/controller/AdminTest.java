package com.ironhack.midterm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.controller.dto.MoneyDTO;
import com.ironhack.midterm.controller.dto.SavingsCreationDTO;
import com.ironhack.midterm.dao.account.Savings;
import com.ironhack.midterm.dao.account.StudentChecking;
import com.ironhack.midterm.dao.account.ThirdPartyAccount;
import com.ironhack.midterm.dao.test_utils.Populator;
import com.ironhack.midterm.dao.user.Admin;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AdminTest {
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
  void updateExistingAccountBalanceWithAnonymousUserFails() {
    String body =
        objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("200.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch("/account/1/balance").content(body).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void updateExistingAccountBalanceWithAuthorizedUser() {
    String body =
        objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("200.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch("/account/1/balance")
                    .with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("200.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));
  }

  @SneakyThrows
  @Test
  void updateOldAccountBalance() {
    String body =
        objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("200.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch("/account/3/balance")
                    .with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertTrue(mvcResult.getResponse().getContentAsString().contains("200.00"));
    assertTrue(mvcResult.getResponse().getContentAsString().contains("USD"));
  }

  @SneakyThrows
  @Test
  void updateNonExistingAccountBalance() {
    String body =
        objectMapper.writeValueAsString(new MoneyDTO(new Money(new BigDecimal("200.00"))));
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch("/account/999/balance")
                    .with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void createNewSavingsUnauthorizedFails() {
    String body =
        objectMapper.writeValueAsString(
            new SavingsCreationDTO(
                1L,
                null,
                "bcd",
                new MoneyDTO(new Money(new BigDecimal("10"))),
                new MoneyDTO(new Money(new BigDecimal("100"))),
                new BigDecimal("0.01")));
    MvcResult mvcResult =
        mockMvc
            .perform(
                put("/account/new/savings")
                    // .with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void createNewSavings() {
    String body =
        objectMapper.writeValueAsString(
            new SavingsCreationDTO(
                1L,
                null,
                "bcd",
                new MoneyDTO(new Money(new BigDecimal("10"))),
                new MoneyDTO(new Money(new BigDecimal("100"))),
                new BigDecimal("0.01")));
    MvcResult mvcResult =
        mockMvc
            .perform(
                put("/account/new/savings")
                    .with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    var root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    var id = root.get("id").asLong();
    var optionalAccount = accountRepository.findById(id);
    assertTrue(optionalAccount.isPresent());

    var account = optionalAccount.get();
    assertTrue(account.getBalance().getAmount().compareTo(BigDecimal.ZERO) == 0);

    assertTrue(account instanceof Savings);
    var savings = (Savings) account;
    assertTrue(savings.getMinimumBalance().getAmount().compareTo(new BigDecimal("100")) == 0);
  }

  @SneakyThrows
  @Test
  void createNewSavingsCreatesStudentChecking() {
    String body =
        objectMapper.writeValueAsString(
            new SavingsCreationDTO(
                2L,
                null,
                "bcd",
                new MoneyDTO(new Money(new BigDecimal("10"))),
                new MoneyDTO(new Money(new BigDecimal("100"))),
                new BigDecimal("0.01")));
    MvcResult mvcResult =
        mockMvc
            .perform(
                put("/account/new/savings")
                    .with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    var root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    var id = root.get("id").asLong();
    var optionalAccount = accountRepository.findById(id);
    assertTrue(optionalAccount.isPresent());

    var account = optionalAccount.get();
    assertTrue(account.getBalance().getAmount().compareTo(BigDecimal.ZERO) == 0);

    assertTrue(account instanceof StudentChecking);
    // var studentChecking = (StudentChecking) account;
  }

  @SneakyThrows
  @Test
  void createNewThirdPartyUnauthorizedFails() {
    MvcResult mvcResult =
        mockMvc
            .perform(
                put("/account/new/thirdparty")
                    // .with(httpBasic("admin", "123456"))
                    .param("name", "BitCoin")
                    .param("hashedKey", "xxx"))
            .andExpect(status().isUnauthorized())
            .andReturn();
  }

  @SneakyThrows
  @Test
  void createNewThirdParty() {
    MvcResult mvcResult =
        mockMvc
            .perform(
                put("/account/new/thirdparty")
                    .with(httpBasic("admin", "123456"))
                    .param("name", "BitCoin")
                    .param("hashedKey", "xxx"))
            .andExpect(status().isOk())
            .andReturn();

    var root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    var id = root.get("id").asLong();
    var optionalAccount = accountRepository.findById(id);
    assertTrue(optionalAccount.isPresent());

    var account = optionalAccount.get();

    assertTrue(account instanceof ThirdPartyAccount);
  }

  @SneakyThrows
  @Test
  void createDuplicateHashedKeyThirdPartyFails() {
    MvcResult mvcResult =
        mockMvc
            .perform(
                put("/account/new/thirdparty")
                    .with(httpBasic("admin", "123456"))
                    .param("name", "BitCoin")
                    .param("hashedKey", "xxx"))
            .andExpect(status().isOk())
            .andReturn();

    mvcResult =
        mockMvc
            .perform(
                put("/account/new/thirdparty")
                    .with(httpBasic("admin", "123456"))
                    .param("name", "LightCoin")
                    .param("hashedKey", "xxx"))
            .andExpect(status().isNotAcceptable())
            .andReturn();
  }
}
