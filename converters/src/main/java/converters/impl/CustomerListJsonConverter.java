package converters.impl;

import converters.JsonConverter;
import model.entity.Customer;

import java.util.List;

public class CustomerListJsonConverter extends JsonConverter<List<Customer>> {
  public CustomerListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
