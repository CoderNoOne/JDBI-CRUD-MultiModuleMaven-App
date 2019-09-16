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

  public void addNewSale(Integer movieId, Integer customerId, LocalDateTime startDateTime) {

    if (movieId == null || customerId == null || startDateTime == null) {
      throw new AppException("Arguments cannot be null");
    }

    var salesStand = createSalesStand(movieId, customerId, startDateTime);
    boolean isValid = new SalesStandValidator().validateEntity(salesStand, false);

    if (isValid) {
      salesStandRepository.add(salesStand);
    } else {
      throw new AppException("Movie start date time is not valid");
    }

  }

}

