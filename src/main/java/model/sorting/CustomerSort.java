package model.sorting;

import exceptions.AppException;
import model.Customer;
import model.Movie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomerSort {

  private List<Comparator<Customer>> comparators;

  private CustomerSort(CustomerSortBuilder customerSortBuilder) {
    comparators = customerSortBuilder.comparators;
  }

  public Comparator<Customer> getComparator() {

    if (comparators.isEmpty()) {
      throw new AppException("NO COMPARATORS AVAILABLE");
    }
    Comparator<Customer> comparator = comparators.get(0);

    comparators.stream().skip(1).forEach(comparator::thenComparing);

    return comparator;
  }

  public static class CustomerSortBuilder {

    private List<Comparator<Customer>> comparators = new ArrayList<>();

    public CustomerSortBuilder name(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Customer::getName) : Comparator.comparing(Customer::getName).reversed());
      return this;
    }

    public CustomerSortBuilder surname(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Customer::getSurname) : Comparator.comparing(Customer::getSurname).reversed());
      return this;
    }

    public CustomerSortBuilder age(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Customer::getAge) : Comparator.comparing(Customer::getAge).reversed());
      return this;
    }

    public CustomerSortBuilder email(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Customer::getEmail) : Comparator.comparing(Customer::getEmail).reversed());
      return this;
    }

    public CustomerSort build() {
      return new CustomerSort(this);
    }
  }
}
