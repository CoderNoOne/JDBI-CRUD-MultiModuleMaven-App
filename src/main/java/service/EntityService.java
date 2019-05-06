package service;

import converters.MovieJsonConverter;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.Customer;
import model.Movie;
import repository.impl.CustomerRepository;
import repository.impl.LoyaltyCardRepository;
import repository.impl.MovieRepository;
import repository.impl.SalesStandRepository;
import validators.*;
import validators.impl.CustomerValidator;
import validators.impl.LoyaltyCardValidator;
import validators.impl.MovieValidator;
import validators.impl.SalesStandValidator;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntityService {


  private final int LOYALTY_CARD_MIN_MOVIE_NUMBER = 5; // X

  private final CustomerRepository customerRepository;
  private final MovieRepository movieRepository;
  private final LoyaltyCardRepository loyaltyCardRepository;
  private final SalesStandRepository salesStandRepository;

  // referencje lokalne
  /*private final CustomerValidator customerValidator;
  private final MovieValidator movieValidator;
  private final LoyaltyCardValidator loyaltyCardValidator;
  private final SalesStandValidator salesStandValidator;*/


  // przeniesc do interfejsu Validator
  private <T> boolean validateEntity(Validator<T> validator, T entity) {

    Map<String, String> entityErrors = validator.validate(entity);

    if (validator.hasErrors()) {
      System.out.println(entityErrors
              .entrySet()
              .stream()
              .map(e -> e.getKey() + " : " + e.getValue())
              .collect(Collectors.joining("\n")));
    }
    return !validator.hasErrors();
  }

  private Customer createCustomer(String name, String surname, int age, String email) {

    // String customerName = UserDataUtils.getString("Input customer name");
    // String customerSurname = UserDataUtils.getString("Input customer surname");
    // Integer customerAge = UserDataUtils.getInt("Input customer age");
    // String customerEmail = UserDataUtils.getString("Input customer Email");

    return Customer.builder().name(name).surname(surname).age(age).email(email).build();
  }

  public boolean addCustomer(String name, String surname, int age, String email) {
    Customer customer = createCustomer(name, surname, age, email);
    boolean isValid = validateEntity(new CustomerValidator(), customer);

    if (isValid) {
      customerRepository.add(customer);
    }
    return isValid;
  }

  private Movie createMovie(final String jsonfileName) {

    return new MovieJsonConverter(jsonfileName)
            .fromJson()
            .orElseThrow(() -> new AppException("FILE " + jsonfileName + " is empty"));
  }

  public boolean addMovie(final String jsonfileName) {
    Movie movie = createMovie(jsonfileName);
    boolean isValid = validateEntity(movieValidator, movie);
    if (isValid) {
      movieRepository.add(movie);
    }
    return isValid;
  }

  //3

  public void deleteMovie(final Integer id) {
    movieRepository.delete(id);
  }

  public void showAllMovies() {
    movieRepository.findAll().forEach(System.out::println);
  }

  public Optional<Movie> findMovieById(final Integer id) {
    return movieRepository.findById(id);
  }

  public void deleteCustomer(final Integer id) {
    customerRepository.delete(id);
  }

  public void showAllCustomers() {
    customerRepository.findAll().forEach(System.out::println);
  }

  public Optional<Customer> findCustomerById(final Integer id) {
    return customerRepository.findById(id);
  }

  //modyfikowanie

  public void updateMovie(Movie movie) {
    movieRepository.update(movie);
  }

  public boolean updateMovieDetail() {

    Integer movieId = userDataUtils.getInt("Input movie id");
    String movieTitle = userDataUtils.getString("Input movie title");
    String movieGenre = userDataUtils.getString("Input movie genre");
    String movieReleaseDate = userDataUtils.getDate("Input movie release date");
    Integer movieDuration = userDataUtils.getInt("Input movie duration");
    BigDecimal moviePrice = userDataUtils.getBigDecimal("Input movie price");

    Movie movie = Movie.builder()
            .id(movieId)
            .title(movieTitle)
            .genre(movieGenre)
            .price(moviePrice)
            .duration(movieDuration)
            .releaseDate(movieReleaseDate)
            .build();

    boolean isValid = validateEntity(movieValidator, movie);

    if (isValid) {
      updateMovie(movie);
    }
    return isValid;
  }

  public void updateCustomer(Customer customer) {
    customerRepository.update(customer);
  }

  public boolean updateCustomerDetail() {

    Integer customerId = userDataUtils.getInt("Input customer id");
    String customerName = userDataUtils.getString("Input customer name");
    String customerSurname = userDataUtils.getString("Input customer surname");
    Integer customerAge = userDataUtils.getInt("Input customer age");
    String customerEmail = userDataUtils.getString("Input customer email");

    final Customer customer = Customer.builder()
            .id(customerId)
            .name(customerName)
            .surname(customerSurname)
            .age(customerAge)
            .email(customerEmail)
            .build();

    boolean isValid = validateEntity(customerValidator, customer);

    if (isValid) {
      updateCustomer(customer);
    }
    return isValid;

  }
}
