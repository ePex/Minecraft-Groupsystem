package org.pexdev.groupsystem.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeUtil {

  private TimeUtil() {

    log.error("TimeUtil class should be used in a static manner!");
  }

  /**
   * Parses a string like "4d7m23s" into milliseconds. If the string is null or empty, returns 0
   * (meaning permanent).
   */
  public static long toMilliseconds(final String time) {

    if (time == null || time.isEmpty()) {
      return 0;
    }

    var totalMillis = 0;

    // Lowercase for uniformity
    final var lower = time.toLowerCase();

    // Regex: optional capturing groups for days (\\d+d), minutes (\\d+m), seconds (\\d+s)
    // We'll do a simple approach:
    // 1) Find all numbers followed by 'd', 'm', or 's'.
    // 2) Convert them to appropriate time in ms.
    // e.g. 1d = 86400000 ms, 1m = 60000 ms, 1s = 1000 ms.

    // Alternatively: split manually
    // This is just one example approach:
    final var numberBuffer = new StringBuilder();
    for (final var c : lower.toCharArray()) {
      if (Character.isDigit(c)) {
        numberBuffer.append(c);
      } else {
        // It's a letter: d, m, s
        if (!numberBuffer.isEmpty()) {
          final var value = Integer.parseInt(numberBuffer.toString());
          if (c == 'd') {
            totalMillis += (int) (value * 86400000L);
          } else if (c == 'm') {
            totalMillis += (int) (value * 60000L);
          } else if (c == 's') {
            totalMillis += (int) (value * 1000L);
          }
          numberBuffer.setLength(0);
        }
      }
    }

    return totalMillis;
  }

  // Convert milliseconds to a "xd ym zs" style string
  public static String toTimeString(final long millis) {

    var seconds = millis / 1000;
    final var days = seconds / 86400;
    seconds %= 86400;
    final var minutes = seconds / 60;
    final var secs = seconds % 60;

    final var sb = new StringBuilder();
    if (days > 0) {
      sb.append(days).append("d");
    }
    if (minutes > 0) {
      sb.append(minutes).append("m");
    }
    if (secs > 0) {
      sb.append(secs).append("s");
    }
    if (sb.isEmpty()) {
      sb.append("0s");
    }

    return sb.toString();
  }
}
