package validators;

import model.LoyaltyCard;

import java.util.HashMap;
import java.util.Map;

public class SalesStandValidator implements Validator<LoyaltyCard> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(LoyaltyCard salesStand) {

    if (salesStand == null) {
      errors.put("Sales Stand", "Sales Stand object is null");
      return errors;
    }

    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

}
