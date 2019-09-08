package service.others;

import converters.impl.CustomerListJsonConverter;
import converters.impl.MovieListJsonConverter;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import repository.entity_repository.impl.CustomerRepository;
import repository.entity_repository.impl.LoyaltyCardRepository;
import repository.entity_repository.impl.MovieRepository;
import repository.entity_repository.impl.SalesStandRepository;
import validators.impl.CustomerValidator;
import validators.impl.MovieValidator;


import java.util.concurrent.atomic.AtomicInteger;

import static utils.others.UserDataUtils.printMessage;

@RequiredArgsConstructor
public class DataInitializeService {

  private final MovieRepository movieRepository = new MovieRepository();
  private final CustomerRepository customerRepository = new CustomerRepository();
  private final LoyaltyCardRepository loyaltyCardRepository = new LoyaltyCardRepository();
  private final SalesStandRepository salesStandRepository = new SalesStandRepository();

  public void init() {
    deleteSalesStands();
    deleteCustomers();
    deleteLoyaltyCards();
    deleteMovies();
    initMovies("exampleMovies.json");
    initCustomers("exampleCustomers.json");
  }

  private void initMovies(final String moviesJsonFilename) {

    var movieValidator = new MovieValidator();
    AtomicInteger atomicInteger = new AtomicInteger(1);
    movieRepository.deleteAll();
    new MovieListJsonConverter(moviesJsonFilename)
            .fromJson()
            .orElseThrow(() -> new AppException("FILE " + moviesJsonFilename + " is empty"))
            .stream()
            .filter(movie -> {

              if (movieValidator.hasErrors()) {
                printMessage("MOVIE NO: " + atomicInteger.get());
                movieValidator.validateEntity(movie);
              }
              atomicInteger.incrementAndGet();
              return !movieValidator.hasErrors();
            }).forEach(movieRepository::add);
  }

  private void initCustomers(final String customersJsonFilename) {
    customerRepository.deleteAll();

    var customerValidator = new CustomerValidator();
    AtomicInteger atomicInteger = new AtomicInteger(1);
    customerRepository.deleteAll();
    new CustomerListJsonConverter(customersJsonFilename)
            .fromJson()
            .orElseThrow(() -> new AppException("FILE " + customersJsonFilename + " is empty"))
            .stream()
            .filter(customer -> {
              if (customerValidator.hasErrors()) {
                printMessage("CUSTOMER NO: " + atomicInteger.get());
                customerValidator.validateEntity(customer);
              }
              atomicInteger.incrementAndGet();
              return !customerValidator.hasErrors();
            }).forEach(customerRepository::add);
  }


  private void deleteLoyaltyCards() {
    loyaltyCardRepository.deleteAll();
  }

  private void deleteSalesStands() {
    salesStandRepository.deleteAll();
  }

  private void deleteCustomers() {
    customerRepository.deleteAll();
  }

  private void deleteMovies() {
    movieRepository.deleteAll();
  }
}
