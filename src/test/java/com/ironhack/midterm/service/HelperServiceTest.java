package com.ironhack.midterm.service;

import com.ironhack.midterm.repository.AccountHolderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HelperServiceTest {
  @Autowired private HelperService helperService;

  @Test
  void populateTest() throws ParseException {
    helperService.populate();
  }
}
