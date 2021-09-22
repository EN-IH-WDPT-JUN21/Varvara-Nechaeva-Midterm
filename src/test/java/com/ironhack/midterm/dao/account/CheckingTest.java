package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckingTest {

  @Test
  void checkDefaultValues() {
    Checking checking = new Checking();
    assertEquals(Constants.CHECKING_MINUM_BALANCE, checking.getMinimumBalance());
    assertEquals(Constants.CHECKING_MONTHLY_MAINTENANCE_FEE, checking.getMonthlyMaintenanceFee());
    assertEquals(Constants.DEFAULT_PENALTY_FEE, checking.getPenaltyFee());
  }
}
