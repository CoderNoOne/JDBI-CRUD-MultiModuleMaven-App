package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.LoyaltyCard;
import model.entity.Movie;
import model.others.CustomerWithLoyaltyCard;
import repository.impl.LoyaltyCardRepository;
import utils.EmailUtils;
import utils.UserDataUtils;
import validators.impl.LoyaltyCardValidator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class LoyaltyCardService {

  private final int LOYALTY_CARD_MIN_MOVIE_NUMBER = 5;
  private final LoyaltyCardRepository loyaltyCardRepository;

  private LoyaltyCard createLoyaltyCard(BigDecimal discount, LocalDate expirationDate, Integer moviesNumber) {
    return LoyaltyCard.builder()
            .discount(discount)
            .expirationDate(expirationDate)
            .moviesNumber(moviesNumber)
            .build();
  }

  private boolean addLoyaltyCard(BigDecimal discount, LocalDate expirationDate, Integer moviesNumber) {
    var loyaltyCard = createLoyaltyCard(discount, expirationDate, moviesNumber);
    boolean isValid = new LoyaltyCardValidator().validateEntity(loyaltyCard);

    if (isValid) {
      loyaltyCardRepository.add(loyaltyCard);
    }
    return isValid;
  }

  private void addLoyaltyCardForCustomer(Customer customer) {
    if (addNewLoyaltyCard()) {
      customer.setLoyaltyCardId(getNewlyCreatedLoyaltyCardId());
    }
  }

  private void verifyIfCustomerCanGetLoyaltyCard(Integer ticketsNumber, Customer customer) {

    /*odczyt z pliku jesli nie ma id customera !=-1 to wez ticketsNumber z argumentu metody, w przeciwnym razie wez ticketsNumber - wartosc odczytana z pliku*/
    ticketsNumber = readTicketsNumberFromFileByCustomerId(customer) == -1 ? ticketsNumber : ticketsNumber - readTicketsNumberFromFileByCustomerId(customer);

    if (ticketsNumber >= LOYALTY_CARD_MIN_MOVIE_NUMBER) {
      switch (UserDataUtils.getString("Do you want to add a loyalty card? (y/n)").toLowerCase()) {
        case "y" -> {
          addLoyaltyCardForCustomer(customer);
          //zapis do pliku aktualnej wartosci ticketsNumber dla klienta
          addOrUpdateTicketsNumberToFileBoughtByCustomer(ticketsNumber, customer);
        }
        case "n" -> System.out.println("TOO BAD. MAYBE NEXT TIME!");
        default -> verifyIfCustomerCanGetLoyaltyCard(ticketsNumber, customer);/*throw new AppException("ACTION NOT DEFINED");*/
      }
    }
  }

  private Integer readTicketsNumberFromFileByCustomerId(Customer customer) {

    String ticketsNumber = "-1"; //jeżęli taka wartość customera o danym Id nie ma w zapisanego w pliku
    try {
      ticketsNumber = Files.readAllLines(Paths.get("updatedTicketNumberForCustomers.txt")).stream()
              .filter(line -> line.startsWith(String.valueOf(customer.getId())))
              .map(line -> line.substring((line.indexOf('=') + 1)))
              .findFirst().orElse(ticketsNumber);

    } catch (IOException e) {
      e.printStackTrace();
      throw new AppException("Error with properties.txt file reading");
    }

    return Integer.parseInt(ticketsNumber);
  }

  private void addOrUpdateTicketsNumberToFileBoughtByCustomer(int ticketsNumber, Customer customer) {

//    FileInputStream in = new FileInputStream("properties.txt");
//    Properties properties = new Properties();
//    properties.load();
    boolean[] isCustomerPresent = {false};
    try {
      Map<String, String> collect = Files.readAllLines(Paths.get("updatedTicketNumberForCustomers.txt")).stream()
              .map(line -> {
                var arr = line.split("[=]");
                //update
                if (isCustomerPresent[0] = Integer.parseInt(arr[0]) == (customer.getId())) {
                  arr[1] = String.valueOf(Integer.parseInt(arr[1]) + ticketsNumber);
                }
                return arr;
              }).
                      collect(Collectors.groupingBy(arr -> String.valueOf(arr[0]),
                              Collectors.mapping(arr -> arr[1], Collectors.joining())));
    } catch (IOException e) {
      e.printStackTrace();
    }


    //1 odczyt z pliku linia po linii=
    //2 split the line
    //3 prepare another line with updated data and write to temp file
    //4 finally delete your old file and rename temp file to old file
    //5 end


  }

  private boolean addNewLoyaltyCard() {
    return addLoyaltyCard(new BigDecimal("5"), LocalDate.now().plusMonths(3), 2);

  }

  private Integer getNewlyCreatedLoyaltyCardId() {
    return loyaltyCardRepository.findAll().get(loyaltyCardRepository.findAll().size() - 1).getId();
  }

  private boolean doCustomerPosesActiveLoyaltyCardByCustomerId(Integer customerId) {
    var customerWithLoyaltyCardOptional = loyaltyCardRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customerId);

    return customerWithLoyaltyCardOptional.isPresent() &&
            Objects.nonNull(customerWithLoyaltyCardOptional.get().getLoyaltyCardId()) &&
            customerWithLoyaltyCardOptional.get().getLoyaltyCardExpirationDate().compareTo(LocalDate.now()) > 0;
  }

  private void decreaseMoviesNumberByLoyaltyCardId(Integer loyaltyCardId) {
    var loyaltyCardOptional = loyaltyCardRepository.findById(loyaltyCardId);

    if (loyaltyCardOptional.isPresent()) {
      var loyaltyCard = loyaltyCardOptional.get();
      loyaltyCard.setMoviesNumber(loyaltyCard.getMoviesNumber() - 1);
      loyaltyCardRepository.update(loyaltyCard);
    }
  }

  public void buyTicket(Customer customer, Integer ticketsNumber, Movie movie, LocalDateTime movieStartTime) {

    if (!doCustomerPosesActiveLoyaltyCardByCustomerId(customer.getId())) {
      verifyIfCustomerCanGetLoyaltyCard(ticketsNumber, customer);
    } else {
      var loyaltyCardId = loyaltyCardRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customer.getId()).get().getLoyaltyCardId();
      decreaseMoviesNumberByLoyaltyCardId(loyaltyCardId);
      movie.setPrice(movie.getPrice().subtract(loyaltyCardRepository.findById(loyaltyCardId).get().getDiscount()));
    }

    EmailUtils.sendMoviePurchaseConfirmation(customer.getEmail(), "Movie Ticket purchase detail", movie, movieStartTime);
  }
}
