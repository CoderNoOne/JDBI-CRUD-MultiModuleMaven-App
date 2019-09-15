package update;

import entity.Customer;

import static others.UserDataUtils.getInt;
import static others.UserDataUtils.getString;


public interface UpdateCustomerUtils {

  static Customer getUpdatedCustomer(Integer id) {

    return Customer.builder()
            .id(id)
            .name(getString("Do you want to update customer name? (Y/N)").equalsIgnoreCase("Y") ? getString("Input customer new name") : null)
            .surname(getString("Do you want to update customer surname? (Y/N)").equalsIgnoreCase("Y") ? getString("Input customer new surname") : null)
            .age(getString("Do you want to update customer age? (Y/N)").equalsIgnoreCase("Y") ? getInt("Input customer new age") : null)
            .email(getString("Do you want to update customer email? (Y/N)").equalsIgnoreCase("Y") ? getString("Input customer new email") : null)
            .build();
  }

}


