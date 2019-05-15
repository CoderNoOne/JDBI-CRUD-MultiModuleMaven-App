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
            "Customer id - {0}\n" +
                    "Customer name - {1}\n" +
                    "Customer surname- {2}\n" +
                    "Customer age - {3}\n" +
                    "Customer email - {4}\n" +
                    "Customer loyaltyCardId - {5}",

            Objects.nonNull(id) ? id : "Customer object not persisted in db yet",
            name,
            surname,
            age,
            email,
            Objects.nonNull(loyaltyCardId) ? loyaltyCardId : "NO LOYALTY CARD YET"
    );
  }
}
