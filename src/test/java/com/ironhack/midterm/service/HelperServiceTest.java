package com.ironhack.midterm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

@SpringBootTest
class HelperServiceTest {
  @Autowired private HelperService helperService;

  @Test
  void populateTest() throws ParseException {
    helperService.populate();
  }
}
