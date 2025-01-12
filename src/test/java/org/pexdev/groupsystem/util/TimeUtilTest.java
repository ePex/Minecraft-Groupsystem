package org.pexdev.groupsystem.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeUtilTest {

  @Test
  void testNullOrEmpty() {

    // Null or empty string should return 0 (meaning permanent)
    Assertions.assertEquals(0, TimeUtil.toMilliseconds(null));
    Assertions.assertEquals(0, TimeUtil.toMilliseconds(""));
  }

  @Test
  void testDaysMinutesSeconds() {

    // 4d7m23s = 4 days, 7 minutes, 23 seconds
    // 4 days = 4 * 86400000 = 345600000 ms
    // 7 minutes = 7 * 60000 = 420000 ms
    // 23 seconds = 23 * 1000 = 23000 ms
    // total = 345600000 + 420000 + 23000 = 346043000
    long result = TimeUtil.toMilliseconds("4d7m23s");
    Assertions.assertEquals(346043000L, result);
  }

  @Test
  void testOnlyMinutes() {

    long result = TimeUtil.toMilliseconds("10m");
    // 10 minutes = 600000 ms
    Assertions.assertEquals(600000, result);
  }

  @Test
  void testOnlySeconds() {

    long result = TimeUtil.toMilliseconds("30s");
    Assertions.assertEquals(30000, result);
  }

  @Test
  void testInvalidFormat() {

    // If someone types nonsense, e.g. "abc", the method might parse 0
    long result = TimeUtil.toMilliseconds("abc");
    Assertions.assertEquals(0, result);
  }
}
