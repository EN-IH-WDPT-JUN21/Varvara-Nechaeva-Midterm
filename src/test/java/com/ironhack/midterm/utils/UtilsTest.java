package com.ironhack.midterm.utils;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static com.ironhack.midterm.utils.Utils.*;

import static java.util.Calendar.DATE;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

  @Test
  void getDiffMonthsTest() {
    assertEquals(12, getDiffMonths(oneYearBack(), new Date()));
  }

  @Test
  void getDiffMonthsTest2() {
    assertEquals(2, getDiffMonths(twoMonthsBack(), new Date()));
  }

  @Test
  void getDiffMonthsTest3() {
    Calendar cal = Calendar.getInstance();
    cal.add(DATE, -35);
    Date d1 = cal.getTime();
    assertEquals(1, getDiffMonths(d1, new Date()));
  }

  @Test
  void getDiffMonthsTest4() {
    Calendar cal = Calendar.getInstance();
    cal.add(DATE, -20);
    Date d1 = cal.getTime();
    assertEquals(0, getDiffMonths(d1, new Date()));
  }
}
