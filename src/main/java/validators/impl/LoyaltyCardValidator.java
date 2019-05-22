package validators.impl;

import model.entity.LoyaltyCard;
import validators.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.others.SimulateTimeFlowUtils.getClock;
import static utils.others.UserDataUtils.printMessage;

public class LoyaltyCardValidator implements Validator<LoyaltyCard> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(LoyaltyCard loyaltyCard) {

    if (loyaltyCard == null) {
      errors.put("Loyalty Card object", "loyalty card object is null");
      return errors;
    }

    if (!isMovieNumberValid(loyaltyCard)) {
      errors.put("Movie number", "Movie number should be greater than zero");
    }

    if (!isDiscountValid(loyaltyCard)) {
      errors.put("Discount value", "Discount value should be greater than zero");
    }

    if (!isExpirationDateValid(loyaltyCard)) {
      errors.put("Loyalty Card Expiration Date", "Loyalty Card Expiration should take place in the future");

    }
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public boolean validateEntity(LoyaltyCard loyaltyCard) {
    validate(loyaltyCard);

    if (hasErrors()) {
      printMessage(errors
              .entrySet()
              .stream()
              .map(e -> e.getKey() + " : " + e.getValue())
              .collect(Collectors.joining("\n")));
    }
    return !hasErrors();
  }

  private boolean isMovieNumberValid(LoyaltyCard loyaltyCard) {
    return loyaltyCard.getMoviesNumber() > 0;
  }

  private boolean isDiscountValid(LoyaltyCard loyaltyCard) {
    return loyaltyCard.getDiscount().compareTo(BigDecimal.ZERO) > 0;
  }

  private boolean isExpirationDateValid(LoyaltyCard loyaltyCard) {
    return loyaltyCard.getExpirationDate().compareTo(LocalDate.now(getClock())) > 0;
  }
}
