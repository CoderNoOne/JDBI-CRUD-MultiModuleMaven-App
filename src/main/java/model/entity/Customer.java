package model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer implements Comparable<Customer> {

  private Integer id;
  private String name;
  private String surname;
  private Integer age;
  private String email;
  private Integer loyaltyCardId;

  @Override
  public int compareTo(Customer o) {
    return Comparator.comparing(Customer::getSurname).thenComparing(Customer::getName).compare(this, o);
  }
}
