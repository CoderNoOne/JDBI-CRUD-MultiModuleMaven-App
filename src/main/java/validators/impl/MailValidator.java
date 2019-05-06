package validators.impl;

import org.apache.commons.validator.routines.EmailValidator;

// po co robic klase ktora za chwile jedyne co robi wto wywoluj metode z klasy nadrzednej
class MailValidator extends EmailValidator {

  MailValidator(boolean allowLocal) {
    super(allowLocal);
  }
}
