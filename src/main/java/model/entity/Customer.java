package model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

  private Integer id;
  private String name;
  private String surname;
  private Integer age;
  private String email;
  private Integer loyaltyCardId;

  @Override
  public String toString() {
    return MessageFormat.format(
            "\nCustomer name - {0}\n" +
                    "Customer surname- {1}\n" +
                    "Customer age - {2}\n" +
                    "Customer email - {3}\n" +
                    "Customer loyaltyCardId - {4}",

            name,
            surname,
            age,
            email,
            Objects.nonNull(loyaltyCardId) ? loyaltyCardId: "NO LOYALTY CARD YET"
    );
  }
}
