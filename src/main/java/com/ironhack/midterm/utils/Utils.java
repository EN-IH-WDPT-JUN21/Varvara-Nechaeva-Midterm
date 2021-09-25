package com.ironhack.midterm.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import static java.util.Calendar.*;

public abstract class Utils {

  public static Date oneYearBack() {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.add(Calendar.YEAR, -1); // to get previous year add -1
    return cal.getTime();
  }

  public static Date twoYearsBack() {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.add(Calendar.YEAR, -2);
    return cal.getTime();
  }

  public static int getDiffYears(Date first, Date last) {
    Calendar a = getCalendar(first);
    Calendar b = getCalendar(last);
    int diff = b.get(YEAR) - a.get(YEAR);
    if (a.get(MONTH) > b.get(MONTH)
        || (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
      diff--;
    }
    return diff;
  }

  public static Calendar getCalendar(Date date) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.setTime(date);
    return cal;
  }

  public static Date oneMonthBack() {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.add(MONTH, -1);
    return cal.getTime();
  }

  public static Date twoMonthsBack() {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.add(MONTH, -2);
    return cal.getTime();
  }

  public static int getDiffMonths(Date first, Date last) {
    Calendar a = getCalendar(first);
    Calendar b = getCalendar(last);
    int diffYears = getDiffYears(first, last);
    int diff = b.get(MONTH) - a.get(MONTH);
    if (a.get(DATE) > b.get(DATE)) {
      diff--;
    }
    return diffYears * 12 + diff;
  }

  public static Date oneSecondBack() {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.add(SECOND, -1);
    return cal.getTime();
  }

  public static Date addTwentyFourHours(Date date) {
    Calendar cal = getCalendar(date);
    cal.add(HOUR, 24);
    return cal.getTime();
  }

  public static Date subtractTwentyFourHours(Date date) {
    Calendar cal = getCalendar(date);
    cal.add(HOUR, -24);
    return cal.getTime();
  }
}
