package utils;

import java.time.*;

public class SimulateTimeFlowUtils {

  private static Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

  private SimulateTimeFlowUtils() {
  }

  private static void moveDateTimeForwardBySpecifiedDaysAmount(int days) {
      clock = Clock.offset(clock, Duration.ofDays(days));
  }
}
