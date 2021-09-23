package com.ironhack.midterm.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import static java.util.Calendar.*;

public abstract class Utils {

  public static Date oneYearBack() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -1); // to get previous year add -1
    return cal.getTime();
  }

  public static Date twoYearsBack() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -2); // to get previous year add -1
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
}
