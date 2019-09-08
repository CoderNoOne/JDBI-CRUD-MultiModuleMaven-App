package converters.impl;

import converters.JsonConverter;
import entity.Customer;

import java.util.List;

public class CustomerListJsonConverter extends JsonConverter<List<Customer>> {
  public CustomerListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
