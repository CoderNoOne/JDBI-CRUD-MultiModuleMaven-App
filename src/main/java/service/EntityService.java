//package service;
//
//import converters.impl.MovieJsonConverter;
//import exceptions.AppException;
//import lombok.RequiredArgsConstructor;
//import model.entity.Customer;
//import model.entity.Movie;
//import repository.entity_repository.impl.CustomerRepository;
//import repository.entity_repository.impl.LoyaltyCardRepository;
//import repository.entity_repository.impl.MovieRepository;
//import repository.entity_repository.impl.SalesStandRepository;
//import service.entity_service.CustomerService;
//import validators.impl.CustomerValidator;
//import validators.impl.MovieValidator;
//
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//public class EntityService {
//
//  private final int LOYALTY_CARD_MIN_MOVIE_NUMBER = 0; // X
//
//  private final CustomerRepository customerRepository;
//  private final MovieRepository movieRepository;
//  private final LoyaltyCardRepository loyaltyCardRepository;
//  private final SalesStandRepository salesStandRepository;
//
//  private Customer createCustomer(String name, String surname, int age, String email) {
//    return Customer.builder().name(name).surname(surname).age(age).email(email).build();
//  }
//
//  public boolean addCustomer(String name, String surname, int age, String email) {
//    Customer customer = createCustomer(name, surname, age, email);
//    boolean isValid = new CustomerValidator().validateEntity(customer);
//
//    if (isValid) {
//      customerRepository.add(customer);
//    }
//    return isValid;
//  }
//
//  private Movie createMovie(final String jsonfileName) {
//
//    return new MovieJsonConverter(jsonfileName)
//            .fromJson()
//            .orElseThrow(() -> new AppException("FILE " + jsonfileName + " is empty"));
//  }
//
//  public boolean addMovie(final String jsonfileName) {
//    Movie movie = createMovie(jsonfileName);
//    boolean isValid = new MovieValidator().validateEntity(movie);
//    if (isValid) {
//      movieRepository.add(movie);
//    }
//    return isValid;
//  }
//
//  //3
//
//  public void deleteMovie(final Integer id) {
//    movieRepository.delete(id);
//  }
//
//  public void showAllMovies() {
//    movieRepository.findAll().forEach(System.out::println);
//  }
//
//  public Optional<Movie> findMovieById(final Integer id) {
//    return movieRepository.findById(id);
//  }
//
//  public void deleteCustomer(final Integer id) {
//    customerRepository.delete(id);
//  }
//
//  public void showAllCustomers() {
//    int[] counter = {1};
//    customerRepository.findAll().forEach(cus -> System.out.println(String.join(" ", "Customer no.", String.valueOf(counter[0]++), "\n", cus.toString(), "\n")));
//  }
//
//  public Optional<Customer> findCustomerById(final Integer id) {
//    return customerRepository.findById(id);
//  }
//
//  //modyfikowanie
//
//  private void updateMovie(Movie movie) {
//    movieRepository.update(movie);
//  }
//
//  public boolean updateMovieDetail(Integer id, String title, String genre, LocalDate releaseDate, Integer duration, BigDecimal price) {
//
//    Movie movie = Movie.builder().id(id).title(title).genre(genre).price(price).duration(duration).releaseDate(releaseDate).build();
//
//    boolean isValid = new MovieValidator().validateEntity(movie);
//
//    if (isValid) {
//      updateMovie(movie);
//    }
//    return isValid;
//  }
//
//  private void updateCustomer(Customer customer) {
//    customerRepository.update(customer);
//  }
//
//  public boolean updateCustomerDetail(Integer id, String name, String surname, Integer age, String email) {
//
//    Customer customer = Customer.builder().id(id).name(name).surname(surname).age(age).email(email).build();
//
//    boolean isValid = new CustomerValidator().validateEntity(customer);
//
//    if (isValid) {
//      updateCustomer(customer);
//    }
//    return isValid;
//  }
//
//  public void buyTicket(String name, String surname, String email) {
//    CustomerService customerService = new CustomerService(new CustomerRepository());
//    Customer customer = customerService.getCustomerFromUserInput(name, surname, email);
//
////    System.out.println("AVAILABLE MOVIES");
////    showAllMovies();
////
////    Integer movieId = UserDataUtils.getInt("Input movie id");
////
////    Movie movieById = movieRepository.findById(movieId).orElseThrow(() -> new AppException(""));
//
////    LocalDateTime localDateTime = UserDataUtils.getLocalDateTime("Input movie start time in format 'year-month-day HH:mm'");
////
////    SalesStand salesStand = SalesStand.builder().movieId(movieById.getId()).customerId(customer.getId()).startDateTime(localDateTime).build();
//
////    var isValid = new SalesStandValidator().validateEntity(salesStand);
////    if (!isValid) {
////    salesStandRepository.add(salesStand);
////    }
////
////    if (salesStandRepository.ticketsNumberBoughtByCustomerId(customer.getId()) >= LOYALTY_CARD_MIN_MOVIE_NUMBER) {
////      switch (UserDataUtils.getString("Do you want to add a loyalty card? (y/n)").toLowerCase()) {
////        case "y" -> loyaltyCardRepository.add(LoyaltyCard.builder()
////                .discount(new BigDecimal("5")).expirationDate(LocalDate.now().plusMonths(3)).moviesNumber(2).build());
////        case "n" -> System.out.println("TOO BAD. MAYBE NEXT TIME!");
////        default -> throw new AppException("ACTION NOT DEFINED");
////      }
////
////      customer.setLoyaltyCardId(loyaltyCardRepository.findAll().get(loyaltyCardRepository.findAll().size() - 1).getId());
////      customerRepository.update(customer);
//    }
//
//
//  }
//
//
