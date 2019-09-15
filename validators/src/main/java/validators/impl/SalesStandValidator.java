package validators.impl;

import entity.SalesStand;
import others.SimulateTimeFlowUtils;
import validators.Validator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static others.UserDataUtils.printMessage;

public class SalesStandValidator implements Validator<SalesStand> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(SalesStand salesStand, boolean isUpdate) {

    if (salesStand == null) {
      errors.put("Sales Stand", "Sales Stand object is null");
      return errors;
    }

    if (!isStartDateTimeValid(salesStand, isUpdate)) {
      errors.put("Sales stand start date time valid", "start time should take place between 8.00 and 22.30");
    }
    return errors;
  }

  private boolean isStartDateTimeValid(SalesStand salesStand, boolean isUpdate) {

    var startDateTime = salesStand.getStartDateTime();
    var presentDateTime = LocalDateTime.now(SimulateTimeFlowUtils.getClock());

    return !isUpdate ?
            startDateTime != null && startDateTime.compareTo(presentDateTime) > 0 && (startDateTime.getMinute() == 0 || startDateTime.getMinute() == 30)
                    && (startDateTime.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && startDateTime.toLocalTime().compareTo(LocalTime.of(22, 30)) <= 0)
            : startDateTime == null || startDateTime.compareTo(presentDateTime) > 0 && (startDateTime.getMinute() == 0 || startDateTime.getMinute() == 30)
            && (startDateTime.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && startDateTime.toLocalTime().compareTo(LocalTime.of(22, 30)) <= 0);
  }

  @Override
  public boolean hasErrors() {
    return false;
  }

  @Override
  public boolean validateEntity(SalesStand salesStand, boolean isUpdate) {

    validate(salesStand, isUpdate);

    if (hasErrors()) {
      printMessage(errors
              .entrySet()
              .stream()
              .map(e -> e.getKey() + " : " + e.getValue())
              .collect(Collectors.joining("\n")));
    }
    return !hasErrors();
  }
}
