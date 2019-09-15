package entity_service;

import entity_repository.impl.SalesStandRepository;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import entity.Customer;
import entity.Movie;
import entity.SalesStand;


import validators.impl.SalesStandValidator;

import java.time.LocalDateTime;


@RequiredArgsConstructor
public class SalesStandService {

  private final SalesStandRepository salesStandRepository;

  private static SalesStand createSalesStand(Integer movieId, Integer customerId, LocalDateTime startDateTime) {
    return SalesStand.builder()
            .customerId(customerId)
            .movieId(movieId)
            .startDateTime(startDateTime)
            .build();
  }

  private boolean addSalesStand(Movie movie, Customer customer, LocalDateTime startDateTime) {
    var salesStand = createSalesStand(movie.getId(), customer.getId(), startDateTime);
    boolean isValid = new SalesStandValidator().validateEntity(salesStand, false);

    if (isValid) {
      salesStandRepository.add(salesStand);
    }
    return isValid;
  }

  public boolean addNewSale(Movie movie, Customer customer, LocalDateTime startDateTime) {
    if (!addSalesStand(movie, customer, startDateTime)) {
      throw new AppException("Movie start date time is not valid");
    }
    return true;
  }
}

