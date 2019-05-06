package converters;

import model.Customer;

import java.util.List;

public class CustomerListJsonConverter extends JsonConverter<List<Customer>> {
  public CustomerListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
